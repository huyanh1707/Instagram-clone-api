package com.ju17th.instagramcloneapi.payload.post;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

}
