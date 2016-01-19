package com.tj.batch.spring.util;

public enum Constants {
	SITE_INBOX_DATE_FILE("SiteInboxBatchStartTime.properties"),

	SQL_TAB("SqlTab.properties"),

	USERAD_RCRTRESUME_ADS("UserAd_RcrtResume_Ads"),

	USERAD_RCRTJOB_ADS("UserAd_RcrtJob_Ads");

	private final String value;

	Constants(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
