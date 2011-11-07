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
	
	private Map<Organisation, OrganisationSummary> organisationSummaryMap;
	private Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap;
	private Map<SurveySection, SectionSummary> sectionSummaryMap;
	
	private Map<Organisation, ObjectiveSummary> facilityObjectiveSummaryMap;
	private Map<Organisation, SectionSummary> facilitySectionSummaryMap;

	public SummaryPage(Survey survey, Organisation organisation,
			List<Organisation> facilities,
			Map<Organisation, OrganisationSummary> organisationSummaryMap) {
		this.survey = survey;
		this.organisation = organisation;
		this.organisationSummaryMap = organisationSummaryMap;
	}

	public SummaryPage(Survey survey, Organisation organisation,
			List<SurveyObjective> objectives,
			Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap,
			boolean test) {
		this.survey = survey;
		this.organisation = organisation;
		this.objectiveSummaryMap = objectiveSummaryMap;
	}

	public SummaryPage(SurveyObjective objective, Organisation organisation,
			List<SurveySection> sections,
			Map<SurveySection, SectionSummary> sectionSummaryMap) {
		this.survey = objective.getSurvey();
		this.objective = objective;
		this.organisation = organisation;
		this.sectionSummaryMap = sectionSummaryMap;
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
		List<Organisation> sortedFacilities = new ArrayList<Organisation>(organisationSummaryMap.keySet());
		Collections.sort(sortedFacilities, OrganisationSorter.BY_LEVEL);
		return sortedFacilities;
	}

	public List<SurveyObjective> getObjectives() {
		List<SurveyObjective> sortedObjectives = new ArrayList<SurveyObjective>(objectiveSummaryMap.keySet());
		Collections.sort(sortedObjectives, new Comparator<SurveyObjective>() {
			@Override
			public int compare(SurveyObjective arg0, SurveyObjective arg1) {
				ObjectiveSummary summary0 = objectiveSummaryMap.get(arg0);
				ObjectiveSummary summary1 = objectiveSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedObjectives);
		return sortedObjectives;
	}	

	public List<SurveySection> getSections() {
		List<SurveySection> sortedSections = new ArrayList<SurveySection>(sectionSummaryMap.keySet());
		Collections.sort(sortedSections, new Comparator<SurveySection>() {
			@Override
			public int compare(SurveySection arg0, SurveySection arg1) {
				SectionSummary summary0 = sectionSummaryMap.get(arg0);
				SectionSummary summary1 = sectionSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sortedSections);
		return sortedSections;
	}

	public OrganisationSummary getOrganisationSummary(Organisation organisation) {
		return organisationSummaryMap.get(organisation);
	}

	public ObjectiveSummary getObjectiveSummary(SurveyObjective objective) {
		return objectiveSummaryMap.get(objective);
	}

	public SectionSummary getSectionSummary(SurveySection section) {
		return sectionSummaryMap.get(section);
	}

	public Map<Organisation, ObjectiveSummary> getFacilityObjectiveSummaryMap() {
		return facilityObjectiveSummaryMap;
	}	
	
	public List<Organisation> getObjectiveFacilities() {
		List<Organisation> sortedFacilities = new ArrayList<Organisation>(facilityObjectiveSummaryMap.keySet());
		Collections.sort(sortedFacilities, OrganisationSorter.BY_LEVEL);
		return sortedFacilities;
	}
	
	public ObjectiveSummary getObjectiveSummary(Organisation facility) {
		return facilityObjectiveSummaryMap.get(facility);
	}
	
	public Map<Organisation, SectionSummary> getFacilitySectionSummaryMap() {
		return facilitySectionSummaryMap;
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
