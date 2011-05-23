package org.chai.kevin

import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Expression.ExpressionType;
import org.chai.kevin.cost.CostObjective;
import org.chai.kevin.cost.CostRampUp;
import org.chai.kevin.cost.CostRampUpYear;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.dashboard.DashboardCalculation;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.dashboard.DashboardTarget;
import org.hisp.dhis.dataelement.Constant;
import org.chai.kevin.DataElement;
import org.chai.kevin.DataElement.DataElementType;
import org.chai.kevin.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;

class IntegrationTestInitializer {

	private static final log = LogFactory.getLog(this)

	static def createExpressions() {
		// indicators
//		new IndicatorType(name:"one", factor: 1).save(failOnError: true)
//		new Indicator(name:"Constant 10", shortName: "Constant 10", code: "CONST10", numerator: "10", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true);
//		new Indicator(name:"Constant 20", shortName: "Constant 20", code: "CONST20", numerator: "20", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true);
		new Expression(name:"Constant 10", expression: "10", type: ExpressionType.VALUE).save(failOnError: true)
		new Expression(name:"Constant 20", expression: "20", type: ExpressionType.VALUE).save(failOnError: true)
		
	}
	
	static def createDashboard() {
//		log.debug("creating database, data values: "+DataValue.list()+", organisations: "+OrganisationUnit.list()+", periods: "+Period.list());
		
		// objectives and targets for dashboard
		new DashboardTarget(
				name: "Nurse A1", description: "Nurse A1",
				calculations: [
					"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Constant 10")),
					"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Constant 10"))
				]
			).save(failOnError: true)
		new DashboardTarget(
				name: "Nurse A2", description: "Nurse A2",
				calculations: [
					"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Constant 20")),
					"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Constant 20"))
				]
			).save(failOnError: true)
			
		def staffing = new DashboardObjective(root: false, name:"Staffing", description: "Staffing", objectiveEntries: [])
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Nurse A1"), weight: 1, order: 1)
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Nurse A2"), weight: 1, order: 2)
		staffing.save(failOnError: true)
		
		def hrh = new DashboardObjective(root: false, name:"Human Resources for Health", description: "Human Resources for Health", objectiveEntries: [])
		hrh.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardObjective.findByName("Staffing"), weight: 1, order: 1)
		hrh.save(failOnError: true)
		
		def root = new DashboardObjective(root: true, name:"Strategic Objectives", description: "Strategic Objectives", objectiveEntries: [])
		root.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardObjective.findByName("Human Resources for Health"), weight: 1, order: 1)
		root.save(failOnError: true)
		
	}
	
	static def createDataElements() {
		def dataElement = new DataElement(name:"Element 1", shortName: "Element 1", code: "CODE", description: "Description", type: DataElementType.INT)
		dataElement.save(failOnError: true)
	}
	
	static def addNonConstantData() {
		// Data Elements
		// data value
		new DataValue(
				dataElement: DataElement.findByName("Element 1"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Butaro DH"),
			value: "40",
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		// Indicators on data elements
//		new Indicator(name:"Indicator Element 1", shortName: "Indicator Element 1", code: "ELEM1", numerator: "["+dataElement.id+"]", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true)
		new Expression(name:"Expression Element 1", expression: "["+DataElement.findByName("Element 1").id+"]", type: ExpressionType.VALUE).save(failOnError: true)
		
		// objectives and targets for dashboard
		new DashboardTarget(
				name: "Target 1", description: "Target 1",
				calculations: [
					"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Expression Element 1")),
					"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Expression Element 1"))
				]
			).save(failOnError: true)

		def staffing = DashboardObjective.findByName("Staffing")
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Target 1"), weight: 1, order: 3)
		staffing.save(failOnError: true)
	}
	
	static def createConstants() {
		new Constant(name:"Constant 1000", shortName:"CONST1", value: "1000", description: "Description").save(failOnError: true)
	}
	
	static def addCostData() {
		
		new CostRampUp(name: "Constant", years: [
			1: new CostRampUpYear(year: 1, value: 0.2),
			2: new CostRampUpYear(year: 2, value: 0.2),
			3: new CostRampUpYear(year: 3, value: 0.2),
			4: new CostRampUpYear(year: 4, value: 0.2),
			5: new CostRampUpYear(year: 5, value: 0.2)
		]).save(failOnError: true);
	
		def costObjective1 = new CostObjective(name:"Human Resources for Health")
		costObjective1.addTarget new CostTarget(name:"Training", expression: Expression.findByName("Constant 10"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, groupUuidString: "Health Center,District Hospital")
		costObjective1.addTarget new CostTarget(name:"Average", expression: Expression.findByName("Constant 10"), expressionEnd: Expression.findByName("Constant 20"), costRampUp: CostRampUp.findByName("Constant"), costType: CostType.INVESTMENT, groupUuidString: "Health Center,District Hospital")
		costObjective1.save(failOnError: true)
		
		def costObjective2 = new CostObjective(name:"Geographical Access")
		costObjective2.save(failOnError: true)
	}
	
}
