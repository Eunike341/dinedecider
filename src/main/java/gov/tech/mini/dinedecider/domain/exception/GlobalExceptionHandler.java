package gov.tech.mini.dinedecider.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex, WebRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getErrorCode().getStatusCode());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorDetail(new Date(), ex.getMessage(), request.getDescription(false)), status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        return new ResponseEntity<>(new ErrorDetail(new Date(), "Please contact administrator", request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

