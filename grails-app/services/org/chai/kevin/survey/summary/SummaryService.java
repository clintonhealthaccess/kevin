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
		List<DataLocationEntity> dataEntities = location.collectDataLocationEntities(null, null);
		
		Map<DataEntityType, List<SurveyProgram>> programMap = new HashMap<DataEntityType, List<SurveyProgram>>();
		Map<DataEntityType, List<SurveyQuestion>> questionMap = new HashMap<DataEntityType, List<SurveyQuestion>>();

		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		Map<DataLocationEntity, ProgramSummary> programSummaryMap = new HashMap<DataLocationEntity, ProgramSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity dataEntity : dataEntities) {
			if (!programMap.containsKey(dataEntity.getType())) {
				programMap.put(dataEntity.getType(), survey.getPrograms(dataEntity.getType()));
			}
			Integer submittedPrograms = surveyValueService.getNumberOfSurveyEnteredPrograms(survey, dataEntity, true, null, null);
			
			if (!questionMap.containsKey(dataEntity.getType())) {
				List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
				for (SurveyProgram program : programMap.get(dataEntity.getType())) {
					questions.addAll(program.getQuestions(dataEntity.getType()));				
				}
				questionMap.put(dataEntity.getType(), questions);
			}
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataEntity, null, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questionMap.get(dataEntity.getType()).size(), completedQuestions);
			ProgramSummary programSummary = new ProgramSummary(programMap.get(dataEntity.getType()).size(), submittedPrograms);
			
			questionSummaryMap.put(dataEntity, questionSummary);
			programSummaryMap.put(dataEntity, programSummary);
			
			totalQuestions += questionMap.get(dataEntity.getType()).size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataEntities, questionSummaryMap, programSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramSummaryPage(LocationEntity location, SurveyProgram program) {
		List<DataLocationEntity> dataEntities = location.collectDataLocationEntities(null, null);

		Map<DataLocationEntity, SurveyEnteredProgram> enteredProgramMap = new HashMap<DataLocationEntity, SurveyEnteredProgram>();
		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity dataEntity : dataEntities) {
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataEntity);
			List<SurveyQuestion> questions = program.getQuestions(dataEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(program.getSurvey(), dataEntity, program, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredProgramMap.put(dataEntity, enteredProgram);
			questionSummaryMap.put(dataEntity, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataEntities, questionSummaryMap, enteredProgramMap, true);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionSummaryPage(LocationEntity location, SurveySection section) {
		List<DataLocationEntity> dataEntities = location.collectDataLocationEntities(null, null);

		Map<DataLocationEntity, QuestionSummary> questionSummaryMap = new HashMap<DataLocationEntity, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocationEntity dataEntity : dataEntities) {
			List<SurveyQuestion> questions = section.getQuestions(dataEntity.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(section.getSurvey(), dataEntity, null, section, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			questionSummaryMap.put(dataEntity, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataEntities, questionSummaryMap);		
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
}
