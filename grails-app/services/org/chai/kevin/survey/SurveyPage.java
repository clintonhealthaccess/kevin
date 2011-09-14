package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion;
import org.chai.kevin.survey.validation.SurveyEnteredQuestion.QuestionStatus;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.chai.kevin.survey.validation.SurveyEnteredSection.SectionStatus;
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
			if (sections.get(section).getStatus() == SectionStatus.INCOMPLETE) result.add(section);
		}
		return result;
	}
	
	public List<SurveyQuestion> getInvalidQuestions(SurveyObjective objective) {
		List<SurveyQuestion> result = new ArrayList<SurveyQuestion>();
		for (SurveySection section : objective.getSections(organisation.getOrganisationUnitGroup())) {
			for (SurveyQuestion question : section.getQuestions(organisation.getOrganisationUnitGroup())) {
				if (questions.get(question).getStatus() == QuestionStatus.INVALID 
					&& 
					!questions.get(question).isSkipped()) result.add(question);
			}
		}
		return result;
	}
	
	public boolean isLastSection(SurveySection surveySection) {
		// TODO
		return false;
	}
	
	public boolean canSubmit(SurveyObjective surveyObjective) {
		// TODO
		return false;
	}
}
