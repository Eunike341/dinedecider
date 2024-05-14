package gov.tech.mini.dinedecider.domain.exception;

public enum ErrorCode {
    USER_NOT_FOUND(404),
    SESSION_NOT_FOUND(404),
    USER_NOT_ADMIN(403),
    SESSION_ENDED(410),
    NO_AVAILABLE_SELECTION(422),
    INVALID_SUBMISSION(400);

    private final int statusCode;

    ErrorCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
