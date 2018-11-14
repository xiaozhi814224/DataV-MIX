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
import com.haier.fintech.modules.report.utils.BaiduMapUtils;

@Service("reportBMapAdapters")
public class ReportBMapAdaptersImple implements ReportApiService{
	private static final Logger log = LoggerFactory.getLogger(ReportBMapAdaptersImple.class);
	
	@Autowired 
	private ReportApiDao reportApiDao;
	
	@Autowired
	private BaiduMapUtils baiduMapUtils;
	
	@Override
	public Map<String, Object> formatResult(Object obj, String column, String rows) {
		List<HashMap<String, Object>> listOracle = (List<HashMap<String, Object>>)obj;
		DynamicDataSource.DbContextHolder.setDataSource("first");
		Map areaInfo = (Map)listOracle.get(0);
		String _province = (String)areaInfo.get("PROVINCE");
		String _city = (String)areaInfo.get("CITY");
		String _district = (String)areaInfo.get("DISTRICT");	
		if(StringUtils.isBlank(_city) && StringUtils.isBlank(_district)) {//全国
			_province = "";
			_city = "";
		}else if(StringUtils.isBlank(_district) && StringUtils.isNotBlank(_city)) {//全省
			_city = "";
		}
		String sql = getSql(_province,_city);		
		List<HashMap<String, Object>> listMySqlData = reportApiDao.getMapData(sql);//获取mysql表地图省、市经纬度
		Map<String,Object> map = handelBMapData(listMySqlData,listOracle);//地图数据处理
		return map;
	}
	//mysql 地区经纬度 查询sql
	public String getSql(String _province,String _city) {
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
		return sql;
	}
	//地图数据处理
	private Map<String, Object> handelBMapData(List<HashMap<String, Object>> listMySqlData,List<HashMap<String, Object>> listOracleSqlData) {
		Map<String,Object> result = new HashMap<String,Object>();
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();//返回前台数据
		List<Map<String,Object>> listWarnMap = new ArrayList<Map<String,Object>>();//未找到匹配经纬度的地区名称
		for (HashMap<String, Object> oracleData : listOracleSqlData) {
			Map<String, Object> map = new HashMap<String, Object>();
			boolean flag = false;
			Object obj = oracleData.get("RESULTS");//触点数
			String province = (String)oracleData.get("PROVINCE");//省
			String city = (String)oracleData.get("CITY");//市
			String addr = (String)oracleData.get("ADDR");//区(小区)
			String lng = (String) oracleData.get("LNG");//经度
			String lat = (String) oracleData.get("LAT");//维度
			String distinct = (String)oracleData.get("DISTRICT");//区
			if(StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city)  && StringUtils.isNotBlank(addr)) {//查询市下区的经纬度
				flag = handelDistinctData(map,province,city,distinct,addr,lng,lat,obj,flag);
			}else {
				flag = handelProvinceOrCityData(map,province,city,addr,listMySqlData,obj,flag);//处理省 市经纬度
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
	/**
	 * 处理区(小区)经纬度
	 * @param map 处理的数据
	 * @param addr 区(小区)名称
	 * @param lng  经度
	 * @param lat  维度
	 * @param obj  触点数
	 * @param flag 数据是否有效     false无经纬度，舍弃
	 * @return
	 */
	private Boolean handelDistinctData(Map<String, Object> map,String province,String city,String distinct,String addr,String lng,String lat,Object obj,Boolean flag){
		map.put("areaName",addr);//区(小区的名字)
		List list = new ArrayList();
		if(StringUtils.isNotBlank(lng) && StringUtils.isNotBlank(lat) ) {//市下区(小区已有经纬度)
			flag = true;
			list.add(lng);
			list.add(lat);
			list.add(obj);
			map.put("value", list);
		}else {
			Map<String,Object> distinctMap = baiduMapUtils.getCoordinate(province,city,distinct,addr);
			Object lng_ = distinctMap.get("lng");//经度
			Object lat_ = distinctMap.get("lat");//维度
			if(lng_ != null && lat_ != null) {
				flag = true;
				list.add(lng_);
				list.add(lat_);
				list.add(obj);
				map.put("value", list);				
			}
		}
		return flag;
	}
	/**
	 * 处理省、市经纬度
	 * @param map  处理的数据
	 * @param province 省
	 * @param city   市
	 * @param distinct 区(市/区下面区的小区)
	 * @param listMySqlData 地图 省、市经纬度
	 * @param obj  触点数
	 * @param flag 数据是否有效     false无经纬度，舍弃
	 * @return
	 */
	private Boolean handelProvinceOrCityData(Map<String,Object> map,String province,String city,String distinct, List<HashMap<String, Object>> listMySqlData,Object obj,boolean flag ) {
		for (HashMap<String, Object> mysqlData : listMySqlData) {
			String areaName =(String)mysqlData.get("area_name");
			String lng = (String) mysqlData.get("lon");//经度
			String lat = (String) mysqlData.get("lat");//维度
			List list = new ArrayList();
			map.put("areaName", areaName);
			if(StringUtils.isBlank(city) && StringUtils.isBlank(distinct)) {
				if(province.equals(areaName)) {//全国
					flag = true;
					list.add(lng);
					list.add(lat);
					list.add(obj);
					map.put("value", list);
					break;
				}
			}else if(StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city) && StringUtils.isBlank(distinct)) {//省下的市
				if(city.equals(areaName)) {
					flag = true;
					list.add(lng);
					list.add(lat);
					list.add(obj);
					map.put("value", list);
					break;
				}
			}
			
	}
		return flag;
	}
}
