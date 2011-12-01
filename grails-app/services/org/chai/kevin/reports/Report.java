package org.chai.kevin.reports;

public class Report {
	
	private String value;	

	public Report(String value) {
		this.setValue(value);
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
