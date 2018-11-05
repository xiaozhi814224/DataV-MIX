package com.haier.fintech.modules.report.datasets.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.modules.report.datasets.entity.DataSetEntity;

public interface DataSetService extends IService<DataSetEntity> {
	PageUtils queryPage(HashMap<String,Object> params);
	//新增数据集
	void save(DataSetEntity dataSetEntity,Long userId);
	//更新数据集
	void updateDataSet(DataSetEntity dataSetEntity, Long userId);
	//查询数据集信息
	DataSetEntity getInfoById(Long id);
	//删除数据集
	void deleteByIds(Long[] ids);
	//获取数据集列表
	List<DataSetEntity> getDataSetList();	
	//读取数据
	//List<HashMap<String, Object>> readData(Long dataSetId);
	//获取数据集的列
	List<Map<String, Object>> getListColums(Long dataSetId);
	// 数据集获取数据结果
	List<HashMap<String, Object>> getDatas(HashMap<String, Object> params);
	
	
	List<HashMap<String, Object>> getChartData(Map<String, Object> map);
}
