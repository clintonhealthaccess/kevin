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
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.util.JSONUtils;
import org.chai.kevin.value.AveragePartialValue;
import org.chai.kevin.value.CalculationPartialValue;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;
import org.chai.kevin.value.Value;

class RefreshValueServiceSpec extends IntegrationTests {

	def refreshValueService;
	
	def "test refresh normalized elements"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		
		then:
		NormalizedDataElementValue.count() == 0
		normalizedDataElement.calculated == null
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp != null
		normalizedDataElement.calculated != null
	}
	
	def "test refresh normalized elements updates timestamps"() {
		when:
		def period = newPeriod()
		setupLocationTree()
		def calculated = new Date()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]), calculated: calculated)
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, Value.NULL_INSTANCE())
		def timestamp = normalizedDataElementValue.timestamp
		
		then:
		NormalizedDataElementValue.count() == 1
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);

		then:
		NormalizedDataElementValue.count() == 2
		!NormalizedDataElementValue.list()[0].timestamp.equals(timestamp)	
		!normalizedDataElement.calculated.equals(calculated)
	}
	
	def "test normalized data elements not calculated at non-data-location level"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
	}
	
	def "test refresh calculations"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def average = newAverage("1", CODE(2))
		
		then:
		AveragePartialValue.count() == 0
		average.calculated == null
		
		when:
		refreshValueService.refreshCalculation(average);

		then:
		AveragePartialValue.count() == 8
		AveragePartialValue.list()[0].timestamp != null
		average.calculated != null
	}
	
	def "test refresh calculations updates timestamps"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def calculated = new Date()
		def average = newAverage("1", CODE(2), calculated)
		def partialValue = newAveragePartialValue(average, period, Location.findByCode(BURERA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		def timestamp = partialValue.timestamp
		
		then:
		AveragePartialValue.count() == 1
		
		when:
		refreshValueService.refreshCalculation(average);

		then:
		AveragePartialValue.count() == 8
		!AveragePartialValue.list()[0].timestamp.equals(timestamp)	
		!average.calculated.equals(calculated)
	}
	
	def "test refresh normalized data elements refreshes dependencies first"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement2);
		
		then:
		NormalizedDataElementValue.count() == 4
		NormalizedDataElementValue.list()[0].value.numberValue == 1
		NormalizedDataElementValue.list()[1].value.numberValue == 1
		NormalizedDataElementValue.list()[2].value.numberValue == 1
		NormalizedDataElementValue.list()[3].value.numberValue == 1
	}
	
	def "test refresh normalized data element for period and location refreshes dependencies first"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].value.numberValue == 1
	}
	
	def "test refresh with circular dependencies"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[:]]))
		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
		normalizedDataElement1.expressionMap = e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement2.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement2.id]])
		normalizedDataElement1.save(failOnError: true)
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].value.isNull()
		NormalizedDataElementValue.list()[1].value.isNull()
		
	}
	
	def "test needs update when raw data element has value"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(KIVUYE), v("10"))
		def normalizedDataElement1 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(HEALTH_CENTER_GROUP):"\$"+rawDataElement.id]]))
		
		expect:
		refreshValueService.needsUpdate(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period)
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("10"))
		
		then:
		!refreshValueService.needsUpdate(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period)
		
		when:
		normalizedDataElement1.timestamp = new Date()
		normalizedDataElement1.save(failOnError: true)
		
		then:
		refreshValueService.needsUpdate(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period)
		
		when:
		value1.timestamp = new Date()
		value1.save(failOnError: true)
		
		then:
		!refreshValueService.needsUpdate(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period)
		
		when:
		def normalizedDataElement2 = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
		
		then:
		refreshValueService.needsUpdate(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period)
		
		when:
		newNormalizedDataElementValue(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("10"))
		
		then:
		!refreshValueService.needsUpdate(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period)
		
		when:
		rawDataElement.setTimestamp(new Date())
		
		then:
		refreshValueService.needsUpdate(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period)
		refreshValueService.needsUpdate(normalizedDataElement1, DataLocation.findByCode(KIVUYE), period)
		
	}
	
	// these are commented out because they don't work with the propagation=REQUIRES_NEW
	// annotation in RefreshValueService
//	def "test refresh normalized data elements respects dependency"() {
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1", (HEALTH_CENTER_GROUP):"1"]]))
//		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
//		
//		when:
//		refreshValueService.refreshNormalizedDataElements()
//		
//		then:
//		NormalizedDataElementValue.count() == 4
//		NormalizedDataElementValue.list()[0].value.numberValue == 1
//		NormalizedDataElementValue.list()[1].value.numberValue == 1
//		NormalizedDataElementValue.list()[2].value.numberValue == 1
//		NormalizedDataElementValue.list()[3].value.numberValue == 1
//	}
//	
//	def "test refresh normalized data elements with circular dependency still works"() {
//		setup:
//		setupLocationTree()
//		def period = newPeriod()
//		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[:]]))
//		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
//		normalizedDataElement1.expressionMap = e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement2.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement2.id]])
//		normalizedDataElement1.save(failOnError: true)
//		
//		when:
//		refreshValueService.refreshNormalizedDataElements()
//		
//		then:
//		NormalizedDataElementValue.count() == 4
//		NormalizedDataElementValue.list()[0].value.isNull()
//		NormalizedDataElementValue.list()[1].value.isNull()
//		NormalizedDataElementValue.list()[2].value.isNull()
//		NormalizedDataElementValue.list()[3].value.isNull()
//	}
	
	
}
