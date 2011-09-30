package org.chai.kevin

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Date;

import org.apache.commons.logging.LogFactory;
import org.chai.kevin.cost.CostObjective;
import org.chai.kevin.cost.CostRampUp;
import org.chai.kevin.cost.CostRampUpYear;
import org.chai.kevin.cost.CostTarget;
import org.chai.kevin.cost.CostTarget.CostType;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.Expression;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.DataValue;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;

class IntegrationTestInitializer extends Initializer {

	private static final log = LogFactory.getLog(this)

	static def createExpressions() {
		// indicators
//		new IndicatorType(names:j(["en":"one"]), factor: 1).save(failOnError: true)
//		new Indicator(names:j(["en":"Constant 10"]), shortName: "Constant 10", code: "CONST10", numerator: "10", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true);
//		new Indicator(names:j(["en":"Constant 20"]), shortName: "Constant 20", code: "CONST20", numerator: "20", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true);
		new Expression(names:j(["en":"Constant 10"]), code:"CONST10", expression: "10", type: Type.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
		new Expression(names:j(["en":"Constant 20"]), code:"CONST20", expression: "20", type: Type.TYPE_NUMBER, timestamp: new Date()).save(failOnError: true)
		
	}
	
	static def createDashboard() {
//		log.debug("creating database, data values: "+DataValue.list()+", organisations: "+OrganisationUnit.list()+", periods: "+Period.list());
		
		def root = new DashboardObjective(root: true, names:j(["en":"Strategic Objectives"]), code:"OBJ", descriptions:j(["en":"Strategic Objectives"]), objectiveEntries: []).save(failOnError:true)
		def hrh = new DashboardObjectiveEntry(entry: new DashboardObjective(root: false, names:j(["en":"Human Resources for Health"]), code:"HRH", descriptions:j(["en":"Human Resources for Health"]), objectiveEntries: []), weight: 1, order: 1)
		root.addObjectiveEntry(hrh)
		hrh.save(failOnError: true)
		root.save(failOnError: true)
		
		def staffing = new DashboardObjectiveEntry(entry: new DashboardObjective(root: false, names:j(["en":"Staffing"]), code:"STAFFING", descriptions:j(["en":"Staffing"]), objectiveEntries: []), weight: 1, order: 1)
		hrh.entry.addObjectiveEntry(staffing)
		staffing.save(failOnError: true)
		hrh.save(failOnError: true)
		
		def calculation1 = new Average(expressions: [
			"District Hospital": Expression.findByCode("CONST10"),
			"Health Center": Expression.findByCode("CONST20")
		], timestamp:new Date(), type: Type.TYPE_NUMBER)
		calculation1.save()
		
		def target1 = new DashboardObjectiveEntry(entry: new DashboardTarget(
				names:j(["en":"Nurse A1"]), code:"A1", descriptions:j(["en":"Nurse A1"]),
				calculation: calculation1
			), weight: 1, order: 1)
		
		def calculation2 = new Average(expressions: [
				"District Hospital": Expression.findByCode("CONST20"),
				"Health Center": Expression.findByCode("CONST20")
			], timestamp:new Date(), type: Type.TYPE_NUMBER)
		calculation2.save()
		
		def target2 = new DashboardObjectiveEntry(entry: new DashboardTarget(
				names:j(["en":"Nurse A2"]), code:"A2", descriptions:j(["en":"Nurse A2"]),
				calculation: calculation2
			), weight: 1, order: 2)
		
		staffing.entry.addObjectiveEntry(target1)
		staffing.entry.addObjectiveEntry(target2)
		target1.save()
		target2.save()
		staffing.save()
		
	}
	
	static def createDataElements() {
		def dataElement = new DataElement(names:j(["en":"Element 1"]), code: "CODE", descriptions:j(["en":"Description"]), type: Type.TYPE_NUMBER)
		dataElement.save(failOnError: true)
	}
	
	static def addNonConstantData() {
		// Data Elements
		// data value
		new DataValue(
				dataElement: DataElement.findByCode("CODE"),
				period: Period.list()[1],
	//			optionCombo: DataElementCategoryOptionCombo.list()[0],
				organisationUnit: OrganisationUnit.findByName("Butaro DH"),
			value: v("40"),
//			comment: "Comment",
//			storedBy: "StoredBy",
			timestamp: new Date(),
//			followup: false,
		).save(failOnError: true)
		
		// Indicators on data elements
//		new Indicator(names:j(["en":"Indicator Element 1"]), shortName: "Indicator Element 1", code: "ELEM1", numerator: "["+dataElement.id+"]", denominator: "1", indicatorType: IndicatorType.findByName("one")).save(failOnError: true)
		new Expression(names:j(["en":"Expression Element 1"]), code:"EXPRELEM1", expression: "["+DataElement.findByCode("CODE").id+"]", type: Type.TYPE_NUMBER).save(failOnError: true)
		
		def calculation3 = new Average(expressions: [
			"District Hospital": Expression.findByCode("EXPRELEM1"),
			"Health Center": Expression.findByCode("EXPRELEM1")
		], timestamp:new Date(), type: Type.TYPE_NUMBER)
		calculation3.save()
		
		// objectives and targets for dashboard
		new DashboardTarget(
				names:j(["en":"Target 1"]), code:"TARGET1", descriptions:j(["en":"Target 1"]),
				calculation: calculation3
			).save(failOnError: true)

		def staffing = DashboardObjective.findByCode("STAFFING")
		staffing.addObjectiveEntry new DashboardObjectiveEntry(entry: DashboardTarget.findByCode("TARGET1"), weight: 1, order: 3)
		staffing.save(failOnError: true)
	}
	
	
}
