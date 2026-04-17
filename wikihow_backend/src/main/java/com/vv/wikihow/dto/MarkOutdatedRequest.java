package com.vv.wikihow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 标记过时请求 DTO
 */
@Data
public class MarkOutdatedRequest {
    
    /**
     * 过时原因
     */
    @NotBlank(message = "过时原因不能为空")
    @Size(max = 500, message = "过时原因不能超过500字")
    private String reason;
}
