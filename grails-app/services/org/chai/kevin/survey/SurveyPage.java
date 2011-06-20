package org.chai.kevin.survey;

import java.util.Map;

import org.chai.kevin.DataElement;
import org.chai.kevin.DataValue;
import org.chai.kevin.Organisation;
import org.hisp.dhis.period.Period;

public class SurveyPage {
	private Period period;
	private Organisation organisation;
	private SurveySubSection section;
	private Map<SurveyQuestion, Map<DataElement, DataValue>> values;

	public SurveyPage(Period period, Organisation organisation,
			SurveySubSection section,
			Map<SurveyQuestion, Map<DataElement, DataValue>> values) {
		super();
		this.period = period;
		this.organisation = organisation;
		this.setSection(section);
		this.values = values;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public void setValues(
			Map<SurveyQuestion, Map<DataElement, DataValue>> values) {
		this.values = values;
	}

	public Map<SurveyQuestion, Map<DataElement, DataValue>> getValues() {
		return values;
	}

	public void setSection(SurveySubSection section) {
		this.section = section;
	}

	public SurveySubSection getSection() {
		return section;
	}

	
}
