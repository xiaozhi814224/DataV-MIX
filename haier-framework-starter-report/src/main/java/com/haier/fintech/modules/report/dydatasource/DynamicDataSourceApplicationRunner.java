package com.haier.fintech.modules.report.dydatasource;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.haier.fintech.modules.report.datasources.service.DataSourceService;
@Component
public class DynamicDataSourceApplicationRunner implements ApplicationRunner {
	@Resource
	DataSourceService dataSourceService;
	
    @Override
    public void run(ApplicationArguments var1) throws Exception{
    	 //dataSourceService.loadDynamicDataSource();
    }
}