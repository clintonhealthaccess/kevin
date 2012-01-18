package org.chai.kevin.survey.workflow;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.chai.kevin.data.Type;
import org.chai.kevin.survey.SurveySimpleQuestion;
import org.chai.kevin.value.Value;

@Entity
@Table(name="dhsst_survey_workflow")
public class Workflow extends SurveySimpleQuestion {

	private String fixedHeaderPrefix;
	private List<WorkflowStep> steps;
	
	@Basic
	public String getFixedHeaderPrefix() {
		return fixedHeaderPrefix;
	}
	
	public void setFixedHeaderPrefix(String fixedHeaderPrefix) {
		this.fixedHeaderPrefix = fixedHeaderPrefix;
	}
	
	@OneToMany(mappedBy="workflow", targetEntity=WorkflowStep.class)
	public List<WorkflowStep> getSteps() {
		return steps;
	}
	
	public void setSteps(List<WorkflowStep> steps) {
		this.steps = steps;
	}

	@Transient
	@Override
	public QuestionType getType() {
		return QuestionType.WORKFLOW;
	}
	
	@Override
	protected Workflow newInstance() {
		return new Workflow();
	}

	@Transient
	public Type getType(WorkflowStep step) {
		return getSurveyElement().getDataElement().getType().getType(step.getPrefix());
	}
	
	@Transient
	public Value getValue(Value value, WorkflowStep step) {
		return getType(step).getValue(value, step.getPrefix());
	}
	
}
