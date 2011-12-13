package org.chai.kevin.value;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

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

public class ExpressionServiceSpec extends IntegrationTests {

	def expressionService;
	def valueService;
	
	def "test normalized data elements at facility level"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(10), Type.TYPE_NUMBER())
		def normalizedDataElement = null
		def result = null
		
		when: "data element is missing"
		normalizedDataElement = new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$0"]])).save(validate: false)
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(BUTARO), period)
		
		then:
		result.value == Value.NULL
		result.status == Status.MISSING_DATA_ELEMENT
		
		when: "value is missing for facility"
		normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id]]))
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(BUTARO), period)
		
		then:
		result.value == Value.NULL
		result.status == Status.MISSING_VALUE

		when: "expression is missing for facility type"
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(KIVUYE), period)
				
		then:
		result.value == Value.NULL
		result.status == Status.DOES_NOT_APPLY
		
		when: "everything is fine"
		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(BUTARO), v("40"))
		normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id+" * 2"]]))
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(BUTARO), period)
		
		then:
		result.value.numberValue == 80d
		result.status == Status.VALID
			
		when: "null value"
		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), Value.NULL)
		normalizedDataElement = newNormalizedDataElement(CODE(4), Type.TYPE_NUMBER(), e([(period.id+''):[(HEALTH_CENTER_GROUP):"\$"+dataElement.id]]))
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(KIVUYE), period)
		
		then:
		result.value == Value.NULL
		result.status == Status.ERROR
	}
	
	def "test normalized data element at different levels"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(10), Type.TYPE_NUMBER())
		def normalizedDataElement = null
		def result = null
		
		when: 
		normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(organisation), period)
		
		then:
		thrown IllegalArgumentException
		
		where:
		organisation << [RWANDA, NORTH, BURERA]
	}
	
	def "test normalized data element with typing errors"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = null
		def result = null
		
		when:
		normalizedDataElement = newNormalizedDataElement(CODE(2), type, [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):formula]])
		result = expressionService.calculateValue(normalizedDataElement, getOrganisation(BUTARO), period)
		
		then:
		result.status == Status.ERROR
		result.value == Value.NULL
		
		where:
		type				| formula
		Type.TYPE_BOOL()	| "1"
		Type.TYPE_NUMBER()	| "true"
		
	}
	
	def "test sum calculation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1",(HEALTH_CENTER_GROUP):"1"]])
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("1")])

		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(NORTH), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		def burera = OrganisationUnit.findByName(BURERA)
		def dh = OrganisationUnitGroup.findByUuid(DISTRICT_HOSPITAL_GROUP)
		newOrganisationUnit("dummy", burera, dh)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(sum, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("2"), v("1")]))
	}
	
	def "test sum with missing facility type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("1")])

		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(KIVUYE), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("0")])
		
		when:
		result = expressionService.calculatePartialValues(sum, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("0"), v("1")]))
		
		when:
		def burera = OrganisationUnit.findByName(BURERA)
		def dh = OrganisationUnitGroup.findByUuid(DISTRICT_HOSPITAL_GROUP)
		newOrganisationUnit("dummy", burera, dh)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(sum, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("0"), v("2")]))
	}
	
	def "test average with valid calculation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1",(HEALTH_CENTER_GROUP):"1"]])
		def average = newAverage("\$"+normalizedDataElement.id, CODE(2))
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(average, getOrganisation(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("1")])

		when:
		result = expressionService.calculatePartialValues(average, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		result = expressionService.calculatePartialValues(average, getOrganisation(NORTH), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		def burera = OrganisationUnit.findByName(BURERA)
		def dh = OrganisationUnitGroup.findByUuid(DISTRICT_HOSPITAL_GROUP)
		newOrganisationUnit("dummy", burera, dh)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(average, getOrganisation(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("2"), v("1")]))

	}

