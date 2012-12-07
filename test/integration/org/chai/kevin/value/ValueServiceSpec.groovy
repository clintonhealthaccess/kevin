package org.chai.kevin.value

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

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.Period;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.Summ;
import org.chai.kevin.data.Type;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.AggregationValue;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;

class ValueServiceSpec extends IntegrationTests {

	def valueService;
	
	def "test get raw data element value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def type = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		
		when: "empty value list"
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		then:
		valueService.getDataElementValue(rawDataElement, DataLocation.findByCode(BUTARO), period) == null
		
		when:
		def dataValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("1"))
		
		then:
		valueService.getDataElementValue(rawDataElement, DataLocation.findByCode(BUTARO), period).equals(dataValue)
	}
	
	def "test get normalized data element value"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		
		when:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		
		then: "empty value list"
		valueService.getDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period) == null
		
		when:
		def dataValue = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		
		then:
		valueService.getDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period).equals(dataValue)
	}
	
	def "test get ratio value"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		
		when:
		def ratio = newSum("1", CODE(1))
		def expectedValue = new SumValue([], ratio, period, DataLocation.findByCode(BUTARO))
		def value = valueService.getCalculationValue(ratio, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def partialValue = newSumPartialValue(ratio, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		expectedValue = new SumValue([partialValue], ratio, period, DataLocation.findByCode(BUTARO))
		value = valueService.getCalculationValue(ratio, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))

		then:
		value.equals(expectedValue)
	}
	
	def "test get sum value"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		
		when:
		def sum = newSum("1", CODE(1))
		def expectedValue = new SumValue([], sum, period, DataLocation.findByCode(BUTARO))
		def value = valueService.getCalculationValue(sum, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def partialValue = newSumPartialValue(sum, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		expectedValue = new SumValue([partialValue], sum, period, DataLocation.findByCode(BUTARO))
		value = valueService.getCalculationValue(sum, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))

		then:
		value.equals(expectedValue)
	}
	
	def "test get mode value"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		
		when:
		def mode = newMode("1", CODE(1), Type.TYPE_LIST(Type.TYPE_NUMBER()))
		def expectedValue = new ModeValue(["1"], mode, period, DataLocation.findByCode(BUTARO))
		def value = valueService.getCalculationValue(mode, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def partialValue = newModePartialValue(mode, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		expectedValue = new ModeValue([partialValue], mode, period, DataLocation.findByCode(BUTARO))
		value = valueService.getCalculationValue(mode, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))

		then:
		value.equals(expectedValue)
	}
	
	def "test get aggregation value"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		
		when:
		def aggregation = newAggregation("1", CODE(1))
		def expectedValue = new AggregationValue([], aggregation, period, DataLocation.findByCode(BUTARO))
		def value = valueService.getCalculationValue(aggregation, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))
		 
		then:
		value.equals(expectedValue)
		
		when:
		def dataElement = newRawDataElement(CODE(2), Type.TYPE_NUMBER());
		aggregation = newAggregation("\$"+dataElement.id, CODE(3))
		def partialValue = newAggregationPartialValue(aggregation, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), "\$"+dataElement.id, v("1"))
		expectedValue = new AggregationValue([partialValue], aggregation, period, DataLocation.findByCode(BUTARO))
		value = valueService.getCalculationValue(aggregation, DataLocation.findByCode(BUTARO), period, s([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]))

		then:
		value.equals(expectedValue)
	}
	
	def "test number of values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def type = DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)
		
		when: 
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.getNumberOfValues(rawDataElement, period) == 1
		valueService.getNumberOfValues(DataLocation.findByCode(BUTARO), RawDataElementValue.class) == 1
		valueService.getNumberOfValues(DataLocation.findByCode(KIVUYE), RawDataElementValue.class) == 0
		
		when:
		def newPeriod = newPeriod(2006)
		
		then:
		valueService.getNumberOfValues(rawDataElement, newPeriod) == 0
		valueService.getNumberOfValues(DataLocation.findByCode(BUTARO), RawDataElementValue.class) == 1
				
		when:
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		
		then:
		valueService.getNumberOfValues(rawDataElement2, period) == 0
		valueService.getNumberOfValues(DataLocation.findByCode(KIVUYE), RawDataElementValue.class) == 0
	}
	
	def "test number of values with status and wrong type"() {
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		valueService.getNumberOfValues(rawDataElement, Status.VALID, null)
		
		then:
		thrown IllegalArgumentException
	}

	def "test number of values with status"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		when:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.ERROR, v("1"))
		
		then:
		valueService.getNumberOfValues(normalizedDataElement, Status.ERROR, null) == 1
		valueService.getNumberOfValues(normalizedDataElement, Status.VALID, null) == 0
		valueService.getNumberOfValues(normalizedDataElement, Status.ERROR, period) == 1
		
		when:
		def period2 = newPeriod(2006)
		
		then:
		valueService.getNumberOfValues(normalizedDataElement, Status.ERROR, period2) == 0
	}
		
	def "test number of values does not count other value types"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		when:
		def rawDataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement1, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.getNumberOfValues(rawDataElement1, period) == 1
	}
	
	def "test value list"() {
		
		setup:
		setupLocationTree()
		def period = newPeriod()
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.listDataValues(rawDataElement, null, period, [:]).equals([rawDataElementValue])
		
		when:
		def period2 = newPeriod(2006)
		def rawDataElementValue2 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.listDataValues(rawDataElement, null, period, [:]).equals([rawDataElementValue])
		s(valueService.listDataValues(rawDataElement, null, null, [:])).equals(s([rawDataElementValue2, rawDataElementValue]))
		
		when:
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def rawDataElementValue21 = newRawDataElementValue(rawDataElement2, period, DataLocation.findByCode(KIVUYE), v("40"))
		
		then:
		valueService.listDataValues(rawDataElement2, null, period, [:]).equals([rawDataElementValue21])
		valueService.listDataValues(rawDataElement, DataLocation.findByCode(BUTARO), period, [:]).equals([rawDataElementValue])
		
		when:
		def sum = newSum("1", CODE(4))
		def sumValue = newSumPartialValue(sum, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 0, v("1"))
		
		then:
		valueService.listDataValues(sum, null, period, [:]).equals([sumValue])
		valueService.listDataValues(sum, DataLocation.findByCode(BUTARO), period, [:]).equals([sumValue])
	}
	
	def "test normalized value list with order"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+""):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		refreshNormalizedDataElement()
		
		when:
		def value1 = NormalizedDataElementValue.list().find {it.status == Status.VALID}
		def value2 = NormalizedDataElementValue.list().find {it.status == Status.MISSING_EXPRESSION}
		
		then:
		valueService.listDataValues(normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value2, value1])
		valueService.listDataValues(normalizedDataElement, null, period, ['sort':'status', 'order':'desc']).equals([value1, value2])
	}
	
	def "test search normalized value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+""):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		refreshNormalizedDataElement()
		
		when:
		def value1 = NormalizedDataElementValue.list().find {it.location.code == KIVUYE}
		def value2 = NormalizedDataElementValue.list().find {it.location.code == BUTARO}
		
		then:
		valueService.searchDataValues(KIVUYE, normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value1])
		valueService.searchDataValues(BUTARO, normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value2])
	}
	
	def "test search normalized value on name and code"() {
		setup:
		setupLocationTree()
		def kivuye = DataLocation.findByCode(KIVUYE)
		def butaro = DataLocation.findByCode(BUTARO)
		kivuye.names_en = 'loc1'
		butaro.names_en = 'loc2'
		kivuye.save(failOnError: true)
		butaro.save(failOnError: true)
		
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+""):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		refreshNormalizedDataElement()
		
		when:
		def value1 = NormalizedDataElementValue.list().find {it.location.code == KIVUYE}
		def value2 = NormalizedDataElementValue.list().find {it.location.code == BUTARO}
		
		then:
		valueService.searchDataValues("loc1", normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value1])
		valueService.searchDataValues("loc2", normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value2])
	}
	
	def "test search normalized value with order"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [(period.id+""):[(DISTRICT_HOSPITAL_GROUP):"1"]])
		refreshNormalizedDataElement()
		
		when:
		def value1 = NormalizedDataElementValue.list().find {it.status == Status.VALID}
		def value2 = NormalizedDataElementValue.list().find {it.status == Status.MISSING_EXPRESSION}
		
		then:
		valueService.searchDataValues("", normalizedDataElement, null, period, ['sort':'status', 'order':'asc']).equals([value2, value1])
		valueService.searchDataValues("", normalizedDataElement, null, period, ['sort':'status', 'order':'desc']).equals([value1, value2])
	}
	
	def "test value count"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		when:
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.countDataValues(null, rawDataElement, null, period) == 1
		
		when:
		def period2 = newPeriod(2006)
		def rawDataElementValue2 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.countDataValues(null, rawDataElement, null, period) == 1
		valueService.countDataValues(null, rawDataElement, null, null) == 2
		
		when:
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def rawDataElementValue21 = newRawDataElementValue(rawDataElement2, period, DataLocation.findByCode(KIVUYE), v("40"))
		
		then:
		valueService.countDataValues(null, rawDataElement2, null, period) == 1
		valueService.countDataValues(null, rawDataElement, DataLocation.findByCode(BUTARO), period) == 1
		
		when:
		def rawDataElementValue22 = newRawDataElementValue(rawDataElement2, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		valueService.countDataValues("but", rawDataElement2, null, period) == 1
		valueService.countDataValues("kiv", rawDataElement2, null, period) == 1
		valueService.countDataValues("", rawDataElement2, null, period) == 2
	}

	def "test delete data element values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		RawDataElementValue.count() == 1
		
		when:
		valueService.deleteValues(rawDataElement, null, null)
		
		then:
		RawDataElementValue.count() == 0
		
		when: "only deletes right values"
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		newRawDataElementValue(rawDataElement2, period, DataLocation.findByCode(BUTARO), v("40"))
		valueService.deleteValues(rawDataElement, null, null)
		
		then:
		RawDataElementValue.count() == 1
	}
	
	def "test delete data element values of period and location"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def rawDataElementValue1 = newRawDataElementValue(rawDataElement, period1, DataLocation.findByCode(BUTARO), v("40"))
		def rawDataElementValue2 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(KIVUYE), v("40"))
		def rawDataElementValue3 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		RawDataElementValue.count() == 3
		
		when:
		valueService.deleteValues(rawDataElement, null, period1)
		
		then:
		RawDataElementValue.count() == 2
		
		when:
		valueService.deleteValues(rawDataElement, DataLocation.findByCode(BUTARO), period2)
		then:
		RawDataElementValue.count() == 1
	}
	
	def "test delete data element values of period"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def rawDataElementValue1 = newRawDataElementValue(rawDataElement, period1, DataLocation.findByCode(BUTARO), v("40"))
		def rawDataElementValue2 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(KIVUYE), v("40"))
		def rawDataElementValue3 = newRawDataElementValue(rawDataElement, period2, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		RawDataElementValue.count() == 3
		
		when:
		valueService.deleteValues(null, null, period1)
		
		then:
		RawDataElementValue.count() == 2
		
		when:
		valueService.deleteValues(null, null, period2)
		then:
		RawDataElementValue.count() == 0
	}
	
	
	def "test delete all data element values of period and location"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		def rawDataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		
		when:
		newRawDataElementValue(rawDataElement1, period1, DataLocation.findByCode(BUTARO), v("40"))
		newRawDataElementValue(rawDataElement1, period2, DataLocation.findByCode(KIVUYE), v("40"))
		newRawDataElementValue(rawDataElement1, period2, DataLocation.findByCode(BUTARO), v("40"))
		newRawDataElementValue(rawDataElement2, period1, DataLocation.findByCode(BUTARO), v("40"))
		newRawDataElementValue(rawDataElement2, period2, DataLocation.findByCode(KIVUYE), v("40"))
		newRawDataElementValue(rawDataElement2, period2, DataLocation.findByCode(BUTARO), v("40"))
		
		then:
		RawDataElementValue.count() == 6
		
		when:
		valueService.deleteValues(null, DataLocation.findByCode(BUTARO), period1)
		
		then:
		RawDataElementValue.count() == 4
		
		when:
		valueService.deleteValues(null, DataLocation.findByCode(BUTARO), null)
		then:
		RawDataElementValue.count() == 2
	}
	
	
	def "test delete data element does not set last value changed date"() {
		setup:
		setupLocationTree()
		def period1 = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		
		when:
		def date = rawDataElement.getLastValueChanged()
		valueService.deleteValues(rawDataElement, null, period1)
		
		then:
		RawDataElement.list()[0].lastValueChanged.equals(date)
	}
	
	def "save raw data element value does not set last value changed date"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), [(period.id+""):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]])
		
		when:
		def date = rawDataElement.lastValueChanged
		def rawDataElementValue = newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("40"))
		valueService.save(rawDataElementValue);
		
		then:
		RawDataElement.list()[0].lastValueChanged.equals(date)
	}
	
	def "test delete calculation values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def ratio = newSum("1", CODE(1))
		
		when:
		newSumPartialValue(ratio, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		
		then:
		SumPartialValue.count() == 1
		
		when:
		valueService.deleteValues(ratio, null, null)
		
		then:
		SumPartialValue.count() == 0
		
		when:
		def ratio2 = newSum("2", CODE(2))
		newSumPartialValue(ratio, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		newSumPartialValue(ratio2, period, DataLocation.findByCode(BUTARO), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		valueService.deleteValues(ratio, null, null)
		
		then:
		SumPartialValue.count() == 1
	}
	
	def "test get partial values with no type"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def ratio = newSum("1", CODE(1))
		
		when:
		def values = valueService.getPartialValues(ratio, Location.findByCode(RWANDA), period, s([]))
		
		then:
		values.empty
	}
	
}
