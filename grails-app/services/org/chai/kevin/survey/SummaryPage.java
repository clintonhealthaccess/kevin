package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;
import org.chai.kevin.OrganisationSorter;

public class SummaryPage {

	private Organisation organisation;
	
	private Survey survey;
	private SurveyObjective objective;
	private SurveySection section;
	
	private Map<Organisation, SurveySummary> facilitySurveySummaryMap;
	private Map<Organisation, ObjectiveSummary> facilityObjectiveSummaryMap;
	private Map<Organisation, SectionSummary> facilitySectionSummaryMap;
	
	private Map<SurveyObjective, ObjectiveSummary> innerObjectiveSummaryMap;
	private Map<SurveySection, SectionSummary> innerSectionSummaryMap;	

	public SummaryPage(Survey survey, Organisation organisation,
			Map<Organisation, SurveySummary> facilitySurveySummaryMap) {
		this.survey = survey;
		this.organisation = organisation;
		this.facilitySurveySummaryMap = facilitySurveySummaryMap;
	}

	public SummaryPage(SurveyObjective objective, Organisation organisation,
			Map<Organisation, ObjectiveSummary> facilityObjectiveSummaryMap) {
		this.objective = objective;
		this.organisation = organisation;
		this.facilityObjectiveSummaryMap = facilityObjectiveSummaryMap;
	}

	public SummaryPage(SurveySection section, Organisation organisation,
			Map<Organisation, SectionSummary> facilitySectionSummaryMap) {
		this.section = section;
		this.organisation = organisation;
		this.facilitySectionSummaryMap = facilitySectionSummaryMap;
	}
	
	public SummaryPage(Survey survey, Organisation organisation,
			Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap,
			boolean test) {
		this.survey = survey;
		this.organisation = organisation;
		this.innerObjectiveSummaryMap = objectiveSummaryMap;
	}

	public SummaryPage(SurveyObjective objective, Organisation organisation,
			Map<SurveySection, SectionSummary> sectionSummaryMap,
			boolean test) {
		this.survey = objective.getSurvey();
		this.objective = objective;
		this.organisation = organisation;
		this.innerSectionSummaryMap = sectionSummaryMap;
	}

	public Organisation getOrganisation() {
		return organisation;
	}
	
	public Survey getSurvey() {
		return survey;
	}

	public SurveyObjective getObjective() {
		return objective;
	}

	public SurveySection getSection() {
		return section;
	}

	public List<Organisation> getFacilities() {
		List<Organisation> sortedFacilities = new ArrayList<Organisation>(facilitySurveySummaryMap.keySet());
		Collections.sort(sortedFacilities, OrganisationSorter.BY_LEVEL);
		return sortedFacilities;
	}

	public List<SurveyObjective> getObjectives() {
		List<SurveyObjective> sortedObjectives = new ArrayList<SurveyObjective>(innerObjectiveSummaryMap.keySet());
		Collections.sort(sortedObjectives, new Comparator<SurveyObjective>() {
			@Override
			public int compare(SurveyObjective arg0, SurveyObjective arg1) {
				ObjectiveSummary summary0 = innerObjectiveSummaryMap.get(arg0);
				ObjectiveSummary summary1 = innerObjectiveSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedObjectives);
		return sortedObjectives;
	}	

	public List<SurveySection> getSections() {
		List<SurveySection> sortedSections = new ArrayList<SurveySection>(innerSectionSummaryMap.keySet());
		Collections.sort(sortedSections, new Comparator<SurveySection>() {
			@Override
			public int compare(SurveySection arg0, SurveySection arg1) {
				SectionSummary summary0 = innerSectionSummaryMap.get(arg0);
				SectionSummary summary1 = innerSectionSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedSections);
		return sortedSections;
	}

	public SurveySummary getSurveySummary(Organisation organisation) {
		return facilitySurveySummaryMap.get(organisation);
	}

	public ObjectiveSummary getObjectiveSummary(SurveyObjective objective) {
		return innerObjectiveSummaryMap.get(objective);
	}

	public SectionSummary getSectionSummary(SurveySection section) {
		return innerSectionSummaryMap.get(section);
	}	
	
	public List<Organisation> getObjectiveFacilities() {
		List<Organisation> sortedFacilities = new ArrayList<Organisation>(facilityObjectiveSummaryMap.keySet());
		Collections.sort(sortedFacilities, OrganisationSorter.BY_LEVEL);
		return sortedFacilities;
	}
	
	public ObjectiveSummary getObjectiveSummary(Organisation facility) {
		return facilityObjectiveSummaryMap.get(facility);
	}
	
	public List<Organisation> getSectionFacilities() {
		List<Organisation> sortedFacilities = new ArrayList<Organisation>(facilitySectionSummaryMap.keySet());
		Collections.sort(sortedFacilities, OrganisationSorter.BY_LEVEL);
		return sortedFacilities;
	}
	
	public SectionSummary getSectionSummary(Organisation facility) {
		return facilitySectionSummaryMap.get(facility);
	}

}
