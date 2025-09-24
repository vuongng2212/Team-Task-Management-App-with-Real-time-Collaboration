package com.example.authservice.exception;

public class AuthException {
    
    // AUTHENTICATION: Core auth failures
    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() {
            super("INVALID_CREDENTIALS: Email or password is incorrect");
        }
    }
    
    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException(String email) {
            super(String.format("USER_EXISTS: Account with email %s already exists", email));
        }
    }
    
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String identifier) {
            super(String.format("USER_NOT_FOUND: No account found for %s", identifier));
        }
    }
    
    // TOKENS: JWT and refresh token issues
    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String tokenType) {
            super(String.format("TOKEN_EXPIRED: %s token has expired", tokenType));
        }
    }
    
    public static class TokenInvalidException extends RuntimeException {
        public TokenInvalidException(String reason) {
            super(String.format("TOKEN_INVALID: %s", reason));
        }
    }
    
    // VALIDATION: Input validation failures
    public static class ValidationException extends RuntimeException {
        public ValidationException(String field, String requirement) {
            super(String.format("VALIDATION_ERROR: %s %s", field, requirement));
        }
    }
    
    // SECURITY: Rate limiting and security policies
    public static class RateLimitExceededException extends RuntimeException {
        public RateLimitExceededException(String action, int minutes) {
            super(String.format("RATE_LIMIT_EXCEEDED: Too many %s attempts. Try again in %d minutes", action, minutes));
        }
    }
    
    public static class AccountInactiveException extends RuntimeException {
        public AccountInactiveException() {
            super("ACCOUNT_INACTIVE: Your account has been deactivated. Contact support");
        }
    }
    
    public static class AccountLockedException extends RuntimeException {
        public AccountLockedException(int minutes) {
            super(String.format("ACCOUNT_LOCKED: Too many failed attempts. Try again in %d minutes", minutes));
        }
    }
    
    // INTEGRATION: Service communication failures
    public static class UserServiceException extends RuntimeException {
        public UserServiceException(String operation, String reason) {
            super(String.format("USER_SERVICE_ERROR: Failed to %s - %s", operation, reason));
        }
    }
}
