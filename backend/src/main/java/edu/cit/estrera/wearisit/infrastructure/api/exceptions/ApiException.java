package edu.cit.estrera.wearisit.infrastructure.api.exceptions;

import edu.cit.estrera.wearisit.infrastructure.api.error.ErrorCode;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object details;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ApiException(ErrorCode errorCode, Object details) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getDetails() {
        return details;
    }
}