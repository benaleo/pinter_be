package com.kasirpinter.pos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogGeneralRequest {

    private String identifier;

    private String model;

    private String fromLog;

    private String toLog;

    private String note;

    private String actionBy;

}
