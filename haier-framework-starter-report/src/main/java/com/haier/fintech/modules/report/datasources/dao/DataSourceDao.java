package com.haier.fintech.modules.report.datasources.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.haier.fintech.modules.report.datasources.entity.DataSourceEntity;
@Mapper
public interface DataSourceDao extends BaseMapper<DataSourceEntity> {

}
