package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
