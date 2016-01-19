package com.tj.batch.spring.service;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javassist.NotFoundException;
import net.sf.jsqlparser.JSQLParserException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tj.batch.spring.configuration.DataSourceContextHolder;
import com.tj.batch.spring.configuration.MyDataSources;
import com.tj.batch.spring.dao.DB2Dao;
import com.tj.batch.spring.model.JobProperties;
import com.tj.batch.spring.util.Constants;
import com.tj.batch.spring.util.PropertyReaderUtil;
import com.tj.batch.spring.util.SqlParserUtil;
import com.tj.batch.spring.util.TableNames;

@Service("db2Service")
public class DB2ServiceImpl implements DB2Service {
	private static final Logger logger = Logger.getLogger(DB2ServiceImpl.class);

	@Autowired
	private DB2Dao db2Dao;

	public void process5or4Queries(JobProperties jbps) {
		try {
			validateSourceAndDestinationDB(jbps.getSource(),
					jbps.getDestination());
			prepareSqlQueries(jbps);
			if (!StringUtils.isBlank(jbps.getStartTimeFile())) {
				process5QueriesWithTime(jbps);
			} else {
				process5QueriesWithoutTime(jbps);
			}
		} catch (Exception ex) {
			logger.error(ex);
		}
	}

	public void prepareColumnsVlaueList(Map<String, Object> columnValueMap,
			List<String> columnList, List<Object> paramList)
			throws NotFoundException {
		paramList.clear();
		for (String column : columnList) {
			if (!columnValueMap.containsKey(column)) {
				String errMsg = "Column=" + column
						+ " not found in columnValueMap=" + columnValueMap;
				throw new NotFoundException(errMsg);
			}
			paramList.add(columnValueMap.get(column));
		}
	}

	public void validateSourceAndDestinationDB(String sourcedb,
			String destinationdb) throws NotFoundException {
		logger.info("Source db=" + sourcedb + " destination db="
				+ destinationdb);
		List<String> dataSources = new MyDataSources().getDataSources();
		if (StringUtils.isBlank(sourcedb) || StringUtils.isBlank(destinationdb)) {
			throw new NullPointerException(
					"Source or destination db can not be empty.");
		} else if (!dataSources.contains(sourcedb.trim().toUpperCase())
				|| !dataSources.contains(destinationdb.trim().toUpperCase())) {
			throw new NotFoundException(
					"sourcedb or destinationdb not found in defined datasources="
							+ dataSources.toString());
		}
	}

	public void process5or4QCommonCodeWithSubProcess(List<Object> paramList,
			JobProperties jbps) {
		DataSourceContextHolder.setDataSource(jbps.getSource());
		List<Map<String, Object>> data = db2Dao.select(jbps.getSelect(),
				paramList);
		int successRecords = 0;
		for (Map<String, Object> columnValueMap : data) {
			int status = 0;
			try {
				if (columnValueMap.containsKey("TRANSMIT_STATUS")) {
					columnValueMap.put("TRANSMIT_STATUS", "Y");
				}
				// update destination_db.
				status = updateDestinationDB(columnValueMap, jbps, paramList);
				// Run sub process.
				jbps.setStatus(status);
				status = runSubProcess(jbps, columnValueMap);
				// update source_db.
				jbps.setStatus(status);
				successRecords = updateSourceDB(columnValueMap, jbps,
						paramList, successRecords);
			} catch (Exception ex) {
				logger.error("Problem in sync the reocord=" + columnValueMap);
				logger.error(ex);
			}
		}
		jbps.setTotalRecords(data.size());
		jbps.setSuccessRecords(successRecords);
	}

