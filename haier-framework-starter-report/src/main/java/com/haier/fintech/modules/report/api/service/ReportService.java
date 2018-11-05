package com.haier.fintech.modules.report.api.service;

import java.util.HashMap;
import java.util.Map;

public interface ReportService {

	Map<String, Object> getResult(String reportNum, HashMap<String, String> param);

}
