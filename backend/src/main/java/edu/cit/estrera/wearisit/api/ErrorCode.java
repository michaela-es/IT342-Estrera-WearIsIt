package edu.cit.estrera.wearisit.api;

public enum ErrorCode {

    AUTH_001("AUTH-001", "Invalid credentials"),
    AUTH_002("AUTH-002", "Token expired"),
    AUTH_003("AUTH-003", "Insufficient permissions"),
    AUTH_004("AUTH-004", "Email already registered"),
    AUTH_005("AUTH-005", "Account not found"),

    VALID_001("VALID-001", "Validation failed"),
    VALID_002("VALID-002", "Missing required field"),

    DB_001("DB-001", "Resource not found"),
    DB_002("DB-002", "Duplicate entry"),

    FILE_001("FILE-001", "File too large"),
    FILE_002("FILE-002", "Invalid file type"),

    SYSTEM_001("SYSTEM-001", "Internal server error");

    private final String code;
    private final String message;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}