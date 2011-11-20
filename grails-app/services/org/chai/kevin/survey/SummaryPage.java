package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationSorter;
import org.chai.kevin.survey.validation.SurveyEnteredObjective;

public class SummaryPage {

	private static final String PROGRESS_SORT = "progress";
	private static final String FACILITY_SORT = "facility";
	
	private List<Organisation> facilities;
	
	// for survey summary page
	private Map<Organisation, ObjectiveSummary> objectiveSummaryMap;
	
	// for objective summary page
	private Map<Organisation, SurveyEnteredObjective> enteredObjectiveSummaryMap;
	
	// for survey + objective + section summary page
	private Map<Organisation, QuestionSummary> questionSummaryMap;
	
	// for smaller information tables
	private Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveTableMap;
	private Map<SurveyObjective, QuestionSummary> objectiveQuestionTableMap;
	private Map<SurveySection, QuestionSummary> sectionQuestionTableMap;

	// for survey summary page
	public SummaryPage(List<Organisation> facilities, Map<Organisation, QuestionSummary> questionSummaryMap, Map<Organisation, ObjectiveSummary> objectiveSummaryMap) {
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
		this.objectiveSummaryMap = objectiveSummaryMap;
	}
	
	// for objective summary page
	public SummaryPage(List<Organisation> facilities, Map<Organisation, QuestionSummary> questionSummaryMap, Map<Organisation, SurveyEnteredObjective> enteredObjectiveMap, boolean test) {
		this.facilities = facilities;
		this.enteredObjectiveSummaryMap = enteredObjectiveMap;
		this.questionSummaryMap = questionSummaryMap;
	}

	// for section summary page
	public SummaryPage(List<Organisation> facilities, Map<Organisation, QuestionSummary> questionSummaryMap) {
		this.facilities = facilities;
		this.questionSummaryMap = questionSummaryMap;
	}
	
	// for objective table page
	public SummaryPage(Map<SurveyObjective, SurveyEnteredObjective> enteredObjectiveTableMap, Map<SurveyObjective, QuestionSummary> objectiveQuestionTableMap) {
		this.enteredObjectiveTableMap = enteredObjectiveTableMap;
		this.objectiveQuestionTableMap = objectiveQuestionTableMap;
	}
	
	// for section table page
	public SummaryPage(Map<SurveySection, QuestionSummary> sectionQuestionTableMap) {
		this.sectionQuestionTableMap = sectionQuestionTableMap;
	}
	
	
	public void sort(String parameter, String order) {
		if (facilities == null || parameter == null || order == null) return;
		if (parameter.equals(FACILITY_SORT)) {
			Collections.sort(facilities, OrganisationSorter.BY_LEVEL);
			if (order.equals("desc")) Collections.reverse(facilities); 
		}
		else if (parameter.equals(PROGRESS_SORT)) {
			Collections.sort(facilities, new Comparator<Organisation>() {
				@Override
				public int compare(Organisation arg0, Organisation arg1) {
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
	
	public List<Organisation> getFacilities() {
		return facilities;
	}

	public QuestionSummary getQuestionSummary(Organisation organisation) {
		return questionSummaryMap.get(organisation);
	}

	public ObjectiveSummary getObjectiveSummary(Organisation organisation) {
		return objectiveSummaryMap.get(organisation);
	}

	public SurveyEnteredObjective getSurveyEnteredObjective(Organisation organisation) {
		return enteredObjectiveSummaryMap.get(organisation);
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
