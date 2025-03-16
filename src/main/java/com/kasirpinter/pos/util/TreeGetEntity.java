package com.kasirpinter.pos.util;

import java.util.Optional;
import java.util.function.Function;

import com.kasirpinter.pos.entity.Company;
import com.kasirpinter.pos.entity.CompanyCategory;
import com.kasirpinter.pos.entity.Member;
import com.kasirpinter.pos.entity.MsJobPosition;
import com.kasirpinter.pos.entity.MsShift;
import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.ProductCategory;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Roles;
import com.kasirpinter.pos.entity.Transaction;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.repository.CompanyCategoryRepository;
import com.kasirpinter.pos.repository.CompanyRepository;
import com.kasirpinter.pos.repository.MemberRepository;
import com.kasirpinter.pos.repository.MsJobPositionRepository;
import com.kasirpinter.pos.repository.ProductCategoryRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.RlUserShiftRepository;
import com.kasirpinter.pos.repository.RoleRepository;
import com.kasirpinter.pos.repository.TransactionRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.repository.UserShiftRepository;

import jakarta.persistence.EntityNotFoundException;

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
                                "User not found");
        }

        public static Roles parsingRoleByProjection(String secureId, RoleRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Role not found");
        }

        public static Company parsingCompanyByProjection(String secureId, CompanyRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Company not found");
        }

        public static Product parsingProductByProjection(String secureId, ProductRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Product not found");
        }

        public static ProductCategory parsingProductCategoryByProjection(String secureId,
                        ProductCategoryRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Product Category not found");
        }

        public static Transaction parsingTransactionByProjection(String secureId, TransactionRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Product not found");
        }

        public static Member parsingMemberByProjection(String secureId, MemberRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Product not found");
        }

        public static MsShift parsingUserShiftByProjection(String secureId, UserShiftRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Shift not found");
        }

        public static RlUserShift parsingRlUserShiftByProjection(String secureId, RlUserShiftRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Shift not found");
        }

        public static CompanyCategory parsingCompanyCategoryByProjection(String secureId,
                        CompanyCategoryRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Shift not found");
        }

        public static MsJobPosition parseMsJobPositionByProjection(String secureId,
                                                                   MsJobPositionRepository repository) {
                return getIdBySecureId(
                                secureId,
                                repository::findIdBySecureId,
                                projection -> repository.findById(projection.getId()),
                                "Job Position not found");
        }

}
