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
import org.chai.kevin.dashboard.DashboardService
import org.chai.kevin.data.InfoService;
import org.chai.kevin.reports.ReportService
import org.chai.kevin.maps.MapsService
import org.chai.kevin.survey.SummaryService
import org.chai.kevin.survey.SurveyCopyService
import org.chai.kevin.survey.SurveyExportService
import org.chai.kevin.survey.SurveyPageService
import org.chai.kevin.survey.ValidationService
import org.chai.kevin.value.ExpressionService;
import org.chai.kevin.value.RefreshValueService;
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

Set<String> dashboardSkipLevels = config.dashboard.skip.levels
Set<String> costSkipLevels = config.dashboard.skip.levels
String dsrGroupLevel= config.dsr.group.level
Set<String> exportSkipLevels = config.export.skip.levels

beans = {
	
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
	
	validationService(ValidationService){
		locationService = ref("locationService")
		surveyValueService = ref("surveyValueService")
		surveyService = ref("surveyService")
		jaqlService = ref("jaqlService")
	}
	
	surveyPageService(SurveyPageService){
		languageService = ref("languageService")
		surveyValueService = ref("surveyValueService")
		surveyService = ref("surveyService")
		locationService = ref("locationService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		validationService = ref("validationService")
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
//		grailsApplication = ref("grailsApplication")
		skipLevels = exportSkipLevels
	}
	
	chartService(ChartService){
		valueService = ref("valueService")
		periodService = ref("periodService")
	}

	reportService(ReportService){
		locationService = ref("locationService")
		valueService = ref("valueService")
		dataService = ref("dataService")
		languageService = ref("languageService")
		groupLevel = dsrGroupLevel
	}
	
	mapsService(MapsService) {
		locationService = ref("locationService")
		valueService = ref("valueService")
		infoService = ref("infoService")
	}

	costTableService(CostTableService) {
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
		infoService = ref("infoService")
		valueService = ref("valueService")
		skipLevels = dashboardSkipLevels
	}

	locationService(LocationService) {
		sessionFactory = ref("sessionFactory")
	}
	
	// override the spring cache manager to use the same as hibernate
	springcacheCacheManager(EhCacheManagerFactoryBean) {
		shared = true
		cacheManagerName = "Springcache Plugin Cache Manager"
	}
	
}