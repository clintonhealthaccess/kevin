
package org.chai.kevin.dashboard;

import org.chai.kevin.Gradient;

public class DashboardPercentage extends Gradient {

	private Double value;

	private Boolean hasMissingValue;
	private Boolean hasMissingExpression;
	
	public enum Status {
		VALID,
		MISSING_EXPRESSION,
		MISSING_VALUE
	}
	
	public DashboardPercentage() {}
	
	public DashboardPercentage(Double value, Boolean hasMissingValueStatus, Boolean hasMissingExpressionStatus) {
		this.value = value;

		this.hasMissingValue = hasMissingValueStatus;
		this.hasMissingExpression = hasMissingExpressionStatus;
	}
	
	public Double getValue() {
		return value;
	}
	
	public Boolean isHasMissingExpression() {
		return hasMissingExpression;
	}
	
	public Boolean isHasMissingValue() {
		return hasMissingValue;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	public void setHasMissingExpression(boolean hasMissingExpressionStatus) {
		this.hasMissingExpression = hasMissingExpressionStatus;
	}
	
	public void setHasMissingValue(boolean hasMissingValueStatus) {
		this.hasMissingValue = hasMissingValueStatus;
	}

}