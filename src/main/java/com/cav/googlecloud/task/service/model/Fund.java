package com.cav.googlecloud.task.service.model;

public class Fund {

	private String clientId;
	private String fundId;
	private String fundName;

	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getFundId() {
		return fundId;
	}
	public void setFundId(String fundId) {
		this.fundId = fundId;
	}
	public String getFundName() {
		return fundName;
	}
	public void setFundName(String fundName) {
		this.fundName = fundName;
	}
	@Override
	public String toString() {
		return "Fund [clientId=" + clientId + ", fundId=" + fundId + ", fundName=" + fundName + "]";
	}
}
