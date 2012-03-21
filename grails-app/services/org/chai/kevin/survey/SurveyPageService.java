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
import org.chai.kevin.data.DataService;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.Type;
import org.chai.kevin.data.Type.TypeVisitor;
import org.chai.kevin.data.Type.ValuePredicate;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.form.FormElement;
import org.chai.kevin.form.FormElementService;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.form.FormValidationService;
import org.chai.kevin.form.FormValidationService.ValidatableLocator;
import org.chai.kevin.location.CalculationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyElement.SurveyElementCalculator;
import org.chai.kevin.survey.SurveyElement.SurveyElementSubmitter;
import org.chai.kevin.survey.SurveyQuestion.QuestionType;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyLog;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.ValidatableValue;
import org.chai.kevin.value.Value;
import org.chai.kevin.value.ValueService;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SurveyPageService {
	
	private static Log log = LogFactory.getLog(SurveyPageService.class);
	
	private SurveyService surveyService;
	private FormElementService formElementService;
	private SurveyValueService surveyValueService;
	private ValueService valueService;
	private DataService dataService;
	private FormValidationService formValidationService;
	private SessionFactory sessionFactory;
	private GrailsApplication grailsApplication;
	
	@Transactional(readOnly = true)
	public Survey getDefaultSurvey() {
		return (Survey)sessionFactory.getCurrentSession()
			.createCriteria(Survey.class).add(Restrictions.eq("active", true)).uniqueResult();
	}
	
	private void collectEnums(FormElement element, final Map<String, Enum> enums) {
		element.getDataElement().getType().visit(new TypeVisitor() {
			@Override
			public void handle(Type type, String prefix) {
				if (type.getType() == ValueType.ENUM) {
					if (!enums.containsKey(type.getEnumCode())) {
						enums.put(type.getEnumCode(), dataService.findEnumByCode(type.getEnumCode()));
					}
				}
			}
		});
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(DataLocationEntity entity, SurveyQuestion currentQuestion) {
		if (log.isDebugEnabled()) log.debug("getSurveyPage(entity="+entity+", currentQuestion="+currentQuestion+")");
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		Map<SurveyElement, FormEnteredValue> elements = new HashMap<SurveyElement, FormEnteredValue>();
		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		Map<String, Enum> enums = new HashMap<String, Enum>();
		
		SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(entity, currentQuestion);
		questions.put(currentQuestion, enteredQuestion);
		for (SurveyElement element : currentQuestion.getSurveyElements(entity.getType())) {
			FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
			elements.put(element, enteredValue);
			collectEnums(element, enums);
		}
		
		SurveyPage page = new SurveyPage(entity, currentQuestion.getSurvey(), null, null, null, null, questions, elements, enums);
		if (log.isDebugEnabled()) log.debug("getSurveyPage(...)="+page);
		return page;
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(DataLocationEntity entity, SurveySection currentSection) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		SurveyProgram currentProgram = currentSection.getProgram();
		Survey survey = currentProgram.getSurvey();
		
		Map<SurveyProgram, SurveyEnteredProgram> programs = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		Map<SurveySection, SurveyEnteredSection> sections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyProgram program : survey.getPrograms(entity.getType())) {
			SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
			programs.put(program, enteredProgram);
			
			for (SurveySection section : program.getSections(entity.getType())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(entity, section);
				sections.put(section, enteredSection);
			}
		}
		
		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		Map<SurveyElement, FormEnteredValue> elements = new HashMap<SurveyElement, FormEnteredValue>();
		Map<String, Enum> enums = new HashMap<String, Enum>();
		for (SurveyQuestion question : currentSection.getQuestions(entity.getType())) {
			SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(entity, question);
			questions.put(question, enteredQuestion);
			
			for (SurveyElement element : question.getSurveyElements(entity.getType())) {
				FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
				elements.put(element, enteredValue);
				collectEnums(element, enums);
			}
		}
		
		SurveyPage page = new SurveyPage(entity, survey, currentProgram, currentSection, programs, sections, questions, elements, enums);
		if (log.isDebugEnabled()) log.debug("getSurveyPage(...)="+page);
		return page;
	}
	
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(DataLocationEntity entity, SurveyProgram currentProgram) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		Survey survey = currentProgram.getSurvey();
		
		Map<SurveyProgram, SurveyEnteredProgram> programs = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		Map<SurveySection, SurveyEnteredSection> sections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyProgram program : survey.getPrograms(entity.getType())) {
			SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
			programs.put(program, enteredProgram);
			
			for (SurveySection section : program.getSections(entity.getType())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(entity, section);
				sections.put(section, enteredSection);
			}
		}

		Map<SurveyQuestion, SurveyEnteredQuestion> questions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		Map<SurveyElement, FormEnteredValue> elements = new HashMap<SurveyElement, FormEnteredValue>();
		Map<String, Enum> enums = new HashMap<String, Enum>();
		for (SurveySection section : currentProgram.getSections(entity.getType())) {
			section = (SurveySection)sessionFactory.getCurrentSession().get(SurveySection.class, section.getId());
			for (SurveyQuestion question : section.getQuestions(entity.getType())) {
				SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(entity, question);
				questions.put(question, enteredQuestion);
				
				for (SurveyElement element : question.getSurveyElements(entity.getType())) {
					FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
					elements.put(element, enteredValue);
					collectEnums(element, enums);
				}
			}
		}
		
		SurveyPage page = new SurveyPage(entity, survey, currentProgram, null, programs, sections, questions, elements, enums);
		if (log.isDebugEnabled()) log.debug("getSurveyPage(...)="+page);
		return page;
	}
	
	@Transactional(readOnly = false)
	public SurveyPage getSurveyPagePrint(DataLocationEntity entity,Survey survey) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		DataEntityType entityUnitGroup = entity.getType();
		
		Map<SurveyElement, FormEnteredValue> elements = new LinkedHashMap<SurveyElement, FormEnteredValue>();
		Map<String, Enum> enums = new HashMap<String, Enum>();
		
		for (SurveyProgram program : survey.getPrograms(entityUnitGroup)) {
			for (SurveySection section : program.getSections(entityUnitGroup)) {
				for (SurveyQuestion question : section.getQuestions(entityUnitGroup)) {
					for (SurveyElement element : question.getSurveyElements(entityUnitGroup)) {
						FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
						elements.put(element, enteredValue);
						collectEnums(element, enums);
					}
				}
			}

		}
		return new SurveyPage(entity, survey, null, null, null, null,null, elements, enums);
	}
	

	@Transactional(readOnly = false)
	public SurveyPage getSurveyPage(DataLocationEntity entity, Survey survey) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
		
		Map<SurveyProgram, SurveyEnteredProgram> programs = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		Map<SurveySection, SurveyEnteredSection> sections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyProgram program : survey.getPrograms(entity.getType())) {
			SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
			programs.put(program, enteredProgram);
			
			for (SurveySection section : program.getSections(entity.getType())) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(entity, section);
				sections.put(section, enteredSection);
			}
		}
		return new SurveyPage(entity, survey, null, null, programs, sections, null, null, null);
	}
	
	@Transactional(readOnly = false)
	public void refresh(CalculationEntity entity, Survey survey, boolean closeIfComplete) {
		List<DataLocationEntity> facilities = entity.collectDataLocationEntities(null, null);
		
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		for (DataLocationEntity facility : facilities) {
			survey = (Survey)sessionFactory.getCurrentSession().load(Survey.class, survey.getId());
			facility = (DataLocationEntity)sessionFactory.getCurrentSession().get(DataLocationEntity.class, facility.getId());

			getMe().refreshSurveyForFacilityWithNewTransaction(facility, survey, closeIfComplete);
			sessionFactory.getCurrentSession().clear();
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
	public void refreshSurveyForFacilityWithNewTransaction(DataLocationEntity facility, Survey survey, boolean closeIfComplete) {
		refreshSurveyForFacility(facility, survey, closeIfComplete);
	}
	
	@Transactional(readOnly = false)
	public void refreshSurveyForFacility(DataLocationEntity facility, Survey survey, boolean closeIfComplete) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Set<SurveyProgram> validPrograms = new HashSet<SurveyProgram>(survey.getPrograms(facility.getType()));
		for (SurveyProgram program : survey.getPrograms()) {
			if (validPrograms.contains(program)) refreshProgramForFacility(facility, program, closeIfComplete);
			else deleteSurveyEnteredProgram(program, facility);
		}
	}
	
	private void refreshProgramForFacility(DataLocationEntity facility, SurveyProgram program, boolean closeIfComplete) {
		Set<SurveySection> validSections = new HashSet<SurveySection>(program.getSections(facility.getType()));
		for (SurveySection section : program.getSections()) {
			if (validSections.contains(section)) refreshSectionForFacility(facility, section);
			else deleteSurveyEnteredSection(section, facility);
		}
		
		SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(facility, program);
		setProgramStatus(enteredProgram, facility);
		if (closeIfComplete && enteredProgram.isComplete() && !enteredProgram.isInvalid()) enteredProgram.setClosed(true); 
		surveyValueService.save(enteredProgram);
	}
	
//	@Transactional(readOnly=false, propagation=Propagation.REQUIRES_NEW)
//	public void refreshSectionForFacilityWithNewTransaction(DataLocationEntity facility, SurveySection section) {
//		refreshSectionForFacility(facility, section);
//	}
	
	@Transactional(readOnly = false)
	public void refreshSectionForFacility(DataLocationEntity facility, SurveySection section) {
		sessionFactory.getCurrentSession().setFlushMode(FlushMode.COMMIT);
//		sessionFactory.getCurrentSession().setCacheMode(CacheMode.IGNORE);
		
		Set<SurveyQuestion> validQuestions = new HashSet<SurveyQuestion>(section.getQuestions(facility.getType()));
		for (SurveyQuestion question : section.getQuestions()) {
			if (validQuestions.contains(question)) refreshQuestionForFacility(facility, question);
			else deleteSurveyEnteredQuestion(question, facility);
		}
		SurveyEnteredSection enteredSection = getSurveyEnteredSection(facility, section);
		setSectionStatus(enteredSection, facility);
		surveyValueService.save(enteredSection);
	}
	
	private void refreshQuestionForFacility(DataLocationEntity facility, SurveyQuestion question) {
		Set<FormElement> validElements = new HashSet<FormElement>(question.getSurveyElements(facility.getType()));
		for (SurveyElement element : question.getSurveyElements()) {
			if (validElements.contains(element)) refreshElementForFacility(facility, element);
			else deleteSurveyEnteredValue(element, facility);
		}
		
		SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(facility, question);
		setQuestionStatus(enteredQuestion, facility);
		surveyValueService.save(enteredQuestion);
	}
	
	private void refreshElementForFacility(DataLocationEntity facility, SurveyElement element) {
		Survey survey = element.getSurvey();
		
		FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(facility, element);
		RawDataElementValue rawDataElementValue = valueService.getDataElementValue(element.getDataElement(), facility, survey.getPeriod());
		if (rawDataElementValue != null) enteredValue.setValue(rawDataElementValue.getValue());
		else enteredValue.setValue(Value.NULL_INSTANCE());
		if (survey.getLastPeriod() != null) {
			RawDataElementValue lastDataValue = valueService.getDataElementValue(element.getDataElement(), facility, survey.getLastPeriod());
			if (lastDataValue != null) enteredValue.setLastValue(lastDataValue.getValue());
			else enteredValue.setLastValue(Value.NULL_INSTANCE());
		}
		formElementService.save(enteredValue);
	}
	
	// returns the list of modified elements/questions/sections/programs (skip, validation, etc..)
	// we set the isolation level on READ_UNCOMMITTED to avoid deadlocks because in READ_COMMITTED
	// mode, a write lock is acquired at the beginning and never released till this method terminates
	// which causes other sessions calling this method to timeout
	@Transactional(readOnly = false)
	public SurveyPage modify(DataLocationEntity entity, SurveyProgram program, List<SurveyElement> elements, Map<String, Object> params) {
		if (log.isDebugEnabled()) log.debug("modify(entity="+entity+", elements="+elements+")");
		
		// we acquire a write lock on the program
		// this won't change anything for MyISAM tables
		SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
		sessionFactory.getCurrentSession().buildLockRequest(LockOptions.NONE).setLockMode(LockMode.PESSIMISTIC_WRITE).lock(enteredProgram);
		
		SurveyPage surveyPage = null;
		// if the program is not closed, we go on with the save
		if (!enteredProgram.isClosed()) {
			Map<SurveyElement, FormEnteredValue> affectedElements = new HashMap<SurveyElement, FormEnteredValue>();
			// first we save the values
			for (SurveyElement element : elements) {
				if (log.isDebugEnabled()) log.debug("setting new value for element: "+element);
				
				FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
				ValidatableValue validatableValue = enteredValue.getValidatable();
				
				// merge the values
				// this modifies the value object accordingly
				validatableValue.mergeValue(params, "elements["+element.getId()+"].value", new HashSet<String>());
				
				// set the value and save
				// here, a write lock is acquired on the FormEnteredValue that will be kept
				// till the end of the transaction, if in READ_COMMITTED isolation mode, a timeout
				// is likely to occur because the transaction is quite long
				affectedElements.put(element, enteredValue);
				
				// if it is a checkbox question, we need to reset the values to null
				// FIXME THIS IS A HACK
				resetCheckboxQuestion(entity, element, affectedElements);
			}
			// we evaluate the rules
			surveyPage = evaluateRulesAndSave(entity, elements, affectedElements);
		}

		if (log.isDebugEnabled()) log.debug("modify(...)="+surveyPage);
		return surveyPage;
	}
	
	private ValidatableLocator getLocator() {
		return new ValidatableLocator() {
			@Override
			public ValidatableValue getValidatable(Long id, DataLocationEntity location) {
				FormElement element = formElementService.getFormElement(id);
				FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(location, element);
				if (enteredValue == null) return null;
				return enteredValue.getValidatable();
			}
		};
	}
	
	private SurveyPage evaluateRulesAndSave(DataLocationEntity entity, List<? extends FormElement> elements, Map<SurveyElement, FormEnteredValue> affectedElements) {  
		if (log.isDebugEnabled()) log.debug("evaluateRulesAndSave(entity="+entity+", elements="+elements+")");
		
		// second we get the rules that could be affected by the changes
		List<FormEnteredValue> affectedEnteredValues = new ArrayList<FormEnteredValue>();
		List<SurveyEnteredQuestion> affectedEnteredQuestions = new ArrayList<SurveyEnteredQuestion>();
		SurveyElementCalculator elementCalculator = new SurveyElementCalculator(affectedEnteredValues, affectedEnteredQuestions, formValidationService, formElementService, surveyValueService, getLocator());
		
		for (FormElement element : elements) {
			if (log.isDebugEnabled()) log.debug("getting skip and validation rules for element: "+element);

			element.validate(entity, elementCalculator);
			element.executeSkip(entity, elementCalculator);
		}
		
		// third we add the affected values in the lists
		for (FormEnteredValue formEnteredValue : affectedEnteredValues) {
			affectedElements.put((SurveyElement)formEnteredValue.getFormElement(), formEnteredValue);
		}
		Map<SurveyQuestion, SurveyEnteredQuestion> affectedQuestions = new HashMap<SurveyQuestion, SurveyEnteredQuestion>();
		for (SurveyEnteredQuestion surveyEnteredQuestion : affectedEnteredQuestions) {
			affectedQuestions.put(surveyEnteredQuestion.getQuestion(), surveyEnteredQuestion);
		}
		
		// fourth we propagate the affected changes up the survey tree and save
		if (log.isDebugEnabled()) log.debug("propagating changes up the survey tree");
		for (FormElement element : affectedElements.keySet()) {
			SurveyQuestion question = ((SurveyElement)element).getSurveyQuestion();
			if (!affectedQuestions.containsKey(question)) {
				SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(entity, question);
				affectedQuestions.put(question, enteredQuestion);
			}
		}
		
		Map<SurveySection, SurveyEnteredSection> affectedSections = new HashMap<SurveySection, SurveyEnteredSection>();
		for (SurveyEnteredQuestion question : affectedQuestions.values()) {
			// we set the question status correctly and save
			setQuestionStatus(question, entity);
			
			SurveySection section = question.getQuestion().getSection();
			if (!affectedSections.containsKey(section)) {
				SurveyEnteredSection enteredSection = getSurveyEnteredSection(entity, section);
				affectedSections.put(section, enteredSection);
			}
			
		}
		
		Map<SurveyProgram, SurveyEnteredProgram> affectedPrograms = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		for (SurveyEnteredSection section : affectedSections.values()) {
			// we set the section status correctly and save
			setSectionStatus(section, entity);
			
			SurveyProgram program = section.getSection().getProgram();
			if (!affectedPrograms.containsKey(program)) {
				SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
				affectedPrograms.put(program, enteredProgram);
			}
		}
		
		for (SurveyEnteredProgram program : affectedPrograms.values()) {
			// if the program is not closed and available
			// we set the program status correctly and save
			setProgramStatus(program, entity);
		}
		
		// fifth we save all the values
		for (FormEnteredValue formEnteredValue : affectedElements.values()) {
			formElementService.save(formEnteredValue);
		}
		for (SurveyEnteredQuestion surveyEnteredQuestion : affectedQuestions.values()) {
			surveyValueService.save(surveyEnteredQuestion);
		}
		for (SurveyEnteredSection surveyEnteredSection : affectedSections.values()) {
			surveyValueService.save(surveyEnteredSection);
		}
		for (SurveyEnteredProgram surveyEnteredProgram : affectedPrograms.values()) {
			surveyValueService.save(surveyEnteredProgram);
		}
		
		return new SurveyPage(entity, null, null, null, affectedPrograms, affectedSections, affectedQuestions, affectedElements, null);
	}

	// FIXME HACK 
	// TODO get rid of this
	private void resetCheckboxQuestion(DataLocationEntity entity, SurveyElement element, Map<SurveyElement, FormEnteredValue> affectedElements) {
		if (log.isDebugEnabled()) log.debug("question is of type: "+element.getSurveyQuestion().getType());
		if (element.getSurveyQuestion().getType() == QuestionType.CHECKBOX) {
			if (log.isDebugEnabled()) log.debug("checking if checkbox question needs to be reset");
			boolean reset = true;
			for (SurveyElement elementInQuestion : element.getSurveyQuestion().getSurveyElements(entity.getType())) {
				FormEnteredValue enteredValueForElementInQuestion = formElementService.getOrCreateFormEnteredValue(entity, elementInQuestion);

				if (enteredValueForElementInQuestion.getValue().getBooleanValue() == Boolean.TRUE) reset = false;
			}
			if (log.isDebugEnabled()) log.debug("resetting checkbox question: "+reset);
			for (SurveyElement elementInQuestion : element.getSurveyQuestion().getSurveyElements(entity.getType())) {
				FormEnteredValue enteredValueForElementInQuestion = formElementService.getOrCreateFormEnteredValue(entity, elementInQuestion);

				if (reset) enteredValueForElementInQuestion.getValue().setJsonObject(Value.NULL_INSTANCE().getJsonObject());
				else if (enteredValueForElementInQuestion.getValue().isNull()) {
					enteredValueForElementInQuestion.getValue().setJsonObject(enteredValueForElementInQuestion.getType().getValue(false).getJsonObject());
				}
				
				affectedElements.put(elementInQuestion, enteredValueForElementInQuestion);
			}
		}
	}
	
	private void setProgramStatus(SurveyEnteredProgram program, DataLocationEntity entity) {
		Boolean complete = true;
		Boolean invalid = false;
		for (SurveySection section : program.getProgram().getSections(entity.getType())) {
			SurveyEnteredSection enteredSection = getSurveyEnteredSection(entity, section);
			if (!enteredSection.isComplete()) complete = false;
			if (enteredSection.isInvalid()) invalid = true;
		}
		program.setComplete(complete);
		program.setInvalid(invalid);
	}
	
	private void setSectionStatus(SurveyEnteredSection section, DataLocationEntity entity) {
		Boolean complete = true;
		Boolean invalid = false;
		for (SurveyQuestion question : section.getSection().getQuestions(entity.getType())) {
			SurveyEnteredQuestion enteredQuestion = surveyValueService.getOrCreateSurveyEnteredQuestion(entity, question);
			if (!enteredQuestion.isComplete() && !enteredQuestion.isSkipped()) complete = false;
			if (enteredQuestion.isInvalid() && !enteredQuestion.isSkipped()) invalid = true;
		}
		section.setInvalid(invalid);
		section.setComplete(complete);
	}
	
	private void setQuestionStatus(SurveyEnteredQuestion question, DataLocationEntity entity) {
		Boolean complete = true;
		Boolean invalid = false;
		
		// TODO replace this method by a call to the survey element service
		for (SurveyElement element : question.getQuestion().getSurveyElements(entity.getType())) {
			FormEnteredValue enteredValue = formElementService.getOrCreateFormEnteredValue(entity, element);
			if (!enteredValue.getValidatable().isComplete()) complete = false;
			if (enteredValue.getValidatable().isInvalid()) invalid = true;
		}
		question.setInvalid(invalid);
		question.setComplete(complete);
	}
	
	@Transactional(readOnly = false)
	public boolean submit(DataLocationEntity entity, SurveyProgram program) {
		
		// first we make sure that the program is valid and complete, so we revalidate it
		List<SurveyElement> elements = program.getElements(entity.getType());
		evaluateRulesAndSave(entity, elements, new HashMap<SurveyElement, FormEnteredValue>());
		
		// we get the updated survey and work from that
		SurveyPage surveyPage = getSurveyPage(entity, program);
		if (surveyPage.canSubmit(program)) {
			SurveyElementSubmitter submitter = new SurveyElementSubmitter(surveyValueService, formElementService, valueService);

			// save all the values to data values
			for (SurveyElement element : elements) {
				element.submit(entity, element.getSurvey().getPeriod(), submitter);
			}
			
			// close the program
			SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program);
			enteredProgram.setClosed(true);
			surveyValueService.save(enteredProgram);
	
			// log the event
			logSurveyEvent(entity, program, "submit");
			
			return true;
		}
		else return false;
	}

	private void logSurveyEvent(DataLocationEntity entity, SurveyProgram program, String event) {
		SurveyLog surveyLog = new SurveyLog(program.getSurvey(), program, entity);
		surveyLog.setEvent(event);
		surveyLog.setTimestamp(new Date());
		sessionFactory.getCurrentSession().save(surveyLog);
	}
	
	public void reopen(DataLocationEntity entity, SurveyProgram program) {
		SurveyEnteredProgram enteredProgram = getSurveyEnteredProgram(entity, program); 
		enteredProgram.setClosed(false);
		surveyValueService.save(enteredProgram);
	}
	
	private SurveyEnteredProgram getSurveyEnteredProgram(DataLocationEntity entity, SurveyProgram surveyProgram) {
		SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(surveyProgram, entity);
		if (enteredProgram == null) {
			enteredProgram = new SurveyEnteredProgram(surveyProgram, entity, false, false, false);
//			setProgramStatus(enteredProgram, entity);
			surveyValueService.save(enteredProgram);
		}
		return enteredProgram;
	}
	
	private SurveyEnteredSection getSurveyEnteredSection(DataLocationEntity entity, SurveySection surveySection) {
		SurveyEnteredSection enteredSection = surveyValueService.getSurveyEnteredSection(surveySection, entity);
		if (enteredSection == null) {
			enteredSection = new SurveyEnteredSection(surveySection, entity, false, false);
//			setSectionStatus(enteredSection, entity);
			surveyValueService.save(enteredSection);
		}
		return enteredSection;
	}
	
	

	private void deleteSurveyEnteredProgram(SurveyProgram program, DataLocationEntity entity) {
		SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, entity);
		if (enteredProgram != null) surveyValueService.delete(enteredProgram); 
		
		for (SurveySection section : program.getSections()) {
			deleteSurveyEnteredSection(section, entity);
		}
	}

	private void deleteSurveyEnteredSection(SurveySection section, DataLocationEntity entity) {
		SurveyEnteredSection enteredSection = surveyValueService.getSurveyEnteredSection(section, entity);
		if (enteredSection != null) surveyValueService.delete(enteredSection);
		
		for (SurveyQuestion question : section.getQuestions()) {
			deleteSurveyEnteredQuestion(question, entity);
		}
	}

	private void deleteSurveyEnteredQuestion(SurveyQuestion question, DataLocationEntity entity) {
		SurveyEnteredQuestion enteredQuestion = surveyValueService.getSurveyEnteredQuestion(question, entity);
		if (enteredQuestion != null) surveyValueService.delete(enteredQuestion);
		
		for (FormElement element : question.getSurveyElements()) {
			deleteSurveyEnteredValue(element, entity);
		}
	}

	private void deleteSurveyEnteredValue(FormElement element, DataLocationEntity entity) {
		FormEnteredValue enteredValue = formElementService.getFormEnteredValue(element, entity);
		if (enteredValue != null) formElementService.delete(enteredValue);
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
	public void setFormElementService(FormElementService formElementService) {
		this.formElementService = formElementService;
	}
	
	public void setValueService(ValueService valueService) {
		this.valueService = valueService;
	}
	
	public void setFormValidationService(FormValidationService formValidationService) {
		this.formValidationService = formValidationService;
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void setSurveyService(SurveyService surveyService) {
		this.surveyService = surveyService;
	}

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}
	
	public void setGrailsApplication(GrailsApplication grailsApplication) {
		this.grailsApplication = grailsApplication;
	}
	
	// for internal call through transactional proxy
	private SurveyPageService getMe() {
		return grailsApplication.getMainContext().getBean(SurveyPageService.class);
	}
}
