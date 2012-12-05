package org.chai.kevin.value;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Data;
import org.chai.kevin.data.DataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Enum;
import org.chai.kevin.data.EnumOption;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;

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
	
	def "test normalized data elements at data location level"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(10), Type.TYPE_NUMBER())
		def normalizedDataElement = null
		def result = null
		
		when: "data element is missing"
		normalizedDataElement = new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$0"]]).save(validate: false)
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value == Value.NULL_INSTANCE()
		result.status == Status.MISSING_DATA_ELEMENT
		
		when: "value is missing for data location"
		normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id]])
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value == Value.NULL_INSTANCE()
		result.status == Status.VALID

		when: "expression is missing for data location type"
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period)
				
		then:
		result.value == Value.NULL_INSTANCE()
		result.status == Status.MISSING_EXPRESSION
		
		when: "everything is fine"
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id+" * 2"]])
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value.numberValue == 80d
		result.status == Status.VALID
			
		when: "null value"
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(KIVUYE), Value.NULL_INSTANCE())
		normalizedDataElement = newNormalizedDataElement(CODE(4), Type.TYPE_NUMBER(), [(period.id+''):[(HEALTH_CENTER_GROUP):"\$"+dataElement.id]])
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period)
		
		then:
		result.value == Value.NULL_INSTANCE()
		result.status == Status.VALID
	}
	
	def "test check for null in formulas"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"if (\$"+dataElement.id+" == \"null\") 1 else 0"]])
		def result
		
		when: "value is missing"
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value == Value.VALUE_NUMBER(1);
		
		when: "value is null"
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.NULL_INSTANCE())
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value == Value.VALUE_NUMBER(1);
	}
	
	def "test check for null in formulas with list types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_LIST(Type.TYPE_MAP(["test": Type.TYPE_NUMBER()])), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"if (\$"+dataElement.id+" == \"null\") [] else [{\"test\":1}]"]])
		def result
		
		when: "value is missing"
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value.equals(Value.VALUE_LIST([]));
		
		when: "value is null"
		newRawDataElementValue(dataElement, period, DataLocation.findByCode(BUTARO), Value.NULL_INSTANCE())
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value.equals(Value.VALUE_LIST([]));
	}
	
	def "test normalized data elements expression empty expressions treated as missing"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(10), Type.TYPE_NUMBER())
		def normalizedDataElement = null
		def result = null
		
		when: "data element is missing"
		normalizedDataElement = new NormalizedDataElement(code: CODE(1), type: Type.TYPE_NUMBER(), expressionMap: [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):""]]).save(validate: false)
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.value == Value.NULL_INSTANCE()
		result.status == Status.MISSING_EXPRESSION
	}
	
	def "test normalized data element with typing errors"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = null
		def result = null
		
		when:
		normalizedDataElement = newNormalizedDataElement(CODE(2), type, [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):formula]])
		result = expressionService.calculateValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.status == Status.ERROR
		result.value == Value.NULL_INSTANCE()
		
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
		result = expressionService.calculatePartialValues(sum, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("1")])

		when:
		result = expressionService.calculatePartialValues(sum, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		result = expressionService.calculatePartialValues(sum, Location.findByCode(NORTH), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("1"), v("1")])
		
		when:
		def burera = Location.findByCode(BURERA)
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		newDataLocation(['en': 'dummy'], "dummy", burera, dh)
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(sum, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("2"), v("1")]))
	}

	def "test mode calculation"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"2",(HEALTH_CENTER_GROUP):"1"]])
		def mode = newMode("\$"+normalizedDataElement.id, CODE(2), Type.TYPE_NUMBER())
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(mode, DataLocation.findByCode(KIVUYE), period)
		
		then:
		result.size() == 1
		result*.value.equals([Value.VALUE_MAP(["1":v("1")])])
		
		when:
		result = expressionService.calculatePartialValues(mode, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([Value.VALUE_MAP(["2":v("1")])])

		when:
		result = expressionService.calculatePartialValues(mode, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([Value.VALUE_MAP(["2":v("1")]), Value.VALUE_MAP(["1":v("1")])])
		
		when:
		result = expressionService.calculatePartialValues(mode, Location.findByCode(NORTH), period)
		
		then:
		result.size() == 2
		result*.value.equals([Value.VALUE_MAP(["2":v("1")]), Value.VALUE_MAP(["1":v("1")])])
		
		when:
		def burera = Location.findByCode(BURERA)
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		newDataLocation(['en': 'dummy'], "dummy", burera, dh)
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(mode, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([Value.VALUE_MAP(["2":v("2")]), Value.VALUE_MAP(["1":v("1")])])
	}
		
	def "test sum with missing value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER());
		def sum = newSum("\$"+rawDataElement.id, CODE(2))
		def result
		
		when:
		result = expressionService.calculatePartialValues(sum, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([Value.NULL_INSTANCE()])
		
		when:
		result = expressionService.calculatePartialValues(sum, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([v("0"), v("0")])
	}
	
	def "test mode with missing value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER());
		def mode = newMode("\$"+rawDataElement.id, CODE(2), Type.TYPE_NUMBER())
		def result
		
		when:
		result = expressionService.calculatePartialValues(mode, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v([])])
		
		when:
		result = expressionService.calculatePartialValues(mode, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([v([]), v([])])
	}
	
	def "test sum with missing data location type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		def sum = newSum("\$"+normalizedDataElement.id, CODE(2))
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(sum, DataLocation.findByCode(BUTARO), period)
		
		then:
		result.size() == 1
		result*.value.equals([v("1")])

		when:
		result = expressionService.calculatePartialValues(sum, DataLocation.findByCode(KIVUYE), period)
		
		then:
		result.size() == 1
		result*.value.equals([Value.NULL_INSTANCE()])
		
		when:
		result = expressionService.calculatePartialValues(sum, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("0"), v("1")]))
		
		when:
		def burera = Location.findByCode(BURERA)
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		newDataLocation(['en': 'dummy'], "dummy", burera, dh)
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(sum, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		s(result*.value).equals(s([v("0"), v("2")]))
	}
	
	def "test mode with missing data location type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		def mode = newMode("\$"+normalizedDataElement.id, CODE(2), Type.TYPE_NUMBER())
		def result = null
		refreshNormalizedDataElement()
		
		when:
		result = expressionService.calculatePartialValues(mode, DataLocation.findByCode(BUTARO), period)
		then:
		result.size() == 1
		result*.value.equals([Value.VALUE_MAP(["1":v("1")])])

		when:
		result = expressionService.calculatePartialValues(mode, DataLocation.findByCode(KIVUYE), period)
		
		then:
		result.size() == 1
		result*.value.equals([v([])])
		
		when:
		result = expressionService.calculatePartialValues(mode, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([Value.VALUE_MAP(["1":v("1")]), v([])])
		
		when:
		def burera = Location.findByCode(BURERA)
		def dh = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		newDataLocation(['en': 'dummy'], "dummy", burera, dh)
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		refreshNormalizedDataElement()
		result = expressionService.calculatePartialValues(mode, Location.findByCode(BURERA), period)
		
		then:
		result.size() == 2
		result*.value.equals([Value.VALUE_MAP(["1":v("2")]), v([])])
	}
	
	def "data elements in expression"() {
		setup:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def sum = newSum("1", CODE(2))
		def data = null
		
		when:
		data = expressionService.getDataInExpression("\$"+sum.id+"+"+"\$"+rawDataElement.id, RawDataElement.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([null, rawDataElement]))
		
		when:
		data = expressionService.getDataInExpression("\$"+rawDataElement.id, Summ.class)
		
		then:
		data.size() == 1
		s(data.values()).equals(s([null]))
		
		when:
		data = expressionService.getDataInExpression("\$"+sum.id+"+"+"\$"+rawDataElement.id, Summ.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([null, sum]))
		
		when:
		data = expressionService.getDataInExpression("\$"+sum.id+"+"+"\$"+rawDataElement.id, Data.class)
		
		then:
		data.size() == 2
		s(data.values()).equals(s([sum, rawDataElement]))
		
		when:
		data = expressionService.getDataInExpression("\$"+sum.id+"+"+"\$"+rawDataElement.id, DataElement.class)
		
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
	
	def "expressions with end-of-line are valid"() {
		when:
		def formula = "1\n+1"
		
		then:
		expressionService.expressionIsValid(formula, Data.class)
	}

	def "test expression validation"() {

		setup:
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def formula = null
				
		when:
		formula = "(1"
		expressionService.expressionIsValid(formula, Data.class)
		
		then:
		thrown IllegalArgumentException
		
		when:
		formula = "if((10,1,0)"
		expressionService.expressionIsValid(formula, Data.class)
		
		then:
		thrown IllegalArgumentException
		
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
		
		then: // this changed, there is no more type check
		expressionService.expressionIsValid(formula, Data.class)
		
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
		!expressionService.expressionIsValid(formula, Summ.class)
	}
	
	def "test circular dependency"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		
		when:
		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1",(HEALTH_CENTER_GROUP):"1"]])
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement1) == false
		
		when:
		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id,(HEALTH_CENTER_GROUP):"1"]])
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement2) == false
		
		when:
		def normalizedDataElement3 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement2.id,(HEALTH_CENTER_GROUP):"1"]])
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement3) == false
		
		when:
		normalizedDataElement2.expressionMap = [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement3.id,(HEALTH_CENTER_GROUP):"1"]]
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement3) == true
		
		when:
		normalizedDataElement2.expressionMap = [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id,(HEALTH_CENTER_GROUP):"\$"+normalizedDataElement3.id]]
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement3) == false
		
		when:
		def normalizedDataElement4 = newNormalizedDataElement(CODE(4), Type.TYPE_NUMBER(), [:])
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement4) == false
		
		when:
		def dataElement = newRawDataElement(CODE(5), Type.TYPE_NUMBER())
		def normalizedDataElement5 = newNormalizedDataElement(CODE(6), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+dataElement.id,(HEALTH_CENTER_GROUP):"1"]])
		
		then:
		expressionService.hasCircularDependency(normalizedDataElement5) == false
		
	}
	
}
