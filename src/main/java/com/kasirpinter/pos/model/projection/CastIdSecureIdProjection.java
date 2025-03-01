package com.kasirpinter.pos.model.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CastIdSecureIdProjection {

    private Long id;
    private String secureId;

}
