package org.chai.kevin.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Expression;
import org.chai.kevin.Organisation;
import org.chai.kevin.dashboard.TargetExplanation.RelevantData;
import org.hisp.dhis.common.AbstractNameableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;

public class ExplanationCalculator extends PercentageCalculator {

	public ExplanationCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static Log log = LogFactory.getLog(ExplanationCalculator.class);
	
	private OrganisationUnitService organisationUnitService;
	
	public Explanation explain(DashboardObjective objective, Organisation organisation, Period period) {
		Map<DashboardObjectiveEntry, DashboardPercentage> objectives = new HashMap<DashboardObjectiveEntry, DashboardPercentage>();
		DashboardPercentage average = percentageService.getPercentage(organisation.getOrganisationUnit(), objective, period);
		getValueForObjective(objective, organisation, period, objectives);
		OrganisationUnitLevel level = organisationUnitService.getOrganisationUnitLevelByLevel(organisationUnitService.getLevelOfOrganisationUnit(organisation.getOrganisationUnit()));

		return new ObjectiveExplanation(average, level, objectives);
	}
	
	
	public Explanation explain(DashboardTarget target, Organisation organisation, Period period) {
		OrganisationUnitLevel level = organisationUnitService.getOrganisationUnitLevelByLevel(organisationUnitService.getLevelOfOrganisationUnit(organisation.getOrganisationUnit()));
		DashboardPercentage average = percentageService.getPercentage(organisation.getOrganisationUnit(), target, period);
		
		Explanation explanation;
		DashboardCalculation matchingCalculation = getMatchingCalculation(target, organisation);
		if (!isValid(matchingCalculation) && organisation.getChildren().size() == 0) {
			if (log.isInfoEnabled()) log.info("no matching groups and organisation has no children, organisation: "+organisation+", objective: "+target);
			explanation = new TargetExplanation(average, level);
		}
		else {
			if (isValid(matchingCalculation)) {
				DashboardExpressionExplanation expressionExplanation = getExpressionExplanation(target, organisation, period, matchingCalculation);
				explanation = new TargetExplanation(expressionExplanation, average, level);
			}
			else {
				List<DashboardExpressionExplanation> expressionExplanations = new ArrayList<DashboardExpressionExplanation>();
				for (DashboardCalculation calculation : target.getCalculations().values()) {
					if (isValid(calculation)) {
						DashboardExpressionExplanation expressionExplanation = getExpressionExplanation(target, organisation, period, calculation);
						expressionExplanations.add(expressionExplanation);
					}
				}
				
				Map<Organisation, DashboardPercentage> values = new HashMap<Organisation, DashboardPercentage>();
				getValueForNonLeafTarget(target, organisation, period, values);
				
				explanation = new TargetExplanation(expressionExplanations, average, level, values);
			}
		}
		return explanation;
	}


	private DashboardExpressionExplanation getExpressionExplanation(DashboardTarget target, Organisation organisation, Period period, DashboardCalculation matchingCalculation) {
		Expression expression = matchingCalculation.getExpression();
		
		String htmlExpression = getHTMLFormula(expression.getExpression(), organisation, target);

		DashboardPercentage value = null;
		List<RelevantData> relevantDatas = null;
		if (organisation.getChildren().size() == 0) {
			// we calculate the values
			Map<AbstractNameableObject, Object> valueMap = new LinkedHashMap<AbstractNameableObject, Object>();
			value = getValueForLeafTarget(expression, target, organisation, period, valueMap);
			
			relevantDatas = new ArrayList<RelevantData>();
			for (Entry<AbstractNameableObject, Object> entry : valueMap.entrySet()) {
				relevantDatas.add(new RelevantData(entry.getKey(), entry.getValue()));
			}
		}
		else {
			// we calculate the averages
			// not here though
		}
		
		DashboardExpressionExplanation expressionExplanation = new DashboardExpressionExplanation(matchingCalculation, htmlExpression, value, relevantDatas);
		return expressionExplanation;
	}
	

	private String getHTMLFormula(String formula, Organisation organisation, DashboardEntry entry) {
		if (log.isDebugEnabled()) log.debug("getHTMLFormula(formula="+formula+")");
		
		Set<DataElement> dataElements = expressionService.getDataElementsInExpression(formula);
		Map<Integer, String> replacement = new HashMap<Integer, String>();
		for (DataElement dataElement : dataElements) {
			replacement.put(dataElement.getId(), "<span data-organisation=\""+organisation.getOrganisationUnit().getId()+"\" data-objective=\""+entry.getId()+"\" data-id=\""+dataElement.getId()+"\" class=\"element\" id=\"element-"+organisation.getOrganisationUnit().getId()+"-"+entry.getId()+"-"+dataElement.getId()+"\">["+dataElement.getId()+"]</span>");
		}
		return expressionService.convertStringExpression(formula, replacement);
	}
	
	public void setOrganisationUnitService(OrganisationUnitService organisationUnitService) {
		this.organisationUnitService = organisationUnitService;
	}
	
}
