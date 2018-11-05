package com.haier.fintech.modules.report.chart.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.common.utils.Query;
import com.haier.fintech.modules.report.chart.dao.ChartDao;
import com.haier.fintech.modules.report.chart.entity.ChartEntity;
import com.haier.fintech.modules.report.chart.service.ChartService;
@Service("chartService")
public class ChartServiceImple extends ServiceImpl<ChartDao, ChartEntity> implements ChartService{
	@Autowired
	private ChartDao chartDao;

	@Override
	public PageUtils queryPage(HashMap<String, Object> params) {
		Page<ChartEntity> page = null;
	    EntityWrapper<ChartEntity> eWrapper = new EntityWrapper<ChartEntity>();	
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
		page = this.selectPage(new Query<ChartEntity>(params).getPage(),eWrapper); 
		return new PageUtils(page);
	}
	/**
	 * 新增图表
	 */
	@Override
	public void save(ChartEntity chartEntity,Long userId) {
		chartEntity.setCreateTime(new Date());
		chartEntity.setUpdateTime(new Date());
		chartEntity.setCreateUser(userId);
		chartEntity.setUpdateUser(userId);
		baseMapper.insert(chartEntity);
	}
	/**
	 * 查询图表信息
	 */
	@Override
	public ChartEntity getInfoById(Long id) {
		ChartEntity entity = new ChartEntity();
		entity.setId(id);
		return baseMapper.selectOne(entity);
	}
	/**
	 * 更新（创建的）图标
	 */
	@Override
	public void updateDataSet(ChartEntity chartEntity, Long userId) {
		chartEntity.setUpdateTime(new Date());
		chartEntity.setUpdateUser(userId);
		baseMapper.updateById(chartEntity);		
	}
	@Override
	public void deleteByIds(Long[] ids) {
		List<Long> list = Arrays.asList(ids);
		baseMapper.deleteBatchIds(list);
	}
	//首页-根据reportId获取数据 
	@Override
	public Map<String, Object> getDataById(String reportId) {
		return chartDao.selectDataById(reportId);
	}
	@Override
	public Boolean checkReportNumUnique(Map<String, Object> map) {
		ChartEntity chartEntity = new ChartEntity();
		chartEntity.setReportNum((String)map.get("reportNum"));
		ChartEntity  ce = baseMapper.selectOne(chartEntity);
		if(ce != null) {
			return false;
		}else {
			return true;
		}
	}
	@Override
	public ChartEntity getDataByreportNum(String reportNum) {//根据reportNum 查询记录
		ChartEntity chartEntity = new ChartEntity();
		chartEntity.setReportNum(reportNum);			
		return baseMapper.selectOne(chartEntity);
	}

}
