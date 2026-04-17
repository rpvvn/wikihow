package com.vv.wikihow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文章版本实体
 * 用于存储文章的历史版本信息
 */
@Data
@TableName("article_version")
public class ArticleVersion {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 版本号
     */
    private Integer versionNumber;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 内容快照（JSON格式存储步骤）
     */
    private String contentSnapshot;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 标签
     */
    private String tags;

    /**
     * 难度
     */
    private String difficulty;

    /**
     * 参考文献（JSON格式）
     */
    @TableField("`references`")
    private String references;

    /**
     * 修改人ID
     */
    private Long userId;

    /**
     * 修改说明
     */
    private String changeDescription;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
