package com.haier.fintech.modules.report.api.service;

import java.util.Map;
public interface ReportApiService {
	
	/**
	 * 格式化结果集
	 * @param obj 结果集
	 * @param column x
	 * @param rows y
	 * @return
	 */
	Map<String,Object> formatResult(Object obj,String column,String rows);
}
