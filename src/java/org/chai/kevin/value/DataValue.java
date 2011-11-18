package org.chai.kevin.value;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

public interface DataValue {

	public abstract OrganisationUnit getOrganisationUnit();

	public abstract Period getPeriod();

	public abstract Value getValue();

}