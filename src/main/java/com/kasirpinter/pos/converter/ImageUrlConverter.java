package com.kasirpinter.pos.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUrlConverter {

    @Value("${app.base.url}")
    private String baseUrl;

    public String getUserAvatar(String userId){
        return baseUrl + "/get/file/user/" + userId + "/avatar";
    }

    public String getUserCover(String userId){
        return baseUrl + "/get/file/user/" + userId + "/cover";
    }

}
