package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.Type.ValueType;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredValue;
import org.hisp.dhis.period.Period;

public class SurveyPage {

	private Organisation organisation;
	private Survey survey;
	private SurveyObjective objective;
	private SurveySection section;
	private Map<SurveyObjective, SurveyEnteredObjective> objectives;
	private Map<SurveySection, SurveyEnteredSection> sections;
	private Map<SurveyQuestion, SurveyEnteredQuestion> questions;
	private Map<SurveyElement, SurveyEnteredValue> elements;
	
	public SurveyPage(Organisation organisation, Survey survey, 
			SurveyObjective objective, SurveySection section,
			Map<SurveyObjective, SurveyEnteredObjective> objectives,
			Map<SurveySection, SurveyEnteredSection> sections,
			Map<SurveyQuestion, SurveyEnteredQuestion> questions,
			Map<SurveyElement, SurveyEnteredValue> elements) {
		super();
		this.organisation = organisation;
		this.survey = survey;
		this.objective = objective;
		this.section = section;
		this.objectives = objectives;
		this.sections = sections;
		this.questions = questions;
		this.elements = elements;
	}

	public Period getPeriod() {
		return survey.getPeriod();
	}
	
	public Organisation getOrganisation() {
		return organisation;
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
	
	public Map<SurveyObjective, SurveyEnteredObjective> getObjectives() {
		return objectives;
	}

	public Map<SurveySection, SurveyEnteredSection> getSections() {
		return sections;
	}

	public Map<SurveyQuestion, SurveyEnteredQuestion> getQuestions() {
		return questions;
	}

	public Map<SurveyElement, SurveyEnteredValue> getElements() {
		return elements;
	}

	public List<SurveySection> getIncompleteSections(SurveyObjective objective) {
		List<SurveySection> result = new ArrayList<SurveySection>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			if (!sections.get(section).isComplete()) result.add(section);
		}
		return result;
	}
	
	public List<SurveyQuestion> getInvalidQuestions(SurveyObjective objective) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				if (questions.get(question).isInvalid()
					&& 
					!questions.get(question).isSkipped()) result.add(question);
			}
		}
		return result;
	}
	
	public List<SurveyQuestion> getListQuestions(Survey survey){
		List<SurveyQuestion> simpleQuestions = new ArrayList<SurveyQuestion>();
		for (SurveyObjective obj : survey.getObjectives(organisation.getOrganisationUnitGroup()))
			for(SurveySection section: obj.getSections(organisation.getOrganisationUnitGroup()))
				for(SurveyQuestion question: section.getQuestions(organisation.getOrganisationUnitGroup()))
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
		List<SurveySection> sections = surveySection.getObjective().getSections(organisation.getOrganisationUnitGroup());
		if (sections.indexOf(surveySection) == sections.size() - 1) return true;
		return false;
	}
	
	public SurveySection getNextSection(SurveySection surveySection) {
		List<SurveySection> sections = surveySection.getObjective().getSections(organisation.getOrganisationUnitGroup());
		int index = sections.indexOf(surveySection);
		return sections.get(index+1);
	}
	
	public boolean canSubmit(SurveyObjective surveyObjective) {
		return !objectives.get(surveyObjective).isClosed() && objectives.get(surveyObjective).isComplete() && !objectives.get(surveyObjective).isInvalid();
	}
	
	public boolean isReadonly(SurveyObjective surveyObjective) {
		return !surveyObjective.getSurvey().isActive()
		|| !SecurityUtils.getSubject().isPermitted("editSurvey:save:"+organisation.getId()) 
		|| objectives.get(objective).isClosed(); 
	}
}
