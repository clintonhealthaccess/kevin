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
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyValueService;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.springframework.transaction.annotation.Transactional;

public class SummaryService {

	private LocationService locationService;
	private SurveyValueService surveyValueService;
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionTable(DataLocationEntity dataLocationEntity, SurveyProgram program) {
		List<SurveySection> sections = program.getSections(dataLocationEntity.getType());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(dataLocationEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(program.getSurvey(), dataLocationEntity, null, section, true, false, true);
			
			questionSummaryMap.put(section, new QuestionSummary(questions.size(), completedQuestions));
		}
		return new SurveySummaryPage(questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramTable(DataLocationEntity dataLocationEntity, Survey survey) {
		List<SurveyProgram> programs = survey.getPrograms(dataLocationEntity.getType());
		Map<SurveyProgram, QuestionSummary> questionSummaryMap = new HashMap<SurveyProgram, QuestionSummary>();
		Map<SurveyProgram, SurveyEnteredProgram> enteredProgramMap = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		
		for (SurveyProgram program : programs) {
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataLocationEntity);
			List<SurveyQuestion> questions = program.getQuestions(dataLocationEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataLocationEntity, program, null, true, false, true);
			
			questionSummaryMap.put(program, new QuestionSummary(questions.size(), completedQuestions));
			enteredProgramMap.put(program, enteredProgram);
		}
		
		return new SurveySummaryPage(enteredProgramMap, questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSurveySummaryPage(LocationEntity location, Survey survey) {
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, null);
		
		Map<DataEntityType, List<SurveyProgram>> programMap = new HashMap<DataEntityType, List<SurveyProgram>>();
		Map<DataEntityType, List<SurveyQuestion>> questionMap = new HashMap<DataEntityType, List<SurveyQuestion>>();

		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		Map<DataLocationEntity, ProgramSummary> programSummaryMap = new HashMap<DataLocationEntity, ProgramSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity facility : facilities) {
			if (!programMap.containsKey(facility.getType())) {
				programMap.put(facility.getType(), survey.getPrograms(facility.getType()));
			}
			Integer submittedPrograms = surveyValueService.getNumberOfSurveyEnteredPrograms(survey, facility, true, null, null);
			
			if (!questionMap.containsKey(facility.getType())) {
				List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
				for (SurveyProgram program : programMap.get(facility.getType())) {
					questions.addAll(program.getQuestions(facility.getType()));				
				}
				questionMap.put(facility.getType(), questions);
			}
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, facility, null, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questionMap.get(facility.getType()).size(), completedQuestions);
			ProgramSummary programSummary = new ProgramSummary(programMap.get(facility.getType()).size(), submittedPrograms);
			
			questionSummaryMap.put(facility, questionSummary);
			programSummaryMap.put(facility, programSummary);
			
			totalQuestions += questionMap.get(facility.getType()).size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), facilities, questionSummaryMap, programSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramSummaryPage(LocationEntity location, SurveyProgram program) {
		List<DataLocationEntity> facilities = location.collectDataLocationEntities(null, null);

		Map<DataLocationEntity, SurveyEnteredProgram> enteredProgramMap = new HashMap<DataLocationEntity, SurveyEnteredProgram>();
		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity facility : facilities) {
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, facility);
			List<SurveyQuestion> questions = program.getQuestions(facility.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(program.getSurvey(), facility, program, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredProgramMap.put(facility, enteredProgram);
			questionSummaryMap.put(facility, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), facilities, questionSummaryMap, enteredProgramMap, true);
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
