package com.haier.fintech.modules.report.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.haier.fintech.common.utils.JsonUtil;
import com.haier.fintech.datasources.DynamicDataSource;
import com.haier.fintech.modules.report.api.dao.ReportApiDao;
import com.haier.fintech.modules.report.api.service.ReportApiService;
@Service("reportMapAdapters")
public class ReportMapAdaptersImple implements ReportApiService {
	private static final Logger log = LoggerFactory.getLogger(ReportMapAdaptersImple.class);

	@Autowired 
	private ReportApiDao reportApiDao;
	
	@Override
	public Map<String, Object> formatResult(Object obj, String column, String rows) {
		List<HashMap<String, Object>> list = (List<HashMap<String, Object>>)obj;
		DynamicDataSource.DbContextHolder.setDataSource("first");
		List<HashMap<String, Object>> listMySqlData = reportApiDao.getMapData();//地图省份经纬
		Map<String,Object> map = handelMapData(listMySqlData,list);
		return map;
	}
	//地图数据处理
	private Map<String, Object> handelMapData(List<HashMap<String, Object>> listMySqlData,List<HashMap<String, Object>> listOracleSqlData) {
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> listWarnMap = new ArrayList<Map<String,Object>>();
		for (HashMap<String, Object> oracleData : listOracleSqlData) {
			Map<String, Object> map = new HashMap<String, Object>();
			boolean flag = false;
			Object obj = oracleData.get("RESULTS");
			String city = (String)oracleData.get("AREANAME");			
			map.put("areaName", city);
			for (HashMap<String, Object> mysqlData : listMySqlData) {
					String areaName =(String)mysqlData.get("area_name");
					String lon = (String) mysqlData.get("lon");//经度
					String lan = (String) mysqlData.get("lat");//维度
					List list = new ArrayList();
					if(city.equals(areaName)) {
						flag = true;
						list.add(lon);
						list.add(lan);
						list.add(obj);
						map.put("value", list);
						break;
					}
			}
			if(flag) {
				listMap.add(map);
			}else {
				listWarnMap.add(map);
			}
		}
		if(!(CollectionUtils.isEmpty(listWarnMap))) {
			log.info("经纬度未匹配上的地区信息===============》" +JsonUtil.getJsonByObj(listWarnMap));
		}
		result.put("datas", listMap);
		return result;
	}

}
