package org.chai.kevin.data;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.value.StoredValue;
import org.hisp.dhis.period.Period;

@Entity(name="NormalizedDataElement")
@Table(name="dhsst_normalized_data_element")
public class NormalizedDataElement extends Data<StoredValue> {

	private static final long serialVersionUID = 2997759905778290196L;

	// json text example : {"1":{"DH":"$1 + $2"}, "2":{"HC":"$1 + $2 + $3"}}
	private ExpressionMap expressionMap = new ExpressionMap();
	
	@AttributeOverrides({
		@AttributeOverride(name="jsonText", column=@Column(name="expressionMap", nullable=false))
	})
	public ExpressionMap getExpressionMap() {
		return expressionMap;
	}
	
	public void setExpressionMap(ExpressionMap expressionMap) {
		this.expressionMap = expressionMap;
	}
	
	@Transient
	public String getExpression(Period period, String groupUuid) {
		return expressionMap.get(period.getId()+"").get(groupUuid);
	}
	
}
