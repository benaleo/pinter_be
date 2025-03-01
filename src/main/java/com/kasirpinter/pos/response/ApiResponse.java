package com.kasirpinter.pos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;

}
