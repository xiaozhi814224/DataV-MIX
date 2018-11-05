package com.haier.fintech.modules.report.datasets.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.sql.parser.SQLParser;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.common.utils.Query;
import com.haier.fintech.modules.report.api.dao.ReportApiDao;
import com.haier.fintech.modules.report.datasets.dao.DataSetDao;
import com.haier.fintech.modules.report.datasets.entity.DataSetEntity;
import com.haier.fintech.modules.report.datasets.service.DataSetService;
import com.haier.fintech.modules.report.datasources.entity.DataSourceEntity;
import com.haier.fintech.modules.report.datasources.service.DataSourceService;
import com.haier.fintech.modules.report.utils.SqlParserUtils;

@Service("dataSetService")
public class DataSetServiceImple extends ServiceImpl<DataSetDao, DataSetEntity> implements DataSetService {
	@Autowired
	DataSourceService dataSourceService;
	@Autowired
	ReportApiDao reportApiDao;
	@Override
	public PageUtils queryPage(HashMap<String, Object> params) {
		DataSetEntity en = new DataSetEntity();
		Page<DataSetEntity> page = null;
		en = JSON.parseObject(JSON.toJSONString(params), DataSetEntity.class) ;
	    EntityWrapper<DataSetEntity> eWrapper = new EntityWrapper<DataSetEntity>();	
	    String name = (String)params.get("name");
	    String loadType = (String)params.get("loadType");
	    String timeStr = (String)params.get("createTime");
	    String configId = (String)params.get("configId");
	    if(StringUtils.isNotBlank(configId)) {
	    	eWrapper.eq("config_id", configId);
	    }
	    if(StringUtils.isNotBlank(loadType)) {
	    	eWrapper.eq("load_type", loadType);
	    }
	    if(StringUtils.isNotBlank(timeStr)) {
	    	String [] createTime = timeStr.split(",");
	    	eWrapper.between("create_time", createTime[0], createTime[1]);
	    }
	    if(StringUtils.isNotBlank(name)) {
	    	eWrapper.eq("name", name);
	    }
		page = this.selectPage(new Query<DataSetEntity>(params).getPage(),eWrapper); 
		return new PageUtils(page);
	}
	/**
	 * 新增数据集
	 */
	@Override
	public void save(DataSetEntity dataSetEntity,Long userId) {
		dataSetEntity.setCreateTime(new Date());
		dataSetEntity.setUpdateTime(new Date());
		dataSetEntity.setCreateUser(userId);
		dataSetEntity.setUpdateUser(userId);
		baseMapper.insert(dataSetEntity);
	}
	/**
	 * 查询数据集信息
	 */
	@Override
	public DataSetEntity getInfoById(Long id) {
		DataSetEntity entity = new DataSetEntity();
		entity.setId(id);
		return baseMapper.selectOne(entity);
	}
	/**
	 * 更新数据集
	 */
	@Override
	public void updateDataSet(DataSetEntity dataSetEntity, Long userId) {
		dataSetEntity.setUpdateTime(new Date());
		dataSetEntity.setUpdateUser(userId);
		baseMapper.updateById(dataSetEntity);		
	}
	@Override
	public void deleteByIds(Long[] ids) {
		List<Long> list = Arrays.asList(ids);
		baseMapper.deleteBatchIds(list);
	}
	/**
	 * 获取数据集列表
	 */
	@Override
	public List<DataSetEntity> getDataSetList() {
		EntityWrapper<DataSetEntity> wrapper = new EntityWrapper<DataSetEntity>();
		return baseMapper.selectList(wrapper);
	}
	@Override
	public List<Map<String, Object>> getListColums(Long dataSetId) {
		DataSetEntity dse = new DataSetEntity();
		dse.setId(dataSetId);
		dse = baseMapper.selectOne(dse);
		String sql = dse.getSql();
		List<String> listColumns = SqlParserUtils.parseColumnName(sql);
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		for (int i=0;i<listColumns.size();i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("label", listColumns.get(i));
			map.put("value", listColumns.get(i));
			listMap.add(map);
		}
        return listMap;
	  
	}
//	public List<Map<String, Object>> getResult(Map<String, Object> map){
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		for(String key : map.keySet()){ // key value
//			   Map<String, Object> resultMap = new HashMap<String, Object>();
//			   resultMap.put("label", key);
//			   resultMap.put("value", key);
//			   list.add(resultMap);
//	        }
//		return list;
//	}
	
	@Override
	public List<HashMap<String, Object>> getDatas(HashMap<String, Object> params) {
		String sql= (String) params.get("sql");
		List<Map<String,String>> listMap =(List<Map<String,String>>)params.get("filterCert");
		
		for (Map<String, String> map : listMap) {
			for (Entry<String, String> entry : map.entrySet()) {
			     String key = entry.getKey();
			     entry.getValue();
			     sql = sql.replace("#{" + key + "}" , entry.getValue());
			}		
		}
		List<HashMap<String, Object>> list = reportApiDao.getColumList(sql);
		
		return list;
	}
	@Override
	public List<HashMap<String, Object>> getChartData(Map<String, Object> map) {
		Long dataSetId = ((Integer)map.get("dataSetId")).longValue();	
		DataSetEntity entity = new DataSetEntity();
		entity.setId(dataSetId);
		DataSetEntity obj =  baseMapper.selectOne(entity);//数据集信息
		EntityWrapper<DataSourceEntity> ew = new EntityWrapper<DataSourceEntity>();
		ew.eq("id", obj.getDataSourceId());
		DataSourceEntity dse = dataSourceService.selectOne(ew);//数据源信息
		String sql = obj.getSql();
        List<Map<String,String>> listMap =(List<Map<String,String>>)map.get("filterCert");
		
		for (Map<String, String> certMap : listMap) {
			for (Entry<String, String> entry : certMap.entrySet()) {
			     String key = entry.getKey();
			     entry.getValue();
			     sql = sql.replace("#{" + key + "}" , entry.getValue());
			}		
		}
		List<HashMap<String, Object>> list = reportApiDao.getColumList(sql);
		return list;
	}
}
