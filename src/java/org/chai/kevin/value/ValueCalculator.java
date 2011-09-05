package org.chai.kevin.value;

import org.chai.kevin.data.Data;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface ValueCalculator<T extends Value> {

	public T getValue(Data<T> data, OrganisationUnit organisationUnit, Period period);
	
}
