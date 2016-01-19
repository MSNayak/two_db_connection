package com.tj.batch.spring.configuration;

public class DataSourceContextHolder {

	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	public static void setDataSource(String dataSource) {
		// set default datasources.
		if (dataSource == null) {
			contextHolder.set(MyDataSources.CAND);
		} else {
			contextHolder.set(dataSource);
		}
	}

	public static String getDataSource() {
		return (String) contextHolder.get();
	}

	public static void clearDataSource() {
		contextHolder.remove();
	}
}
