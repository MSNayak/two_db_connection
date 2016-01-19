package com.tj.batch.spring.service;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tj.batch.spring.dao.MysqlDao;

@Service("mysqlService")
public class MysqlServiceImpl implements MysqlService {
	@Autowired
	MysqlDao mysqlDao;

	public BigInteger countRecords() {
		return mysqlDao.countRecords();
	}

}
