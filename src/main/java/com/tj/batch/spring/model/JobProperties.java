package com.tj.batch.spring.model;

public class JobProperties {
	private String startTimeFile;
	private String tableName;
	private String source;
	private String destination;

	private String select;
	private String update;
	private String select1;
	private String update1;
	private String insert;

	private int totalRecords = 0;

	private int successRecords = 0;

	private int status = 0;

	private String processName;

	private String subProcess;


	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getSuccessRecords() {
		return successRecords;
	}

	public void setSuccessRecords(int successRecords) {
		this.successRecords = successRecords;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStartTimeFile() {
		return startTimeFile;
	}

	public void setStartTimeFile(String startTimeFile) {
		this.startTimeFile = startTimeFile;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getSelect() {
		return select;
	}

	public void setSelect(String select) {
		this.select = select;
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getSelect1() {
		return select1;
	}

	public void setSelect1(String select1) {
		this.select1 = select1;
	}

	public String getUpdate1() {
		return update1;
	}

	public void setUpdate1(String update1) {
		this.update1 = update1;
	}

	public String getInsert() {
		return insert;
	}

	public void setInsert(String insert) {
		this.insert = insert;
	}

	public String getSubProcess() {
		return subProcess;
	}

	public void setSubProcess(String subProcess) {
		this.subProcess = subProcess;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

}
