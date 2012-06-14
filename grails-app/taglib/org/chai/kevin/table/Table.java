package org.chai.kevin.table;

import java.util.List;

public class Table {

	private TableLine tableLine;
	private String caption;
	private List<String> columns;
	private boolean displayTotal;
	private List<String> cssClasses;
	
	public Table(String caption, List<String> columns, List<Line> lines, boolean displayTotal, List<String> cssClasses) {
		this.caption = caption;
		this.columns = columns;
		this.displayTotal = displayTotal;
		this.cssClasses = cssClasses;
		this.tableLine = new TableLine(lines);
	}
	
	public String getCaption() {
		return caption;
	}
	
	public List<String> getColumns() {
		return columns;
	}
	
	public TableLine getTableLine() {
		return tableLine; 
	}
	
	public List<String> getCssClasses() {
		return cssClasses;
	}
	
	public boolean isDisplayTotal() {
		return displayTotal;
	}
	
}
