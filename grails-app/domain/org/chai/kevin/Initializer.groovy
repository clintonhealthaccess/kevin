package org.chai.kevin

import java.util.Date;

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
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategory;
import org.hisp.dhis.dataelement.DataElementCategoryCombo;
import org.hisp.dhis.dataelement.DataElementCategoryOption;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.Enum;
import org.hisp.dhis.dataelement.EnumOption;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;

class Initializer {

	static Date mar01 = getDate( 2005, 3, 1 );
	static Date mar31 = getDate( 2005, 3, 31 );
	static Date mar011 = getDate( 2006, 3, 1 );
	static Date mar311 = getDate( 2006, 3, 31 );
	
	static def createDummyStructure() {
		
		if (!Period.count()) {
			// periods
			def monthly = new MonthlyPeriodType();
			monthly.save(failOnError: true)
			def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
			period.save(failOnError: true)
			
			def period2 = new Period(periodType: monthly, startDate: mar011, endDate: mar311)
			period2.save(failOnError: true)
		}
		
		if (!DataElementCategory.count()) {
			// Categories
			def categoryOption = new DataElementCategoryOption(name: DataElementCategoryOption.DEFAULT_NAME)
			categoryOption.save(failOnError: true)
			def category = new DataElementCategory( name: DataElementCategory.DEFAULT_NAME )
			category.categoryOptions = [categoryOption]
			category.save(failOnError: true)
			def categoryCombo = new DataElementCategoryCombo(name: DataElementCategoryCombo.DEFAULT_CATEGORY_COMBO_NAME)
			categoryCombo.categories = [category]
			categoryCombo.save(failOnError: true)
			def categoryOptionCombo = new DataElementCategoryOptionCombo(categoryCombo: categoryCombo)
			categoryOption.categoryOptionCombos = [categoryOptionCombo]
			categoryOptionCombo.save(failOnError: true)
		}
		
		if (!OrganisationUnit.count()) {	
			// organisation level
			new OrganisationUnitLevel(level: 1, name: "Country").save(failOnError: true)
			new OrganisationUnitLevel(level: 2, name: "Province").save(failOnError: true)
			new OrganisationUnitLevel(level: 3, name: "District").save(failOnError: true)
			new OrganisationUnitLevel(level: 4, name: "Facility").save(failOnError: true)
	
			// organisations
			def rwanda = new OrganisationUnit(name: "Rwanda", shortName: "RW")
			def north = new OrganisationUnit(name: "North", shortName:"RW,N", parent: rwanda)
			rwanda.children = [north];
			def burera = new OrganisationUnit(name: "Burera", shortName:"RW,N,BU", parent: north)
			north.children = [burera]
			def butaro = new OrganisationUnit(name: "Butaro DH", shortName:"RW,N,BU,BUDH", parent: burera)
			def kivuye = new OrganisationUnit(name: "Kivuye HC", shortName:"RW,N,BU,KIHC", parent: burera)
			burera.children = [butaro, kivuye]
			rwanda.save(failOnError: true, flush: true)
			
			def groupSet = new OrganisationUnitGroupSet(name:"Type")
			groupSet.save(failOnError: true)
			
			// organisation groups
			def dh = new OrganisationUnitGroup(name: "District Hospital", uuid: "District Hospital", members: [butaro], groupSet: OrganisationUnitGroupSet.findByName('Type'))
			def hc = new OrganisationUnitGroup(name: "Health Center", uuid: "Health Center", members: [kivuye], groupSet: OrganisationUnitGroupSet.findByName('Type'))
			butaro.groups = [dh]
			kivuye.groups = [hc]
			butaro.save(failOnError: true)
			kivuye.save(failOnError: true)
			dh.save(failOnError: true)
			hc.save(failOnError: true)
			
			groupSet.organisationUnitGroups = [dh, hc]
			groupSet.save(failOnError: true)
		}
		
	}
	
