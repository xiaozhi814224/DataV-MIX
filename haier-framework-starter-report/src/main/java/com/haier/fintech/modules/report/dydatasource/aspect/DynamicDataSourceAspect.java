package com.haier.fintech.modules.report.dydatasource.aspect;

import com.haier.fintech.datasources.DynamicDataSource;
import com.haier.fintech.modules.report.dydatasource.annotation.TargetDataSource;

import java.lang.reflect.Method;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect implements Ordered {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Pointcut("@annotation(com.haier.fintech.modules.report.dydatasource.annotation.TargetDataSource)")
	public void targetDataSourcePointCut() {
	}

	@Around("targetDataSourcePointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();

		TargetDataSource ds = (TargetDataSource) method.getAnnotation(TargetDataSource.class);
		if (ds != null) {
			DynamicDataSource.DbContextHolder.setDataSource("datav");
			this.logger.debug("set datasource is " + ds.name());
		}
		try {
			return point.proceed();
		} finally {
			DynamicDataSource.DbContextHolder.clearDataSource();
			this.logger.debug("clean datasource");
		}
	}

	public int getOrder() {
		return 2;
	}
}
