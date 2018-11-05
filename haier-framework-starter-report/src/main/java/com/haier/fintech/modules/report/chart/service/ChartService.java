package com.haier.fintech.modules.report.chart.service;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.modules.report.chart.entity.ChartEntity;

public interface ChartService extends IService<ChartEntity> {
	PageUtils queryPage(HashMap<String,Object> params);
	//新增图表
	void save(ChartEntity chartEntity,Long userId);
	//更新图表
	void updateDataSet(ChartEntity chartEntity, Long userId);
	//查询图表信息
	ChartEntity getInfoById(Long id);
	//删除图表
	void deleteByIds(Long[] ids);
	//首页-根据reportId获取数据
	Map<String, Object> getDataById(String reportId);
	
	Boolean checkReportNumUnique(Map<String, Object> map);//校验图表编号是否唯一
	
	ChartEntity getDataByreportNum(String reportNum);//根据reportNum查询
}
