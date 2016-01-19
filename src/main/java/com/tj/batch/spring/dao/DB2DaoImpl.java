package com.tj.batch.spring.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Repository;

@Repository
public class DB2DaoImpl extends CommonSessionFactory implements DB2Dao {

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> select(String sqlSelect,
			List<Object> paramList) {
		List<Map<String, Object>> aliasToValueMapList = null;
		Session candSession = null;
		try {
			candSession = openDB2Session();
			Query query = candSession.createSQLQuery(sqlSelect);
			prepareQueryWithParams(query, paramList);
			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			aliasToValueMapList = query.list();
		} catch (QueryException ex) {
			throw ex;
		} finally {
			candSession.close();
		}
		return aliasToValueMapList;
	}

	public boolean isExist(String sqlQuery, List<Object> paramList) {
		Session session = null;
		try {
			boolean flag = false;
			session = openDB2Session();
			Query query = session.createSQLQuery(sqlQuery);
			prepareQueryWithParams(query, paramList);
			Object data = query.uniqueResult();
			flag = data != null ? true : false;
			return flag;
		} finally {
			session.close();
		}
	}

	public Query prepareQueryWithParams(Query query, List<Object> paramList) {
		for (int i = 0; i < paramList.size(); i++) {
			if (paramList.get(i) != null) {
				query.setString(i, paramList.get(i).toString());
			} else {
				query.setParameter(i, paramList.get(i));
			}
		}
		return query;
	}

	public int updateDB(String sqlQuery, List<Object> paramList) {
		Session empSession = null;
		try {
			empSession = openDB2Session();
			Query query = empSession.createSQLQuery(sqlQuery);
			prepareQueryWithParams(query, paramList);
			int status = query.executeUpdate();
			return status;
		} finally {
			empSession.close();
		}
	}

}
