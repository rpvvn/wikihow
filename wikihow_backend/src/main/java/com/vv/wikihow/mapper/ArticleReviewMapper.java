package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.ArticleReview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章审核记录 Mapper 接口
 */
@Mapper
public interface ArticleReviewMapper extends BaseMapper<ArticleReview> {
}
