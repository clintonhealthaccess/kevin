import grails.util.GrailsUtil;

import org.chai.kevin.TimestampListener;
import org.chai.kevin.ValueService;
import org.chai.kevin.ExpressionService;
import org.chai.kevin.InfoService;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.cost.CostTableService;
import org.chai.kevin.dashboard.DashboardController;
import org.chai.kevin.dashboard.DashboardService;
import org.chai.kevin.dashboard.ExplanationCalculator;
import org.chai.kevin.dashboard.PercentageCalculator;
import org.chai.kevin.dsr.DsrService;
import org.chai.kevin.maps.MapsService;
import org.springframework.format.number.PercentFormatter;

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.codehaus.groovy.grails.orm.hibernate.HibernateEventListeners;
def config = CH.config

String facilityTypeGroup = config.facility.type.group
Set<Integer> dashboardSkipLevels = config.dashboard.skip.levels
Set<Integer> costSkipLevels = config.dashboard.skip.levels
int organisationLevel = config.facility.level

beans = {
	dsrService(DsrService){
		organisationService = ref("organisationService")
	}
	
	mapsService(MapsService) {
		organisationService = ref("organisationService")
	}
	
	costTableService(CostTableService) {
		costService = ref("costService")
		organisationService = ref("organisationService")
		skipLevels = costSkipLevels
	}
	
	valueService(ValueService) {
		sessionFactory = ref("sessionFactory")
	}
	
	expressionService(ExpressionService) {
		dataService = ref("dataService")
		organisationService = ref("organisationService")
		valueService = ref("valueService")
	}
	
	infoService(InfoService) {
		expressionService = ref("expressionService")
		valueService = ref("valueService")
	}
	
	dashboardService(DashboardService) {
		dashboardObjectiveService = ref("dashboardObjectiveService")
		organisationService = ref("organisationService")
		infoService = ref("infoService")
		expressionService = ref("expressionService")
		valueService = ref("valueService")
		periodService = ref("periodService")
		skipLevels = dashboardSkipLevels
	}
	
	organisationService(OrganisationService) {
		group = facilityTypeGroup
		organisationUnitService = ref("organisationUnitService")
		organisationUnitGroupService = ref("organisationUnitGroupService")
		facilityLevel = organisationLevel
	}
	
//	timestampListener(TimestampListener)
//	
//	hibernateEventListeners(HibernateEventListeners) {
//		listenerMap = [	'pre-insert': timestampListener,
//						'pre-update': timestampListener]
//	}
	
//	beans = {
//		customPropertyEditorRegistrar(TranslationPropertyEditorRegistrar)
//	}
}