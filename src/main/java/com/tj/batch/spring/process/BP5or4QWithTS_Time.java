package com.tj.batch.spring.process;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.tj.batch.spring.configuration.AppConfig;
import com.tj.batch.spring.model.JobProperties;
import com.tj.batch.spring.service.DB2Service;

public class BP5or4QWithTS_Time {
	AbstractApplicationContext context = new AnnotationConfigApplicationContext(
			AppConfig.class);
	private static Logger logger = Logger.getLogger(BP5or4QWithTS_Time.class);
	DB2Service db2Service = (DB2Service) context.getBean("db2Service");

	public static void main(String[] args) throws IOException {
		logger.info("====BP5or4QWithTS_Time has been started=====");
		BP5or4QWithTS_Time sicbp = new BP5or4QWithTS_Time();
		JobProperties jbps = new JobProperties();
		// If there in no start time file.
		if (!StringUtils.isBlank(args[0]) && !args[0].equalsIgnoreCase("null")) {
			jbps.setStartTimeFile(args[0]);
		}
		jbps.setTableName(args[1]);
		jbps.setSource(args[2]);
		jbps.setDestination(args[3]);
		sicbp.process(jbps);
		logger.info("====BP5or4QWithTS_Time has been finished=====");
	}

	private void process(JobProperties jbps) {
		db2Service.process5or4Queries(jbps);
	}
}
