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
import org.chai.kevin.DataElement;
import org.chai.kevin.Enum;
import org.chai.kevin.EnumOption;
import org.hisp.dhis.dataset.DataSet;
import org.chai.kevin.DataValue;
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
//			Initializer.createUsers()
			Initializer.createRootObjective()
//			Initializer.createIndicatorType()
			
			break;
		case "development":
//			Initializer.createUsers();
			Initializer.createDummyStructure();
			Initializer.createDataElementsAndExpressions();
			Initializer.createDashboard();
			Initializer.createCost();
			Initializer.createDsr();
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
