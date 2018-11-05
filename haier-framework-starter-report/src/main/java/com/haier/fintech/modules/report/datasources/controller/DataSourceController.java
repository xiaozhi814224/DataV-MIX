package com.haier.fintech.modules.report.datasources.controller;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.haier.fintech.base.controller.AbstractController;
import com.haier.fintech.common.utils.PageUtils;
import com.haier.fintech.common.utils.R;
import com.haier.fintech.modules.report.datasources.entity.DataSourceEntity;
import com.haier.fintech.modules.report.datasources.service.DataSourceService;

@RestController
@RequestMapping("/sys/dataSource")
public class DataSourceController  extends AbstractController{
	@Autowired
	private DataSourceService dataSourceService;
	/**
	 * 列表
	 */
	@GetMapping("/list")
	public R list(@RequestParam HashMap<String,Object> params){
		PageUtils page = dataSourceService.queryPage(params);
		return R.ok().put("page", page);
	}
	@PostMapping("/save")
	public R save(@RequestBody DataSourceEntity dataSourceEntity) {
		dataSourceService.save(dataSourceEntity,getUserId());
		return R.ok();
		
	}
	@PostMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		dataSourceService.deleteByIds(ids);
		return R.ok();
	}
	@GetMapping("/getInfo/{id}")
	public R info(@PathVariable("id") Long id) {
		DataSourceEntity data = dataSourceService.getInfoById(id);
		return R.ok().put("data", data);
	}
	@PostMapping("/update")
	public R update(@RequestBody DataSourceEntity dataSourceEntity) {
		dataSourceService.updateDataSource(dataSourceEntity,getUserId());
		return R.ok();
	}
	/**
	 * 获取数据集列表
	 */
	@GetMapping("/getDataSourceList")
	public R getDataSourceList(){
		List<DataSourceEntity> list = dataSourceService.getDataSourceList();
		return R.ok().put("list", list);
	}
	@PostMapping("/checkConnect")
	public R checkConnect(@RequestBody DataSourceEntity dataSourceEntity) {
		 R result = new R();
		try {
			Integer type = dataSourceEntity.getType();
			if(type == 3) {//非关系型数据库  MongoDB
//				MongoDatabase mdb = dataSourceService.checkMongoDbConnect(dataSourceEntity);
//				if(mdb == null) {
//					result=R.error("数据库连接失败");
//				}
			}else {//关系型数据库				
				Connection con = dataSourceService.checkConnect(dataSourceEntity);
				if(con == null) {
					result=R.error("数据库连接失败");
				}
			}
			result = R.ok();
		} catch (Exception e) {
			e.printStackTrace();
	        result=R.error("数据库连接失败");
		}
		return result;
	}
	
}
