package com.eaut.footballclubmanagement;

public final class OperationResult<T> {
    public enum Status { SUCCESS, ERROR, AUTH_REQUIRED }

    private final Status status;
    private final T data;
    private final String message;

    private OperationResult(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> OperationResult<T> success(T data) {
        return new OperationResult<>(Status.SUCCESS, data, null);
    }

    public static <T> OperationResult<T> error(String message) {
        return new OperationResult<>(Status.ERROR, null, message);
    }

    public static <T> OperationResult<T> authRequired(String message) {
        return new OperationResult<>(Status.AUTH_REQUIRED, null, message);
    }

    public Status getStatus() { return status; }
    public T getData() { return data; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return status == Status.SUCCESS; }
}
