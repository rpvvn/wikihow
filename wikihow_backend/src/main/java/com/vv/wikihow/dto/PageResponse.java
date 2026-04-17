package com.vv.wikihow.dto;

import lombok.Data;
import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageResponse<T> {

    private List<T> list;
    private Long total;
    private Integer page;
    private Integer size;

    public static <T> PageResponse<T> of(List<T> list, Long total, Integer page, Integer size) {
        PageResponse<T> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(total);
        response.setPage(page);
        response.setSize(size);
        return response;
    }
}
