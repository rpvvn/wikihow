package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章步骤实体
 */
@Data
@TableName("article_step")
public class ArticleStep {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Integer stepOrder;

    private String title;

    private String content;

    private String image;

    private LocalDateTime createdAt;
}
