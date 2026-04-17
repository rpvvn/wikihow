package com.vv.wikihow.dto;

import lombok.Data;

/**
 * 审核请求 DTO
 */
@Data
public class ReviewRequest {
    
    /**
     * 审核意见（拒绝时必填）
     */
    private String comment;
}