	static def createDataElementsAndExpressions() {
		
		if (!Enum.count()) {
			// Enumerations
			def enume = new Enum(name:"Enum 1", code:"ENUM1");
			def enumOption1 = new EnumOption(name:"Value 1", value:"value1", code:"OPTION1", enume: enume);
			def enumOption2 = new EnumOption(name:"Value 2", value:"value2", code:"OPTION2", enume: enume);
			enume.enumOptions = [enumOption1, enumOption2]
			enume.save(failOnError: true)
			enumOption1.save(failOnError: true)
			enumOption2.save(failOnError: true)
		}
		
		if (!DataElement.count()) {
			// Data Elements
			def dataElement1 = new DataElement(name:"Element 1", shortName: "Element 1", code:"CODE1", type: DataElement.VALUE_TYPE_INT, aggregationOperator: DataElement.AGGREGATION_OPERATOR_SUM)
			def dataElement2 = new DataElement(name:"Element 2", shortName: "Element 2", code:"CODE2", type: DataElement.VALUE_TYPE_INT, aggregationOperator: DataElement.AGGREGATION_OPERATOR_SUM)
			def dataElement3 = new DataElement(name:"Element 3", shortName: "Element 3", code:"CODE3", type: DataElement.VALUE_TYPE_ENUM, enumType: Enum.findByName('Enum 1'), aggregationOperator: DataElement.AGGREGATION_OPERATOR_SUM)
			// Data Sets
			def dataSet1 = new DataSet(name:"Dataset 1", shortName: "Dataset 1", code:"DATASET1", periodType: MonthlyPeriodType.list()[0])
			def dataSet2 = new DataSet(name:"Dataset 2", shortName: "Dataset 2", code:"DATASET2", periodType: MonthlyPeriodType.list()[0])
	
			dataElement1.dataSets = [dataSet1]
			dataElement2.dataSets = [dataSet2]
			dataSet1.dataElements.add dataElement1
			dataSet2.dataElements.add dataElement2
			dataElement1.save(failOnError: true)
			dataElement2.save(failOnError: true)
			dataSet1.save(failOnError: true)
			dataSet2.save(failOnError: true)
			
			dataElement3.save(failOnError: true)
			
			// data value
			new DataValue(
				dataElement: DataElement.findByName("Element 1"),
				period: Period.list()[1],
				optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Butaro DH"),
				value: "30",
				comment: "Comment",
				storedBy: "StoredBy",
				timestamp: new Date(),
				followup: false,
			).save(failOnError: true)
	
			
			// data value
			new DataValue(
				dataElement: DataElement.findByName("Element 1"),
				period: Period.list()[1],
				optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Kivuye HC"),
				value: "40",
				comment: "Comment",
				storedBy: "StoredBy",
				timestamp: new Date(),
				followup: false,
			).save(failOnError: true)
	
			// data value
			new DataValue(
				dataElement: DataElement.findByName("Element 3"),
				period: Period.list()[1],
				optionCombo: DataElementCategoryOptionCombo.list()[0],
				source: OrganisationUnit.findByName("Kivuye HC"),
				value: "value1",
				comment: "Comment",
				storedBy: "StoredBy",
				timestamp: new Date(),
				followup: false,
			).save(failOnError: true)
		}
		
		if (!Expression.count()) {
			// indicators
	//		new IndicatorType(name:"one", factor: 100).save(failOnError: true)
			new Expression(name:"Constant 10", expression: "10", type: ExpressionType.VALUE).save(failOnError: true)
			new Expression(name:"Constant 20", expression: "20", type: ExpressionType.VALUE).save(failOnError: true)
			new Expression(name:"Element 1", expression: "["+DataElement.findByName("Element 1").id+"] + ["+DataElement.findByName("Element 1").id+"]", type: ExpressionType.VALUE).save(failOnError: true)
			new Expression(name:"Element 2", expression: "["+DataElement.findByName("Element 2").id+"]", type: ExpressionType.VALUE).save(failOnError: true)
			new Expression(name:"Element 3", expression: "["+DataElement.findByName("Element 3").id+"]", type: ExpressionType.VALUE).save(failOnError: true)
		}
		
		if (!Constant.count()) {
			new Constant(name:"Constant 1000", shortName:"CONST1", value: "1000", description: "Description").save(failOnError: true)
		}
	}
	
	static def createCost() {
		if (!CostRampUp.count()) {
			// Cost
			new CostRampUp(name: "Constant", years: [
				1: new CostRampUpYear(year: 1, value: 0.2),
				2: new CostRampUpYear(year: 2, value: 0.2),
				3: new CostRampUpYear(year: 3, value: 0.2),
				4: new CostRampUpYear(year: 4, value: 0.2),
				5: new CostRampUpYear(year: 5, value: 0.2)
			]).save(failOnError: true);
		}
	
		if (!CostObjective.count()) {
			new CostTarget(
				name: "Annual Internet Access Cost", description: "Annual Internet Access Cost",
				expression: Expression.findByName("Constant 10"),
				costType: CostType.OPERATION,
				costRampUp: CostRampUp.findByName("Constant"),
				groupUuidString: "District Hospital,Health Center"
			).save(failOnError: true)
			
			new CostTarget(
				name: "Connecting Facilities to the Internet", description: "Connecting Facilities to the Internet",
				expression: Expression.findByName("Constant 10"),
				costType: CostType.INVESTMENT,
				costRampUp: CostRampUp.findByName("Constant"),
				groupUuidString: "District Hospital,Health Center"
			).save(failOnError: true)
			
			new CostTarget(
				name: "New Phones for CHW Head Leader/Trainer & Assistant-Maintenance & Insurance", description: "New Phones for CHW Head Leader/Trainer & Assistant-Maintenance & Insurance",
				expression: Expression.findByName("Constant 10"),
				costType: CostType.INVESTMENT,
				costRampUp: CostRampUp.findByName("Constant"),
				groupUuidString: "District Hospital,Health Center"
			).save(failOnError: true)
			
			def ga = new CostObjective(name: "Geographical Access", description: "Geographical Access",)
			ga.addTarget(CostTarget.findByName("Annual Internet Access Cost"));
			ga.addTarget(CostTarget.findByName("Connecting Facilities to the Internet"));
			ga.addTarget(CostTarget.findByName("New Phones for CHW Head Leader/Trainer & Assistant-Maintenance & Insurance"));
			ga.save(failOnError: true)
			
			
			new CostTarget(
				name: "Facility Staff Training", description: "Facility Staff Training",
				expression: Expression.findByName("Constant 10"),
				costType: CostType.INVESTMENT,
				costRampUp: CostRampUp.findByName("Constant")
			).save(failOnError: true)
			
			def hrh = new CostObjective(name: "Human Resources for Health", description: "Human Resources for Health",)
			hrh.addTarget(CostTarget.findByName("Facility Staff Training"));
			hrh.save(failOnError: true)
		}		
	}
	
