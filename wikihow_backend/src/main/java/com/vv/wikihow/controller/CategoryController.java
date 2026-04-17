package com.vv.wikihow.controller;

import com.vv.wikihow.common.Result;
import com.vv.wikihow.dto.CategoryVO;
import com.vv.wikihow.entity.Category;
import com.vv.wikihow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类列表（树形）
     */
    @GetMapping
    public Result<List<Category>> getCategories() {
        List<Category> categories = categoryService.getCategoryTree();
        return Result.success(categories);
    }

    /**
     * 获取分类列表（带文章数量）
     */
    @GetMapping("/with-count")
    public Result<List<CategoryVO>> getCategoriesWithCount() {
        List<CategoryVO> categories = categoryService.getCategoriesWithArticleCount();
        return Result.success(categories);
    }

    /**
     * 获取单个分类
     */
    @GetMapping("/{id}")
    public Result<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 创建分类（管理员）
     */
    @PostMapping
    public Result<Category> createCategory(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return Result.success("创建成功", created);
    }

    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
