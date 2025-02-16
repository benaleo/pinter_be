package com.kopibery.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CastKeyValueProjection {
    private String key;
    private String value;
}
