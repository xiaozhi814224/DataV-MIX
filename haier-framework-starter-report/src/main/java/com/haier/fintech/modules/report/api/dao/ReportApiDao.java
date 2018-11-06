package com.haier.fintech.modules.report.api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReportApiDao {
	/**
	 * 平均走势图
	 * @param name
	 * @return
	 
	@Select("SELECT room_no AS '房间号',reshi AS '热水' ,lengshui AS '冷水',dian AS '电' from  report_zoushi_view where community = #{name}")
	public List<HashMap<String,Object>> getjson1(String name);*/
	
	/**
	 * 对比图
	 * @return
	 
	@Select("select (CASE WHEN a.subject='00' THEN '热水' WHEN a.subject='01' THEN '冷水' WHEN a.subject='02' THEN '电' END) as '类型', "+
			"REPLACE(FORMAT(sum(case when a.community = '上海大宁国际社区' then a.recharge_num else 0 end)/3,2),',','') as '上海大宁国际社区',"+
			"REPLACE(FORMAT(sum(case when a.community = '上海江宁路社区' then a.recharge_num else 0 end)/3,2),',','') as '上海江宁路社区'"+
			" from tb_iot_recharge_info a "+    
			"where 1=1"+     
			" and a.recharge_num>0"+   
			" and a.subject !='03'"+
			" group by subject")
	public List<HashMap<String,Object>> getjson2();*/
	
	/**
	 * 风险
	 * @return
	 
	@Select(" select room_no as '房间号', reshi as '热水充值',lengshui as  '冷水充值', dian as '电充值' from report_yichang_view a where a.community  = #{name} ")
	public List<HashMap<String,Object>> getjson3(String name);*/
	
	/*@Select("select" + 
			"(SELECT  count(DISTINCT(room_no)) FROM tb_iot_recharge_info where community = '上海大宁国际社区') as totalDn," + 
			"(select  count(DISTINCT(room_no)) from tb_iot_recharge_info where community = '上海大宁国际社区' and  recharge_num <0) AS dnSy," + 
			"(SELECT  count(DISTINCT(room_no)) FROM tb_iot_recharge_info where community = '上海江宁路社区' ) as totalJn," + 
			"(select  count(DISTINCT(room_no)) from tb_iot_recharge_info where community = '上海江宁路社区' and  recharge_num <0) AS jnSy" + 
			" from tb_iot_recharge_info  GROUP BY totalDn")
	public HashMap<String, Object> getData();*/
	/**
	 * 获取结果集列名
	 * @param sql
	 * @return
	 */
	@Select("${sql}")
	public List<HashMap<String, Object>> getColumList(@Param("sql") String sql);

	@Select("select * from tb_files")
	public List<HashMap<String, Object>> getFileData();
	
	@Select("SELECT count(1) from  report_zoushi_view ")
	public Integer getjson4(String name);
	
	@Select("${sql}")
	List<HashMap<String, Object>> getData(@Param("sql") String sql);
	//查询地图数据(省份经纬度)
	@Select("${sql}")
	public List<HashMap<String, Object>> getMapData(@Param("sql")String sql);
	@Select("SELECT	* FROM " + 
			"			tb_chart_config c " + 
			"		LEFT JOIN tb_data_set_config s ON c.data_set_id = s.id " + 
			"		where " + 
			"	          c.id =#{reportId}" + 
			"	   ")
	public Map<String, Object> getDataById(String reportId);
}
