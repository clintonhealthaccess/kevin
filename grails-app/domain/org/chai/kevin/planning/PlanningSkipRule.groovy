package org.chai.kevin.planning;

import org.apache.commons.lang.NotImplementedException
import org.chai.kevin.form.FormCloner
import org.chai.kevin.form.FormSkipRule
import org.chai.kevin.form.FormElement.ElementCalculator
import org.chai.location.DataLocation

class PlanningSkipRule extends FormSkipRule {

	static belongsTo = [planning: Planning]		

	static mapping = {
		table 'dhsst_planning_skip_rule'
		planning column: 'planning'
	}
	
	@Override
	public void deepCopy(FormSkipRule copy, FormCloner surveyCloner) {
		throw new NotImplementedException();
	}
	
	@Override
	public void evaluate(DataLocation dataLocation, ElementCalculator calculator) {
		super.evaluate(dataLocation, calculator);
	}

	public String toString(){
		return "PlanningSkipRule[getId()=" + getId() + ", getExpression()='" + getExpression() + "']";
	}
}
