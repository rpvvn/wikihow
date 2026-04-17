package com.vv.wikihow.dto;

import com.vv.wikihow.entity.Category;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 分类视图对象（树形结构）
 */
@Data
public class CategoryVO {

    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private Long parentId;
    private Integer sortOrder;
    private Integer articleCount = 0;
    private List<CategoryVO> children = new ArrayList<>();

    public static CategoryVO fromEntity(Category category) {
        CategoryVO vo = new CategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setDescription(category.getDescription());
        vo.setCoverImage(category.getCoverImage());
        vo.setParentId(category.getParentId());
        vo.setSortOrder(category.getSortOrder());
        return vo;
    }
}
