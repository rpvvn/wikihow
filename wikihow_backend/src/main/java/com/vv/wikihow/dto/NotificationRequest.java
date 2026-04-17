package com.vv.wikihow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送通知请求DTO
 */
@Data
public class NotificationRequest {
    
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    @NotBlank(message = "通知内容不能为空")
    private String content;
}
