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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationService;
import org.chai.kevin.ValueService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredObjective.ObjectiveStatus;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion.QuestionStatus;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredSection.SectionStatus;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.springframework.transaction.annotation.Transactional;

public class SurveyPageService {
	
	private static Log log = LogFactory.getLog(SurveyPageService.class);
	
	private SurveyElementService surveyElementService;
	private OrganisationService organisationService;
	private ValueService valueService;
	private ValidationService validationService;
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionTable(Organisation organisation, SurveyObjective objective) {
		organisationService.loadGroup(organisation);
		
		List<SurveySection> sections = objective.getSections(organisation.getOrganisationUnitGroup());
		Map<SurveySection, SectionSummary> sectionSummaryMap = new HashMap<SurveySection, SectionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyElement> elements = section.getSurveyElements(organisation.getOrganisationUnitGroup());
			Integer submittedElements = surveyElementService.getNumberOfSurveyEnteredValues(objective.getSurvey(), organisation.getOrganisationUnit(), null, section);
			
			sectionSummaryMap.put(section, new SectionSummary(section, elements.size(), submittedElements));
		}
		return new SummaryPage(objective, organisation, sections, sectionSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveTable(Organisation organisation, Survey survey) {
		organisationService.loadGroup(organisation);
		
		List<SurveyObjective> objectives = survey.getObjectives(organisation.getOrganisationUnitGroup());
		Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap = new HashMap<SurveyObjective, ObjectiveSummary>();
		
		for (SurveyObjective objective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(objective, organisation.getOrganisationUnit());
			List<SurveyElement> elements = objective.getElements(organisation.getOrganisationUnitGroup());
			Integer submittedElements = surveyElementService.getNumberOfSurveyEnteredValues(survey, organisation.getOrganisationUnit(), objective, null);
			
			objectiveSummaryMap.put(objective, new ObjectiveSummary(objective, enteredObjective, elements.size(), submittedElements));
		}
		
		return new SummaryPage(survey, organisation, objectives, objectiveSummaryMap, false);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getSummaryPage(Organisation organisation, Survey survey) {
		if (organisation == null || survey == null) return new SummaryPage(survey, organisation, null, null);
		
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
		Map<OrganisationUnitGroup, List<SurveyObjective>> objectiveMap = new HashMap<OrganisationUnitGroup, List<SurveyObjective>>();
		Map<OrganisationUnitGroup, List<SurveyElement>> elementMap = new HashMap<OrganisationUnitGroup, List<SurveyElement>>();

		Map<Organisation, OrganisationSummary> summaryMap = new HashMap<Organisation, OrganisationSummary>();
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);

			if (!objectiveMap.containsKey(facility.getOrganisationUnitGroup())) {
				objectiveMap.put(facility.getOrganisationUnitGroup(), survey.getObjectives(facility.getOrganisationUnitGroup()));
			}
			Integer submittedObjectives = surveyElementService.getNumberOfSurveyEnteredObjectives(survey, facility.getOrganisationUnit(), ObjectiveStatus.CLOSED);
			
			if (!elementMap.containsKey(facility.getOrganisationUnitGroup())) {
				List<SurveyElement> elements = new ArrayList<SurveyElement>();
				for (SurveyObjective objective : objectiveMap.get(facility.getOrganisationUnitGroup())) {
					elements.addAll(objective.getElements(facility.getOrganisationUnitGroup()));				
				}
				elementMap.put(facility.getOrganisationUnitGroup(), elements);
			}
			Integer enteredValues = surveyElementService.getNumberOfSurveyEnteredValues(survey, facility.getOrganisationUnit(), null, null);
			
			OrganisationSummary summary = new OrganisationSummary(facility,  
					submittedObjectives, objectiveMap.get(facility.getOrganisationUnitGroup()).size(), 
					enteredValues, elementMap.get(facility.getOrganisationUnitGroup()).size());
			
			summaryMap.put(facility, summary);
		}
		return new SummaryPage(survey, organisation, facilities, summaryMap);
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, SurveyQuestion currentQuestion) {
		
		Map<SurveyElement, SurveyEnteredValue> elements = new LinkedHashMap<SurveyElement, SurveyEnteredValue>();
		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new LinkedHashMap<SurveyQuestion, SurveyEnteredQuestion>();
		
		SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, currentQuestion);
		questions.put(currentQuestion, enteredQuestion);
		for (SurveyElement element : currentQuestion.getSurveyElements(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
			elements.put(element, enteredValue);
		}
		
		return new SurveyPage(organisation, currentQuestion.getSurvey(), null, null, null, null, questions, elements);
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, SurveySection currentSection) {
		organisationService.loadGroup(organisation);
		
		SurveyObjective currentObjective = currentSection.getObjective();
		Survey survey = currentObjective.getSurvey();
		
		Map<SurveyObjective, SurveyEnteredObjective> objectives = new LinkedHashMap<SurveyObjective, SurveyEnteredObjective>();
		Map<SurveySection, SurveyEnteredSection> sections = new LinkedHashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyObjective objective : survey.getObjectives(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
			objectives.put(objective, enteredObjective);
			
			for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
				sections.put(section, enteredSection);
			}
		}
		
		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new LinkedHashMap<SurveyQuestion, SurveyEnteredQuestion>();
		Map<SurveyElement, SurveyEnteredValue> elements = new LinkedHashMap<SurveyElement, SurveyEnteredValue>();
		for (SurveyQuestion question : currentSection.getQuestions(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
			questions.put(question, enteredQuestion);
			
			for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
				elements.put(element, enteredValue);
			}
		}
		
