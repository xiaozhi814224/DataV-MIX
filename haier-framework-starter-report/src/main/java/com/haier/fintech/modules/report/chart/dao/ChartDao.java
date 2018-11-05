package com.haier.fintech.modules.report.chart.dao;

import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.haier.fintech.modules.report.chart.entity.ChartEntity;

@Mapper
public interface ChartDao extends BaseMapper<ChartEntity> {
	 
	//首页-根据reportId获取数据  
	 @Select("SELECT\r\n" + 
	 		"			*\r\n" + 
	 		"		FROM\r\n" + 
	 		"			tb_chart_config c\r\n" + 
	 		"		LEFT JOIN tb_data_set_config s ON c.data_set_id = s.id where 1=1 AND c.id =#{_parameter} ")
	 Map<String,Object> selectDataById(String _parameter);

}
