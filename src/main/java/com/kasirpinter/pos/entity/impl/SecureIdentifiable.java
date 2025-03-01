package com.kasirpinter.pos.entity.impl;

public interface SecureIdentifiable {
    Long getId();

    String getSecureId();

    Boolean getIsActive();
}
