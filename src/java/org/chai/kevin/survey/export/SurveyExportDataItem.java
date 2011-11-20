package org.chai.kevin.survey.export;

import java.util.HashMap;

public class SurveyExportDataItem extends HashMap<String, Object> implements Comparable<SurveyExportDataItem> {

	private int order;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6010354007810833128L;
	
	public SurveyExportDataItem(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public int compareTo(SurveyExportDataItem o) {
		int order0 = order;
		int order1 = o.getOrder();
		
		if (order0 > order1) return 1;
		else if (order1 > order0) return -1;
		return 0;
	}
}
