package org.chai.kevin.table;

import java.util.List;

public class Table {

	private TableLine tableLine;
	private List<String> columns;
	private List<String> cssClasses;
	
	public Table(List<String> columns, List<Line> lines, List<String> cssClasses) {
		this.columns = columns;
		this.cssClasses = cssClasses;
		this.tableLine = new TableLine(lines);
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
	
}
