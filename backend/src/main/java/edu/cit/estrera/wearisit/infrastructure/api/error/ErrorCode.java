package edu.cit.estrera.wearisit.infrastructure.api.error;

import lombok.Getter;

@Getter
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

    AUTH_010("AUTH-010","Invalid or expired token" ),

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
    SYSTEM_001("SYSTEM-001", "Internal server error"),

    CAT_001("CAT-001", "Category not found"),
    CAT_002("CAT-002", "Category already exists"),
    CAT_003("CAT-003", "Cannot delete category with existing tags"),
    CAT_004("CAT-004", "Category name cannot be empty"),
    TAG_001("TAG-001", "Tag is in use and cannot be deleted"),

    OUTFIT_001("OUTFIT-001", "Outfit not found"),
    OUTFIT_002("OUTFIT-002", "You don't own this outfit"),
    OUTFIT_003("OUTFIT-003", "Invalid outfit composition"),
    OUTFIT_004("OUTFIT-004", "Duplicate items in outfit"),
    OUTFIT_005("OUTFIT-005", "Item does not belong to user"),
    OUTFIT_006("OUTFIT-006", "Unknown item type"),
    OUTFIT_007("OUTFIT-007", "Outfit must have between 2 and 8 items"),
    ADMIN_001("ADMIN-001", "Email address is not authorized to register as an administrator."),
    ADMIN_002("ADMIN-002", "Access denied. Admin role required.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }

}