package org.chai.kevin;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.math.NumberUtils;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.ExpressionValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;

public class ExpressionInfo extends Info {

	private ExpressionValue expressionValue;
	private Map<Organisation, Map<DataElement, DataValue>> values;
	
	public ExpressionInfo(ExpressionValue expressionValue, Map<Organisation, Map<DataElement, DataValue>> values) {
		this.expressionValue = expressionValue;
		this.values = values;
	}

	public ExpressionValue getExpressionValue() {
		return expressionValue;
	}
	
	public Expression getExpression() {
		return expressionValue.getExpression();
	}
	
	public OrganisationUnit getOrganisation() {
		return expressionValue.getOrganisationUnit();
	}
	
	public Map<Organisation, Map<DataElement, DataValue>> getValues() {
		return values;
	}
	
	public Map<DataElement, DataValue> getValuesForOrganisation() {
		for (Entry<Organisation, Map<DataElement, DataValue>> entry : values.entrySet()) {
			if (entry.getKey().getOrganisationUnit().equals(expressionValue.getOrganisationUnit())) return entry.getValue();
		}
		return null;
	}
	
	public String getValue() {
		if (expressionValue.getValue() == null) return null;
		return String.valueOf(expressionValue.getValue());
	}

	@Override
	public String getTemplate() {
		return "/info/expressionInfo";
	}

	
}
