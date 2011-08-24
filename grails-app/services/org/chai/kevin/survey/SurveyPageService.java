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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.chai.kevin.survey.validation.SurveyEnteredSection.SectionStatus;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.springframework.transaction.annotation.Transactional;

public class SurveyPageService {
	
	private static Log log = LogFactory.getLog(SurveyPageService.class);
	
	private SurveyElementService surveyElementService;
	private OrganisationService organisationService;
	
	@Transactional(readOnly = true)
	public SurveyPage getSurveyPage(Organisation currentOrganisation, Survey survey) {
		organisationService.loadGroup(currentOrganisation);
		
		return new SurveyPage(currentOrganisation, survey, null, null, 
				new HashMap<SurveyElement, SurveyEnteredValue>(), new HashMap<SurveySection, SurveyEnteredSection>(), 
				getSurveyEnteredObjectiveMap(survey.getObjectives(currentOrganisation.getOrganisationUnitGroup()), currentOrganisation), new HashMap<Long, SurveyElementValue>());
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
		List<SurveyElement> surveyElements = new ArrayList<SurveyElement>();
		if (currentSection != null) surveyElements.addAll(currentSection.getSurveyElements(currentOrganisation.getOrganisationUnitGroup()));
		else if (currentObjective != null) {
			for (SurveySection surveySection : currentObjective.getSections(currentOrganisation.getOrganisationUnitGroup())) {
				surveyElements.addAll(surveySection.getSurveyElements(currentOrganisation.getOrganisationUnitGroup()));
			}
		}
		List<SurveySection> sections = new ArrayList<SurveySection>();
		if (currentObjective != null) sections.addAll(currentObjective.getSections(currentOrganisation.getOrganisationUnitGroup()));
		
		return new SurveyPage(currentOrganisation, survey, currentObjective, currentSection, 
				getSurveyEnteredValueMap(surveyElements, currentOrganisation),
				getSurveyEnteredSectionMap(sections, currentOrganisation),
				getSurveyEnteredObjectiveMap(survey.getObjectives(currentOrganisation.getOrganisationUnitGroup()), currentOrganisation),
				getSurveyElementValueMap(currentOrganisation, elements));
	}
	
	private Map<SurveyElement, SurveyEnteredValue> getSurveyEnteredValueMap(List<SurveyElement> elements, Organisation organisation) {
		Map<SurveyElement, SurveyEnteredValue> map = new HashMap<SurveyElement, SurveyEnteredValue>();
		for (SurveyElement element : elements) {
			SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(element, organisation.getOrganisationUnit());
			if (enteredValue == null) {
				enteredValue = new SurveyEnteredValue(element, organisation.getOrganisationUnit(), null);
			}
			map.put(element, enteredValue);
		}
		return map;
	}
	
	private Map<SurveyObjective, SurveyEnteredObjective> getSurveyEnteredObjectiveMap(List<SurveyObjective> objectives, Organisation organisation) {
		Map<SurveyObjective, SurveyEnteredObjective> result = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		for (SurveyObjective surveyObjective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
			if (enteredObjective == null) {
				enteredObjective = new SurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit(), ObjectiveStatus.INCOMPLETE);
			}
			result.put(surveyObjective, enteredObjective);
		}
		return result;
	}
	
	private Map<SurveySection, SurveyEnteredSection> getSurveyEnteredSectionMap(List<SurveySection> sections, Organisation organisation) {
		Map<SurveySection, SurveyEnteredSection> result = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveySection surveySection : sections) {
			SurveyEnteredSection enteredSection = surveyElementService.getSurveyEnteredSection(surveySection, organisation.getOrganisationUnit());
			if (enteredSection == null) {
				enteredSection = new SurveyEnteredSection(surveySection, organisation.getOrganisationUnit(), SectionStatus.INCOMPLETE);
			}
			result.put(surveySection, enteredSection);
		}
		return result;
	}
	
	private Map<Long, SurveyElementValue> getSurveyElementValueMap(Organisation currentOrganisation, List<SurveyElement> surveyElements) {
		Map<Long, SurveyElementValue> map = new HashMap<Long, SurveyElementValue>();
		for (SurveyElement surveyElement : surveyElements) {
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
