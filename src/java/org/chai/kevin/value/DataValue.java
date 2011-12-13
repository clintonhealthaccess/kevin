package org.chai.kevin.value;

import org.chai.kevin.location.CalculationEntity;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface DataValue {

	public abstract CalculationEntity getEntity();

	public abstract Period getPeriod();

	public abstract Value getValue();

}