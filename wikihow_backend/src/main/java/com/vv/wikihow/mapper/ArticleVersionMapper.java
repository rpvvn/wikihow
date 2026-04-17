package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.ArticleVersion;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 文章版本Mapper接口
 */
@Mapper
public interface ArticleVersionMapper extends BaseMapper<ArticleVersion> {

    /**
     * 获取文章的最大版本号
     */
    @Select("SELECT COALESCE(MAX(version_number), 0) FROM article_version WHERE article_id = #{articleId}")
    Integer getMaxVersionNumber(@Param("articleId") Long articleId);
}
