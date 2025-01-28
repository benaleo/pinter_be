package com.kopibery.pos.util;

import com.kopibery.pos.entity.*;
import com.kopibery.pos.repository.*;
import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;
import java.util.function.Function;

public class TreeGetEntity {

    public static <T, U> U getIdBySecureId(
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

    public static Company parsingCompanyByProjection(String secureId, CompanyRepository repository) {
        return getIdBySecureId(
                secureId,
                repository::findIdBySecureId,
                projection -> repository.findById(projection.getId()),
                "Company not found"
        );
    }

    public static Product parsingProductByProjection(String secureId, ProductRepository repository) {
        return getIdBySecureId(
                secureId,
                repository::findIdBySecureId,
                projection -> repository.findById(projection.getId()),
                "Product not found"
        );
    }

    public static ProductCategory parsingProductCategoryByProjection(String secureId, ProductCategoryRepository repository) {
        return getIdBySecureId(
                secureId,
                repository::findIdBySecureId,
                projection -> repository.findById(projection.getId()),
                "Product not found"
        );
    }


}
