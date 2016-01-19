package com.tj.batch.spring.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSource();
	}
	
	/*@Override
	public DataSource resolveSpecifiedDataSource(Object dataSource)
			throws IllegalArgumentException {
		return super.resolveSpecifiedDataSource(dataSource);
	}
	
	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
	}*/
	/*@Override
	protected DataSource determineTargetDataSource() {
		return super.determineTargetDataSource();
	}*/
}
