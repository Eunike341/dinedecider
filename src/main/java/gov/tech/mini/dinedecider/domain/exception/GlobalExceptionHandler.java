package gov.tech.mini.dinedecider.domain.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex, WebRequest request) {
        LOG.error("API exception", ex);
        HttpStatus status = HttpStatus.resolve(ex.getErrorCode().getStatusCode());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorDetail(new Date(), ex.getErrorCode().name(), ex.getMessage()), status);
    }

    @ExceptionHandler({ConstraintViolationException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> handleInputException(Exception ex) {
        LOG.error("Input exception", ex);
        return new ResponseEntity<>(new ErrorDetail(new Date(), ErrorCode.INVALID_INPUT.name(), null),
                HttpStatus.resolve(ErrorCode.INVALID_INPUT.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        LOG.error("System exception", ex);
        return new ResponseEntity<>(new ErrorDetail(new Date(), ErrorCode.SYSTEM_EXCEPTION.name(), request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

