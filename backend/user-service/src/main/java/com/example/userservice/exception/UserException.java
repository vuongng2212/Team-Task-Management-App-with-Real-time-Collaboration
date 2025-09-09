package com.example.userservice.exception;

public class UserException {
    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String userId) {
            super(String.format("User with ID %s not found", userId));
        }
    }

    public static class ValidationException extends RuntimeException{
        public ValidationException(String message) {
            super(String.format("Validation failed: %s", message));
        }
    }

    public static class UnauthorizedException extends RuntimeException{
        public UnauthorizedException(String message) {
            super(String.format("Unauthorized: %s", message));
        }
    }

    public static class ForbiddenException extends RuntimeException{
        public ForbiddenException(String message) {
            super(String.format("Forbidden: %s", message));
        }
    }
}