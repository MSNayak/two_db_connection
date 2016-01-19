package com.tj.batch.spring.process;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.tj.batch.spring.configuration.AppConfig;
import com.tj.batch.spring.model.JobProperties;
import com.tj.batch.spring.service.DB2Service;
import com.tj.batch.spring.util.Constants;

public class UserAd_RcrtJobAdsBatchProcess {
	AbstractApplicationContext context = new AnnotationConfigApplicationContext(
			AppConfig.class);
	private static Logger logger = Logger
			.getLogger(UserAd_RcrtJobAdsBatchProcess.class);
	DB2Service db2Service = (DB2Service) context.getBean("db2Service");

	public static void main(String[] args) throws IOException {
		logger.info("====UserAd_RcrtJobAdsBatchProcess has been started=====");
		UserAd_RcrtJobAdsBatchProcess sicbp = new UserAd_RcrtJobAdsBatchProcess();
		JobProperties jbps = new JobProperties();
		jbps.setStartTimeFile(args[0]);
		jbps.setTableName(args[1]);
		jbps.setSource(args[2]);
		jbps.setDestination(args[3]);
		jbps.setProcessName(Constants.USERAD_RCRTJOB_ADS.getValue());
		sicbp.process(jbps);
		logger.info("====UserAd_RcrtJobAdsBatchProcess has been finished=====");
	}

	private void process(JobProperties jbps) {
		db2Service.process5or4Queries(jbps);
	}
}
