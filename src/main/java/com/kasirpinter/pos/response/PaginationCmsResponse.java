package com.kasirpinter.pos.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class PaginationCmsResponse<T> implements Serializable {

    private boolean success;
    private String message;
    private Object data;
    private List<Map<String, List<?>>> attributes;

    public PaginationCmsResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public PaginationCmsResponse(boolean success, String message, Object data, List<Map<String, List<?>>> attributes) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.attributes = attributes;
    }
}
