package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.LocationService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.springframework.transaction.annotation.Transactional;

public class SummaryService {

	private LocationService locationService;
	private SurveyValueService surveyValueService;
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionTable(Organisation organisation, SurveyObjective objective) {
		locationService.loadGroup(organisation);
		
		List<SurveySection> sections = objective.getSections(organisation.getOrganisationUnitGroup());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(organisation.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), organisation.getOrganisationUnit(), null, section, true, false, true);
			
			questionSummaryMap.put(section, new QuestionSummary(questions.size(), completedQuestions));
		}
		return new SummaryPage(questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveTable(Organisation organisation, Survey survey) {
		locationService.loadGroup(organisation);
		
		List<SurveyObjective> objectives = survey.getObjectives(organisation.getOrganisationUnitGroup());
		Map<SurveyObjective, QuestionSummary> questionSummaryMap = new HashMap<SurveyObjective, QuestionSummary>();
		Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		
		for (SurveyObjective objective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, organisation.getOrganisationUnit());
			List<SurveyQuestion> questions = objective.getQuestions(organisation.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, organisation.getOrganisationUnit(), objective, null, true, false, true);
			
			questionSummaryMap.put(objective, new QuestionSummary(questions.size(), completedQuestions));
			enteredObjectiveMap.put(objective, enteredObjective);
		}
		
		return new SummaryPage(enteredObjectiveMap, questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SummaryPage getSurveySummaryPage(Organisation organisation, Survey survey) {
		List<Organisation> facilities = locationService.getChildrenOfLevel(organisation, locationService.getFacilityLevel());
		Map<OrganisationUnitGroup, List<SurveyObjective>> objectiveMap = new HashMap<OrganisationUnitGroup, List<SurveyObjective>>();
		Map<OrganisationUnitGroup, List<SurveyQuestion>> questionMap = new HashMap<OrganisationUnitGroup, List<SurveyQuestion>>();

		Map<Organisation, QuestionSummary> questionSummaryMap = new HashMap<Organisation, QuestionSummary>();
		Map<Organisation, ObjectiveSummary> objectiveSummaryMap = new HashMap<Organisation, ObjectiveSummary>();
		for (Organisation facility : facilities) {
			locationService.loadGroup(facility);

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
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, facility.getOrganisationUnit(), null, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questionMap.get(facility.getOrganisationUnitGroup()).size(), completedQuestions);
			ObjectiveSummary objectiveSummary = new ObjectiveSummary(objectiveMap.get(facility.getOrganisationUnitGroup()).size(), submittedObjectives);
			
			questionSummaryMap.put(facility, questionSummary);
			objectiveSummaryMap.put(facility, objectiveSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap, objectiveSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveSummaryPage(Organisation organisation, SurveyObjective objective) {
		List<Organisation> facilities = locationService.getChildrenOfLevel(organisation, locationService.getFacilityLevel());

		Map<Organisation, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<Organisation, SurveyEnteredObjective>();
		Map<Organisation, QuestionSummary> questionSummaryMap = new HashMap<Organisation, QuestionSummary>();
		for (Organisation facility : facilities) {
			locationService.loadGroup(facility);						
			
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, facility.getOrganisationUnit());
			List<SurveyQuestion> questions = objective.getQuestions(facility.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), facility.getOrganisationUnit(), objective, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredObjectiveMap.put(facility, enteredObjective);
			questionSummaryMap.put(facility, questionSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap, enteredObjectiveMap, true);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionSummaryPage(Organisation organisation, SurveySection section) {
		List<Organisation> facilities = locationService.getChildrenOfLevel(organisation, locationService.getFacilityLevel());

		Map<Organisation, QuestionSummary> questionSummaryMap = new HashMap<Organisation, QuestionSummary>();
		
		for (Organisation facility : facilities) {
			locationService.loadGroup(facility);						
			
			List<SurveyQuestion> questions = section.getQuestions(facility.getOrganisationUnitGroup());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(section.getSurvey(), facility.getOrganisationUnit(), null, section, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			questionSummaryMap.put(facility, questionSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap);		
	}
	
	public void setOrganisationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
}
