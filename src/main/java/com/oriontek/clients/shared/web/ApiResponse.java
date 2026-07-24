package com.oriontek.clients.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.oriontek.clients.shared.pagination.PageResponse;
import com.oriontek.clients.shared.pagination.PaginationMeta;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean successful, T data, PaginationMeta pagination, ApiError error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<List<T>> paged(PageResponse<T> page) {
        return new ApiResponse<>(true, page.content(), PaginationMeta.from(page), null);
    }

    public static <T> ApiResponse<T> failure(ApiError error) {
        return new ApiResponse<>(false, null, null, error);
    }
}
