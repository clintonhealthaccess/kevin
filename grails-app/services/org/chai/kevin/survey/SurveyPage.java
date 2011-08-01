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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.ValueService;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveyValidationRule;
import org.chai.kevin.value.DataValue;
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
	// all entered survey elements displayed on the page
	private Map<Long, SurveyElementValue> surveyElements;
	
	private Organisation organisation;
	private Survey survey;
	private SurveyObjective objective;
	private SurveySection currentSection;
	
	// entered values for the whole objective
	private Map<SurveyElement, SurveyEnteredValue> enteredValues;
	
	private Map<SurveyObjective, SurveyEnteredObjective> enteredObjectives = new HashMap<SurveyObjective, SurveyEnteredObjective>();
	private Set<SurveyQuestion> skippedQuestions = new HashSet<SurveyQuestion>();
	
	public SurveyPage(){}

	public SurveyPage(Organisation organisation, 
			Survey survey, SurveyObjective objective, SurveySection currentSection, 
			Map<SurveyElement, SurveyEnteredValue> enteredValues,
//			Map<SurveyObjective, SurveyEnteredObjective> enteredObjectives, 
			Map<Long, SurveyElementValue> surveyElements) {
		this.organisation = organisation;
		this.survey = survey;
		this.objective = objective;
		this.currentSection = currentSection;
		this.enteredValues = enteredValues;
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
	
	public Map<SurveyElement, SurveyEnteredValue> getEnteredValues() {
		return enteredValues;
	}
	
	public Period getPeriod() {
		return getSurvey().getPeriod();
	}
	
	public SurveyObjective getObjective() {
		return objective;
	}

	public Survey getSurvey() {
		return survey;
	}
	
	public Organisation getOrganisation() {
		return organisation;
	}
	
	public SurveySection getSection() {
		return currentSection;
	}
	
	public boolean isSkipped(SurveyQuestion surveyQuestion) {
		return skippedQuestions.contains(surveyQuestion);
	}
	
	public boolean canSubmit() {
		return getIncompleteSections().isEmpty() && getInvalidQuestions().isEmpty();
	}
	
	public boolean isValid(SurveyQuestion question) {
		if (!skippedQuestions.contains(question)) {
			for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue value = enteredValues.get(element);
				if (!value.getSkipped() && !value.getValid()) return false;
			}
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
			if (skippedQuestions.contains(element.getSurveyQuestion())) continue;
			
			SurveyEnteredValue enteredValue = enteredValues.get(element);
			
			if (!enteredValue.getSkipped()) {
				if (enteredValue.getValue() == null) {
					status = SectionStatus.INCOMPLETE;
				}
				else {
					if (!enteredValue.getValid()) {
						status = SectionStatus.INVALID;
						break;
					}
				}
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
	

	// for JSON answer
	public List<SurveyElement> getSkippedElements() {
		List<SurveyElement> result = new ArrayList<SurveyElement>();
		for (SurveyEnteredValue surveyEnteredValue : enteredValues.values()) {
			if (surveyEnteredValue.getSkipped()) result.add(surveyEnteredValue.getSurveyElement());
		}
		return result;
	}
	
	// for JSON answer
	public List<SurveyQuestion> getSkippedQuestions() {
		return new ArrayList<SurveyQuestion>(skippedQuestions);
	}
	
	// for JSON answer
	public List<SurveySection> getIncompleteSections() {
		List<SurveySection> result = new ArrayList<SurveySection>();
		section: for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyElement element : section.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValue = enteredValues.get(element);
				if (!enteredValue.getSkipped() && enteredValue.getValue() == null) { 
					result.add(section);
					continue section;
				}
			}
		}
		
		return result;
	}
	// for JSON answer
	public Map<SurveySection, List<SurveyQuestion>> getInvalidQuestions() {
		Map<SurveySection, List<SurveyQuestion>> result = new LinkedHashMap<SurveySection, List<SurveyQuestion>>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			List<SurveyQuestion> invalidQuestions = new ArrayList<SurveyQuestion>();
			for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				if (!isValid(question)) invalidQuestions.add(question);
			}
			if (!invalidQuestions.isEmpty()) result.put(section, invalidQuestions);
		}
		return result;
	}
	
	
	// side-effect method
	public void transferValues(ValidationService validationService, SurveyElementService surveyElementService) {
		// we sanitize the element values
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			surveyElementValue.sanitizeValues(this);	
		}
		
		// we transfer the values to the entered element value
		for (SurveyElementValue surveyElementValue : surveyElements.values()) {
			SurveyEnteredValue enteredValue = enteredValues.get(surveyElementValue.getSurveyElement());
			if ((surveyElementValue.getValue() == null && enteredValue.getValue() != null) ||
				(surveyElementValue.getValue() != null && !surveyElementValue.getValue().equals(enteredValue.getValue()))
			) {
				// the value changed
				enteredValue.setAcceptedWarnings(new ArrayList<Long>());
				enteredValue.setValue(surveyElementValue.getValue());
			}
			else {
				// the value didn't change
				if (!surveyElementValue.getAcceptedWarnings().isEmpty()) {
					enteredValue.getAcceptedWarnings().addAll(surveyElementValue.getAcceptedWarnings());
				}
			}
		}
	}
	
	// side-effect method
	// TODO create entered objectives and entered values at survey initialization
	public void createEnteredValues(SurveyElementService surveyElementService) {
		// we create all entered values
		for (SurveySection surveySection : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyElement surveyElement : surveySection.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValue = enteredValues.get(surveyElement);
				if (enteredValue == null) {
					enteredValue = new SurveyEnteredValue(surveyElement, organisation.getOrganisationUnit(), null);
					surveyElementService.save(enteredValue);
					enteredValues.put(enteredValue.getSurveyElement(), enteredValue);
				}
			}
		}
	}
	
	// side-effect method
	public void userValidation(ValidationService validationService, SurveyElementService surveyElementService) {
		
		// we validate
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			// skipped questions
			for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				if (validationService.isSkipped(this, question)) skippedQuestions.add(question);
			}
			
			// skipped survey elements
			for (SurveyElement surveyElement : section.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyElementValue elementValue = surveyElements.get(surveyElement.getId());
				SurveyEnteredValue enteredValue = enteredValues.get(surveyElement);
				if (skippedQuestions.contains(surveyElement.getSurveyQuestion()) || validationService.isSkipped(this, surveyElement)) {
					enteredValue.setSkipped(true);
				}
				else enteredValue.setSkipped(false);
				
				Set<SurveyValidationRule> errors = new HashSet<SurveyValidationRule>();
				Set<SurveyValidationRule> warnings = new HashSet<SurveyValidationRule>();
				if (enteredValue.getValue() != null) {
					for (SurveyValidationRule validationRule : enteredValue.getSurveyElement().getValidationRules()) {
						if (!validationService.validate(this, enteredValue.getSurveyElement(), validationRule)) {
							if (!validationRule.getAllowOutlier()) errors.add(validationRule);
							else {
								if (!enteredValue.getAcceptedWarnings().contains(validationRule.getId())) warnings.add(validationRule);
							}
						}
					}
				}
				if (elementValue != null) {
					elementValue.setInvalidErrors(errors);
					elementValue.setInvalidWarnings(warnings);
				}
				enteredValue.setValid(errors.isEmpty() && warnings.isEmpty());
			}
		}
		
		// survey objective status
		// TODO create entered objectives and entered values at survey initialization
		for (SurveyObjective surveyObjective : objective.getSurvey().getObjectives(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
			if (enteredObjective == null) {
				enteredObjective = new SurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
			}
			enteredObjective.setStatus(getStatus(surveyObjective, surveyElementService));
			enteredObjectives.put(surveyObjective, enteredObjective);
		}
	}
	
	// side-effect method
	public void persistState(SurveyElementService surveyElementService) {
		SurveyEnteredObjective currentEnteredObjective = enteredObjectives.get(objective);
		if (currentEnteredObjective.getStatus() == ObjectiveStatus.CLOSED || currentEnteredObjective.getStatus() == ObjectiveStatus.UNAVAILABLE) 
			throw new IllegalStateException("trying to persist the state of a closed or unavailable objective");
		
		for (SurveyEnteredValue surveyEnteredValue : enteredValues.values()) {
			surveyElementService.save(surveyEnteredValue);
		}
		
		for (SurveyEnteredObjective enteredObjective : enteredObjectives.values()) {
			surveyElementService.save(enteredObjective);
		}
	}
	
	// side-effect method
	public void submit(SurveyElementService surveyElementService, ValueService valueService) {
		if (log.isDebugEnabled()) log.debug("submit()");
		// TODO throw that away if objective is not closed
		
		for (SurveyEnteredValue enteredValue : enteredValues.values()) {
			// we don't save skipped data values
			if (enteredValue.getSkipped() || isSkipped(enteredValue.getSurveyElement().getSurveyQuestion())) continue;
			
			DataElement dataElement = enteredValue.getSurveyElement().getDataElement();
			DataValue value = valueService.getValue(dataElement, organisation.getOrganisationUnit(), getPeriod());
			if (value == null) {
				value = new DataValue(dataElement, organisation.getOrganisationUnit(), getPeriod(), null);
			}
			value.setValue(enteredValue.getValue());
			valueService.save(value);
		}
		
		SurveyEnteredObjective currentEnteredObjective = enteredObjectives.get(objective);
		currentEnteredObjective.setStatus(ObjectiveStatus.CLOSED);
		surveyElementService.save(currentEnteredObjective);
	}
	
	@Override
	public String toString() {
		return "SurveyPage [period=" + getPeriod() + ", organisation="
				+ organisation + ", section=" + currentSection
				+ ", surveyElements=" + getSurveyElements() + "]";
	}
	
}
