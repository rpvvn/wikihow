package com.vv.wikihow.service;

import com.vv.wikihow.dto.CategoryVO;
import com.vv.wikihow.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /**
     * 获取所有分类（树形结构）
     */
    List<Category> getCategoryTree();

    /**
     * 获取所有分类（平铺）
     */
    List<Category> getAllCategories();

    /**
     * 获取所有分类（带文章数量）
     */
    List<CategoryVO> getCategoriesWithArticleCount();

    /**
     * 根据ID获取分类
     */
    Category getCategoryById(Long id);

    /**
     * 创建分类
     */
    Category createCategory(Category category);

    /**
     * 更新分类
     */
    Category updateCategory(Long id, Category category);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);
}
