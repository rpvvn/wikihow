package com.vv.wikihow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vv.wikihow.entity.OutdatedReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * 过时内容举报 Mapper 接口
 */
@Mapper
public interface OutdatedReportMapper extends BaseMapper<OutdatedReport> {
}
