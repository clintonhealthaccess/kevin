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
import org.chai.kevin.Period;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.form.FormEnteredValue;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;

public class SurveyPage {

	private final static Log log = LogFactory.getLog(SurveyPage.class);
	
	private DataLocation dataLocation;
	private Survey survey;
	private SurveyProgram program;
	private SurveySection section;
	private Map<SurveyProgram, SurveyEnteredProgram> programs;
	private Map<SurveySection, SurveyEnteredSection> sections;
	private Map<SurveyQuestion, SurveyEnteredQuestion> questions;
	private Map<SurveyElement, FormEnteredValue> elements;
//	private Comparator<Orderable<Ordering>> comparator;
	private Map<String, Enum> enums;
	
	public SurveyPage(DataLocation dataLocation, Survey survey, 
			SurveyProgram program, SurveySection section,
			Map<SurveyProgram, SurveyEnteredProgram> programs,
			Map<SurveySection, SurveyEnteredSection> sections,
			Map<SurveyQuestion, SurveyEnteredQuestion> questions,
			Map<SurveyElement, FormEnteredValue> elements,
//			Comparator<Orderable<Ordering>> comparator
			Map<String, Enum> enums) {
		super();
		this.dataLocation = dataLocation;
		this.survey = survey;
		this.program = program;
		this.section = section;
		this.programs = programs;
		this.sections = sections;
		this.questions = questions;
		this.elements = elements;
		this.enums = enums;
//		this.comparator = comparator;
	}

	public Period getPeriod() {
		return survey.getPeriod();
	}
	
	public DataLocation getLocation() {
		return dataLocation;
	}

	public Survey getSurvey() {
		return survey;
	}

	public SurveyProgram getProgram() {
		return program;
	}
	
	public SurveySection getSection() {
		return section;
	}
	
	public Map<String, Enum> getEnums() {
		return enums;
	}
	
//	public Enum getEnum(String code) {
//		return enums.get(code);
//	}
	
	public Map<SurveyProgram, SurveyEnteredProgram> getEnteredPrograms() {
		return programs;
	}

	public Map<SurveySection, SurveyEnteredSection> getEnteredSections() {
		return sections;
	}

	public Map<SurveyQuestion, SurveyEnteredQuestion> getEnteredQuestions() {
		return questions;
	}

	public Map<SurveyElement, FormEnteredValue> getElements() {
		return elements;
	}
	
	public Integer getQuestionNumber(SurveyQuestion question) {
		return getQuestions(question.getSection()).indexOf(question)+1;
	}

	public List<SurveyCheckboxOption> getOptions(SurveyCheckboxQuestion question) {
		List<SurveyCheckboxOption> options = question.getOptions(dataLocation.getType());
		Collections.sort(options);
		return options;
	}
	
	public List<SurveyTableColumn> getColumns(SurveyTableQuestion question) {
		List<SurveyTableColumn> columns = question.getColumns(dataLocation.getType());
		Collections.sort(columns);
		return columns;
	}
	
	public List<SurveyTableRow> getRows(SurveyTableQuestion question) {
		List<SurveyTableRow> rows = question.getRows(dataLocation.getType());
		Collections.sort(rows);
		return rows;
	}
	
	public List<SurveyQuestion> getQuestions(SurveySection section) {
		List<SurveyQuestion> questions = section.getQuestions(dataLocation.getType());
		Collections.sort(questions);
		return questions;
	}
	
	public List<SurveySection> getSections(SurveyProgram program) {
		List<SurveySection> sections = program.getSections(dataLocation.getType());
		Collections.sort(sections);
		return sections;
	}
	
	public List<SurveyProgram> getPrograms() {
		List<SurveyProgram> programs = survey.getPrograms(dataLocation.getType());
		Collections.sort(programs);
		return programs;
	}
	
//	public List<EnumOption> getEnumOptions(Enum enume) {
//		if (enume == null) return new ArrayList<EnumOption>();
//		List<EnumOption> options = enume.getActiveEnumOptions();
//		Collections.sort(options, comparator);
//		return options;
//	}

	public List<SurveySection> getIncompleteSections(SurveyProgram program) {
		if (log.isDebugEnabled()) log.debug("getIncompleteSections(program="+program+")");
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveySection section : program.getSections(dataLocation.getType())) {
			if (!sections.get(section).isComplete()) result.add(section);
		}
		if (log.isDebugEnabled()) log.debug("getIncompleteSections(...)="+result);
		return result;
	}
	
	public List<SurveyQuestion> getInvalidQuestions(SurveyProgram program) {
		if (log.isDebugEnabled()) log.debug("getInvalidQuestions(program="+program+")");
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection section : program.getSections(dataLocation.getType())) {
			for (SurveyQuestion question : section.getQuestions(dataLocation.getType())) {
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
		for (SurveyProgram obj : survey.getPrograms(dataLocation.getType()))
			for(SurveySection section: obj.getSections(dataLocation.getType()))
				for(SurveyQuestion question: section.getQuestions(dataLocation.getType()))
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
		List<SurveySection> sections = surveySection.getProgram().getSections(dataLocation.getType());
		if (sections.indexOf(surveySection) == sections.size() - 1) return true;
		return false;
	}
	
	public SurveySection getNextSection(SurveySection surveySection) {
		List<SurveySection> sections = surveySection.getProgram().getSections(dataLocation.getType());
		int index = sections.indexOf(surveySection);
		return sections.get(index+1);
	}
	
	public boolean canSubmit(SurveyProgram surveyProgram) {
		Map<SurveyProgram, SurveyEnteredProgram> programs = this.getEnteredPrograms();
		boolean isClosed = programs.get(surveyProgram).isClosed();
		boolean isComplete = programs.get(surveyProgram).isComplete();
		boolean isInvalid = programs.get(surveyProgram).isInvalid();
		boolean canSubmit = !isClosed && isComplete && !isInvalid;		
		return canSubmit;
	}
	
	public boolean isReadonly(SurveyProgram surveyProgram) {
		return !surveyProgram.getSurvey().isActive()
		|| !SecurityUtils.getSubject().isPermitted("editSurvey:save:"+dataLocation.getId()) 
		|| programs.get(program).isClosed(); 
	}
}
