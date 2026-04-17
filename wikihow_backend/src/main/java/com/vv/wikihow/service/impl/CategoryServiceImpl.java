package com.vv.wikihow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vv.wikihow.common.ResultCode;
import com.vv.wikihow.dto.CategoryVO;
import com.vv.wikihow.entity.Article;
import com.vv.wikihow.entity.Category;
import com.vv.wikihow.exception.BusinessException;
import com.vv.wikihow.mapper.ArticleMapper;
import com.vv.wikihow.mapper.CategoryMapper;
import com.vv.wikihow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ArticleMapper articleMapper;

    @Override
    public List<Category> getCategoryTree() {
        // 获取所有分类，按排序字段排序
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSortOrder));
    }

    @Override
    public List<CategoryVO> getCategoriesWithArticleCount() {
        // 获取所有分类
        List<Category> allCategories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSortOrder));
        
        // 获取所有已发布文章的分类ID和数量
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getStatus, 1)
                        .select(Article::getCategoryId));
        
        // 统计每个分类的直接文章数量
        Map<Long, Long> directArticleCount = articles.stream()
                .collect(Collectors.groupingBy(Article::getCategoryId, Collectors.counting()));
        
        // 构建分类ID到子分类ID列表的映射
        Map<Long, List<Long>> parentToChildrenMap = allCategories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(
                        Category::getParentId,
                        Collectors.mapping(Category::getId, Collectors.toList())));
        
        // 转换为 CategoryVO 并计算文章数量
        List<CategoryVO> result = new ArrayList<>();
        for (Category category : allCategories) {
            CategoryVO vo = CategoryVO.fromEntity(category);
            
            // 计算文章数量：直接文章数 + 子分类文章数
            long count = directArticleCount.getOrDefault(category.getId(), 0L);
            
            // 如果是一级分类，加上所有子分类的文章数
            if (category.getParentId() == null) {
                List<Long> childIds = parentToChildrenMap.getOrDefault(category.getId(), new ArrayList<>());
                for (Long childId : childIds) {
                    count += directArticleCount.getOrDefault(childId, 0L);
                }
            }
            
            vo.setArticleCount((int) count);
            result.add(vo);
        }
        
        return result;
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分类不存在");
        }
        return category;
    }

    @Override
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public Category updateCategory(Long id, Category updateCategory) {
        Category category = getCategoryById(id);
        
        if (updateCategory.getName() != null) {
            category.setName(updateCategory.getName());
        }
        if (updateCategory.getParentId() != null) {
            category.setParentId(updateCategory.getParentId());
        }
        if (updateCategory.getSortOrder() != null) {
            category.setSortOrder(updateCategory.getSortOrder());
        }
        // 更新描述（允许设置为空）
        category.setDescription(updateCategory.getDescription());
        // 更新封面图（允许设置为空）
        category.setCoverImage(updateCategory.getCoverImage());

        categoryMapper.updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查是否有子分类
        Long childCount = categoryMapper.selectCount(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("该分类下有子分类，无法删除");
        }
        
        categoryMapper.deleteById(id);
    }
}
