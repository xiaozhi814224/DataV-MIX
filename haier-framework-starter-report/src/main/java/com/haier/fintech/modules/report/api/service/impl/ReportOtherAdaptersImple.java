package com.haier.fintech.modules.report.api.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.haier.fintech.modules.report.api.service.ReportApiService;

@Service("reportOtherAdapters")
public class ReportOtherAdaptersImple implements ReportApiService{

	@Override
	public Map<String, Object> formatResult(Object obj, String column, String rows) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("datas", obj);
		return map;
	}

}