		return new SurveyPage(organisation, survey, currentObjective, currentSection, objectives, sections, questions, elements);
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, SurveyObjective currentObjective) {
		organisationService.loadGroup(organisation);
		
		Survey survey = currentObjective.getSurvey();
		
		Map<SurveyObjective, SurveyEnteredObjective> objectives = new LinkedHashMap<SurveyObjective, SurveyEnteredObjective>();
		Map<SurveySection, SurveyEnteredSection> sections = new LinkedHashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyObjective objective : survey.getObjectives(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
			objectives.put(objective, enteredObjective);
			
			for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
				sections.put(section, enteredSection);
			}
		}

		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new LinkedHashMap<SurveyQuestion, SurveyEnteredQuestion>();
		Map<SurveyElement, SurveyEnteredValue> elements = new LinkedHashMap<SurveyElement, SurveyEnteredValue>();
		for (SurveySection section : currentObjective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
				questions.put(question, enteredQuestion);
				
				for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
					SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
					elements.put(element, enteredValue);
				}
			}
		}
		
		return new SurveyPage(organisation, survey, currentObjective, null, objectives, sections, questions, elements);
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, Survey survey) {
		organisationService.loadGroup(organisation);
		
		Map<SurveyObjective, SurveyEnteredObjective> objectives = new LinkedHashMap<SurveyObjective, SurveyEnteredObjective>();
		Map<SurveySection, SurveyEnteredSection> sections = new LinkedHashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyObjective objective : survey.getObjectives(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
			objectives.put(objective, enteredObjective);
			
			
			for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
				sections.put(section, enteredSection);
			}
		}
		return new SurveyPage(organisation, survey, null, null, objectives, sections, null, null);
	}
	
	// returns the list of modified elements/questions/sections/objectives (skip, validation, etc..)
	@Transactional(readOnly = false)
	public SurveyPage modify(Organisation organisation, List<SurveyElement> elements, Map<String, Object> params) {
		organisationService.loadGroup(organisation);

		// first we save the values
		Map<SurveyElement, SurveyEnteredValue> affectedElements = new HashMap<SurveyElement, SurveyEnteredValue>();
		for (SurveyElement element : elements) {
			// values, we shouldn't have to create them as they have to exist already
			SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(element, organisation.getOrganisationUnit());
			Value value = element.getDataElement().getType().getValueFromMap(params, "surveyElements["+element.getId()+"].value");
			enteredValue.setValue(value);
			// accepted warnings
			// TODO
			
			affectedElements.put(element, enteredValue);
		}
		
		// second we get the rules that could be affected by the changes
		Set<SurveyValidationRule> validationRules = new HashSet<SurveyValidationRule>();
		Set<SurveySkipRule> skipRules = new HashSet<SurveySkipRule>();
		for (SurveyElement element : elements) {
			validationRules.addAll(surveyElementService.searchValidationRules(element));
			skipRules.addAll(surveyElementService.searchSkipRules(element));
		}
		
		// third we evaluate those rules
		Map<SurveyQuestion, SurveyEnteredQuestion> affectedQuestions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		for (SurveyValidationRule validationRule : validationRules) {
			Set<String> prefixes = validationService.getInvalidPrefix(validationRule, organisation);
			// TODO get this from map
			SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, validationRule.getSurveyElement());
			enteredValue.addInvalid(validationRule, prefixes);
			
			affectedElements.put(validationRule.getSurveyElement(), enteredValue);
		}
		
		for (SurveySkipRule surveySkipRule : skipRules) {
			for (SurveyElement element : surveySkipRule.getSkippedSurveyElements().keySet()) {
				Set<String> prefixes = validationService.getSkippedPrefix(element, surveySkipRule, organisation);
				// TODO get this from map
				SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
				enteredValue.addSkipped(surveySkipRule, prefixes);
				
				affectedElements.put(element, enteredValue);
			}
			
			boolean skipped = validationService.isSkipped(surveySkipRule, organisation);
			for (SurveyQuestion question : surveySkipRule.getSkippedSurveyQuestions()) {
				// TODO get this from map
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
				if (skipped) enteredQuestion.getSkipped().add(surveySkipRule);
				else enteredQuestion.getSkipped().remove(surveySkipRule);
				
				affectedQuestions.put(question, enteredQuestion);
			}
		}
		
		// fourth we propagate the affected changes up the survey tree and save
		for (SurveyEnteredValue element : affectedElements.values()) {
			// nothing to do here except save
			surveyElementService.save(element);
			
			SurveyQuestion question = element.getSurveyElement().getSurveyQuestion();
			if (!affectedQuestions.containsKey(question)) {
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
				affectedQuestions.put(question, enteredQuestion);
			}
		}
		
		Map<SurveySection, SurveyEnteredSection> affectedSections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyEnteredQuestion question : affectedQuestions.values()) {
			// we set the question status correctly and save
			question.setStatus(calculateStatus(question.getQuestion(), organisation));
			surveyElementService.save(question);
			
			SurveySection section = question.getQuestion().getSection();
			if (!affectedSections.containsKey(section)) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
				affectedSections.put(section, enteredSection);
			}
			
		}
		
		Map<SurveyObjective, SurveyEnteredObjective> affectedObjectives = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		for (SurveyEnteredSection section : affectedSections.values()) {
			// we set the section status correctly and save
			section.setStatus(calculateStatus(section.getSection(), organisation));
			surveyElementService.save(section);
			
			SurveyObjective objective = section.getSection().getObjective();
			if (!affectedObjectives.containsKey(objective)) {
				SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
				affectedObjectives.put(objective, enteredObjective);
			}
		}
		
		for (SurveyEnteredObjective objective : affectedObjectives.values()) {
			// if the objective is not closed and available
			// we set the objective status correctly and save
			if (objective.getStatus() != ObjectiveStatus.CLOSED && objective.getStatus() != ObjectiveStatus.UNAVAILABLE) {
				objective.setStatus(calculateStatus(objective.getObjective(), organisation));
			}
			surveyElementService.save(objective);
		}
		
		return new SurveyPage(organisation, null, null, null, affectedObjectives, affectedSections, affectedQuestions, affectedElements);
	}
	
	
	// TODO refactor this to use only one Status type, as it is the same for all levels
	private ObjectiveStatus calculateStatus(SurveyObjective objective, Organisation organisation) {
		ObjectiveStatus status = null;
		
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
			if (enteredSection.getStatus() == SectionStatus.INCOMPLETE) {
				// if the value is not complete, we set the status as 
				// incomplete but continue the loop, as it could be invalid
				status = ObjectiveStatus.INCOMPLETE;
			}
			if (enteredSection.getStatus() == SectionStatus.INVALID) {
				// if the value is invalid, we set the status to
				// invalid and quit the loop
				status = ObjectiveStatus.INVALID;
				break;
			}
		}
		// if the status was never set, it means it
		// was neither incomplete nor invalid, therefore it is complete
		if (status == null) status = ObjectiveStatus.COMPLETE;
		return status;
	}
	
	// TODO refactor this to use only one Status type, as it is the same for all levels
	private SectionStatus calculateStatus(SurveySection section, Organisation organisation) {
		SectionStatus status = null;
		for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
			if (enteredQuestion.getStatus() == QuestionStatus.INCOMPLETE) {
				// if the value is not complete, we set the status as 
				// incomplete but continue the loop, as it could be invalid
				status = SectionStatus.INCOMPLETE;
			}
			if (enteredQuestion.getStatus() == QuestionStatus.INVALID) {
				// if the value is invalid, we set the status to
				// invalid and quit the loop
				status = SectionStatus.INVALID;
				break;
			}
		}
		// if the status was never set, it means it
		// was neither incomplete nor invalid, therefore it is complete
		if (status == null) status = SectionStatus.COMPLETE;
		return status;
	}
	
	// TODO refactor this to use only one Status type, as it is the same for all levels
	private QuestionStatus calculateStatus(SurveyQuestion question, Organisation organisation) {
		QuestionStatus status = null;
		for (SurveyElement element : question.getSurveyElements(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
			if (!enteredValue.isComplete()) {
				// if the value is not complete, we set the status as 
				// incomplete but continue the loop, as it could be invalid
				status = QuestionStatus.INCOMPLETE;
			}
			if (enteredValue.isInvalid()) {
				// if the value is invalid, we set the status to
				// invalid and quit the loop
				status = QuestionStatus.INVALID;
				break;
			}
		}
		// if the status was never set, it means it
		// was neither incomplete nor invalid, therefore it is complete
		if (status == null) status = QuestionStatus.COMPLETE;
		return status;
	}
	
	
	public void submit(Organisation organisation, SurveyObjective objective) {
		// TODO
	}
	
	public void reopen(Organisation organisation, SurveyObjective objective) {
		// TODO
	}
	
	private SurveyEnteredObjective getSurveyEnteredObjective(Organisation organisation, SurveyObjective surveyObjective) {
		SurveyEnteredObjective enteredObjective = surveyElementService.getSurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
		if (enteredObjective == null) {
			if (surveyObjective.getDependency() != null) {
				// TODO take into account dependency
			}
			enteredObjective = new SurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit(), ObjectiveStatus.INCOMPLETE);
			surveyElementService.save(enteredObjective);
		}
		return enteredObjective;
	}
	
	private SurveyEnteredSection getSurveyEnteredSection(Organisation organisation, SurveySection surveySection) {
		SurveyEnteredSection enteredSection = surveyElementService.getSurveyEnteredSection(surveySection, organisation.getOrganisationUnit());
		if (enteredSection == null) {
			enteredSection = new SurveyEnteredSection(surveySection, organisation.getOrganisationUnit(), SectionStatus.INCOMPLETE);
			surveyElementService.save(enteredSection);
		}
		return enteredSection;
	}
	
	private SurveyEnteredQuestion getSurveyEnteredQuestion(Organisation organisation, SurveyQuestion surveyQuestion) {
		SurveyEnteredQuestion enteredQuestion = surveyElementService.getSurveyEnteredQuestion(surveyQuestion, organisation.getOrganisationUnit());
		if (enteredQuestion == null) {
			enteredQuestion = new SurveyEnteredQuestion(surveyQuestion, organisation.getOrganisationUnit(), QuestionStatus.INCOMPLETE);
			surveyElementService.save(enteredQuestion);
		}
		return enteredQuestion;
	}
	
	private SurveyEnteredValue getSurveyEnteredValue(Organisation organisation, SurveyElement element) {
		SurveyEnteredValue enteredValue = surveyElementService.getSurveyEnteredValue(element, organisation.getOrganisationUnit());
		if (enteredValue == null) {
			Value lastValue = null;
			if (element.getSurvey().getLastPeriod() != null) {
				DataValue lastDataValue = valueService.getValue(element.getDataElement(), organisation.getOrganisationUnit(), element.getSurvey().getLastPeriod());
				if (lastDataValue != null) lastValue = lastDataValue.getValue();
			}
			enteredValue = new SurveyEnteredValue(element, organisation.getOrganisationUnit(), Value.NULL, lastValue);
			surveyElementService.save(enteredValue);
		}
		return enteredValue;
	}

	
	public void setSurveyElementService(SurveyElementService surveyElementService) {
		this.surveyElementService = surveyElementService;
	}
	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}
	
}
