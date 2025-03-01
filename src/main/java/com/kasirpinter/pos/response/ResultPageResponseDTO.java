package com.kasirpinter.pos.response;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class ResultPageResponseDTO<T> implements Serializable {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 8968668353875569332L;

    private List<T> result;
    private Integer currentPage;
    private Integer prevPage;
    private Integer nextPage;
    private Integer firstPage;
    private Integer lastPage;
    private Integer perPage;
    private Long totalItems;


}
