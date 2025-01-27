package com.kopibery.pos.util;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class UploadStreamHelper {

    public static HttpHeaders HeaderStreamHelper(String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName)
                .build());
        return headers;
    }

}
