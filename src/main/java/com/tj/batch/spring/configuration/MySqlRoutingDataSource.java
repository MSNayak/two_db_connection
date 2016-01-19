package com.tj.batch.spring.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MySqlRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getDataSource();
	}
}
