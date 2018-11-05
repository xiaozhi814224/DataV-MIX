package com.haier.fintech.modules.report.datasets.controller;

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
import com.haier.fintech.modules.report.datasets.entity.DataSetEntity;
import com.haier.fintech.modules.report.datasets.service.DataSetService;

@RestController
@RequestMapping("/sys/dataSet")
public class DataSetController extends AbstractController{
	@Autowired
	private DataSetService dataSetService;
	/**
	 * 列表
	 */
	@GetMapping("/list")
	public R list(@RequestParam HashMap<String,Object> params){
		PageUtils page = dataSetService.queryPage(params);
		return R.ok().put("page", page);
	}
	@PostMapping("/save")
	public R save(@RequestBody DataSetEntity dataSetEntity) {
		dataSetService.save(dataSetEntity,getUserId());
		return R.ok();
		
	}
	@PostMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		dataSetService.deleteByIds(ids);
		return R.ok();
	}
	@GetMapping("/getInfo/{id}")
	public R info(@PathVariable("id") Long id) {
		DataSetEntity data = dataSetService.getInfoById(id);
		return R.ok().put("data", data);
	}
	@PostMapping("/update")
	public R update(@RequestBody DataSetEntity dataSetEntity) {
		dataSetService.updateDataSet(dataSetEntity,getUserId());
		return R.ok();
	}
	/**
	 * 获取数据集列表
	 */
	@GetMapping("/getDataSetList")
	public R getDataSetList(){
		List<DataSetEntity> list = dataSetService.getDataSetList();
		return R.ok().put("list", list);
	}
	@PostMapping("/getDatas")
	public R getDatas(@RequestBody HashMap<String,Object> params) {
		List<HashMap<String,Object>> data = dataSetService.getDatas(params);
		return R.ok().put("data", data);
	}
}
