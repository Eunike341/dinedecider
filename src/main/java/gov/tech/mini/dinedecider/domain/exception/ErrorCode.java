package gov.tech.mini.dinedecider.domain.exception;

public enum ErrorCode {
    USER_NOT_FOUND(404),
    SESSION_NOT_FOUND(404),
    USER_NOT_ADMIN(403),
    USER_NOT_IN_SESSION(403),
    SESSION_ENDED(400),
    NO_AVAILABLE_SELECTION(400),
    INVALID_JOIN_ATTEMPT(400),
    INVALID_SUBMISSION(400),
    INVALID_INPUT(400),
    SYSTEM_EXCEPTION(500);

    private final int statusCode;

    ErrorCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
