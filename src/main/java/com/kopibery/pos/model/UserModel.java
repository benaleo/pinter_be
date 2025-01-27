package com.kopibery.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

public class UserModel {

    @Data
    public static class IndexResponse extends AdminModelBaseDTOResponse {

        private String name;
        private String email;

    }

    @Data
    @AllArgsConstructor
    public static class DetailResponse {

        private String name;
        private String email;

    }

    @Data
    public static class CreateRequest {

        private String name;
        private String email;
        private String password;

    }

    @Data
    public static class UpdateRequest {

        private String name;
        private String password;

    }
}
