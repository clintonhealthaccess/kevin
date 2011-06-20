package org.chai.kevin.survey;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@SuppressWarnings("serial")
@Entity(name = "SurveySection")
@Table(name = "dhsst_survey_section")
public class SurveySection extends SurveyTranslatable  {
	
	public static enum Completed {
		COMPLETED, INPROGRESS, NOTSTARTED
	};
	
	private Integer id;
	private Integer order;
	private Completed completed = Completed.NOTSTARTED;
	private List<SurveySubSection> subSections = new ArrayList<SurveySubSection>();

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

	public void setCompleted(Completed completed) {
		this.completed = completed;
	}

	public Completed getCompleted() {
		return completed;
	}

	public void setSubSections(List<SurveySubSection> subSections) {
		this.subSections = subSections;
	}
    @OneToMany(cascade=CascadeType.ALL, targetEntity=SurveySubSection.class, mappedBy="section")
	public List<SurveySubSection> getSubSections() {
		return subSections;
	}

	@Transient
	public void setSectionCompleted() {
		int i = 0;
		for (SurveySubSection subsection : subSections)
			if (subsection.isStatus() && subsection.toString()=="completed")
				i++;
		if (subSections.size() == i)
			this.setCompleted(Completed.COMPLETED);
		if (subSections.size() > 0 && subSections.size() < i)
			this.setCompleted(Completed.INPROGRESS);
		else
			this.setCompleted(Completed.NOTSTARTED);
	}
	
	public void addSubSection(SurveySubSection subSection) {
		subSection.setSection(this);
		subSections.add(subSection);
	}
	
	@Transient
	public String toString() {
		if (this.completed == Completed.COMPLETED)
			return "completed";
		if (this.completed == Completed.INPROGRESS)
			return "inprogress";
		else
			return "notstarted";

	}

	@Transient
	public Map<SurveySubSection, List<SurveyQuestion>> getAllQuestionsOfSurveySection() {
		Map<SurveySubSection, List<SurveyQuestion>> qSubSection = null;
		if (!subSections.isEmpty()) {
			qSubSection = new HashMap<SurveySubSection, List<SurveyQuestion>>();
			for (SurveySubSection subSection : subSections)
				qSubSection.put(subSection, subSection.getQuestions());
		}

		return qSubSection;

	}
}
