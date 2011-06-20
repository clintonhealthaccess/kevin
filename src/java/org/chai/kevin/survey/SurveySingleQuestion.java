package org.chai.kevin.survey;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.chai.kevin.DataElement;

@SuppressWarnings("serial")
@Entity(name = "SurveySingleQuestion")
@Table(name = "dhsst_survey_single_question")
public class SurveySingleQuestion extends SurveyQuestion {

	private DataElement dataElement;

	public void setDataElement(DataElement dataElement) {
		this.dataElement = dataElement;
	}

	public DataElement getDataElement() {
		return dataElement;
	}
    @Transient
	@Override
	public String getTemplate() {
		String gspName = "singleQuestion";
		return gspName;
	}

}
