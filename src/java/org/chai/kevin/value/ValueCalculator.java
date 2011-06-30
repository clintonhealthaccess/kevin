package org.chai.kevin.value;

import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface ValueCalculator {

	public DataValue getValue(DataElement dataElement, OrganisationUnit organisationUnit, Period period);
	public ExpressionValue getValue(Expression expression, OrganisationUnit organisationUnit, Period period);
	public CalculationValue getValue(Calculation calculation, OrganisationUnit organisationUnit, Period period);
	
}
