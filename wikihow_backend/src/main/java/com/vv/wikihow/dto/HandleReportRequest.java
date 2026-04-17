package com.vv.wikihow.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 处理举报请求 DTO
 */
@Data
public class HandleReportRequest {
    
    /**
     * 处理结果: 1已处理（标记过时）/2已忽略
     */
    @NotNull(message = "处理结果不能为空")
    private Integer status;
    
    /**
     * 处理备注
     */
    @Size(max = 500, message = "处理备注不能超过500字")
    private String comment;
    
    /**
     * 过时原因（当status=1时需要）
     */
    @Size(max = 500, message = "过时原因不能超过500字")
    private String outdatedReason;
}
