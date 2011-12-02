package org.chai.kevin.reports;

public class ReportValue {

	private String value;	

	public ReportValue(String value) {
		this.setValue(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
