package org.chai.kevin.table;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

class TableLine extends Line {
	
	protected List<Line> lines;
	private boolean openByDefault = false;
	
	protected TableLine(List<Line> lines) {
		super(null, new ArrayList<String>());
		this.lines = lines;
	}
	
	protected TableLine(String headerValue, List<Line> lines) {
		super(headerValue, new ArrayList<String>());
		this.lines = lines;
	}
	
	protected TableLine(String headerValue, List<Line> lines, List<String> cssClasses) {
		super(headerValue, cssClasses);
		this.lines = lines;
	}

	public List<Line> getLines() {
		List<Line> result = new ArrayList<Line>();
		result.addAll(getNestedTables());
		result.addAll(getSingleLines());
		return result;
	}
	
	public List<Line> getSingleLines() {
		List<Line> result = new ArrayList<Line>();
		for (Line line : lines) {
			List<String> lineGroups = getLineGroups(line);
			if (lineGroups == null || lineGroups.size() == 0) result.add(line);
		}
		return result;
	}
	
	public List<TableLine> getNestedTables() {
		List<TableLine> result = new ArrayList<TableLine>();
		for (Entry<String, List<Line>> entry : getGroupMap().entrySet()) {
			List<String> newGroups = getNewGroups();
			newGroups.add(entry.getKey());
			result.add(new TableLine(StringUtils.join(newGroups, " "+SEPARATOR+" "), entry.getValue(), collectClasses(entry.getValue())));
		}
		return result;
	}

	private List<String> collectClasses(List<Line> lines) {
		Set<String> cssClasses = new HashSet<String>();
		for (Line line : lines) {
			cssClasses.addAll(line.getCssClasses());
		}
		cssClasses.add("table-group");
		return new ArrayList<String>(cssClasses);
	}

	protected List<String> getNewGroups() {
		List<String> newGroups = new ArrayList<String>();
		if (getGroups() != null) newGroups.addAll(getGroups());
		return newGroups;
	}
	
	@Override
	protected List<String> getGroups() {
		List<String> groupsInName = splitName();
		if (groupsInName == null) return null;
		return groupsInName;
	}
	
	private Map<String, List<Line>> getGroupMap() {
		Map<String, List<Line>> result = new LinkedHashMap<String, List<Line>>();
		for (Line line : lines) {
			List<String> lineGroups = getLineGroups(line);
			if (lineGroups != null && lineGroups.size() > 0) {
				if (!result.containsKey(lineGroups.get(0))) {
					result.put(lineGroups.get(0), new ArrayList<Line>());
				}
				result.get(lineGroups.get(0)).add(line);
			}
		}
		return result;
	}

	protected List<String> getLineGroups(Line line) {
		return line.getGroupsWithoutPrefix(getGroups());
	}

	@Override
	public Value getValueForColumn(int i) {
		if (!Type.TYPE_NUMBER().equals(getTypeForColumn(i))) return Value.NULL_INSTANCE();
		Double doubleValue = 0d;
		for (Line line : getLines()) {
			Value value = line.getValueForColumn(i);
			if (value != null && value.getNumberValue() != null) {
				if (doubleValue == null) doubleValue = 0d;
				doubleValue += value.getNumberValue().doubleValue();
			}
		}
		return Value.VALUE_NUMBER(doubleValue);
	}
	
	@Override
	public Type getTypeForColumn(int i) {
		Type type = null;
		for (Line line : getLines()) {
			Type lineType = line.getTypeForColumn(i);
			if (type == null) type = lineType;
			if (type != null && !type.equals(lineType)) return null;
		}
		return type;
	}

	public boolean isOpenByDefault() {
		return openByDefault;
	}
	
	public void setOpenByDefault(boolean openByDefault) {
		this.openByDefault = openByDefault;
	}
	
	@Override
	public String getTemplate() {
		return "tableLine";
	}
	
	@Override
	public String toString() {
		return "TableLine [getHeaderValue()=" + getHeaderValue() + "]";
	}
	
}
