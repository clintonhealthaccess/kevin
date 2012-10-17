package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.chai.location.LocationService;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.survey.Survey;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveyQuestion;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.SurveyValueService;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;
import org.chai.kevin.survey.validation.SurveyEnteredSection;
import org.springframework.transaction.annotation.Transactional;

public class SummaryService {

	private SurveyValueService surveyValueService;
	private LocationService locationService;
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionTable(DataLocation dataLocation, SurveyProgram program) {
		List<SurveySection> sections = program.getSections(dataLocation.getType());
		Map<SurveySection, QuestionSummary> questionSummaryMap = new HashMap<SurveySection, QuestionSummary>();
		
		for (SurveySection section : sections) {
			SurveyEnteredSection enteredSection = surveyValueService.getSurveyEnteredSection(section, dataLocation);
			
			questionSummaryMap.put(section, new QuestionSummary(enteredSection.getTotalQuestions(), enteredSection.getCompletedQuestions()));
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
			
			Integer totalQuestions = 0;
			Integer completedQuestions = 0;
			
			if (enteredProgram != null) {
				totalQuestions = enteredProgram.getTotalQuestions();
				completedQuestions = enteredProgram.getCompletedQuestions();
			}
			
			questionSummaryMap.put(program, new QuestionSummary(totalQuestions, completedQuestions));
			enteredProgramMap.put(program, enteredProgram);
		}
		
		return new SurveySummaryPage(enteredProgramMap, questionSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSurveySummaryPage(Location location, Set<DataLocationType> types, Survey survey) {
		//TODO?
		List<DataLocation> dataLocations = location.collectDataLocations(null, types);		
		
		Map<DataLocationType, List<SurveyProgram>> programMap = new HashMap<DataLocationType, List<SurveyProgram>>();
//		Map<DataLocationType, List<SurveyQuestion>> questionMap = new HashMap<DataLocationType, List<SurveyQuestion>>();

		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		Map<DataLocation, ProgramSummary> programSummaryMap = new HashMap<DataLocation, ProgramSummary>();		
		
		Integer totalQuestionsLocation = 0;
		Integer totalAnsweredQuestionsLocation = 0;
		for (DataLocation dataLocation : dataLocations) {						
			
			if (!programMap.containsKey(dataLocation.getType())) {
				programMap.put(dataLocation.getType(), survey.getPrograms(dataLocation.getType()));
			}
			Integer submittedPrograms = surveyValueService.getNumberOfSurveyEnteredPrograms(survey, dataLocation, true, null, null);
			
			Integer totalQuestions = 0;
			Integer completedQuestions = 0;
			
			for (SurveyProgram program : programMap.get(dataLocation.getType())) {
				SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataLocation);
				
				if (enteredProgram != null) {
					completedQuestions += enteredProgram.getCompletedQuestions();
					totalQuestions += enteredProgram.getTotalQuestions();
				}
			}
			
			QuestionSummary questionSummary = new QuestionSummary(totalQuestions, completedQuestions);
			ProgramSummary programSummary = new ProgramSummary(programMap.get(dataLocation.getType()).size(), submittedPrograms);
			
			questionSummaryMap.put(dataLocation, questionSummary);
			programSummaryMap.put(dataLocation, programSummary);
			
			totalQuestionsLocation += totalQuestions;
			totalAnsweredQuestionsLocation += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestionsLocation, totalAnsweredQuestionsLocation), dataLocations, questionSummaryMap, programSummaryMap);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getProgramSummaryPage(Location location, Set<DataLocationType> types, SurveyProgram program) {
				
		List<DataLocation> dataLocations = location.collectDataLocations(null, types);
		List<DataLocation> programSummaryLocations = new ArrayList<DataLocation>();
		
		Map<DataLocation, SurveyEnteredProgram> enteredProgramMap = new HashMap<DataLocation, SurveyEnteredProgram>();
		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		
		Integer totalQuestionsLocation = 0;
		Integer totalAnsweredQuestionsLocation = 0;
		for (DataLocation dataLocation : dataLocations) {			
		
			if(!program.getTypeCodes().contains(dataLocation.getType().getCode())) continue;
			programSummaryLocations.add(dataLocation);
			
			SurveyEnteredProgram enteredProgram = surveyValueService.getSurveyEnteredProgram(program, dataLocation);
			
			Integer totalQuestions = 0;
			Integer completedQuestions = 0;
			
			if (enteredProgram != null) {
				totalQuestions = enteredProgram.getTotalQuestions();
				completedQuestions = enteredProgram.getCompletedQuestions();
			}
				
			QuestionSummary questionSummary = new QuestionSummary(totalQuestions, completedQuestions);
			
			enteredProgramMap.put(dataLocation, enteredProgram);
			questionSummaryMap.put(dataLocation, questionSummary);
			
			totalQuestionsLocation += totalQuestions;
			totalAnsweredQuestionsLocation += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestionsLocation, totalAnsweredQuestionsLocation), programSummaryLocations, questionSummaryMap, enteredProgramMap, true);
	}
	
	@Transactional(readOnly = true)
	public SurveySummaryPage getSectionSummaryPage(Location location, Set<DataLocationType> types, SurveySection section) {
				
		List<DataLocation> dataLocations = location.collectDataLocations(null, types);		
		List<DataLocation> sectionSummaryLocations = new ArrayList<DataLocation>();
		
		Map<DataLocation, QuestionSummary> questionSummaryMap = new HashMap<DataLocation, QuestionSummary>();
		
		Integer totalQuestionsLocation = 0;
		Integer totalAnsweredQuestionsLocation = 0;
		for (DataLocation dataLocation : dataLocations) {
			
			if(!section.getTypeCodes().contains(dataLocation.getType().getCode())) continue;			
			sectionSummaryLocations.add(dataLocation);
			
			SurveyEnteredSection enteredSection = surveyValueService.getSurveyEnteredSection(section, dataLocation);
			
			Integer totalQuestions = 0;
			Integer completedQuestions = 0;
			
			if (enteredSection != null) {
				totalQuestions = enteredSection.getTotalQuestions();
				completedQuestions = enteredSection.getCompletedQuestions();
			}
			
			QuestionSummary questionSummary = new QuestionSummary(totalQuestions, completedQuestions);
			questionSummaryMap.put(dataLocation, questionSummary);
			
			totalQuestionsLocation += totalQuestions;
			totalAnsweredQuestionsLocation += completedQuestions;
		}
		return new SurveySummaryPage(new QuestionSummary(totalQuestionsLocation, totalAnsweredQuestionsLocation), sectionSummaryLocations, questionSummaryMap);		
	}
	
	public void setSurveyValueService(SurveyValueService surveyValueService) {
		this.surveyValueService = surveyValueService;
	}
	
	public void setLocationService(LocationService locationValueService) {
		this.locationService = locationService;
	}
}
