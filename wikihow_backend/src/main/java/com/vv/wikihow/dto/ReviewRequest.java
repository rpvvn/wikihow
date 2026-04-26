package com.vv.wikihow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 复核申请请求 DTO
 */
@Data
public class ReviewRequest {
    
    /**
     * 申请原因
     */
    @NotBlank(message = "申请原因不能为空")
    @Size(min = 10, message = "申请原因至少10个字")
    @Size(max = 500, message = "申请原因不能超过500字")
    private String reason;
}
