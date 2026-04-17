package com.vv.wikihow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 过时内容举报请求 DTO
 */
@Data
public class OutdatedReportRequest {
    
    /**
     * 举报原因
     */
    @NotBlank(message = "举报原因不能为空")
    @Size(max = 500, message = "举报原因不能超过500字")
    private String reason;
}
