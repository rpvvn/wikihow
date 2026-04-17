package com.vv.wikihow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

/**
 * 文章创建/更新请求
 */
@Data
public class ArticleRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;

    private String coverImage;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private List<String> tags;

    private String difficulty = "MEDIUM";

    private List<StepRequest> steps;

    /**
     * 参考文献列表
     */
    private List<ReferenceRequest> references;

    @Data
    public static class StepRequest {
        private Integer order;
        private String title;
        private String content;
        private String image;
    }

    @Data
    public static class ReferenceRequest {
        private String title;
        private String url;
        private String author;
        private String publishDate;
    }
}
