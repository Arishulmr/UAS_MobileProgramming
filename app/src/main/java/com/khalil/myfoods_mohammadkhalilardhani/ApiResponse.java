package com.khalil.myfoods_mohammadkhalilardhani;

public class ApiResponse<T> {
    private String status;
    private T data;

    public String getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }
}

