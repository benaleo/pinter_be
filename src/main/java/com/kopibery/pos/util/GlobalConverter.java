package com.kopibery.pos.util;

import com.kopibery.pos.entity.AbstractEntity;
import com.kopibery.pos.model.AdminModelBaseDTOResponse;
import com.kopibery.pos.model.search.ListOfFilterPagination;
import com.kopibery.pos.model.search.SavedKeywordAndPageable;
import com.kopibery.pos.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class GlobalConverter {

    public static <D extends AbstractEntity> void CmsAdminCreateAtBy(
            D data, Long adminId
    ) {
        data.setCreatedAt(LocalDateTime.now());
        data.setUpdatedAt(LocalDateTime.now());
        data.setCreatedBy(adminId);
    }

    public static <D extends AbstractEntity> void CmsAdminUpdateAtBy(
            D data, Long adminId
    ) {
        data.setUpdatedAt(LocalDateTime.now());
        data.setUpdatedBy(adminId);
    }


    public static <T extends AdminModelBaseDTOResponse, D extends AbstractEntity> void CmsIDTimeStampResponseAndId(
            T dto, D data, UserRepository userRepository
    ) {
        dto.setId(data.getSecureId());
        dto.setCreatedAt(data.getCreatedAt() != null ? Formatter.formatLocalDateTime(data.getCreatedAt()) : null);
        dto.setUpdatedAt(data.getUpdatedAt() != null ? Formatter.formatLocalDateTime(data.getUpdatedAt()) : null);
        dto.setCreatedBy(data.getCreatedBy() != null ? userRepository.findById(data.getCreatedBy()).orElseThrow().getName() : null);
        dto.setUpdatedBy(data.getUpdatedBy() != null ? userRepository.findById(data.getUpdatedBy()).orElseThrow().getName() : null);
    }

    public static <T extends AdminModelBaseDTOResponse> void CmsIDTimeStampResponseAndIdProjection(
            T dto, String dataId, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy
    ) {
        dto.setId(dataId);
        dto.setCreatedAt(createdAt != null ? Formatter.formatLocalDateTime(createdAt) : null);
        dto.setUpdatedAt(updatedAt != null ? Formatter.formatLocalDateTime(updatedAt) : null);
        dto.setCreatedBy(createdBy);
        dto.setUpdatedBy(updatedBy);
    }



    public static String getParseImage(
            String imageUrl,
            String baseUrl
    ) {
        return Objects.isNull(imageUrl) || imageUrl.isBlank() ? null :
                imageUrl.startsWith("uploads/") ?
                        baseUrl + "/" + imageUrl :
                        imageUrl.startsWith("/uploads/") ? baseUrl + imageUrl : imageUrl;
    }

    public static String getAvatarImage(
            String imageUrl,
            String baseUrl
    ) {
        return Objects.isNull(imageUrl) || imageUrl.isBlank() ? baseUrl + "/uploads/default/avatar.png" :
                imageUrl.startsWith("uploads/") ?
                        baseUrl + "/" + imageUrl :
                        imageUrl.startsWith("/uploads/") ? baseUrl + imageUrl : imageUrl;
    }

    public static String getAvatarImageGroup(
            String imageUrl,
            String baseUrl
    ) {
        return Objects.isNull(imageUrl) || imageUrl.isBlank() ? baseUrl + "/uploads/default/avatar_group.png" :
                imageUrl.startsWith("uploads/") ?
                        baseUrl + "/" + imageUrl :
                        imageUrl.startsWith("/uploads/") ? baseUrl + imageUrl : imageUrl;
    }


    public static String replaceImagePath(String imagePath) {
        return imagePath.replace("src/main/resources/static/", "/");
    }

    // Helper highlight list to array
    public static List<String> convertListToArray(String lists) {
        List<String> data = new ArrayList<>();
        String[] listArray = lists != null ? lists.split(",") : new String[0];
        for (String list : listArray) {
            data.add(list.trim());
        }
        return data;
    }

    // Helper for parsing tag
    public static String convertTagString(String tagList) {
        String tagName = tagList.replaceAll("[^a-zA-Z0-9\\s]", "") // Remove special characters
                .trim()
                .replaceAll("\\s+", " "); // Normalize spaces

        // Capitalize the first letter of each word and remove spaces
        return Arrays.stream(tagName.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining());
    }

    public static String convertTotalLikesToString(Long totalLikes) {
        if (totalLikes >= 10000) {
            double value = totalLikes / 1000.0;
            return String.format("%.1fK", value);
        }
        return String.valueOf(totalLikes);
    }

    public static String getMentionId(String text) {
        String mentionId;
        if (text == null || !text.contains("@[__") || !text.contains("__]")) {
            return null;
        } else {
            String[] split = text.split("@\\[__");
            String[] split2 = split[1].split("__]");

            mentionId = split2[0];
            return mentionId;
        }
    }


    // Helper pagination
    public static SavedKeywordAndPageable appsCreatePageable(
            Integer pages,
            Integer limit,
            String sortBy,
            String direction,
            String keyword,
            ListOfFilterPagination discardList
    ) {

        // Add wildcard to keyword for SQL LIKE queries
        keyword = StringUtils.isEmpty(keyword) ? "%" : "%" + keyword + "%";

        // Create pageable with sort direction and sorting field
        Sort sort = Sort.by(new Sort.Order(PaginationUtil.getSortBy(direction), sortBy));
        Pageable pageable = PageRequest.of(pages, limit, sort);

        return new SavedKeywordAndPageable(keyword, pageable);
    }

    // Helper pagination
    public static SavedKeywordAndPageable createPageable(
            Integer pages,
            Integer limit,
            String sortBy,
            String direction,
            String keyword
    ) {

        keyword = StringUtils.isEmpty(keyword) ? "%" : "%" + keyword + "%";
        Sort sort = Sort.by(new Sort.Order(PaginationUtil.getSortBy(direction), sortBy));
        Pageable pageable = PageRequest.of(pages, limit, sort);
        return new SavedKeywordAndPageable(keyword, pageable);
    }

    public static Pageable oldSetPageable(Integer pages, Integer limit, String sortBy, String direction, Page<?> firstResult, Long totalData) {
        long totalRecords = firstResult != null ? firstResult.getTotalElements() : totalData;
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        if (pages >= totalPages) {
            pages = 0;
        }
        return PageRequest.of(pages, limit, Sort.by(new Sort.Order(PaginationUtil.getSortBy(direction), sortBy)));
    }


    public static SavedKeywordAndPageable eZcreatePageable(
            Integer pages,
            Integer limit,
            String sortBy,
            String direction,
            String keyword,
            Page<?> firstResult,
            Long totalData) {

        long totalRecords = firstResult != null ? firstResult.getTotalElements() : totalData;
        log.info("Total records: " + totalRecords);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        log.info("Total pages: " + totalPages);
        if (pages >= totalPages) {
            pages = 0;
            log.info("Reset pages to 0");
        }

        keyword = StringUtils.isEmpty(keyword) ? "%" : "%" + keyword + "%";
        Sort sort = Sort.by(new Sort.Order(PaginationUtil.getSortBy(direction), sortBy));
        Pageable pageable = PageRequest.of(pages, limit, sort);
        return new SavedKeywordAndPageable(keyword, pageable);
    }

}
