package com.example.authservice.exception;

public class AuthException {
    //AUTHENTICATION EXCEPTIONS
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("Invalid credential: Email or password is incorrect");
        }
    }

public static class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("User already exists");
    }
}

public static class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }
}

//TOKENS: JWT and refresh token
public static class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String tokenType) {
        super("Token expired: " + tokenType);
    }
}

public static class TokenInvalidException extends RuntimeException {
    public TokenInvalidException(String reason) {
        super("Token invalid: " + reason);
    }
}

// VALIDATION: Input validation failures
public static class ValidationException extends RuntimeException {
    public ValidationException(String field, String requirement) {
        super("Validation failed for " + field + ": " + requirement);
    }
}
// SECURITY: Rate limiting
public static class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String action, int minutes) {
        super("Rate limit exceeded for " + action + " in the last " + minutes + " minutes");
    }
}

public static class AccountInactiveException extends RuntimeException {
    public AccountInactiveException() {
        super("Account is inactive for user: " );
    }
}

//INTEGRATION: Service communication failures
public static class UserServiceException extends RuntimeException {
    public UserServiceException(String operation, String reason) {
        super(String.format("USER_SERVICE_ERROR: Failed to %s - %s", operation, reason));
    }
}
}
