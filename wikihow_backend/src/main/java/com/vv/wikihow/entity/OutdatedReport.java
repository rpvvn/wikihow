package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 过时内容举报实体
 */
@Data
@TableName("outdated_report")
public class OutdatedReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 举报用户ID
     */
    private Long userId;

    /**
     * 举报原因
     */
    private String reason;

    /**
     * 处理状态: 0待处理/1已处理/2已忽略
     */
    private Integer status;

    /**
     * 处理人ID
     */
    private Long handlerId;

    /**
     * 处理备注
     */
    private String handleComment;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // 状态常量
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_HANDLED = 1;
    public static final int STATUS_IGNORED = 2;
}
