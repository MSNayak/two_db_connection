package com.tj.batch.spring.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MyDataSources {
	public static String CAND = "CANDB";
	public static String HIRE = "EMPDB";
	public static String MYSQLEMP = "MySqlEMP";
	public static String MYSQLAPA = "MySqlAPA";

	public List<String> getDataSources() {
		List<String> list = new ArrayList<String>();
		for (Field f : getClass().getDeclaredFields()) {
			try {
				list.add(f.get(this).toString());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
}
