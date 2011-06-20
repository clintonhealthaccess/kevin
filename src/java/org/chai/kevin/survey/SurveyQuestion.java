package org.chai.kevin.survey;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;

@SuppressWarnings("serial")
@Entity(name = "SurveyQuestion")
@Table(name = "dhsst_survey_question")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class SurveyQuestion extends SurveyTranslatable {
	public static enum Answered {
		ANSWERED, NOTANSWERED, ANSWERNOTCORRECT
	};

	private Integer id;
	private Integer order;
	private boolean status = true;
	private Answered answered = Answered.NOTANSWERED;
	private SurveySubSection subSection;
	//private List<OrganisationUnitGroup> groups;

	public void setId(Integer id) {
		this.id = id;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	@Basic
	@Column(name = "ordering")
	public Integer getOrder() {
		return order;
	}

	public void setSubSection(SurveySubSection subSection) {
		this.subSection = subSection;
	}

	@ManyToOne(targetEntity = SurveySubSection.class, optional = false)
	public SurveySubSection getSubSection() {
		return subSection;
	}

//	public void setGroups(List<OrganisationUnitGroup> groups) {
//		this.groups = groups;
//	}
//
//	// optional has be set to false
//	@ManyToOne(targetEntity = OrganisationUnitGroup.class, optional = true)
//	public List<OrganisationUnitGroup> getGroups() {
//		return groups;
//	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isStatus() {
		return status;
	}

	public void setAnswered(Answered answered) {
		this.answered = answered;
	}

	public Answered getAnswered() {
		return answered;
	}

//	public void addOrganisationGroup(OrganisationUnitGroup group) {
//		groups.add(group);
//	}

	@Transient
	public String toString() {
		if (this.answered == Answered.ANSWERED)
			return "answered";
		if (this.answered == Answered.NOTANSWERED)
			return "notanswered";
		else
			return "answernotcorrect";

	}

	@Transient
	public abstract String getTemplate();

}
