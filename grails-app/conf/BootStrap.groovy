import org.chai.kevin.Initializer;
import org.chai.kevin.cost.CostRampUpYear;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

import java.util.Date;

import grails.util.GrailsUtil;

import org.apache.commons.lang.time.DateUtils;
import org.chai.kevin.cost.CostObjective;
import org.chai.kevin.cost.CostRampUp;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.dashboard.DashboardCalculation;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.Enum;
import org.hisp.dhis.dataelement.EnumOption;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;

class BootStrap {

    def init = { servletContext ->

		switch (GrailsUtil.environment) {
		case "production":
			Initializer.createUsers()
			Initializer.createRootObjective()
//			Initializer.createIndicatorType()
			
			break;
		case "development":
			Initializer.createUsers();
			Initializer.createDummyStructure();
			Initializer.createDataElementsAndExpressions();
			Initializer.createDashboard();
			Initializer.createCost();
			Initializer.createMaps();
			
			break;
		}
		
    }

    def destroy = {
//		switch (GrailsUtil.environment) {
//			case "production":
//				break;
//			case "development":
////				deleteAll();
//				break;
//		}
    }

}
