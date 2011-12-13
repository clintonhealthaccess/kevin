package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.chai.kevin.Orderable;
import org.chai.kevin.Ordering;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.hisp.dhis.period.Period;

public class SurveyPage {

	private final static Log log = LogFactory.getLog(SurveyPage.class);
	
	private DataEntity entity;
	private Survey survey;
	private SurveyObjective objective;
	private SurveySection section;
	private Map<SurveyObjective, SurveyEnteredObjective> objectives;
	private Map<SurveySection, SurveyEnteredSection> sections;
	private Map<SurveyQuestion, SurveyEnteredQuestion> questions;
	private Map<SurveyElement, SurveyEnteredValue> elements;
	private Comparator<Orderable<Ordering>> comparator;
	private Map<String, Enum> enums;
	
	public SurveyPage(DataEntity entity, Survey survey, 
			SurveyObjective objective, SurveySection section,
			Map<SurveyObjective, SurveyEnteredObjective> objectives,
			Map<SurveySection, SurveyEnteredSection> sections,
			Map<SurveyQuestion, SurveyEnteredQuestion> questions,
			Map<SurveyElement, SurveyEnteredValue> elements,
			Map<String, Enum> enums,
			Comparator<Orderable<Ordering>> comparator) {
		super();
		this.entity = entity;
		this.survey = survey;
		this.objective = objective;
		this.section = section;
		this.objectives = objectives;
		this.sections = sections;
		this.questions = questions;
		this.elements = elements;
		this.enums = enums;
		this.comparator = comparator;
	}

	public Period getPeriod() {
		return survey.getPeriod();
	}
	
	public DataEntity getOrganisation() {
		return entity;
	}

	public Survey getSurvey() {
		return survey;
	}

	public SurveyObjective getObjective() {
		return objective;
	}
	
	public SurveySection getSection() {
		return section;
	}
	
	public Enum getEnum(String code) {
		return enums.get(code);
	}
	
	public Map<SurveyObjective, SurveyEnteredObjective> getEnteredObjectives() {
		return objectives;
	}

	public Map<SurveySection, SurveyEnteredSection> getEnteredSections() {
		return sections;
	}

	public Map<SurveyQuestion, SurveyEnteredQuestion> getEnteredQuestions() {
		return questions;
	}

	public Map<SurveyElement, SurveyEnteredValue> getElements() {
		return elements;
	}
	
	public Integer getQuestionNumber(SurveyQuestion question) {
		return getQuestions(question.getSection()).indexOf(question)+1;
	}

	public List<SurveyCheckboxOption> getOptions(SurveyCheckboxQuestion question) {
		List<SurveyCheckboxOption> options = question.getOptions(entity.getType());
		Collections.sort(options);
		return options;
	}
	
	public List<SurveyTableColumn> getColumns(SurveyTableQuestion question) {
		List<SurveyTableColumn> columns = question.getColumns(entity.getType());
		Collections.sort(columns);
		return columns;
	}
	
	public List<SurveyTableRow> getRows(SurveyTableQuestion question) {
		List<SurveyTableRow> rows = question.getRows(entity.getType());
		Collections.sort(rows);
		return rows;
	}
	
	public List<SurveyQuestion> getQuestions(SurveySection section) {
		List<SurveyQuestion> questions = section.getQuestions(entity.getType());
		Collections.sort(questions);
		return questions;
	}
	
	public List<SurveySection> getSections(SurveyObjective objective) {
		List<SurveySection> sections = objective.getSections(entity.getType());
		Collections.sort(sections);
		return sections;
	}
	
	public List<SurveyObjective> getObjectives() {
		List<SurveyObjective> objectives = survey.getObjectives(entity.getType());
		Collections.sort(objectives);
		return objectives;
	}
	
	public List<EnumOption> getEnumOptions(Enum enume) {
		if (enume == null) return new ArrayList<EnumOption>();
		List<EnumOption> options = enume.getActiveEnumOptions();
		Collections.sort(options, comparator);
		return options;
	}

	public List<SurveySection> getIncompleteSections(SurveyObjective objective) {
		if (log.isDebugEnabled()) log.debug("getIncompleteSections(objective="+objective+")");
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveySection section : objective.getSections(entity.getType())) {
			if (!sections.get(section).isComplete()) result.add(section);
		}
		if (log.isDebugEnabled()) log.debug("getIncompleteSections(...)="+result);
		return result;
	}
	
	public List<SurveyQuestion> getInvalidQuestions(SurveyObjective objective) {
		if (log.isDebugEnabled()) log.debug("getInvalidQuestions(objective="+objective+")");
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection section : objective.getSections(entity.getType())) {
			for (SurveyQuestion question : section.getQuestions(entity.getType())) {
				if (questions.get(question).isInvalid() &&  !questions.get(question).isSkipped()) {
					result.add(question);
				}
			}
		}
		if (log.isDebugEnabled()) log.debug("getInvalidQuestions(...)="+result);
		return result;
	}
	
	public List<SurveyQuestion> getListQuestions(Survey survey){
		List<SurveyQuestion> simpleQuestions = new ArrayList<SurveyQuestion>();
		for (SurveyObjective obj : survey.getObjectives(entity.getType()))
			for(SurveySection section: obj.getSections(entity.getType()))
				for(SurveyQuestion question: section.getQuestions(entity.getType()))
					//TODO this has to apply to all type questions
					if(question instanceof SurveySimpleQuestion) {
						SurveySimpleQuestion simpleQuestion = (SurveySimpleQuestion)question;
						if (simpleQuestion.getSurveyElement().getDataElement().getType().getType() == ValueType.LIST) {
							simpleQuestions.add(simpleQuestion);
						}
					}
		return simpleQuestions;
	}
	
	public boolean isLastSection(SurveySection surveySection) {
		List<SurveySection> sections = surveySection.getObjective().getSections(entity.getType());
		if (sections.indexOf(surveySection) == sections.size() - 1) return true;
		return false;
	}
	
	public SurveySection getNextSection(SurveySection surveySection) {
		List<SurveySection> sections = surveySection.getObjective().getSections(entity.getType());
		int index = sections.indexOf(surveySection);
		return sections.get(index+1);
	}
	
	public boolean canSubmit(SurveyObjective surveyObjective) {
		return !objectives.get(surveyObjective).isClosed() && objectives.get(surveyObjective).isComplete() && !objectives.get(surveyObjective).isInvalid();
	}
	
	public boolean isReadonly(SurveyObjective surveyObjective) {
		return !surveyObjective.getSurvey().isActive()
		|| !SecurityUtils.getSubject().isPermitted("editSurvey:save:"+entity.getId()) 
		|| objectives.get(objective).isClosed(); 
	}
}
