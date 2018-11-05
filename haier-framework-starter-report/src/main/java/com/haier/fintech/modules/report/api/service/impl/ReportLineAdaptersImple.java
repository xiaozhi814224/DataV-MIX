package com.haier.fintech.modules.report.api.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.haier.fintech.modules.report.api.service.ReportApiService;

@Service("reportLineApiService")
public class ReportLineAdaptersImple implements ReportApiService {// 折线或柱状数据封装
	// 折线实现类
	@Override
	public Map<String, Object> formatResult(Object obj, String column, String rows) {
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) obj;
		ArrayList<String> colArr = new ArrayList<String>();
		ArrayList<ArrayList<Object>> dataList = new ArrayList<ArrayList<Object>>();
		ArrayList<Object> xData = new ArrayList<Object>();
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		String[] yColumn = rows.split(",");
		
		HashMap<String, ArrayList<Object>> yMap = new HashMap<String, ArrayList<Object>>();
		for (String yc : yColumn) {
			colArr.add(yc);
			ArrayList<Object> yList = new ArrayList<Object>();
			yMap.put(yc, yList);
		}
		for (HashMap<String, Object> tempMap : list) {
			ArrayList<Object> yTemp = new ArrayList<Object>();
			tempMap.get(column);
			xData.add(tempMap.get(column));
			for (String yc : yColumn) {
				yMap.get(yc).add(tempMap.get(yc));
			}
		}
		dataList.add(xData);
		for (String yc : yColumn) {
			dataList.add(yMap.get(yc));
		}
		resMap.put("legend", colArr);
		resMap.put("datas", dataList);

		return resMap;
	}
}
