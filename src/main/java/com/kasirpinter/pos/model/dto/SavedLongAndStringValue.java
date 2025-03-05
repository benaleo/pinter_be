package com.kasirpinter.pos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SavedLongAndStringValue {
    private Long key;
    private String value;
}
