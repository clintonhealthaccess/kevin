package org.chai.kevin.table;

import java.util.ArrayList;
import java.util.List;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.Value;

public class NumberLine extends Line {

	private List<Type> types;
	private List<Value> values;
	
	public NumberLine(String headerValue, List<Value> values, List<Type> types) {
		super(headerValue, new ArrayList<String>());
		this.values = values;
		this.types = types;
	}
	
	public NumberLine(String headerValue, List<Value> values, List<Type> types, List<String> cssClasses) {
		super(headerValue, cssClasses);
		this.values = values;
		this.types = types;
	}

	@Override
	public Value getValueForColumn(int i) {
		return values.get(i);
	}
	
	@Override
	public Type getTypeForColumn(int i) {
		return types.get(i);
	}
	
	@Override
	protected List<String> getGroups() {
		List<String> groupsInName = splitName();
		if (groupsInName == null) return null;
		groupsInName.remove(groupsInName.size() - 1);
		return groupsInName;
	}

	@Override
	public List<Line> getLines() {
		List<Line> result = new ArrayList<Line>();
		result.add(this);
		return result;
	}

	@Override
	public String getTemplate() {
		return "numberLine";
	}

	@Override
	public String toString() {
		return "NumberLine [getHeaderValue()=" + getHeaderValue() + "]";
	}

}
