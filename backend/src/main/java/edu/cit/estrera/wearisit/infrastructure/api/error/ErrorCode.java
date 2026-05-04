package edu.cit.estrera.wearisit.infrastructure.api.error;

public enum ErrorCode {

    AUTH_001("AUTH-001", "Invalid credentials"),
    AUTH_002("AUTH-002", "Token expired"),
    AUTH_003("AUTH-003", "Insufficient permissions"),
    AUTH_004("AUTH-004", "Email already registered"),
    AUTH_005("AUTH-005", "Account not found"),
    AUTH_006("AUTH-006", "Email format is invalid"),
    AUTH_007("AUTH-007", "Password cannot be empty"),
    AUTH_008("AUTH-008", "Username cannot be empty"),
    AUTH_009("AUTH-009", "Username already exists"),


    VALID_001("VALID-001", "Validation failed"),
    VALID_002("VALID-002", "Missing required field"),

    DB_001("DB-001", "Resource not found"),
    DB_002("DB-002", "Duplicate entry"),

    // Add clothing item specific errors
    ITEM_001("ITEM-001", "Item not found"),
    ITEM_002("ITEM-002", "You don't own this item"),
    ITEM_003("ITEM-003", "Type not found"),
    ITEM_004("ITEM-004", "Category not found"),
    ITEM_005("ITEM-005", "Tag not found"),

    FILE_001("FILE-001", "File size exceeds the maximum limit of 10MB"),
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