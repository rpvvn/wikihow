package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
