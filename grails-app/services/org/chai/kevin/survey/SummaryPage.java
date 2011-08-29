package org.chai.kevin.survey;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.chai.kevin.Organisation;

public class SummaryPage {

	private Organisation organisation;

	private Survey survey;
	private List<Organisation> facilities;
	private Map<Organisation, OrganisationSummary> organisationSummaryMap;
	
	private List<SurveyObjective> objectives;
	private Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap;
	
	private SurveyObjective objective;
	private List<SurveySection> sections;
	private Map<SurveySection, SectionSummary> sectionSummaryMap;
	
	public SummaryPage(Survey survey, Organisation organisation,
			List<Organisation> facilities,
			Map<Organisation, OrganisationSummary> organisationSummaryMap) {
		this.survey = survey;
		this.organisation = organisation;
		this.facilities = facilities;
		this.organisationSummaryMap = organisationSummaryMap;
	}

	public SummaryPage(Survey survey, Organisation organisation,
			List<SurveyObjective> objectives,
			Map<SurveyObjective, ObjectiveSummary> objectiveSummaryMap, boolean test) {
		this.survey = survey;
		this.organisation = organisation;
		this.objectives = objectives;
		this.objectiveSummaryMap = objectiveSummaryMap;
	}

	public SummaryPage(SurveyObjective objective, Organisation organisation,
			List<SurveySection> sections,
			Map<SurveySection, SectionSummary> sectionSummaryMap) {
		this.survey = objective.getSurvey();
		this.objective = objective;
		this.organisation = organisation;
		this.sections = sections;
		this.sectionSummaryMap = sectionSummaryMap; 
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
	
	public List<Organisation> getFacilities() {
		if (facilities == null) return null;
		Collections.sort(facilities, new Comparator<Organisation>() {
			@Override
			public int compare(Organisation arg0, Organisation arg1) {
				OrganisationSummary summary0 = organisationSummaryMap.get(arg0);
				OrganisationSummary summary1 = organisationSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(facilities);
		return facilities;
	}
	
	public List<SurveyObjective> getObjectives() {
		if (objectives == null) return null;
		Collections.sort(objectives, new Comparator<SurveyObjective>() {
			@Override
			public int compare(SurveyObjective arg0, SurveyObjective arg1) {
				ObjectiveSummary summary0 = objectiveSummaryMap.get(arg0);
				ObjectiveSummary summary1 = objectiveSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(objectives);
		return objectives;
	}
	
	public List<SurveySection> getSections() {
		if (sections == null) return null;
		Collections.sort(sections, new Comparator<SurveySection>() {
			@Override
			public int compare(SurveySection arg0, SurveySection arg1) {
				SectionSummary summary0 = sectionSummaryMap.get(arg0);
				SectionSummary summary1 = sectionSummaryMap.get(arg1);
				return summary0.compareTo(summary1);
			}
		});
		Collections.reverse(sections);
		return sections;
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
	
}
