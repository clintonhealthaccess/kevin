package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationService;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyObjective;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyValueService;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;
import org.springframework.transaction.annotation.Transactional;

public class SummaryService {

	private LocationService locationService;
	private SurveyValueService surveyValueService;
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionTable(DataLocationEntity dataLocationEntity, SurveyObjective objective) {
		List<SurveySection> sections = objective.getSections(dataLocationEntity.getType());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(dataLocationEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), dataLocationEntity, null, section, true, false, true);
			
			questionSummaryMap.put(section, new QuestionSummary(questions.size(), completedQuestions));
		}
		return new SurveySummaryPage(questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getObjectiveTable(DataLocationEntity dataLocationEntity, Survey survey) {
		List<SurveyObjective> objectives = survey.getObjectives(dataLocationEntity.getType());
		Map<SurveyObjective, QuestionSummary> questionSummaryMap = new HashMap<SurveyObjective, QuestionSummary>();
		Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<SurveyObjective, SurveyEnteredObjective>();
		
		for (SurveyObjective objective : objectives) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, dataLocationEntity);
			List<SurveyQuestion> questions = objective.getQuestions(dataLocationEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataLocationEntity, objective, null, true, false, true);
			
			questionSummaryMap.put(objective, new QuestionSummary(questions.size(), completedQuestions));
			enteredObjectiveMap.put(objective, enteredObjective);
		}
		
		return new SurveySummaryPage(enteredObjectiveMap, questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSurveySummaryPage(LocationEntity location, Survey survey) {
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, null);
		
		Map<DataEntityType, List<SurveyObjective>> objectiveMap = new HashMap<DataEntityType, List<SurveyObjective>>();
		Map<DataEntityType, List<SurveyQuestion>> questionMap = new HashMap<DataEntityType, List<SurveyQuestion>>();

		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		Map<DataLocationEntity, ObjectiveSummary> objectiveSummaryMap = new HashMap<DataLocationEntity, ObjectiveSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity facility : facilities) {
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
			
			totalQuestions += questionMap.get(facility.getType()).size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), facilities, questionSummaryMap, objectiveSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getObjectiveSummaryPage(LocationEntity location, SurveyObjective objective) {
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, null);

		Map<DataLocationEntity, SurveyEnteredObjective> enteredObjectiveMap = new HashMap<DataLocationEntity, SurveyEnteredObjective>();
		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity facility : facilities) {
			SurveyEnteredObjective enteredObjective = surveyValueService.getSurveyEnteredObjective(objective, facility);
			List<SurveyQuestion> questions = objective.getQuestions(facility.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(objective.getSurvey(), facility, objective, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredObjectiveMap.put(facility, enteredObjective);
			questionSummaryMap.put(facility, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), facilities, questionSummaryMap, enteredObjectiveMap, true);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionSummaryPage(LocationEntity location, SurveySection section) {
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, null);

		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity facility : facilities) {
			List<SurveyQuestion> questions = section.getQuestions(facility.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(section.getSurvey(), facility, null, section, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			questionSummaryMap.put(facility, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), facilities, questionSummaryMap);		
	}
	
	public void setLocationService(LocationService locationService) {
		this.locationService = locationService;
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
}
