package com.kopibery.pos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SavedStringAndLongValue {

    private String key;
    private Long value;

}
