package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationService;
import org.chai.kevin.location.DataEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.springframework.transaction.annotation.Transactional;

public class SummaryService {

	private LocationService locationService;
	private SurveyValueService surveyValueService;
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionTable(DataEntity dataEntity, SurveyObjective objective) {
		List<SurveySection> sections = objective.getSections(dataEntity.getType());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(dataEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), dataEntity, null, section, true, false, true);
			
			questionSummaryMap.put(section, new QuestionSummary(questions.size(), completedQuestions));
		}
		return new SummaryPage(questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveTable(DataEntity dataEntity, Survey survey) {
		List<SurveyObjective> objectives = survey.getObjectives(dataEntity.getType());
		Map<SurveyObjective, QuestionSummary> questionSummaryMap = new HashMap<SurveyObjective, QuestionSummary>();
		Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		
		for (SurveyObjective objective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, dataEntity);
			List<SurveyQuestion> questions = objective.getQuestions(dataEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataEntity, objective, null, true, false, true);
			
			questionSummaryMap.put(objective, new QuestionSummary(questions.size(), completedQuestions));
			enteredObjectiveMap.put(objective, enteredObjective);
		}
		
		return new SummaryPage(enteredObjectiveMap, questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SummaryPage getSurveySummaryPage(LocationEntity location, Survey survey) {
		List<DataEntity> facilities = locationService.getDataEntities(location);
		
		Map<DataEntityType, List<SurveyObjective>> objectiveMap = new HashMap<DataEntityType, List<SurveyObjective>>();
		Map<DataEntityType, List<SurveyQuestion>> questionMap = new HashMap<DataEntityType, List<SurveyQuestion>>();

		Map<DataEntity, QuestionSummary> questionSummaryMap = new HashMap<DataEntity, QuestionSummary>();
		Map<DataEntity, ObjectiveSummary> objectiveSummaryMap = new HashMap<DataEntity, ObjectiveSummary>();
		for (DataEntity facility : facilities) {
			if (!objectiveMap.containsKey(facility.getType())) {
				objectiveMap.put(facility.getType(), survey.getObjectives(facility.getType()));
			}
			Integer submittedObjectives = surveyValueService.getNumberOfSurveyEnteredObjectives(survey, facility, true, null, null);
			
			if (!questionMap.containsKey(facility.getType())) {
				List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
				for (SurveyObjective objective : objectiveMap.get(facility.getType())) {
					questions.addAll(objective.getQuestions(facility.getType()));				
				}
				questionMap.put(facility.getType(), questions);
			}
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, facility, null, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questionMap.get(facility.getType()).size(), completedQuestions);
			ObjectiveSummary objectiveSummary = new ObjectiveSummary(objectiveMap.get(facility.getType()).size(), submittedObjectives);
			
			questionSummaryMap.put(facility, questionSummary);
			objectiveSummaryMap.put(facility, objectiveSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap, objectiveSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getObjectiveSummaryPage(LocationEntity location, SurveyObjective objective) {
		List<DataEntity> facilities = locationService.getDataEntities(location);

		Map<DataEntity, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<DataEntity, SurveyEnteredObjective>();
		Map<DataEntity, QuestionSummary> questionSummaryMap = new HashMap<DataEntity, QuestionSummary>();
		for (DataEntity facility : facilities) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, facility);
			List<SurveyQuestion> questions = objective.getQuestions(facility.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), facility, objective, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredObjectiveMap.put(facility, enteredObjective);
			questionSummaryMap.put(facility, questionSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap, enteredObjectiveMap, true);
	}
	
	@Transactional(readOnly = true)
	public SummaryPage getSectionSummaryPage(LocationEntity location, SurveySection section) {
		List<DataEntity> facilities = locationService.getDataEntities(location);

		Map<DataEntity, QuestionSummary> questionSummaryMap = new HashMap<DataEntity, QuestionSummary>();
		
		for (DataEntity facility : facilities) {
			List<SurveyQuestion> questions = section.getQuestions(facility.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(section.getSurvey(), facility, null, section, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			questionSummaryMap.put(facility, questionSummary);
		}
		return new SummaryPage(facilities, questionSummaryMap);		
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
}
