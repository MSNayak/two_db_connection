package com.tj.batch.spring.dao;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public abstract class CommonSessionFactory {

	@Resource(name = "mysqlSessionFactory")
	private SessionFactory mysqlSessionFactory;

	@Resource(name = "db2SessionFactory")
	private SessionFactory db2SessionFactory;

	protected Session getCurrentMysqlSession() {
		return mysqlSessionFactory.getCurrentSession();
	}

	protected Session openMysqlSession() {
		return mysqlSessionFactory.openSession();
	}

	protected Session getCurrentDB2Session() {
		return db2SessionFactory.getCurrentSession();
	}

	protected Session openDB2Session() {
		return db2SessionFactory.openSession();
	}
}
