import grails.util.GrailsUtil;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.cost.CostTableService;
import org.chai.kevin.dashboard.DashboardController;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.ExplanationCalculator;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.hisp.dhis.dataelement.ConstantService;

def defaultSkipLevels;
def defaultOrganisationlevel;

switch(GrailsUtil.environment) {
	case "production":
		defaultSkipLevels = [4]
		defaultOrganisationlevel = 5
	break
	case "development":
	case "test":
		defaultSkipLevels = []
		defaultOrganisationlevel = 4
	break
}

beans = {
	costTableService(CostTableService) {
		costService = ref("costService")
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		organisationLevel = defaultOrganisationlevel
	}
	
	expressionService(ExpressionService) {
		constantService = ref("constantService")
		dataElementService = ref("dataElementService")
		aggregationService = ref("aggregationService")
		dataElementCategoryService = ref("dataElementCategoryService")
		dhisExpressionService = ref("dhisExpressionService")
	}
	
	dashboardService(DashboardService) {
		dashboardObjectiveService = ref("dashboardObjectiveService")
		organisationService = ref("organisationService")
		percentageService = ref("percentageService")
		expressionService = ref("expressionService")
		organisationUnitService = ref("organisationUnitService")
		periodService = ref("periodService")
	}
	
	organisationService(OrganisationService) {
		organisationUnitService = ref("organisationUnitService")
		organisationUnitGroupService = ref("organisationUnitGroupService")
		skipLevels = defaultSkipLevels
		groups = ['Type']
	}
}