	private int runSubProcess(JobProperties jbps,
			Map<String, Object> columnValueMap) {
		JobProperties jbps2 = new JobProperties();
		jbps2.setStatus(jbps.getStatus());
		jbps2.setSource(jbps.getSource());
		jbps2.setDestination(jbps.getDestination());
		if (!StringUtils.isBlank(jbps.getProcessName()) && jbps.getStatus() > 0) {
			List<Object> paramList = new ArrayList<Object>();
			String NET_STATUS = columnValueMap.get("NET_STATUS") != null ? columnValueMap
					.get("NET_STATUS").toString() : "";
			if (jbps.getProcessName().equalsIgnoreCase(
					Constants.USERAD_RCRTRESUME_ADS.getValue())) {
				// run sub process for this.
				if ((NET_STATUS.equals("11") || NET_STATUS.equals("20"))) {
					paramList.add(columnValueMap.get("AD_ID"));
					paramList.add(columnValueMap.get("BOOKING_CENTER"));
					jbps2.setTableName(TableNames.RCRT_RESUME_ADS.getValue());
					prepareSqlQueries(jbps2);
					process5or4QCommonCode(paramList, jbps2);
				}
			} else if (jbps.getProcessName().equalsIgnoreCase(
					Constants.USERAD_RCRTJOB_ADS.getValue())) {
				if ((NET_STATUS.equals("11") || NET_STATUS.equals("13"))) {
					paramList.add(columnValueMap.get("AD_ID"));
					paramList.add(columnValueMap.get("BOOKING_CENTER"));
					jbps2.setTableName(TableNames.RCRT_JOB_ADS.getValue());
					prepareSqlQueries(jbps2);
					process5or4QCommonCode(paramList, jbps2);

					// run second sub process.
					paramList.clear();
					paramList.add(columnValueMap.get("AD_ID"));
					jbps2.setTableName(TableNames.RCRT_JOB_EXTENSION.getValue());
					prepareSqlQueries(jbps2);
					process5or4QCommonCode(paramList, jbps2);
				}
			}
		}
		return jbps2.getStatus();
	}

	private void process5or4QCommonCode(List<Object> paramList,
			JobProperties jbps) {
		DataSourceContextHolder.setDataSource(jbps.getSource());
		List<Map<String, Object>> data = db2Dao.select(jbps.getSelect(),
				paramList);
		int successRecords = 0;
		for (Map<String, Object> columnValueMap : data) {
			int status = 0;
			try {
				if (columnValueMap.containsKey("TRANSMIT_STATUS")) {
					columnValueMap.put("TRANSMIT_STATUS", "Y");
				}
				// update destination_db.
				status = updateDestinationDB(columnValueMap, jbps, paramList);
				// update source_db.
				jbps.setStatus(status);
				successRecords = updateSourceDB(columnValueMap, jbps,
						paramList, successRecords);
			} catch (Exception ex) {
				logger.error("Problem in sync the reocord=" + columnValueMap);
				logger.error(ex);
			}
			jbps.setStatus(status);
		}
		jbps.setTotalRecords(data.size());
		jbps.setSuccessRecords(successRecords);
	}

	private void process5QueriesWithTime(JobProperties jbps)
			throws ConfigurationException {
		PropertiesConfiguration siteInboxFile = new PropertiesConfiguration(
				jbps.getStartTimeFile());
		String startTime = siteInboxFile.getProperty("startTime").toString();
		logger.info("startTime=" + siteInboxFile.getProperty("startTime"));
		Calendar toDate = new GregorianCalendar();
		Date trialTime2 = new Date();
		toDate.setTime(trialTime2);

		Calendar fromDate = new GregorianCalendar();
		Date trialTime1 = null;
		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd-HH.mm.ss.SSSSSS");
		if (!StringUtils.isBlank(startTime)) {
			trialTime1 = formatter.parse(startTime, new ParsePosition(0));
			fromDate.setTime(trialTime1);
			Calendar nextDay = (Calendar) fromDate.clone();
			Long totalRecords = 0L;
			Long successRecords = 0L;
			while (fromDate.before(toDate)) {
				Date date = fromDate.getTime();
				String dateString = formatter.format(date);
				nextDay.add(Calendar.MINUTE, 30);
				String tomString = formatter.format(nextDay.getTime());
				// logger.info("====From=" + dateString + "    to=" +
				// tomString);
				List<Object> paramList = new ArrayList<Object>();
				// logger.info(sqlSelect);
				paramList.add(dateString);
				paramList.add(tomString);
				// for sub process.
				if (!StringUtils.isBlank(jbps.getProcessName())) {
					if (jbps.getProcessName().equalsIgnoreCase(
							Constants.USERAD_RCRTRESUME_ADS.getValue())) {
						paramList.add("2711");
					} else if (jbps.getProcessName().equalsIgnoreCase(
							Constants.USERAD_RCRTJOB_ADS.getValue())) {
						paramList.add("2710");
					}
					paramList.add("N");
					process5or4QCommonCodeWithSubProcess(paramList, jbps);
				} else {
					paramList.add("N");
					process5or4QCommonCode(paramList, jbps);
				}
				totalRecords = totalRecords + jbps.getTotalRecords();
				successRecords = successRecords + jbps.getSuccessRecords();
				fromDate.add(Calendar.MINUTE, 30);
			}
			logger.info("Total Records Found=" + totalRecords);
			logger.info("Total Success Records=" + successRecords);
			toDate.add(Calendar.MINUTE, -40);
			siteInboxFile.setProperty("startTime",
					formatter.format(toDate.getTime()));
			siteInboxFile.save();
			logger.info("End Time=" + formatter.format(toDate.getTime()));
		} else {
			logger.error("Dates mentioned are not valid...... Please check the property file:=="
					+ startTime);
			logger.info("Example of property file is:startTime=2007-01-22-09.51.09.000615");
		}
	}

