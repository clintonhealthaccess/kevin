package org.chai.kevin.survey;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.CascadeType;

@SuppressWarnings("serial")
@Entity(name = "SurveyCheckboxQuestion")
@Table(name = "dhsst_survey_checkbox_question")
public class SurveyCheckboxQuestion extends SurveyQuestion {

	List<SurveyCheckboxOption> options;
	
    @OneToMany(cascade = CascadeType.ALL,targetEntity=SurveyCheckboxOption.class, mappedBy="question")
	public List<SurveyCheckboxOption> getOptions() {
		return options;
	}

	public void setOptions(List<SurveyCheckboxOption> options) {
		this.options = options;
	}

	public void addCheckboxOption(SurveyCheckboxOption option) {
		option.setQuestion(this);
		options.add(option);
	}
	
    @Transient
	@Override
	public String getTemplate() {
		String gspName = "checkboxQuestion";
		return gspName;
	}

}
