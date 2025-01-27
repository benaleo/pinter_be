package com.kopibery.pos.model.search;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ListOfFilterPagination {
    private String keyword;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reportStatus;
    private String promotionStatus;
    private String roleId;
    private Boolean status;
    private String stringStatus;
    private LocalDate postAt;
    private LocalDate date;
    private Integer statusEnum;

    public ListOfFilterPagination(String keyword) {
        this.keyword = keyword;
    }

    public ListOfFilterPagination(String keyword, Integer statusEnum) {
        this.keyword = keyword;
        this.statusEnum = statusEnum;
    }

    public ListOfFilterPagination(String keyword, LocalDate startDate, LocalDate endDate) {
        this.keyword = keyword;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public ListOfFilterPagination(String keyword, String roleId, Boolean status) {
        this.keyword = keyword;
        this.roleId = roleId;
        this.status = status;
    }

    public ListOfFilterPagination(String keyword, String stringStatus, LocalDate postAt) {
        this.keyword = keyword;
        this.stringStatus = stringStatus;
        this.postAt = postAt;
    }

    public ListOfFilterPagination(String keyword, LocalDate startDate, LocalDate endDate, Boolean status) {
        this.keyword = keyword;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public ListOfFilterPagination(String keyword, LocalDate startDate, LocalDate endDate, String reportStatus) {
        this.keyword = keyword;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportStatus = reportStatus;
    }

    public ListOfFilterPagination(String keyword, LocalDate date, Boolean status, String stringStatus) {
        this.keyword = keyword;
        this.date = date;
        this.status = status;
        this.stringStatus = stringStatus;
    }
}
