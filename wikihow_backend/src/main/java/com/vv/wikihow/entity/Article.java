package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章实体
 */
@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    private String coverImage;

    private Long userId;

    private Long categoryId;

    private String tags;

    private String difficulty;

    private Integer viewCount;

    private Integer likeCount;

    private Integer favoriteCount;

    private Integer commentCount;

    private Integer status;

    /**
     * 是否过时: 0否/1是
     */
    private Integer isOutdated;

    /**
     * 过时原因
     */
    private String outdatedReason;

    /**
     * 参考文献（JSON格式存储）
     */
    @TableField("`references`")
    private String references;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
