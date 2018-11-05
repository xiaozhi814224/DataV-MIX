package com.haier.fintech.modules.report.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haier.fintech.common.utils.SpringContextUtils;
import com.haier.fintech.datasources.DynamicDataSource;
import com.haier.fintech.modules.report.api.dao.ReportApiDao;
import com.haier.fintech.modules.report.api.service.ReportApiService;
import com.haier.fintech.modules.report.api.service.ReportService;
import com.haier.fintech.modules.report.chart.entity.ChartEntity;
import com.haier.fintech.modules.report.chart.service.ChartService;
@Service("reportService")
public class ReportServiceImple implements ReportService{
	@Autowired
	private ChartService chartService;
	@Autowired 
	private ReportApiDao reportApiDao;

	@Override
	public Map<String, Object> getResult(String reportNum, HashMap<String, String> param) {
		DynamicDataSource.DbContextHolder.setDataSource("first");
		ChartEntity chartEntity = chartService.getDataByreportNum(reportNum);
		Long chartId = chartEntity.getId();//chartId
		String serviceName = chartEntity.getServiceName();//serviceName
		Map<String,Object> map = reportApiDao.getDataById(String.valueOf(chartId));
		String sql = (String)map.get("sql");
		String column = (String)map.get("colum");
		String row = (String)map.get("row");
		Long dataSourceId = (Long)map.get("data_source_id");
		for (Entry<String, String> entry : param.entrySet()) {//替换sql中的条件
		     String key = entry.getKey();
		     sql = sql.replace("#{" + key + "}" , entry.getValue());
		}		
		DynamicDataSource.DbContextHolder.setDataSource("datav" + dataSourceId);
		List<HashMap<String, Object>> list = reportApiDao.getData(sql);	
		
		ReportApiService  reportApiService = SpringContextUtils.getBean(serviceName,ReportApiService.class);
		Map<String,Object> resultMap = reportApiService.formatResult(list,column,row);
		DynamicDataSource.DbContextHolder.setDataSource("first");
		return resultMap;
	}
	
}
