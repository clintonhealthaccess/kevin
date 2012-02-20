import org.chai.kevin.ExpressionService
import org.chai.kevin.InfoService
import org.chai.kevin.JaqlService
import org.chai.kevin.RefreshValueService
import org.chai.kevin.chart.ChartService
import org.chai.kevin.cost.CostTableService
import org.chai.kevin.dashboard.DashboardService
import org.chai.kevin.maps.MapsService
import org.chai.kevin.JaqlService
import org.chai.kevin.LocationService
import org.chai.kevin.chart.ChartService
import org.chai.kevin.cost.CostTableService
import org.chai.kevin.dashboard.DashboardPercentageService;
import org.chai.kevin.dashboard.DashboardService
import org.chai.kevin.data.InfoService;
import org.chai.kevin.planning.PlanningService;
import org.chai.kevin.reports.ReportService
import org.chai.kevin.dsr.DsrService
import org.chai.kevin.export.ExportDataElementService;
import org.chai.kevin.fct.FctService
import org.chai.kevin.importer.ImporterService;
import org.chai.kevin.maps.MapsService
import org.chai.kevin.survey.SurveyCopyService
import org.chai.kevin.survey.SurveyExportService
import org.chai.kevin.survey.SurveyPageService
import org.chai.kevin.survey.SurveyValidationService
import org.chai.kevin.survey.summary.SummaryService;
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.RefreshValueService;
import org.chai.kevin.value.ValidationService;
import org.chai.kevin.value.ValueService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean
/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

def config = CH.config

String facilityTypeGroup = config.facility.type.group
Set<String> dashboardSkipLevels = config.dashboard.skip.levels
Set<String> costSkipLevels = config.cost.skip.levels
String dsrGroupLevel= config.dsr.group.level
Set<String> exportSkipLevels = config.export.skip.levels

beans = {
	
//	exportDataElementService(ExportDataElementService){
//		dataElementService=ref("dataElementService")
//		locationService = ref("locationService")
//		valueService = ref("valueService")
//		infoService = ref("infoService")
//		facilityLevel = locationLevel
//	}
	
	importerService(ImporterService){
//		locationService = ref("locationService")
	}

	validationService(ValidationService){
		jaqlService = ref("jaqlService")
	}
	
	surveyCopyService(SurveyCopyService) {
		sessionFactory = ref("sessionFactory")
		languageService = ref("languageService")
	}
	
	jaqlService(JaqlService) { bean ->
		bean.singleton = true
		grailsApplication = ref("grailsApplication")
	}
	
	refreshValueService(RefreshValueService) {
		expressionService = ref("expressionService")
		valueService = ref("valueService")
		sessionFactory = ref("sessionFactory")
		dataService = ref("dataService")
		grailsApplication = ref("grailsApplication")
	}
	
	surveyValidationService(SurveyValidationService){
		validationService = ref("validationService")
	}
	
	surveyPageService(SurveyPageService){
		surveyValueService = ref("surveyValueService")
		surveyService = ref("surveyService")
		locationService = ref("locationService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		surveyValidationService = ref("surveyValidationService")
		sessionFactory = ref("sessionFactory")
		grailsApplication = ref("grailsApplication")
	}
	
	summaryService(SummaryService){
		locationService = ref("locationService")
		surveyValueService = ref("surveyValueService")
	}

	surveyExportService(SurveyExportService){
		locationService = ref("locationService")
		surveyValueService = ref("surveyValueService")
		languageService = ref("languageService")
		sessionFactory = ref("sessionFactory")
		skipLevels = exportSkipLevels
	}
	
	chartService(ChartService){
		valueService = ref("valueService")
		periodService = ref("periodService")
	}

	reportService(ReportService){
		dataService = ref("dataService")
		languageService = ref("languageService")
		locationService = ref("locationService")
		valueService = ref("valueService")		
		sessionFactory = ref("sessionFactory")		
	}
	
	dsrService(DsrService){		
		reportService = ref("reportService")
		locationService = ref("locationService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		languageService = ref("languageService")
	}
	
	fctService(FctService){
		reportService = ref("reportService")
		locationService = ref("locationService")
		valueService = ref("valueService")
	}
	
	mapsService(MapsService) {
		locationService = ref("locationService")
		valueService = ref("valueService")
		infoService = ref("infoService")
	}

	costTableService(CostTableService) {
		reportService = ref("reportService")
		costService = ref("costService")
		locationService = ref("locationService")
		valueService = ref("valueService")
		skipLevels = costSkipLevels
	}
	
	valueService(ValueService) {
		sessionFactory = ref("sessionFactory")
		dataService = ref("dataService")
	}
	
	expressionService(ExpressionService) {
		dataService = ref("dataService")
		locationService = ref("locationService")
		valueService = ref("valueService")
		jaqlService = ref("jaqlService")
	}
	
	infoService(InfoService) {
		expressionService = ref("expressionService")
		valueService = ref("valueService")
		locationService = ref("locationService")
	}

	dashboardService(DashboardService) {
		reportService = ref("reportService")
		locationService = ref("locationService")
		sessionFactory = ref("sessionFactory")
		dashboardPercentageService = ref("dashboardPercentageService")
		skipLevels = dashboardSkipLevels				
	}
	
	dashboardPercentageService(DashboardPercentageService) {
//		infoService = ref("infoService")
		valueService = ref("valueService")
		dashboardService = ref("dashboardService")
	}

	planningService(PlanningService) {
		valueService = ref("valueService")
		dataService = ref("dataService")
		locationService = ref("locationService")
		sessionFactory = ref("sessionFactory")
		refreshValueService = ref("refreshValueService")
	}
	
	// override the spring cache manager to use the same as hibernate
	springcacheCacheManager(EhCacheManagerFactoryBean) {
		shared = true
		cacheManagerName = "Springcache Plugin Cache Manager"
	}
	
}