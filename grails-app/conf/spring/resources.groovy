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

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
def config = CH.config

String facilityTypeGroup = config.facility.type.group
Set<Integer> dashboardSkipLevels = config.dashboard.skip.levels
Set<Integer> costSkipLevels = config.dashboard.skip.levels
Integer facilityLevel = config.facility.level

beans = {
	dsrService(DsrService){
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		organisationLevel = facilityLevel
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
	}
	
	costTableService(CostTableService) {
		costService = ref("costService")
		expressionService = ref("expressionService")
		organisationService = ref("organisationService")
		organisationLevel = facilityLevel
		skipLevels = costSkipLevels
	}
	
	expressionService(ExpressionService) {
		dataService = ref("dataService")
		dataValueService = ref("dataValueService")
		organisationService = ref("organisationService")
		organisationLevel = facilityLevel
	}
	
	dashboardService(DashboardService) {
		dashboardObjectiveService = ref("dashboardObjectiveService")
		organisationService = ref("organisationService")
		percentageService = ref("percentageService")
		expressionService = ref("expressionService")
		organisationUnitService = ref("organisationUnitService")
		periodService = ref("periodService")
		skipLevels = dashboardSkipLevels
	}
	
	organisationService(OrganisationService) {
		group = facilityTypeGroup
		organisationUnitService = ref("organisationUnitService")
		organisationUnitGroupService = ref("organisationUnitGroupService")
	}
	
//	beans = {
//		customPropertyEditorRegistrar(TranslationPropertyEditorRegistrar)
//	}
}