package org.chai.kevin.table;

import java.util.ArrayList;
import java.util.List;

public class AggregateLine extends TableLine {

	public AggregateLine(String headerValue, List<Line> lines) {
		super(headerValue, lines);
	}
	
	public AggregateLine(String headerValue, List<Line> lines, List<String> cssClasses) {
		super(headerValue, lines, cssClasses);
	}

	@Override
	protected List<String> getNewGroups() {
		return new ArrayList<String>();
	}
	
	@Override
	protected List<String> getGroups() {
		List<String> groupsInName = splitName();
		if (groupsInName == null) return null;
		groupsInName.remove(groupsInName.size() - 1);
		return groupsInName;
	}
	
	@Override
	protected List<String> getLineGroups(Line line) {
		return line.getGroups();
	}
	
	@Override
	public String getTemplate() {
		return "tableLine";
	}

	@Override
	public String toString() {
		return "AggregateLine [getHeaderValue()=" + getHeaderValue() + "]";
	}
	
}
