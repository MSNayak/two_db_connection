package com.tj.batch.spring.dao;

import java.math.BigInteger;

import org.springframework.stereotype.Repository;

@Repository
public class MysqlDaoImpl extends CommonSessionFactory implements MysqlDao {

	public BigInteger countRecords() {
		String sql = "select count(*) from employee";
		BigInteger count = (BigInteger) openMysqlSession().createSQLQuery(sql)
				.uniqueResult();
		return count;
	}

}
