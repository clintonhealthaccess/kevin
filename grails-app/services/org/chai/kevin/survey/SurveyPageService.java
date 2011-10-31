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
import java.util.Date;
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
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.survey.SurveyQuestion.QuestionType;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.chai.kevin.survey.validation.SurveyLog;
import org.chai.kevin.value.DataValue;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SurveyPageService {
	
	private static Log log = LogFactory.getLog(SurveyPageService.class);
	
	private SurveyService surveyService;
	private SurveyValueService surveyValueService;
	private OrganisationService organisationService;
	private ValueService valueService;
	private ValidationService validationService;
	private SessionFactory sessionFactory;
	private GrailsApplication grailsApplication;
	
	@Transactional(readOnly = true)
	public Survey getDefaultSurvey() {
		return (Survey)sessionFactory.getCurrentSession()
			.createCriteria(Survey.class).add(Restrictions.eq("active", true)).uniqueResult();
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionTable(Organisation organisation, SurveyObjective objective) {
		organisationService.loadGroup(organisation);
		
		List<SurveySection> sections = objective.getSections(organisation.getOrganisationUnitGroup());
		Map<SurveySection, SectionSummary> sectionSummaryMap = new HashMap<SurveySection, SectionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(organisation.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), organisation.getOrganisationUnit(), null, section, true, false);
			
			sectionSummaryMap.put(section, new SectionSummary(section, questions.size(), completedQuestions));
		}
		return new SummaryPage(objective, organisation, sections, sectionSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveTable(Organisation organisation, Survey survey) {
		organisationService.loadGroup(organisation);
		
		List<SurveyObjective> objectives = survey.getObjectives(organisation.getOrganisationUnitGroup());
		Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap = new HashMap<SurveyObjective, ObjectiveSummary>();
		
		for (SurveyObjective objective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, organisation.getOrganisationUnit());
			List<SurveyQuestion> questions = objective.getQuestions(organisation.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, organisation.getOrganisationUnit(), objective, null, true, false);
			
			objectiveSummaryMap.put(objective, new ObjectiveSummary(objective, enteredObjective, questions.size(), completedQuestions));
		}
		
		return new SummaryPage(survey, organisation, objectives, objectiveSummaryMap, false);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getSummaryPage(Organisation organisation, Survey survey) {
		if (organisation == null || survey == null) return new SummaryPage(survey, organisation, null, null);
		
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
		Map<OrganisationUnitGroup, List<SurveyObjective>> objectiveMap = new HashMap<OrganisationUnitGroup, List<SurveyObjective>>();
		Map<OrganisationUnitGroup, List<SurveyQuestion>> questionMap = new HashMap<OrganisationUnitGroup, List<SurveyQuestion>>();

		Map<Organisation, OrganisationSummary> summaryMap = new HashMap<Organisation, OrganisationSummary>();
		for (Organisation facility : facilities) {
			organisationService.loadGroup(facility);

			if (!objectiveMap.containsKey(facility.getOrganisationUnitGroup())) {
				objectiveMap.put(facility.getOrganisationUnitGroup(), survey.getObjectives(facility.getOrganisationUnitGroup()));
			}
			Integer submittedObjectives = surveyValueService.getNumberOfSurveyEnteredObjectives(survey, facility.getOrganisationUnit(), true, null, null);
			
			if (!questionMap.containsKey(facility.getOrganisationUnitGroup())) {
				List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
				for (SurveyObjective objective : objectiveMap.get(facility.getOrganisationUnitGroup())) {
					questions.addAll(objective.getQuestions(facility.getOrganisationUnitGroup()));				
				}
				questionMap.put(facility.getOrganisationUnitGroup(), questions);
			}
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, facility.getOrganisationUnit(), null, null, true, false);
			
			OrganisationSummary summary = new OrganisationSummary(facility,  
					submittedObjectives, objectiveMap.get(facility.getOrganisationUnitGroup()).size(), 
					completedQuestions, questionMap.get(facility.getOrganisationUnitGroup()).size());
			
			summaryMap.put(facility, summary);
		}
		return new SummaryPage(survey, organisation, facilities, summaryMap);
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, SurveyQuestion currentQuestion) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
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
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
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
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
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
	public SurveyPage getSurveyPagePrint(Organisation organisation,Survey survey) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		organisationService.loadGroup(organisation);
		OrganisationUnitGroup organisationUnitGroup = organisation.getOrganisationUnitGroup();
		
		Map<SurveyElement, SurveyEnteredValue> elements = new LinkedHashMap<SurveyElement, SurveyEnteredValue>();
		
		for (SurveyObjective objective : survey.getObjectives(organisationUnitGroup)) {
			for (SurveySection section : objective.getSections(organisationUnitGroup)) {
				for (SurveyQuestion question : section.getQuestions(organisationUnitGroup)) {
					for (SurveyElement element : question.getSurveyElements(organisationUnitGroup)) {
						SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
						elements.put(element, enteredValue);
					}
				}
			}

		}
		return new SurveyPage(organisation, survey, null, null, null, null,null, elements);
	}
	

	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(Organisation organisation, Survey survey) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
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
	
	@Transactional(readOnly = false)
	public void refresh(Organisation organisation, Survey survey, boolean closeIfComplete) {
		List<Organisation> facilities = organisationService.getChildrenOfLevel(organisation, organisationService.getFacilityLevel());
	
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		for (Organisation facility : facilities) {
			survey = (Survey)sessionFactory.getCurrentSession().load(Survey.class, survey.getId());
			facility.setOrganisationUnit((OrganisationUnit)sessionFactory.getCurrentSession().get(OrganisationUnit.class, facility.getOrganisationUnit().getId()));

			getMe().refreshSurveyForFacilityWithNewTransaction(facility, survey, closeIfComplete);
			sessionFactory.getCurrentSession().clear();
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void refreshSurveyForFacilityWithNewTransaction(Organisation facility, Survey survey, boolean closeIfComplete) {
		refreshSurveyForFacility(facility, survey, closeIfComplete);
	}
	
	@Transactional(readOnly = false)
	public void refreshSurveyForFacility(Organisation facility, Survey survey, boolean closeIfComplete) {
		organisationService.loadGroup(facility);

		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		for (SurveyObjective objective : survey.getObjectives(facility.getOrganisationUnitGroup())) {
			refreshObjectiveForFacility(facility, objective, closeIfComplete);
		}
	}

	private void refreshObjectiveForFacility(Organisation facility, SurveyObjective objective, boolean closeIfComplete) {
		for (SurveySection section : objective.getSections(facility.getOrganisationUnitGroup())) {
			getMe().refreshSectionForFacilityWithNewTransaction(facility, section);
		}
		
		SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(facility, objective);
		setObjectiveStatus(enteredObjective, facility);
		if (closeIfComplete && enteredObjective.isComplete() && !enteredObjective.isInvalid()) enteredObjective.setClosed(true); 
		surveyValueService.save(enteredObjective);
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void refreshSectionForFacilityWithNewTransaction(Organisation facility, SurveySection section) {
		refreshSectionForFacility(facility, section);
	}
	
	@Transactional(readOnly = false)
	public void refreshSectionForFacility(Organisation facility, SurveySection section) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		organisationService.loadGroup(facility);
		
		for (SurveyQuestion question : section.getQuestions(facility.getOrganisationUnitGroup())) {
			refreshQuestionForFacility(facility, question); 
		}
		
		SurveyEnteredSection enteredSection = getSurveyEnteredSection(facility, section);
		setSectionStatus(enteredSection, facility);
		surveyValueService.save(enteredSection);
	}
	
	private void refreshQuestionForFacility(Organisation facility, SurveyQuestion question) {
		for (SurveyElement element : question.getSurveyElements(facility.getOrganisationUnitGroup())) {
			refreshElementForFacility(facility, element);
		}
		
		SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(facility, question);
		setQuestionStatus(enteredQuestion, facility);
		surveyValueService.save(enteredQuestion);
	}
	
	private void refreshElementForFacility(Organisation facility, SurveyElement element) {
		Survey survey = element.getSurvey();
		
		SurveyEnteredValue enteredValue = getSurveyEnteredValue(facility, element);
		DataValue dataValue = valueService.getValue(element.getDataElement(), facility.getOrganisationUnit(), survey.getPeriod());
		if (dataValue != null) enteredValue.setValue(dataValue.getValue());
		if (survey.getLastPeriod() != null) {
			DataValue lastDataValue = valueService.getValue(element.getDataElement(), facility.getOrganisationUnit(), survey.getLastPeriod());
			if (lastDataValue != null) enteredValue.setLastValue(lastDataValue.getValue());
		}
		surveyValueService.save(enteredValue);
	}
	
	// returns the list of modified elements/questions/sections/objectives (skip, validation, etc..)
	// we set the isolation level on READ_UNCOMMITTED to avoid deadlocks because in READ_COMMITTED
	// mode, a write lock is acquired at the beginning and never released till this method terminates
	// which causes other sessions calling this method to timeout
	@Transactional(readOnly = false)
	public SurveyPage modify(Organisation organisation, SurveyObjective objective, List<SurveyElement> elements, Map<String, Object> params) {
		if (log.isDebugEnabled()) log.debug("modify(organisation="+organisation+", elements="+elements+")");
		organisationService.loadGroup(organisation);
		
		// we acquire a write lock on the objective
		// this won't change anything for MyISAM tables
		SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
		sessionFactory.getCurrentSession().buildLockRequest(LockOptions.NONE).setLockMode(LockMode.PESSIMISTIC_WRITE).lock(enteredObjective);
		
		SurveyPage surveyPage = null;
		// if the objective is not closed, we go on with the save
		if (!enteredObjective.isClosed()) {
			Set<String> attributes = new HashSet<String>();
			attributes.add("warning");

			Map<SurveyElement, SurveyEnteredValue> affectedElements = new HashMap<SurveyElement, SurveyEnteredValue>();
			// first we save the values
			for (SurveyElement element : elements) {
				if (log.isDebugEnabled()) log.debug("setting new value for element: "+element);
				
				SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
				
				final Type valueType = element.getDataElement().getType();
				final Value oldValue = enteredValue.getValue();
				
				if (log.isDebugEnabled()) log.debug("getting new value from parameters for element: "+element);
				Value value = valueType.mergeValueFromMap(oldValue, params, "surveyElements["+element.getId()+"].value", attributes);
				
				// reset accepted warnings for changed values
				if (log.isDebugEnabled()) log.debug("resetting warning for modified prefixes: "+element);
				valueType.transformValue(value, new ValuePredicate() {
					@Override
					public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
						Value oldPrefix = valueType.getValue(oldValue, currentPrefix);
						if (oldPrefix != null && oldPrefix.getAttribute("warning") != null) {
							if (!oldPrefix.getValueWithoutAttributes().equals(currentValue.getValueWithoutAttributes())) {
								currentValue.setAttribute("warning", null);
								return true;
							}
						}
						return false;
					}
				});
				
				// set the value and save
				// here, a write lock is acquired on the SurveyEnteredValue that will be kept
				// till the end of the transaction, if in READ_COMMITTED isolation mode, a timeout
				// is likely to occur because the transaction is quite long
				enteredValue.setValue(value);
				affectedElements.put(element, enteredValue);
				
				// if it is a checkbox question, we need to reset the values to null
				// FIXME THIS IS A HACK
				resetCheckboxQuestion(organisation, element, affectedElements);
			}
			// we evaluate the rules
			surveyPage = evaluateRulesAndSave(organisation, elements, affectedElements);
		}

		if (log.isDebugEnabled()) log.debug("modify(...)="+surveyPage);
		return surveyPage;
	}
		
		
	private SurveyPage evaluateRulesAndSave(Organisation organisation, List<SurveyElement> elements, Map<SurveyElement, SurveyEnteredValue> affectedElements) {  
		if (log.isDebugEnabled()) log.debug("evaluateRulesAndSave(organisation="+organisation+", elements="+elements+")");
		
		// second we get the rules that could be affected by the changes
		Set<SurveyValidationRule> validationRules = new HashSet<SurveyValidationRule>();
		Set<SurveySkipRule> skipRules = new HashSet<SurveySkipRule>();
		for (SurveyElement element : elements) {
			if (log.isDebugEnabled()) log.debug("getting skip and validation rules for element: "+element);

			validationRules.addAll(surveyService.searchValidationRules(element, organisation.getOrganisationUnitGroup()));
			skipRules.addAll(surveyService.searchSkipRules(element));
		}
		
		// third we evaluate those rules
		Map<SurveyQuestion, SurveyEnteredQuestion> affectedQuestions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		for (SurveyValidationRule validationRule : validationRules) {
			if (log.isDebugEnabled()) log.debug("getting invalid prefixes for validation rule: "+validationRule);
			
			Set<String> prefixes = validationService.getInvalidPrefix(validationRule, organisation);

			SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, validationRule.getSurveyElement());
			enteredValue.setInvalid(validationRule, prefixes);
			
			affectedElements.put(validationRule.getSurveyElement(), enteredValue);
		}
		
		for (SurveySkipRule surveySkipRule : skipRules) {
			for (SurveyElement element : surveySkipRule.getSkippedSurveyElements().keySet()) {
				if (log.isDebugEnabled()) log.debug("getting skipped prefixes for skip rule: "+surveySkipRule+", element: "+element);
				
				Set<String> prefixes = validationService.getSkippedPrefix(element, surveySkipRule, organisation);

				SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
				enteredValue.setSkipped(surveySkipRule, prefixes);
				
				affectedElements.put(element, enteredValue);
			}

			boolean skipped = validationService.isSkipped(surveySkipRule, organisation);
			for (SurveyQuestion question : surveySkipRule.getSkippedSurveyQuestions()) {
				
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
				if (skipped) enteredQuestion.getSkippedRules().add(surveySkipRule);
				else enteredQuestion.getSkippedRules().remove(surveySkipRule);
				
				affectedQuestions.put(question, enteredQuestion);
			}
		}
		
		// fourth we propagate the affected changes up the survey tree and save
		if (log.isDebugEnabled()) log.debug("propagating changes up the survey tree");
		for (SurveyEnteredValue element : affectedElements.values()) {
			SurveyQuestion question = element.getSurveyElement().getSurveyQuestion();
			if (!affectedQuestions.containsKey(question)) {
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
				affectedQuestions.put(question, enteredQuestion);
			}
		}
		
		Map<SurveySection, SurveyEnteredSection> affectedSections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyEnteredQuestion question : affectedQuestions.values()) {
			// we set the question status correctly and save
			setQuestionStatus(question, organisation);
			
			SurveySection section = question.getQuestion().getSection();
			if (!affectedSections.containsKey(section)) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
				affectedSections.put(section, enteredSection);
			}
			
		}
		
		Map<SurveyObjective, SurveyEnteredObjective> affectedObjectives = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		for (SurveyEnteredSection section : affectedSections.values()) {
			// we set the section status correctly and save
			setSectionStatus(section, organisation);
			
			SurveyObjective objective = section.getSection().getObjective();
			if (!affectedObjectives.containsKey(objective)) {
				SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
				affectedObjectives.put(objective, enteredObjective);
			}
		}
		
		for (SurveyEnteredObjective objective : affectedObjectives.values()) {
			// if the objective is not closed and available
			// we set the objective status correctly and save
			setObjectiveStatus(objective, organisation);
		}
		
		return new SurveyPage(organisation, null, null, null, affectedObjectives, affectedSections, affectedQuestions, affectedElements);
	}

	// FIXME HACK 
	// TODO get rid of this
	private void resetCheckboxQuestion(Organisation organisation, SurveyElement element, Map<SurveyElement, SurveyEnteredValue> affectedElements) {
		if (log.isDebugEnabled()) log.debug("question is of type: "+element.getSurveyQuestion().getType());
		if (element.getSurveyQuestion().getType() == QuestionType.CHECKBOX) {
			if (log.isDebugEnabled()) log.debug("checking if checkbox question needs to be reset");
			boolean reset = true;
			for (SurveyElement elementInQuestion : element.getSurveyQuestion().getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValueForElementInQuestion = getSurveyEnteredValue(organisation, elementInQuestion);

				if (enteredValueForElementInQuestion.getValue().getBooleanValue() == Boolean.TRUE) reset = false;
			}
			if (log.isDebugEnabled()) log.debug("resetting checkbox question: "+reset);
			for (SurveyElement elementInQuestion : element.getSurveyQuestion().getSurveyElements(organisation.getOrganisationUnitGroup())) {
				SurveyEnteredValue enteredValueForElementInQuestion = getSurveyEnteredValue(organisation, elementInQuestion);

				if (reset) enteredValueForElementInQuestion.getValue().setJsonObject(Value.NULL.getJsonObject());
				else if (enteredValueForElementInQuestion.getValue().isNull()) {
					enteredValueForElementInQuestion.getValue().setJsonObject(enteredValueForElementInQuestion.getType().getValue(false).getJsonObject());
				}
				
				affectedElements.put(elementInQuestion, enteredValueForElementInQuestion);
			}
		}
	}
	
	private void setObjectiveStatus(SurveyEnteredObjective objective, Organisation organisation) {
		Boolean complete = true;
		Boolean invalid = false;
		for (SurveySection section : objective.getObjective().getSections(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredSection enteredSection = getSurveyEnteredSection(organisation, section);
			if (!enteredSection.isComplete()) complete = false;
			if (enteredSection.isInvalid()) invalid = true;
		}
		objective.setComplete(complete);
		objective.setInvalid(invalid);
	}
	
	private void setSectionStatus(SurveyEnteredSection section, Organisation organisation) {
		Boolean complete = true;
		Boolean invalid = false;
		for (SurveyQuestion question : section.getSection().getQuestions(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, question);
			if (!enteredQuestion.isComplete() && !enteredQuestion.isSkipped()) complete = false;
			if (enteredQuestion.isInvalid() && !enteredQuestion.isSkipped()) invalid = true;
		}
		section.setInvalid(invalid);
		section.setComplete(complete);
	}
	
	private void setQuestionStatus(SurveyEnteredQuestion question, Organisation organisation) {
		Boolean complete = true;
		Boolean invalid = false;
		
		// TODO replace this method by a call to the survey element service
		for (SurveyElement element : question.getQuestion().getSurveyElements(organisation.getOrganisationUnitGroup())) {
			SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
			if (!enteredValue.isComplete()) complete = false;
			if (enteredValue.isInvalid()) invalid = true;
		}
		question.setInvalid(invalid);
		question.setComplete(complete);
	}
	
	@Transactional(readOnly = false)
	public boolean submit(Organisation organisation, SurveyObjective objective) {
		organisationService.loadGroup(organisation);
		
		// first we make sure that the objective is valid and complete, so we revalidate it
		List<SurveyElement> elements = objective.getElements(organisation.getOrganisationUnitGroup());
		evaluateRulesAndSave(organisation, elements, new HashMap<SurveyElement, SurveyEnteredValue>());
		
		// we get the updated survey and work from that
		SurveyPage surveyPage = getSurveyPage(organisation, objective);
		if (surveyPage.canSubmit(objective)) {
			// save all the values to data values
			for (SurveyElement element : elements) {
				SurveyEnteredValue enteredValue = getSurveyEnteredValue(organisation, element);
				Value valueToSave = null;
				// if the question is skipped we save NULL
				SurveyEnteredQuestion enteredQuestion = getSurveyEnteredQuestion(organisation, element.getSurveyQuestion());
				if (enteredQuestion.isSkipped()) {
					valueToSave = Value.NULL;
				}
				else {
					final Type type = enteredValue.getType();
					valueToSave = new Value(enteredValue.getValue().getJsonValue());
					type.transformValue(valueToSave, new ValuePredicate() {
						@Override
						public boolean transformValue(Value currentValue, Type currentType, String currentPrefix) {
							// if it is skipped we return NULL
							if (currentValue.getAttribute("skipped") != null) currentValue.setJsonValue(Value.NULL.getStringValue());
							// we remove the attributes
							currentValue.setAttribute("skipped", null);
							currentValue.setAttribute("invalid", null);
							currentValue.setAttribute("warning", null);
							
							return true;
						}
					});
				}
				
				DataValue dataValue = valueService.getValue(element.getDataElement(), organisation.getOrganisationUnit(), objective.getSurvey().getPeriod());
				if (dataValue == null) {
					dataValue = new DataValue(element.getDataElement(), organisation.getOrganisationUnit(), objective.getSurvey().getPeriod(), null);
				}
				dataValue.setValue(valueToSave);
				
				dataValue.setTimestamp(new Date());
				valueService.save(dataValue);
			}
			
			// close the objective
			SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective);
			enteredObjective.setClosed(true);
			surveyValueService.save(enteredObjective);
	
			// log the event
			logSurveyEvent(organisation, objective, "submit");
			
			return true;
		}
		else return false;
	}

	private void logSurveyEvent(Organisation organisation, SurveyObjective objective, String event) {
		SurveyLog surveyLog = new SurveyLog(objective.getSurvey(), objective, organisation.getOrganisationUnit());
		surveyLog.setEvent(event);
		surveyLog.setTimestamp(new Date());
		sessionFactory.getCurrentSession().save(surveyLog);
	}
	
	public void reopen(Organisation organisation, SurveyObjective objective) {
		SurveyEnteredObjective enteredObjective = getSurveyEnteredObjective(organisation, objective); 
		enteredObjective.setClosed(false);
		surveyValueService.save(enteredObjective);
	}
	
	private SurveyEnteredObjective getSurveyEnteredObjective(Organisation organisation, SurveyObjective surveyObjective) {
		SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit());
		if (enteredObjective == null) {
			enteredObjective = new SurveyEnteredObjective(surveyObjective, organisation.getOrganisationUnit(), false, false, false);
//			setObjectiveStatus(enteredObjective, organisation);
			surveyValueService.save(enteredObjective);
		}
		return enteredObjective;
	}
	
	private SurveyEnteredSection getSurveyEnteredSection(Organisation organisation, SurveySection surveySection) {
		SurveyEnteredSection enteredSection = surveyValueService.getSurveyEnteredSection(surveySection, organisation.getOrganisationUnit());
		if (enteredSection == null) {
			enteredSection = new SurveyEnteredSection(surveySection, organisation.getOrganisationUnit(), false, false);
//			setSectionStatus(enteredSection, organisation);
			surveyValueService.save(enteredSection);
		}
		return enteredSection;
	}
	
	private SurveyEnteredQuestion getSurveyEnteredQuestion(Organisation organisation, SurveyQuestion surveyQuestion) {
		SurveyEnteredQuestion enteredQuestion = surveyValueService.getSurveyEnteredQuestion(surveyQuestion, organisation.getOrganisationUnit());
		if (enteredQuestion == null) {
			enteredQuestion = new SurveyEnteredQuestion(surveyQuestion, organisation.getOrganisationUnit(), false, false);
//			setQuestionStatus(enteredQuestion, organisation);
			surveyValueService.save(enteredQuestion);
		}
		return enteredQuestion;
	}
	
	private SurveyEnteredValue getSurveyEnteredValue(Organisation organisation, SurveyElement element) {
		SurveyEnteredValue enteredValue = surveyValueService.getSurveyEnteredValue(element, organisation.getOrganisationUnit());
		if (enteredValue == null) {
//			Value lastValue = null;
//			if (element.getSurvey().getLastPeriod() != null) {
//				DataValue lastDataValue = valueService.getValue(element.getDataElement(), organisation.getOrganisationUnit(), element.getSurvey().getLastPeriod());
//				if (lastDataValue != null) lastValue = lastDataValue.getValue();
//			}
			enteredValue = new SurveyEnteredValue(element, organisation.getOrganisationUnit(), Value.NULL, null);
			surveyValueService.save(enteredValue);
		}
		return enteredValue;
	}

	
	public void setOrganisationService(OrganisationService organisationService) {
		this.organisationService = organisationService;
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}
	
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication;
	}
	
	// for internal call through transactional proxy
	private SurveyPageService getMe() {
		return grailsApplication.getMainContext().getBean(SurveyPageService.class);
	}
}
