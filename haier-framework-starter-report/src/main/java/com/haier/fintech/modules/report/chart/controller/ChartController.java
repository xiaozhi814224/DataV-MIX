package com.haier.fintech.modules.report.chart.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.haier.fintech.modules.report.chart.entity.ChartEntity;
import com.haier.fintech.modules.report.chart.service.ChartService;
import com.haier.fintech.modules.report.datasets.service.DataSetService;
@RestController
@RequestMapping("/sys/chart")
public class ChartController extends AbstractController{

	@Autowired
	private ChartService chartService;

	@Autowired
	private DataSetService dataSetService;
	/**
	 * 列表
	 */
	@GetMapping("/list")
	public R list(@RequestParam HashMap<String,Object> params){
		PageUtils page = chartService.queryPage(params);
		return R.ok().put("page", page);
	}
	@PostMapping("/save")
	public R save(@RequestBody ChartEntity chartEntity) {
		chartService.save(chartEntity,getUserId());
		return R.ok();
		
	}
	@PostMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		chartService.deleteByIds(ids);
		return R.ok();
	}
	@GetMapping("/getInfo/{id}")
	public R info(@PathVariable("id") Long id) {
		ChartEntity data = chartService.getInfoById(id);
		return R.ok().put("data", data);
	}
	@PostMapping("/update")
	public R update(@RequestBody ChartEntity chartEntity) {
		chartService.updateDataSet(chartEntity,getUserId());
		return R.ok();
	}
	@GetMapping("/getColumList/{dataSetId}")
	public R getColumList(@PathVariable("dataSetId") Long dataSetId) {
		List<Map<String,Object>> data = dataSetService.getListColums(dataSetId);
		return R.ok().put("data", data);
	}
	
	@PostMapping("/readData")
	public R readData(@RequestBody Map<String,Object> map) {
		List<HashMap<String,Object>> data = dataSetService.getChartData(map);
		return R.ok().put("data", data);
	}
	@PostMapping("/checkReportNumUnique")
	public R checkReportNumUnique(@RequestBody Map<String,Object> map) {
		Boolean flag = chartService.checkReportNumUnique(map);
		return R.ok().put("flag", flag);
	}

}
