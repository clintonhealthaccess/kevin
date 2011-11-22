package org.chai.kevin.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.Translation;
import org.chai.kevin.util.Utils;

@Entity(name="SurveyValidationRule")
@Table(name="dhsst_survey_validation_rule")
public class SurveyValidationRule {

	private Long id;
	
	private SurveyElement surveyElement;
	private String prefix = "";
	
	private String expression;
	private Boolean allowOutlier;

	private Translation messages = new Translation();
	private List<SurveyElement> dependencies = new ArrayList<SurveyElement>();
	private String groupUuidString;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@ManyToOne(targetEntity=SurveyElement.class, fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	public SurveyElement getSurveyElement() {
		return surveyElement;
	}
	public void setSurveyElement(SurveyElement surveyElement) {
		this.surveyElement = surveyElement;
	}
	
	@Lob
	@Column(nullable=false)
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Embedded
		@AttributeOverrides({
	    @AttributeOverride(name="jsonText", column=@Column(name="jsonMessages", nullable=false))
	})
	public Translation getMessages() {
		return messages;
	}
	
	public void setMessages(Translation messages) {
		this.messages = messages;
	}
	
	@ManyToMany(targetEntity=SurveyElement.class)
	@JoinTable(name="dhsst_survey_validation_dependencies")
	public List<SurveyElement> getDependencies() {
		return dependencies;
	}
	
	public void setDependencies(List<SurveyElement> dependencies) {
		this.dependencies = dependencies;
	}
	
	@Lob
	public String getGroupUuidString() {
		return groupUuidString;
	}

	public void setGroupUuidString(String groupUuidString) {
		this.groupUuidString = groupUuidString;
	}
	
	@Transient
	public Set<String> getGroupUuids() {
		return Utils.split(groupUuidString);
	}
	
	public void setGroupUuids(Set<String> groupUuids) {
		this.groupUuidString = Utils.unsplit(groupUuids);
	}
	
	@Basic
	@Column(nullable=false)
	public Boolean getAllowOutlier() {
		return allowOutlier;
	}
	
	public void setAllowOutlier(Boolean allowOutlier) {
		this.allowOutlier = allowOutlier;
	}
	
	@Basic
	@Column(nullable=false)
	public String getPrefix() {
		return prefix;
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public String toString() {
		return "SurveyValidationRule [surveyElement=" + surveyElement
				+ ", expression=" + expression + "]";
	}
	
	@Transient
	protected void deepCopy(SurveyValidationRule copy, SurveyCloner cloner) {
		copy.setAllowOutlier(getAllowOutlier());
		copy.setPrefix(getPrefix());
		copy.setExpression(cloner.getExpression(getExpression(), copy));
		copy.setSurveyElement(cloner.getElement(getSurveyElement()));
		copy.setMessages(getMessages());
		copy.setGroupUuidString(getGroupUuidString());
		for (SurveyElement element : getDependencies()) {
			SurveyElement newElement = null;
			if (!element.getSurvey().equals(getSurveyElement().getSurvey())) {
				cloner.addUnchangedValidationRule(this, element.getId());
				newElement = element;
			}
			else {
				newElement = cloner.getElement(element);
			}
			copy.getDependencies().add(newElement);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SurveyValidationRule))
			return false;
		SurveyValidationRule other = (SurveyValidationRule) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