	static def createDashboard() {
		if (!DashboardObjective.count()) {
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
						"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Constant 10"))
					]
				).save(failOnError: true)
			new DashboardTarget(
					name: "Target 1", description: "Target 1",
					calculations: [
						"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Element 1")),
						"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Element 1"))
					]
				).save(failOnError: true)
			new DashboardTarget(
					name: "Missing Expression", description: "Missing Expression",
					calculations: [
						"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Element 1")),
					]
				).save(failOnError: true)
			new DashboardTarget(
					name: "Missing Data", description: "Missing Data",
					calculations: [
						"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Element 2")),
						"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Element 2"))
					]
				).save(failOnError: true)
			new DashboardTarget(
					name: "Enum", description: "Enum",
					calculations: [
						"District Hospital": new DashboardCalculation(groupUuid: "District Hospital", expression: Expression.findByName("Element 3")),
						"Health Center": new DashboardCalculation(groupUuid: "Health Center", expression: Expression.findByName("Element 3"))
					]
				).save(failOnError: true)
				
			def staffing = new DashboardObjective(root: false, name:"Staffing", description: "Staffing", weightedObjectives: [])
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Nurse A1"), weight: 1, order: 1)
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Nurse A2"), weight: 1, order: 2)
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Target 1"), weight: 1, order: 3)
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Missing Expression"), weight: 1, order: 4)
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Missing Data"), weight: 1, order: 5)
			staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByName("Enum"), weight: 1, order: 6)
			staffing.save(failOnError: true)
			
			def hrh = new DashboardObjective(root: false, name:"Human Resources for Health", description: "Human Resources for Health", weightedObjectives: [])
			hrh.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardObjective.findByName("Staffing"), weight: 1, order: 1)
			hrh.save(failOnError: true)
			
			def root = new DashboardObjective(root: true, name:"Strategic Objectives", description: "Strategic Objectives", weightedObjectives: [])
			root.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardObjective.findByName("Human Resources for Health"), weight: 1, order: 1)
			root.save(failOnError: true, flush: true)
		}
	}
	
	static def createUsers() {
		// users
		if (!User.count()) {
			new User(surname: "admin", firstName: "admin").save(failOnError: true)
		}
		if (!UserAuthorityGroup.count()) {
			new UserAuthorityGroup(name: "Superuser", authorities: ["ALL"]).save(failOnError: true)
		}
		if (!UserCredentials.count()) {
			new UserCredentials(user: User.findBySurname("admin"), userAuthorityGroups: [UserAuthorityGroup.findByName("Superuser")],username: "admin", password: "48e8f1207baef1ef7fe478a57d19f2e5").save(failOnError: true)
		}
	}
	
	static def createRootObjective() {
		// users
		if (!DashboardObjective.count()) {
			def root = new DashboardObjective(root: true, name:"Strategic Objectives", description: "Strategic Objectives", weightedObjectives: [])
			root.save(failOnError: true)
		}
	}
	
//	static def createIndicatorType() {
//		if (!IndicatorType.count()) {
//			new IndicatorType(name:"one", factor: 100).save(failOnError: true)
//		}
//	}
	
	public static Date getDate( int year, int month, int day )
	{
		final Calendar calendar = Calendar.getInstance();

		calendar.clear();
		calendar.set( Calendar.YEAR, year );
		calendar.set( Calendar.MONTH, month - 1 );
		calendar.set( Calendar.DAY_OF_MONTH, day );

		return calendar.getTime();
	}
	
}
