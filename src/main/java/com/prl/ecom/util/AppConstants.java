package com.prl.ecom.util;

public class AppConstants {
    public static final String API_BASE_PATH = "/api";
    
    // Pagination
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // JWT
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    
    // Validation Messages
    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String INVALID_EMAIL = "Invalid email format";
    public static final String PASSWORD_MIN_LENGTH = "Password must be at least 8 characters";
    public static final String FIRST_NAME_REQUIRED = "First name is required";
    public static final String LAST_NAME_REQUIRED = "Last name is required";
    
    // Error Messages
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_CREDENTIALS = "Invalid email or password";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String CART_NOT_FOUND = "Cart not found";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock for product";
    public static final String INVALID_QUANTITY = "Quantity must be greater than 0";
    public static final String INVALID_TOKEN = "Invalid or expired token";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    
    // Success Messages
    public static final String USER_REGISTERED = "User registered successfully";
    public static final String LOGIN_SUCCESS = "Login successful";
    public static final String ORDER_CREATED = "Order created successfully";
    public static final String PRODUCT_ADDED_TO_CART = "Product added to cart";
    public static final String CART_CLEARED = "Cart cleared successfully";
    
    // Sorting
    public static final String SORT_BY_DEFAULT = "createdAt";
    public static final String SORT_DIRECTION_DESC = "DESC";
}
