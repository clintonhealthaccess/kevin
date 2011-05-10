package org.chai.kevin.dashboard;

import java.util.List;

import org.chai.kevin.dashboard.TargetExplanation.RelevantData;

public class DashboardExpressionExplanation {
	
	private DashboardCalculation calculation;
	private DashboardPercentage percentage;
	private List<RelevantData> relevantDatas;
	private String htmlFormula;
	
	public DashboardExpressionExplanation(DashboardCalculation calculation, String htmlFormula, DashboardPercentage percentage, List<RelevantData> relevantDatas) {
		super();
		this.calculation = calculation;
		this.htmlFormula = htmlFormula;
		this.percentage = percentage;
		this.relevantDatas = relevantDatas;
	}
	
	public DashboardCalculation getCalculation() {
		return calculation;
	}
	
	public DashboardPercentage getPercentage() {
		return percentage;
	}
			
	public List<RelevantData> getRelevantDatas() {
		return relevantDatas;
	}
	
	public String getHtmlFormula() {
		return htmlFormula;
	}
	
	public boolean isConstant() {
		return relevantDatas.size() == 0;
	}
}