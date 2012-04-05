package org.chai.kevin.entity.export;

public class EntityExportDataHeader implements Comparable<EntityExportDataHeader> {

	private String name;
	private int order;	
	
	public EntityExportDataHeader(String name, int order) {
		this.name = name;
		this.order = order;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	
	@Override
	public int compareTo(EntityExportDataHeader o) {
		int order0 = order;
		int order1 = o.getOrder();
		
		if (order0 > order1) return 1;
		else if (order1 > order0) return -1;
		return 0;
	}
}
