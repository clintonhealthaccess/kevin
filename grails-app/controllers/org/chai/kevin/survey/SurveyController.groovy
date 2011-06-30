/** 
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
package org.chai.kevin.survey;
/**
 * @author JeanKahigiso
 *
 */

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
		    SurveySubStrategicObjective currentSubObjective = getCurrentSubObjective()
						
			def surveyPage = surveyService.getSurvey(currentPeriod,currentOrganisation,currentSubObjective)
			
			if (log.isDebugEnabled()) log.debug('survey: '+surveyPage)
			
			Integer organisationLevel = ConfigurationHolder.config.facility.level;
			def allObjectives = SurveyStrategicObjective.list();
			//Sorting sections and corresponding sub-sections
			Collections.sort(allObjectives,new SurveyStrategicObjectiveSorter());
			for (SurveyStrategicObjective objective : allObjectives) {
				Collections.sort(objective.getSubObjectives(),new SurveySubStrategicObjectiveSorter());
			}
			
			[
				periods: Period.list(),
				surveyPage: surveyPage,
				objectives: allObjectives,
				organisationTree: organisationService.getOrganisationTreeUntilLevel(organisationLevel.intValue()-1)
			]
			
		}
		def admin ={
			
			if (log.isDebugEnabled()) log.debug("admin.view, params:"+params)
			

//						
//			def surveyAdminPage = surveyAdminService.getAdminSurvey()
//			
//			if (log.isDebugEnabled()) log.debug('admin: '+surveyAdminPage)
//
//			def allObjectives = SurveyStrategicObjective.list();
//			//Sorting sections and corresponding sub-sections
//			Collections.sort(allObjectives,new SurveyStrategicObjectiveSorter());
//			for (SurveyStrategicObjective objective : allObjectives) {
//				Collections.sort(objective.getSubObjectives(),new SurveySubStrategicObjectiveSorter());
//			}
//			
//			[
//				surveyAdminPage: surveyAdminPage,
//			]
//			
			
		}

}