package org.chai.kevin.entity.export;

import java.util.HashMap;

public class EntityExportDataItem extends HashMap<String, Object> implements Comparable<EntityExportDataItem> {

	private int order;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6010354007810833128L;
	
	public EntityExportDataItem(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public int compareTo(EntityExportDataItem o) {
		int order0 = order;
		int order1 = o.getOrder();
		
		if (order0 > order1) return 1;
		else if (order1 > order0) return -1;
		return 0;
	}
}
