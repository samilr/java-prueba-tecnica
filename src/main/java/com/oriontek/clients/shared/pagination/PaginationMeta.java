package com.oriontek.clients.shared.pagination;

public record PaginationMeta(
        int page, int size, long totalElements, int totalPages, boolean first, boolean last) {

    public static PaginationMeta from(PageResponse<?> page) {
        return new PaginationMeta(
                page.page(),
                page.size(),
                page.totalElements(),
                page.totalPages(),
                page.first(),
                page.last());
    }
}
