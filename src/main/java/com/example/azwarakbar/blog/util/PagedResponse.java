package com.example.azwarakbar.blog.util;

import com.example.azwarakbar.blog.model.Post;
import lombok.Data;

import java.util.List;

@Data
public class PagedResponse<T> {

    private List<T> data;
    private int page;
    private int total;

    public PagedResponse(List<T> objectList, int page, int totalPages) {
        this.data = objectList;
        this.page = page;
        this.total = totalPages;
    }
}
