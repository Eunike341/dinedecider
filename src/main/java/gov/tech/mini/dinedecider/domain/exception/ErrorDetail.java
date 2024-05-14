package gov.tech.mini.dinedecider.domain.exception;

import java.util.Date;

public class ErrorDetail {
    private Date timestamp;
    private String errCode;
    private String message;

    public ErrorDetail(Date timestamp, String errCode, String message) {
        this.timestamp = timestamp;
        this.errCode = errCode;
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

