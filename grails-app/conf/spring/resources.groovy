import grails.util.GrailsUtil;

import org.chai.kevin.DataValueService;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.binding.TranslationPropertyEditorRegistrar;
import org.chai.kevin.cost.CostTableService;
import org.chai.kevin.dashboard.DashboardController;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.ExplanationCalculator;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.chai.kevin.dashboard.PercentageService;
import org.chai.kevin.dsr.DsrService;
import org.chai.kevin.maps.MapsService;
import org.springframework.format.number.PercentFormatter;

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
	dsrService(DsrService){
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		localeService = ref("localeService")
	}
	
	percentageService(PercentageService) {
		sessionFactory = ref("sessionFactory")
	}
	
	dataValueService(DataValueService) {
		sessionFactory = ref("sessionFactory")
	}
	
	mapsService(MapsService) {
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		organisationLevel = defaultOrganisationlevel
	}
	
	costTableService(CostTableService) {
		costService = ref("costService")
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		organisationLevel = defaultOrganisationlevel
	}
	
	expressionService(ExpressionService) {
		dataService = ref("dataService")
		dataValueService = ref("dataValueService")
		organisationService = ref("organisationService")
		facilityLevel = defaultOrganisationlevel
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
	
	beans = {
		customPropertyEditorRegistrar(TranslationPropertyEditorRegistrar)
	}
}