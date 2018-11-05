package com.haier.fintech.modules.report.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.haier.fintech.modules.report.api.service.ReportApiService;

@Service("reportPieApiService")
public class ReportPieAdaptersImple implements ReportApiService {
	@Override
	public Map<String, Object> formatResult(Object obj, String column, String rows) {
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) obj;
		List<Object> colList = new ArrayList<Object>();
		HashMap<String, Object> resMap = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		if (list != null && list.size() > 0) {
			/*
			 * colArr.add(column); HashMap<String, Object> map = list.get(0);
			 * HashMap<String, Object> xMap = new HashMap<String, Object>();
			 * xMap.put("name", column); xMap.put("value", xMap.get(column));
			 * dataList.add(xMap);
			 */
			
			for(HashMap<String, Object> tMap :list) {
				HashMap<String, Object> yMap = new HashMap<String, Object>();
					yMap.put("name", tMap.get(column));
					yMap.put("value", tMap.get(rows));
					colList.add(tMap.get(column));
					dataList.add(yMap);
				
			}
		}
		resMap.put("legend", colList);
		resMap.put("datas", dataList);
		return resMap;
	}
}
