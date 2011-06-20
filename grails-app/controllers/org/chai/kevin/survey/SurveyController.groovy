package org.chai.kevin.survey

import java.util.List;
import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;
import org.chai.kevin.survey.SurveySectionService;
import org.chai.kevin.survey.SurveyService;
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class SurveyController extends AbstractReportController {
	SurveyService surveyService;	
		def index = {
			redirect (action: 'view', params: params)
		}
		
		def view = {

			if (log.isDebugEnabled()) log.debug("survey.view, params:"+params)
			
			Period currentPeriod = getPeriod()
			Organisation currentOrganisation = getOrganisation(true)
		    SurveySubSection currentSubSection = getCurrentSubSection()
						
			def surveyPage = surveyService.getSurvey(currentPeriod,currentOrganisation,currentSubSection)
			
			if (log.isDebugEnabled()) log.debug('survey: '+surveyPage)
			
			Integer organisationLevel = ConfigurationHolder.config.facility.level;
			def allSections = SurveySection.list();
			//Sorting sections and corresponding sub-sections
			Collections.sort(allSections,new SurveySectionSorter());
			for (SurveySection section : allSections) {
				Collections.sort(section.getSubSections(),new SurveySubSectionSorter());
			}
			
			[
				periods: Period.list(),
				surveyPage: surveyPage,
				sections: allSections,
				organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1)
			]
			
		}

}