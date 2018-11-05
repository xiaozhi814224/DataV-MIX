package com.haier.fintech.modules.report.datasources.service;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.modules.report.datasources.entity.DataSourceEntity;

public interface DataSourceService extends IService<DataSourceEntity> {
	PageUtils queryPage(HashMap<String,Object> params);
	//新增数据源
	void save(DataSourceEntity dataSourceEntity,Long userId);
	//更新数据源
	void updateDataSource(DataSourceEntity dataSourceEntity, Long userId);
	//查询数据源信息
	DataSourceEntity getInfoById(Long id);
	//删除数据源
	void deleteByIds(Long[] ids);
	//查询数据源列表
	List<DataSourceEntity> getDataSourceList();
	
	void loadDynamicDataSource();//动态加载数据源
	//查看数据源能否连接成功
	Connection checkConnect(DataSourceEntity dataSourceEntity);
	//查看mongo连接是否成功
//	MongoDatabase checkMongoDbConnect(DataSourceEntity dataSourceEntity);
}
