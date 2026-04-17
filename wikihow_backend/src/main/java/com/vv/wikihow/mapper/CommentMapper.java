package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