//	def "test sum with missing expression"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		
//		when:
//		def sum = newSum([:], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == false
//		result.hasMissingExpression == true
//		result.value == v("0")
//	
//		where:
//		organisationName << [BUTARO, KIVUYE, BURERA, NORTH, RWANDA]
//	}
//		
//	def "test sum with data element"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(BUTARO), v("2"))
//		def expression = newExpression(CODE(3), Type.TYPE_NUMBER(), "\$"+dataElement.id)
//		refreshNormalizedDataElement()
//		
//		when:
//		def sum = newSum([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == false
//		result.hasMissingExpression == false
//		result.value == value
//		
//		where:
//		organisationName	| value
//		KIVUYE				| v("1")
//		BUTARO				| v("2")
//		BURERA				| v("3")
//		NORTH				| v("3")
//		RWANDA				| v("3")
//	}
//	
//	def "test sum with missing values"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
//		def expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "\$"+dataElement.id)
//		refreshNormalizedDataElement()
//		
//		when:
//		def sum = newSum([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(sum, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == missingValues
//		result.hasMissingExpression == false
//		result.value == value
//		
//		where:
//		organisationName	| value		| missingValues
//		BUTARO				| v("0")	| true
//		KIVUYE				| v("1")	| false
//		BURERA				| v("1")	| true
//		NORTH				| v("1")	| true	
//		RWANDA				| v("1")	| true
//		
//	}
//	
//	def "test average"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def kivuye = OrganisationUnit.findByName(KIVUYE)
//		def butaro = OrganisationUnit.findByName(BUTARO)
//		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
//		refreshNormalizedDataElement()
//		
//		when:
//		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(2), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == false
//		result.hasMissingExpression == false  
//		result.value == value
//		
//		where:
//		organisationName	| value
//		BUTARO				| v("1")
//		KIVUYE				| v("1")
//		BURERA				| v("1")
//		NORTH				| v("1")
//		RWANDA				| v("1")
//	}
//	
//	def "test average with missing expression"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//				
//		when:
//		def average = newAverage([:], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == false
//		result.hasMissingExpression == true
//		result.value == Value.NULL
//		
//		where:
//		organisationName << [BUTARO, KIVUYE, BURERA, NORTH, RWANDA]
//	}
//	
//	def "test average with data element"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(BUTARO), v("2"))
//		def expression = newExpression(CODE(3), Type.TYPE_NUMBER(), "\$"+dataElement.id)
//		refreshNormalizedDataElement()
//		
//		when:
//		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == false
//		result.hasMissingExpression == false
//		result.value == value
//		
//		where:
//		organisationName	| value
//		BUTARO				| v("2")
//		KIVUYE				| v("1")
//		BURERA				| v("1.5")
//		NORTH				| v("1.5")
//		RWANDA				| v("1.5")
//	}
//		
//	def "test average with missing values"() {
//		setup:
//		setupOrganisationUnitTree()
//		def period = newPeriod()
//		def dataElement = newRawDataElement(CODE(4), Type.TYPE_NUMBER())
//		newRawDataElementValue(dataElement, period, OrganisationUnit.findByName(KIVUYE), v("1"))
//		def expression = newExpression(CODE(5), Type.TYPE_NUMBER(), "\$"+dataElement.id)
//		refreshNormalizedDataElement()
//		
//		when:
//		def average = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(1), Type.TYPE_NUMBER())
//		def result = expressionService.calculate(average, OrganisationUnit.findByName(organisationName), period)
//		
//		then:
//		result.hasMissingValues == missingValues
//		result.hasMissingExpression == false
//		result.value == value
//		
//		where:
//		organisationName	| value		| missingValues
//		BUTARO				| Value.NULL| true
//		KIVUYE				| v("1")	| false
//		BURERA				| v("1")	| true
//		NORTH				| v("1")	| true
//		RWANDA				| v("1")	| true
//	}
	
	def "data elements in expression"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def average = newAverage("1", CODE(2))
		def data = null
		
		when:
		data = expressionService.getDataInExpression("\$"+average.id+"+"+"\$"+rawDataElement.id, RawDataElement.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([null, rawDataElement]))
		
		when:
		data = expressionService.getDataInExpression("\$"+rawDataElement.id, Average.class)
		
		then:
		data.size() == 1
		s(data.values()).equals(s([null]))
		
		when:
		data = expressionService.getDataInExpression("\$"+average.id+"+"+"\$"+rawDataElement.id, Average.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([null, average]))
		
		when:
		data = expressionService.getDataInExpression("\$"+average.id+"+"+"\$"+rawDataElement.id, Data.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([average, rawDataElement]))
		
		when:
		data = expressionService.getDataInExpression("\$"+average.id+"+"+"\$"+rawDataElement.id, DataElement.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([null, rawDataElement]))
	}
	
	def "data element in expression when wrong format"() {
		when:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def dataElements = expressionService.getDataInExpression("\$"+dataElement.id, RawDataElement.class)
		
		then:
		dataElements.size() == 1
		dataElements.getAt("\$"+dataElement.id).equals(dataElement)
		
		when:
		dataElements = expressionService.getDataInExpression("\$0", RawDataElement.class)
		
		then:
		dataElements.size() == 1
		dataElements.get("\$0") == null
		
		when:
		dataElements = expressionService.getDataInExpression("\$test", RawDataElement.class)
		
		then:
		dataElements.size() == 0
	}
	

	def "test expression validation"() {

		setup:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def formula = null
				
		when:
		formula = "(1"
		
		then:
		!expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "if((10,1,0)"
		
		then:
		!expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "123"
		
		then:
		expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "\$"+dataElement.id+" == 1"
		
		then:
		expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "\$"+dataElement.id+" == \"a\""
		
		then:
		!expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "1\n+1"
		
		then:
		!expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "\$0"
		
		then:
		!expressionService.expressionIsValid(formula, Data.class)
		
		when:
		formula = "if (\$"+dataElement.id+" == null) true else false"
		
		then:
		expressionService.expressionIsValid(formula, Data.class)
		
		when:
		def dataElement2 = newRawDataElement(CODE(2), Type.TYPE_STRING())
		formula = "convert(\$"+dataElement2.id+", schema double)"
		
		then:
		expressionService.expressionIsValid(formula, Data.class)
		
		when:
		def dataElement3 = newRawDataElement(CODE(3), Type.TYPE_STRING())
		formula = "convert(\$"+dataElement3.id+", schema double)"
		
		then:
		!expressionService.expressionIsValid(formula, Sum.class)
	}
	
}
