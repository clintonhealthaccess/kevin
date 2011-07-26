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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.period.Period;

/**
 * Holds the state of a whole objective.
 * 
 * @author fterrier
 */
public class SurveyPage {
	
	private final static Log log = LogFactory.getLog(SurveyPage.class);
	
	public static enum SectionStatus{CLOSED, UNAVAILABLE, COMPLETE, INVALID, INCOMPLETE} 
	
	// we need a map for data binding to work
	private Map<Long, SurveyElementValue> surveyElements;

	private Organisation organisation;
	private SurveyObjective objective;
	private SurveySection currentSection;
	
	private Map<SurveyObjective, SurveyEnteredObjective> enteredObjectives = new HashMap<SurveyObjective, SurveyEnteredObjective>();
	
	public SurveyPage(){}

	public SurveyPage(Organisation organisation, 
			SurveyObjective objective, SurveySection currentSection, 
//			Map<SurveyObjective, SurveyEnteredObjective> enteredObjectives, 
			Map<Long, SurveyElementValue> surveyElements) {
		this.organisation = organisation;
		this.objective = objective;
		this.currentSection = currentSection;
//		this.enteredObjectives = enteredObjectives;
		this.surveyElements = surveyElements;
	}

	// DATA-BINDING START
	// survey elements is filled during data binding
	public Map<Long, SurveyElementValue> getSurveyElements() {
		return surveyElements;
	}
	public void setSurveyElements(Map<Long, SurveyElementValue> surveyElements) {
		this.surveyElements = surveyElements;
	}
	// DATA-BINDING END
	
	public SurveyElementValue getSurveyElementValue(Long id) {
		return surveyElements.get(id);
	}
	
	public Period getPeriod() {
		return getSurvey().getPeriod();
	}
	
	public SurveyObjective getObjective() {
		return objective;
	}

	public Survey getSurvey() {
		return objective.getSurvey();
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public SurveySection getSection() {
		return currentSection;
	}
	
//	public SurveyEnteredObjective getEnteredObjective(SurveyObjective objective) {
//		return enteredObjectives.get(objective);
//	}
	
	public List<SurveySection> getIncompleteSections() {
		List<SurveySection> result = new ArrayList<SurveySection>();
		section: for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyElement element : section.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				if (surveyElements.get(element.getId()).getSurveyEnteredValue() == null) { 
					result.add(section);
					continue section;
				}
			}
		}
		
