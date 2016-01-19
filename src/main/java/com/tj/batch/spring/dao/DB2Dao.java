package com.tj.batch.spring.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;


public interface DB2Dao {

	List<Map<String, Object>> select(String sqlSelect, List<Object> paramList);
	
	boolean isExist(String sqlSelect1, List<Object> paramList);
	
	Query prepareQueryWithParams(Query query, List<Object> paramList);

	int updateDB(String query, List<Object> paramList);
}
