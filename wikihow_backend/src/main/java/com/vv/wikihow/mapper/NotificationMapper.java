package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
