package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationSorter;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyProgram;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.validation.SurveyEnteredProgram;

public class SurveySummaryPage {

	private static final String PROGRESS_SORT = "progress";
	private static final String FACILITY_SORT = "facility";
	
	private QuestionSummary summary;
	private List<DataLocationEntity> facilities;
	
	// for survey summary page
	private Map<DataLocationEntity, ProgramSummary> programSummaryMap;
	
	// for program summary page
	private Map<DataLocationEntity, SurveyEnteredProgram> enteredProgramSummaryMap;
	
	// for survey + program + section summary page
	private Map<DataLocationEntity, QuestionSummary> questionSummaryMap;
	
	// for smaller information tables
	private Map<SurveyProgram, SurveyEnteredProgram> enteredProgramTableMap;
	private Map<SurveyProgram, QuestionSummary> programQuestionTableMap;
	private Map<SurveySection, QuestionSummary> sectionQuestionTableMap;

	// for survey summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap, Map<DataLocationEntity, ProgramSummary> programSummaryMap) {
		this.summary = summary;
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
		this.programSummaryMap = programSummaryMap;
	}
	
	// for program summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap, Map<DataLocationEntity, SurveyEnteredProgram> enteredProgramMap, boolean test) {
		this.summary = summary;
		this.facilities = facilities;
		this.enteredProgramSummaryMap = enteredProgramMap;
		this.questionSummaryMap = questionSummaryMap;
	}

	// for section summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap) {
		this.summary = summary;
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
	}
	
	// for program table page
	public SurveySummaryPage(Map<SurveyProgram, SurveyEnteredProgram> enteredProgramTableMap, Map<SurveyProgram, QuestionSummary> programQuestionTableMap) {
		this.enteredProgramTableMap = enteredProgramTableMap;
		this.programQuestionTableMap = programQuestionTableMap;
	}
	
	// for section table page
	public SurveySummaryPage(Map<SurveySection, QuestionSummary> sectionQuestionTableMap) {
		this.sectionQuestionTableMap = sectionQuestionTableMap;
	}
	
	
	public void sort(String parameter, String order, String language) {
		if (facilities == null || parameter == null || order == null) return;
		if (parameter.equals(FACILITY_SORT)) {
			Collections.sort(facilities, LocationSorter.BY_NAME(language));
			if (order.equals("desc")) Collections.reverse(facilities); 
		}
		else if (parameter.equals(PROGRESS_SORT)) {
			Collections.sort(facilities, new Comparator<DataLocationEntity>() {
				@Override
				public int compare(DataLocationEntity arg0, DataLocationEntity arg1) {
					QuestionSummary summary0 = questionSummaryMap.get(arg0);
					QuestionSummary summary1 = questionSummaryMap.get(arg1);
					return summary0.compareTo(summary1);
				}
			});
			if (order.equals("desc")) Collections.reverse(facilities);
		}
	}
	
	public List<SurveyProgram> getPrograms() {
		List<SurveyProgram> sortedPrograms = new ArrayList<SurveyProgram>(programQuestionTableMap.keySet());
		Collections.sort(sortedPrograms, new Comparator<SurveyProgram>() {
			@Override
			public int compare(SurveyProgram arg0, SurveyProgram arg1) {
				QuestionSummary summary0 = programQuestionTableMap.get(arg0);
				QuestionSummary summary1 = programQuestionTableMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedPrograms);
		return sortedPrograms;
	}	

	public List<SurveySection> getSections() {
		List<SurveySection> sortedSections = new ArrayList<SurveySection>(sectionQuestionTableMap.keySet());
		Collections.sort(sortedSections, new Comparator<SurveySection>() {
			@Override
			public int compare(SurveySection arg0, SurveySection arg1) {
				QuestionSummary summary0 = sectionQuestionTableMap.get(arg0);
				QuestionSummary summary1 = sectionQuestionTableMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedSections);
		return sortedSections;
	}
	
	public QuestionSummary getSummary() {
		return summary;
	}
	
	public List<DataLocationEntity> getFacilities() {
		return facilities;
	}

	public QuestionSummary getQuestionSummary(DataLocationEntity location) {
		return questionSummaryMap.get(location);
	}

	public ProgramSummary getProgramSummary(DataLocationEntity location) {
		return programSummaryMap.get(location);
	}

	public SurveyEnteredProgram getSurveyEnteredProgram(DataLocationEntity location) {
		return enteredProgramSummaryMap.get(location);
	}
	
	public QuestionSummary getQuestionSummary(SurveyProgram program) {
		return programQuestionTableMap.get(program);
	}

	public QuestionSummary getQuestionSummary(SurveySection section) {
		return sectionQuestionTableMap.get(section);
	}

	public SurveyEnteredProgram getSurveyEnteredProgram(SurveyProgram program) {
		return enteredProgramTableMap.get(program);
	}
	
}
