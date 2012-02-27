package org.chai.kevin.survey.summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.LocationSorter;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.survey.SurveyObjective;
import org.chai.kevin.survey.SurveySection;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;

public class SurveySummaryPage {

	private static final String PROGRESS_SORT = "progress";
	private static final String FACILITY_SORT = "facility";
	
	private QuestionSummary summary;
	private List<DataLocationEntity> facilities;
	
	// for survey summary page
	private Map<DataLocationEntity, ObjectiveSummary> objectiveSummaryMap;
	
	// for objective summary page
	private Map<DataLocationEntity, SurveyEnteredObjective> enteredObjectiveSummaryMap;
	
	// for survey + objective + section summary page
	private Map<DataLocationEntity, QuestionSummary> questionSummaryMap;
	
	// for smaller information tables
	private Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveTableMap;
	private Map<SurveyObjective, QuestionSummary> objectiveQuestionTableMap;
	private Map<SurveySection, QuestionSummary> sectionQuestionTableMap;

	// for survey summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap, Map<DataLocationEntity, ObjectiveSummary> objectiveSummaryMap) {
		this.summary = summary;
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
		this.objectiveSummaryMap = objectiveSummaryMap;
	}
	
	// for objective summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap, Map<DataLocationEntity, SurveyEnteredObjective> enteredObjectiveMap, boolean test) {
		this.summary = summary;
		this.facilities = facilities;
		this.enteredObjectiveSummaryMap = enteredObjectiveMap;
		this.questionSummaryMap = questionSummaryMap;
	}

	// for section summary page
	public SurveySummaryPage(QuestionSummary summary, List<DataLocationEntity> facilities, Map<DataLocationEntity, QuestionSummary> questionSummaryMap) {
		this.summary = summary;
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
	}
	
	// for objective table page
	public SurveySummaryPage(Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveTableMap, Map<SurveyObjective, QuestionSummary> objectiveQuestionTableMap) {
		this.enteredObjectiveTableMap = enteredObjectiveTableMap;
		this.objectiveQuestionTableMap = objectiveQuestionTableMap;
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
	
	public List<SurveyObjective> getObjectives() {
		List<SurveyObjective> sortedObjectives = new ArrayList<SurveyObjective>(objectiveQuestionTableMap.keySet());
		Collections.sort(sortedObjectives, new Comparator<SurveyObjective>() {
			@Override
			public int compare(SurveyObjective arg0, SurveyObjective arg1) {
				QuestionSummary summary0 = objectiveQuestionTableMap.get(arg0);
				QuestionSummary summary1 = objectiveQuestionTableMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedObjectives);
		return sortedObjectives;
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

	public ObjectiveSummary getObjectiveSummary(DataLocationEntity location) {
		return objectiveSummaryMap.get(location);
	}

	public SurveyEnteredObjective getSurveyEnteredObjective(DataLocationEntity location) {
		return enteredObjectiveSummaryMap.get(location);
	}
	
	public QuestionSummary getQuestionSummary(SurveyObjective objective) {
		return objectiveQuestionTableMap.get(objective);
	}

	public QuestionSummary getQuestionSummary(SurveySection section) {
		return sectionQuestionTableMap.get(section);
	}

	public SurveyEnteredObjective getSurveyEnteredObjective(SurveyObjective objective) {
		return enteredObjectiveTableMap.get(objective);
	}
	
}
