package com.haier.fintech.modules.report.api.service.impl;

import java.util.ArrayList;
import java.util.Collection;
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
		List<Map<String,Object>> saveDataList = new ArrayList<Map<String,Object>>();//小区经纬度从百度API查询返回后要插入到本地表的数据
		for (HashMap<String, Object> oracleData : listOracleSqlData) {
			Map<String, Object> map = new HashMap<String, Object>();
			boolean flag = false;
			Object obj = oracleData.get("RESULTS");//触点数
			String province = (String)oracleData.get("PROVINCE");//省
			String city = (String)oracleData.get("CITY");//市
			String distinct = (String)oracleData.get("DISTRICT");//区(小区)
			String lon = (String) oracleData.get("lon");//经度
			String lan = (String) oracleData.get("lat");//维度
			String trueDistrict = (String)oracleData.get("TRUEDISTRICT");//区(小区)
			if(StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city)  && StringUtils.isNotBlank(distinct)) {//查询市下区的经纬度
				flag = handelDistinctData(map,province,city,trueDistrict,distinct,lon,lan,obj,saveDataList,flag);
			}else {
				flag = handelProvinceOrCityData(map,province,city,distinct,listMySqlData,obj,flag);//处理省 市经纬度
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
		if(!(CollectionUtils.isEmpty(saveDataList))) {//将要保存到本地表的数据集合
			batchInsertData(saveDataList);
		}
		result.put("datas", listMap);
		return result;
	}
	/**
	 * 将请求百度接口返回的小区经纬度插入到本地库
	 * @param saveDataList   数据集合
	 */
	private void batchInsertData(List<Map<String,Object>> saveDataList) {
		//是springBoot自身的一种异步方式，使用注解实现，非常方便，我们在想要异步执行的方法上加上@Async注解，
	   //在controller上加上@EnableAsync，即可。注意，这里的异步方法，只能在自身之外调用，在本类调用是无效的。
	}
	/**
	 * 处理区(小区)经纬度
	 * @param map 处理的数据
	 * @param distinct 区(小区)名称
	 * @param lon  经度
	 * @param lan  维度
	 * @param obj  触点数
	 * @param flag 数据是否有效     false无经纬度，舍弃
	 * @return
	 */
	private Boolean handelDistinctData(Map<String, Object> map,String province,String city,String trueDistinct,String distinct,String lon,String lan,Object obj,List<Map<String,Object>> saveDataList,Boolean flag){
		map.put("areaName", distinct);//区(小区的名字)
		List list = new ArrayList();
		if(StringUtils.isNotBlank(lon) && StringUtils.isNotBlank(lan) ) {//市下区(小区已有经纬度)
			flag = true;
			list.add(lon);
			list.add(lan);
			list.add(obj);
			map.put("value", list);
		}else {
			Map<String,Object> distinctMap = BaiduMapUtils.getCoordinate(province+city+trueDistinct+distinct);
			Object lng = distinctMap.get("lng");//经度
			Object lat = distinctMap.get("lat");//维度
			if(lng != null && lat != null) {
				flag = true;
				list.add(lng);
				list.add(lat);
				list.add(obj);
				map.put("value", list);				
				
				Map<String,Object> saveData = new HashMap<String,Object>();
				saveData.put("province", province);
				saveData.put("city", "city");
				saveData.put("trueDistinct",trueDistinct);
				saveData.put("distinct", distinct);
				saveData.put("lon",lng);
				saveData.put("lat",lat);
				saveDataList.add(saveData);//加入将要保存到本地表的数据集合
				
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
			String lon = (String) mysqlData.get("lon");//经度
			String lan = (String) mysqlData.get("lat");//维度
			List list = new ArrayList();
			map.put("areaName", areaName);
			if(StringUtils.isBlank(city) && StringUtils.isBlank(distinct)) {
				if(province.equals(areaName)) {//全国
					flag = true;
					list.add(lon);
					list.add(lan);
					list.add(obj);
					map.put("value", list);
					break;
				}
			}else if(StringUtils.isNotBlank(province) && StringUtils.isNotBlank(city) && StringUtils.isBlank(distinct)) {//省下的市
				if(city.equals(areaName)) {
					flag = true;
					list.add(lon);
					list.add(lan);
					list.add(obj);
					map.put("value", list);
					break;
				}
			}
			
	}
		return flag;
	}
}
