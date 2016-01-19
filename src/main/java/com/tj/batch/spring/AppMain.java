package com.tj.batch.spring;

import java.math.BigInteger;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.tj.batch.spring.configuration.AppConfig;
import com.tj.batch.spring.configuration.DataSourceContextHolder;
import com.tj.batch.spring.configuration.MyDataSources;
import com.tj.batch.spring.service.MysqlService;

public class AppMain {

	public static void main(String args[]) {
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(
				AppConfig.class);

	/*	System.out.println("=========================DB2===========================");
		DB2Service db2Service = (DB2Service) context.getBean("db2Service");
		int count = db2Service.countRecords();
		System.out.println("==========Count from Default(Cand) DB-->" + count);
		DataSourceContextHolder.setDataSource(MyDataSources.HIRE);
		count = db2Service.countRecords();
		System.out.println("==========Count from Hire-->" + count);*/
		
		System.out.println("=========================Mysql===========================");
		DataSourceContextHolder.setDataSource(MyDataSources.MYSQLEMP);
		MysqlService mysqlService=(MysqlService) context.getBean("mysqlService");
		BigInteger count2=mysqlService.countRecords();
		System.out.println("==========Count from Local Employee-->" + count2);
		DataSourceContextHolder.setDataSource(MyDataSources.MYSQLAPA);
		count2=mysqlService.countRecords();
		System.out.println("==========Count from APA Employee-->" + count2);
		context.close();
		
	}
}
