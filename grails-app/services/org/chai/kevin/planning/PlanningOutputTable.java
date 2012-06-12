package org.chai.kevin.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.data.Type;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.StoredValue;
import org.chai.kevin.value.Value;

public class PlanningOutputTable {

	private PlanningOutput planningOutput;
	private StoredValue value;
	private Map<PlanningOutputColumn, NormalizedDataElementValue> columns;
	
	public PlanningOutputTable(PlanningOutput planningOutput, StoredValue value, Map<PlanningOutputColumn, NormalizedDataElementValue> columns) {
		this.planningOutput = planningOutput;
		this.value = value;
		this.columns = columns;
	}

	public Type getHeaderType() {
		return planningOutput.getDataElement().getType().getType(planningOutput.getFixedHeader());
	}
	
	public List<Value> getRows() {
		List<Value> values = new ArrayList<Value>();
		if (value != null && !value.getValue().isNull()) {
			for (int i = 0; i < value.getValue().getListValue().size(); i++) {
				values.add(planningOutput.getDataElement().getType().getValue(value.getValue(), PlanningUtils.getPrefix(planningOutput.getFixedHeader(), i)));
			}
		}
		return values;
	}
	
	public Type getValueType(PlanningOutputColumn column) {
		return column.getNormalizedDataElement().getType().getListType();
	}
	
	public Value getValue(int row, PlanningOutputColumn column) {
		if (columns.get(column) == null || columns.get(column).getValue().isNull()) return null;
		return columns.get(column).getValue().getListValue().get(row);
	}
	
}
