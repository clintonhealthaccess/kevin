package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationService;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
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
	public SurveySummaryPage getSectionTable(DataLocation dataLocation, SurveyProgram program) {
		List<SurveySection> sections = program.getSections(dataLocation.getType());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			List<SurveyQuestion> questions = section.getQuestions(dataLocation.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(program.getSurvey(), dataLocation, null, section, true, false, true);
			
			questionSummaryMap.put(section, new QuestionSummary(questions.size(), completedQuestions));
		}
		return new SurveySummaryPage(questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramTable(DataLocation dataLocation, Survey survey) {
		List<SurveyProgram> programs = survey.getPrograms(dataLocation.getType());
		Map<SurveyProgram, QuestionSummary> questionSummaryMap = new HashMap<SurveyProgram, QuestionSummary>();
		Map<SurveyProgram, SurveyEnteredProgram> enteredProgramMap = new HashMap<SurveyProgram, SurveyEnteredProgram>();
		
		for (SurveyProgram program : programs) {
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataLocation);
			List<SurveyQuestion> questions = program.getQuestions(dataLocation.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataLocation, program, null, true, false, true);
			
			questionSummaryMap.put(program, new QuestionSummary(questions.size(), completedQuestions));
			enteredProgramMap.put(program, enteredProgram);
		}
		
		return new SurveySummaryPage(enteredProgramMap, questionSummaryMap);
	}	
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSurveySummaryPage(Location location, Survey survey) {
		List<DataLocation> dataLocations = location.collectDataLocations(null, null);
		
		Map<DataLocationType, List<SurveyProgram>> programMap = new HashMap<DataLocationType, List<SurveyProgram>>();
		Map<DataLocationType, List<SurveyQuestion>> questionMap = new HashMap<DataLocationType, List<SurveyQuestion>>();

		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		Map<DataLocation, ProgramSummary> programSummaryMap = new HashMap<DataLocation, ProgramSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocation dataLocation : dataLocations) {
			if (!programMap.containsKey(dataLocation.getType())) {
				programMap.put(dataLocation.getType(), survey.getPrograms(dataLocation.getType()));
			}
			Integer submittedPrograms = surveyValueService.getNumberOfSurveyEnteredPrograms(survey, dataLocation, true, null, null);
			
			if (!questionMap.containsKey(dataLocation.getType())) {
				List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
				for (SurveyProgram program : programMap.get(dataLocation.getType())) {
					questions.addAll(program.getQuestions(dataLocation.getType()));				
				}
				questionMap.put(dataLocation.getType(), questions);
			}
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(survey, dataLocation, null, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questionMap.get(dataLocation.getType()).size(), completedQuestions);
			ProgramSummary programSummary = new ProgramSummary(programMap.get(dataLocation.getType()).size(), submittedPrograms);
			
			questionSummaryMap.put(dataLocation, questionSummary);
			programSummaryMap.put(dataLocation, programSummary);
			
			totalQuestions += questionMap.get(dataLocation.getType()).size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataLocations, questionSummaryMap, programSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramSummaryPage(Location location, SurveyProgram program) {
		List<DataLocation> dataLocations = location.collectDataLocations(null, null);

		Map<DataLocation, SurveyEnteredProgram> enteredProgramMap = new HashMap<DataLocation, SurveyEnteredProgram>();
		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocation dataLocation : dataLocations) {
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataLocation);
			List<SurveyQuestion> questions = program.getQuestions(dataLocation.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(program.getSurvey(), dataLocation, program, null, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			
			enteredProgramMap.put(dataLocation, enteredProgram);
			questionSummaryMap.put(dataLocation, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataLocations, questionSummaryMap, enteredProgramMap, true);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionSummaryPage(Location location, SurveySection section) {
		List<DataLocation> dataLocations = location.collectDataLocations(null, null);

		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		
		Integer totalQuestions = 0;
		Integer totalAnsweredQuestions = 0;
		for (DataLocation dataLocation : dataLocations) {
			List<SurveyQuestion> questions = section.getQuestions(dataLocation.getType());
			Integer completedQuestions = surveyValueService.getNumberOfSurveyEnteredQuestions(section.getSurvey(), dataLocation, null, section, true, false, true);
			
			QuestionSummary questionSummary = new QuestionSummary(questions.size(), completedQuestions);
			questionSummaryMap.put(dataLocation, questionSummary);
			
			totalQuestions += questions.size();
			totalAnsweredQuestions += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestions, totalAnsweredQuestions), dataLocations, questionSummaryMap);		
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
}