		return result;
	}
	
	public Map<SurveySection, List<SurveyQuestion>> getInvalidQuestions() {
		Map<SurveySection, List<SurveyQuestion>> result = new LinkedHashMap<SurveySection, List<SurveyQuestion>>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			List<SurveyQuestion> invalidQuestions = new ArrayList<SurveyQuestion>();
			question: for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
					if (!surveyElements.get(element.getId()).isValid()) { 
						invalidQuestions.add(question);
						continue question;
					}
				}
			}
			if (!invalidQuestions.isEmpty()) result.put(section, invalidQuestions);
		}
		return result;
	}
	
	public boolean canSubmit() {
		return getIncompleteSections().isEmpty() && getInvalidQuestions().isEmpty();
	}
	
	public boolean isValid(SurveyQuestion question) {
		for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
			SurveyElementValue value = surveyElements.get(element.getId());
			if (!value.isValid()) return false;
		}
		return true;
	}
	
	public SectionStatus getStatus(SurveySection section) {
		SurveyEnteredObjective enteredObjective = enteredObjectives.get(section.getObjective());
		if (enteredObjective != null) {
			if (enteredObjective.getStatus() == ObjectiveStatus.CLOSED) return SectionStatus.CLOSED;
			if (enteredObjective.getStatus() == ObjectiveStatus.UNAVAILABLE) return SectionStatus.UNAVAILABLE;
		}
		
		SectionStatus status = SectionStatus.COMPLETE;
		for (SurveyElement element : section.getSurveyElements(organisation.getOrganisationUnitGroup())) {
			SurveyElementValue value = surveyElements.get(element.getId());
			if (!value.isValid()) {
				status = SectionStatus.INVALID;
				break;
			}
			if (value.getSurveyEnteredValue() == null) {
				status = SectionStatus.INCOMPLETE;
			}
		}
		return status;
	}
	
	public ObjectiveStatus getStatus(SurveyObjective objective) {
		return enteredObjectives.get(objective).getStatus();
	}
	
	// FIXME I don't like this, this method is not really readable
	private ObjectiveStatus getStatus(SurveyObjective objective, SurveyElementService surveyElementService) {
		ObjectiveStatus status = null;
		SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(objective, organisation.getOrganisationUnit());
		// we check if it is closed
		if (enteredObjective != null && enteredObjective.getStatus() == ObjectiveStatus.CLOSED) {
			status = ObjectiveStatus.CLOSED;
		}
		// otherwise we check if the dependency is still open
		else {
			if (objective.getDependency() != null) {
				SurveyEnteredObjective dependentEnteredObjective = surveyElementService.getSurveyEnteredObjective(objective.getDependency(), organisation.getOrganisationUnit());
				if (dependentEnteredObjective == null || dependentEnteredObjective.getStatus() != ObjectiveStatus.CLOSED) status = ObjectiveStatus.UNAVAILABLE;
				if (dependentEnteredObjective != null && dependentEnteredObjective.getStatus() == ObjectiveStatus.CLOSED
					&& enteredObjective != null && enteredObjective.getStatus() == ObjectiveStatus.UNAVAILABLE) {
					status = ObjectiveStatus.INCOMPLETE;
				}
			}
		}
		
		// if none of that holds, the status will be defined by the sections
		if (status == null) {
			if (objective.equals(this.objective)) {
				status = ObjectiveStatus.COMPLETE;
				for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
					if (getStatus(section) == SectionStatus.INVALID) {
						status = ObjectiveStatus.INVALID;
						break;
					}
					if (getStatus(section) == SectionStatus.INCOMPLETE) {
						status = ObjectiveStatus.INCOMPLETE;
					}
				}
			}
			else {
				if (enteredObjective != null) status = enteredObjective.getStatus();
				else status = ObjectiveStatus.INCOMPLETE;
			}
		}
		return status;
	}
	
	public void sanitizeValues() {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			surveyElementValue.sanitizeValues(this);	
		}
	}
	
	public void transferValuesAndValidate(ValidationService validationService) {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			surveyElementValue.transferValuesAndValidate(this, validationService);
		}
	}
	
	public void userValidation(ValidationService validationService) {
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			surveyElementValue.userValidation(this, validationService);
		}
	}
	
	public void initializeObjectiveStatus(SurveyElementService surveyElementService) {
		for (SurveyObjective surveyObjective : objective.getSurvey().getObjectives(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
			if (enteredObjective == null) {
				enteredObjective = new SurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
			}
			enteredObjective.setStatus(getStatus(surveyObjective, surveyElementService));
			surveyElementService.save(enteredObjective);
			enteredObjectives.put(surveyObjective, enteredObjective);
		}
	}
	
	public void persistState(SurveyElementService surveyElementService, List<SurveyElement> elements) {
		SurveyEnteredObjective enteredObjective = enteredObjectives.get(objective);
		if (enteredObjective.getStatus() == ObjectiveStatus.CLOSED || enteredObjective.getStatus() == ObjectiveStatus.UNAVAILABLE) 
			throw new IllegalStateException("trying to persist the state of a closed or unavailable objective");
		
		for (SurveyElement surveyElement : elements) {
			SurveyElementValue surveyElementValue = surveyElements.get(surveyElement.getId());
			if (surveyElementValue.getSurveyEnteredValue() != null) surveyElementService.save(surveyElementValue.getSurveyEnteredValue());
		}
		
		ObjectiveStatus status = getStatus(objective, surveyElementService);
		enteredObjective.setStatus(status);
		surveyElementService.save(enteredObjective);

		initializeObjectiveStatus(surveyElementService);
	}
	
	public void submit(SurveyElementService surveyElementService, ValueService valueService) {
		if (log.isDebugEnabled()) log.debug("submit()");
		
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			DataElement dataElement = surveyElementValue.getSurveyElement().getDataElement();
			DataValue value = valueService.getValue(dataElement, organisation.getOrganisationUnit(), getPeriod());
			if (value == null) {
				value = new DataValue(dataElement, organisation.getOrganisationUnit(), getPeriod(), null);
			}
			value.setValue(surveyElementValue.getSurveyEnteredValue().getValue());
			valueService.save(value);
		}
		
		SurveyEnteredObjective enteredObjective = enteredObjectives.get(objective);
		enteredObjective.setStatus(ObjectiveStatus.CLOSED);
		surveyElementService.save(enteredObjective);
		
		initializeObjectiveStatus(surveyElementService);
	}
	
	@Override
	public String toString() {
		return "SurveyPage [period=" + getPeriod() + ", organisation="
				+ organisation + ", section=" + currentSection
				+ ", surveyElements=" + getSurveyElements() + "]";
	}
	
}
