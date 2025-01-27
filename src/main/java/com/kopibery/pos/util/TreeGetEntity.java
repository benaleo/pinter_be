package com.kopibery.pos.util;

import com.kopibery.pos.entity.Roles;
import com.kopibery.pos.entity.Users;
import com.kopibery.pos.repository.RoleRepository;
import com.kopibery.pos.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.function.Function;

public class TreeGetEntity {

    private static <T, U> U getIdBySecureId(
            String secureId,
            Function<String, Optional<T>> findBySecureIdFunction,
            Function<T, Optional<U>> findByIdFunction,
            String errorMessage) {

        return findBySecureIdFunction.apply(secureId)
                .map(projection -> findByIdFunction.apply(projection)
                        .orElseThrow(() -> new EntityNotFoundException(errorMessage)))
                .orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    public static Users parsingUserByProjection(String secureId, UserRepository repository) {
        return getIdBySecureId(
                secureId,
                repository::findIdBySecureId,
                projection -> repository.findById(projection.getId()),
                "User not found"
        );
    }

    public static Roles parsingRoleByProjection(String secureId, RoleRepository repository) {
        return getIdBySecureId(
                secureId,
                repository::findIdBySecureId,
                projection -> repository.findById(projection.getId()),
                "Role not found"
        );
    }

}