	private void process5QueriesWithoutTime(JobProperties jbps) {
		List<Object> paramList = new ArrayList<Object>();
		paramList.add("N");
		process5or4QCommonCode(paramList, jbps);
		logger.info("Total Records Found=" + jbps.getTotalRecords());
		logger.info("Total Success Records=" + jbps.getSuccessRecords());
	}

	public void prepareSqlQueries(JobProperties jbps) {
		Properties sql = PropertyReaderUtil.getProperties(Constants.SQL_TAB
				.getValue());
		jbps.setSelect(sql.getProperty(jbps.getTableName() + "_select"));
		jbps.setSelect1(sql.getProperty(jbps.getTableName() + "_select_1"));
		jbps.setUpdate(sql.getProperty(jbps.getTableName() + "_update"));
		jbps.setUpdate1(sql.getProperty(jbps.getTableName() + "_update_1"));
		jbps.setInsert(sql.getProperty(jbps.getTableName() + "_insert"));
	}

	private int updateDestinationDB(Map<String, Object> columnValueMap,
			JobProperties jbps, List<Object> paramList)
			throws NotFoundException, JSQLParserException {
		int status = 0;
		List<String> sqlSelect1Columns = SqlParserUtil
				.getWhereColumnsForSelect(jbps.getSelect1());
		prepareColumnsVlaueList(columnValueMap, sqlSelect1Columns, paramList);
		// check sqlSelect1 exist in destination_db.
		DataSourceContextHolder.setDataSource(jbps.getDestination());
		boolean isExist = db2Dao.isExist(jbps.getSelect1(), paramList);
		// update or insert destination_db.
		if (isExist) {
			List<String> sqlUpdate1Columns = SqlParserUtil
					.getColumnsForUpdate(jbps.getUpdate1());
			prepareColumnsVlaueList(columnValueMap, sqlUpdate1Columns,
					paramList);
			status = db2Dao.updateDB(jbps.getUpdate1(), paramList);
			// logger.info("===========Updated Status=" + status);
		}
		// In 4th queries there in no insert statement.else
		// if (!StringUtils.isBlank(sqlInsert))
		else if (!StringUtils.isBlank(jbps.getInsert())) {
			List<String> sqlInsertColumns = SqlParserUtil
					.getColumnsForInsert(jbps.getInsert());
			prepareColumnsVlaueList(columnValueMap, sqlInsertColumns, paramList);
			status = db2Dao.updateDB(jbps.getInsert(), paramList);
			// logger.info("==========Inserted Status=" + status);
		}
		return status;
	}

	private int updateSourceDB(Map<String, Object> columnValueMap,
			JobProperties jbps, List<Object> paramList, int successRecords)
			throws JSQLParserException, NotFoundException {
		// update source_db.
		if (jbps.getStatus() > 0) {
			DataSourceContextHolder.setDataSource(jbps.getSource());
			List<String> sqlUpdateColumns = SqlParserUtil
					.getColumnsForUpdate(jbps.getUpdate());
			prepareColumnsVlaueList(columnValueMap, sqlUpdateColumns, paramList);

			db2Dao.updateDB(jbps.getUpdate(), paramList);
			successRecords++;
		} else {
			logger.error("Problem in updating the second DB server reocord="
					+ columnValueMap);
		}
		return successRecords;
	}
}
