package com.haier.fintech.modules.report.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.haier.fintech.common.utils.R;
import com.haier.fintech.modules.report.api.service.ReportService;

/**
 * 报表统一访问入口
 * @author hanbing
 *
 */
@RestController
@RequestMapping("report")
public class ReportApi {
	@Autowired
	private ReportService reportService;	
	@PostMapping("/queryData/{reportNo}")
	public R getResult(@PathVariable("reportNo") String reportNo,@RequestBody HashMap<String,String> param) {
		Map<String,Object> map = reportService.getResult(reportNo,param);
		return R.ok().put("result", map);
	}
}
