package com.aimsio;

import java.util.Date;

public class Signal {
	
	private String assetUN;
	
	private String status;
	
	private Date entryDate;

	public String getAssetUN() {
		return assetUN;
	}

	public void setAssetUN(String assetUN) {
		this.assetUN = assetUN;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

}
