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
 * @author Jean Kahigiso M.
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.springframework.transaction.annotation.Transactional;

public class SurveyPageService {
	
	private static Log log = LogFactory.getLog(SurveyPageService.class);
	
	private SurveyElementService surveyElementService;
	private OrganisationService organisationService;
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, Survey survey) {
		organisationService.loadGroup(currentOrganisation);
		
		return new SurveyPage(currentOrganisation, survey, null, null, 
				new HashMap<SurveyElement, SurveyEnteredValue>(), new HashMap<Long, SurveyElementValue>());
	}
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, SurveySection currentSection) {
		organisationService.loadGroup(currentOrganisation);
		
		return getSurveyPage(currentOrganisation, currentSection.getObjective().getSurvey(), currentSection.getObjective(), currentSection, currentSection.getSurveyElements(currentOrganisation.getOrganisationUnitGroup()));
	}
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, SurveyObjective currentObjective) {
		organisationService.loadGroup(currentOrganisation);
		List<SurveyElement> surveyElements = new ArrayList<SurveyElement>();
		for (SurveySection surveySection : currentObjective.getSections(currentOrganisation.getOrganisationUnitGroup())) {
			surveyElements.addAll(surveySection.getSurveyElements(currentOrganisation.getOrganisationUnitGroup()));
		}
		return getSurveyPage(currentOrganisation, currentObjective.getSurvey(), currentObjective, null, surveyElements);
	}
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, SurveyObjective currentObjective, List<SurveyElement> surveyElements) {
		if (log.isDebugEnabled()) log.debug("getSurveyPage(organisation="+currentOrganisation+", objective="+currentObjective+", elements="+surveyElements+")");
		organisationService.loadGroup(currentOrganisation);
		
		return getSurveyPage(currentOrganisation, currentObjective.getSurvey(), currentObjective, null, surveyElements);
	}
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, SurveySection currentSection, List<SurveyElement> surveyElements) {
		if (log.isDebugEnabled()) log.debug("getSurveyPage(organisation="+currentOrganisation+", section="+currentSection+", elements="+surveyElements+")");
		organisationService.loadGroup(currentOrganisation);
		
		return getSurveyPage(currentOrganisation, currentSection.getObjective().getSurvey(), currentSection.getObjective(), currentSection, surveyElements);
	}
	
	private SurveyPage getSurveyPage(Organisation currentOrganisation, Survey survey, SurveyObjective currentObjective, SurveySection currentSection, List<SurveyElement> elements) {
		return new SurveyPage(currentOrganisation, survey, currentObjective, currentSection, 
				getSurveyEnteredValueMap(currentObjective, currentOrganisation),
				getSurveyElementValueMap(currentOrganisation, elements));
	}
	
	private Map<SurveyElement, SurveyEnteredValue> getSurveyEnteredValueMap(SurveyObjective objective, Organisation organisation) {
		Map<SurveyElement, SurveyEnteredValue> map = new HashMap<SurveyElement, SurveyEnteredValue>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyElement element : section.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(element, organisation.getOrganisationUnit());
				map.put(element, enteredValue);
			}
		}
		return map;
	}
	
	private Map<Long, SurveyElementValue> getSurveyElementValueMap(Organisation currentOrganisation, List<SurveyElement> surveyElements) {
		Map<Long, SurveyElementValue> map = new HashMap<Long, SurveyElementValue>();
		for (SurveyElement surveyElement : surveyElements) {
			// TODO update surveyEnteredValue from dataValue if necessary, but probably not here
//					SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(surveyElement, currentOrganisation.getOrganisationUnit());
			SurveyElementValue surveyElementValue = new SurveyElementValue(surveyElement, currentOrganisation);
			map.put(surveyElement.getId(), surveyElementValue);
		}
		return map;
	}
	
	public void setSurveyElementService(SurveyElementService surveyElementService) {
		this.surveyElementService = surveyElementService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
}
