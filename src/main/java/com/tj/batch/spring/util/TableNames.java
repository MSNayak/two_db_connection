package com.tj.batch.spring.util;

public enum TableNames {

	SITE_INBOX_RESPONSE("site_inbox_response"),

	CANDIDATE_FILTER_RESPONSE("candidate_filter_response"),

	RCRT_RESUME_ADS("rcrt_resume_ads"),

	RCRT_JOB_ADS("rcrt_job_ads"),

	RCRT_JOB_EXTENSION("rcrt_job_extension");

	private final String tableName;

	TableNames(String tableName) {
		this.tableName = tableName;
	}

	public String getValue() {
		return this.tableName;
	}
}
