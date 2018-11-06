package com.haier.fintech.modules.report.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
		Map areaInfo = (Map)list.get(0);
		String _province = (String)areaInfo.get("PROVINCE");
		String _city = (String)areaInfo.get("CITY");
		String _district = (String)areaInfo.get("DISTRICT");
		if(StringUtils.isBlank(_city) && StringUtils.isBlank(_district)) {//全国
			_province = "";
			_city = "";
		}else if(StringUtils.isBlank(_district) && StringUtils.isNotBlank(_city)) {//全省
			_city = "";
		}
		String sql = "SELECT " + 
				"	* " + 
				"FROM " + 
				"	tb_area " + 
				"WHERE " + 
				"	CASE " + 
				"WHEN _province = '' THEN " + 
				"	`level` = 1 " + 
				"WHEN _province != '' " + 
				"AND _city = '' THEN " + 
				"	`pid` = (" + 
				"		SELECT " + 
				"			id " + 
				"		FROM " + 
				"			tb_area " + 
				"		WHERE " + 
				"			area_name = _province  and `level` = 1 " + 
				"	) " + 
				"WHEN _province != '' " + 
				"AND _city != '' THEN " + 
				"	`pid` = ( " + 
				"		SELECT " + 
				"			id " + 
				"		FROM " + 
				"			tb_area " + 
				"		WHERE " + 
				"			area_name = _city and `level` = 2 " + 
				"	)" + 
				"END";
		sql = sql.replace("_province", "'"+_province + "'").replaceAll("_city", "'" +_city +"'");
		List<HashMap<String, Object>> listMySqlData = reportApiDao.getMapData(sql);//地图省份经纬
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
			String province = (String)oracleData.get("PROVINCE");
			String city = (String)oracleData.get("CITY");
			String distinct = (String)oracleData.get("DISTRICT");			
			for (HashMap<String, Object> mysqlData : listMySqlData) {
					String areaName =(String)mysqlData.get("area_name");
					String lon = (String) mysqlData.get("lon");//经度
					String lan = (String) mysqlData.get("lat");//维度
					List list = new ArrayList();
					if(StringUtils.isBlank(city) && StringUtils.isBlank(distinct)) {
						if(province.equals(areaName)) {//全国
							flag = true;
							list.add(lon);
							list.add(lan);
							list.add(obj);
							map.put("areaName", areaName);
							map.put("value", list);
							break;
						}
					}else if(StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city) && StringUtils.isBlank(distinct)) {//省下的市
						if(city.equals(areaName)) {
							flag = true;
							list.add(lon);
							list.add(lan);
							list.add(obj);
							map.put("areaName", areaName);
							map.put("value", list);
							break;
						}
					}else {//市下的区
						if(distinct.equals(areaName)) {
							flag = true;
							list.add(lon);
							list.add(lan);
							list.add(obj);
							map.put("areaName", areaName);
							map.put("value", list);
							break;
						}
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
