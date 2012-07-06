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
import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType;
import org.chai.kevin.location.Location;
import org.chai.kevin.util.JSONUtils;
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
		def date = normalizedDataElement.lastValueChanged
		
		then:
		NormalizedDataElementValue.count() == 0
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp != null
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements does not update anything when already up-to-date"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def date = normalizedDataElement.lastValueChanged
		normalizedDataElement.refreshed = date
		normalizedDataElement.save(failOnError: true)
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		def value2 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("1"))
		def date2 = value2.timestamp
		
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds == date1.seconds
		NormalizedDataElementValue.list()[1].timestamp.seconds == date2.seconds
		NormalizedDataElement.list()[0].lastValueChanged.equals(date)
		NormalizedDataElement.list()[0].refreshed.equals(date)
	}
	
	def "test refresh normalized elements does not update anything when already up-to-date - with period and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement, DataLocation.findByCode(BUTARO), period);
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].timestamp.seconds == date1.seconds
		NormalizedDataElement.list()[0].lastValueChanged.equals(date)
	}
	
	def "test refresh normalized elements updates when data element is updated"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def date = normalizedDataElement.lastValueChanged
		normalizedDataElement.refreshed = date
		normalizedDataElement.save(failOnError: true)
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		def value2 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("1"))
		def date2 = value2.timestamp
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElementValue.list()[1].timestamp.seconds != date2.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
		NormalizedDataElement.list()[0].refreshed.after(date)
	}
	
	def "test refresh normalized elements updates when data element is updated - with period and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		normalizedDataElement.timestamp = new Date()
		normalizedDataElement.save(failOnError: true)
		Thread.sleep(2000)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement, DataLocation.findByCode(BUTARO), period);
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements updates when dependent data element is updated"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		def value2 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("1"))
		def date2 = value2.timestamp
		rawDataElement.lastValueChanged = new Date()
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElementValue.list()[1].timestamp.seconds != date2.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements updates when dependency last value changed is null"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement1 = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		rawDataElement1.lastValueChanged = null
		rawDataElement1.save(failOnError: true)
		def rawDataElement2 = newRawDataElement(CODE(2), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(3), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement1.id+" + \$"+rawDataElement2.id]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		def value2 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("1"))
		def date2 = value2.timestamp
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElementValue.list()[1].timestamp.seconds != date2.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}


	def "test refresh normalized elements updates when last value changed is set after refresh"() {
		setup:
		def date = new Date()
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		
		when:
		normalizedDataElement.refreshed = date
		normalizedDataElement.lastValueChanged = new Date();
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements does not update when dependent data element last value is changed - with period and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		rawDataElement.lastValueChanged = new Date()
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement, DataLocation.findByCode(BUTARO), period);
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].timestamp.seconds == date1.seconds
		NormalizedDataElement.list()[0].lastValueChanged.equals(date)
	}
	
	def "test refresh normalized elements updates when dependent data element timestamp is changed - with period and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		rawDataElement.timestamp = new Date()
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement, DataLocation.findByCode(BUTARO), period);
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements updates when dependent data element value is updated - with period and location"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		def date = normalizedDataElement.lastValueChanged
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		newRawDataElementValue(rawDataElement, period, DataLocation.findByCode(BUTARO), v("1"))
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement, DataLocation.findByCode(BUTARO), period);
		
		then:
		NormalizedDataElementValue.count() == 1
		NormalizedDataElementValue.list()[0].timestamp.seconds != date1.seconds
		NormalizedDataElement.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh normalized elements does not update when dependent data element is not updated"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def rawDataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def normalizedDataElement = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+rawDataElement.id]]))
		def date = normalizedDataElement.lastValueChanged
		normalizedDataElement.refreshed = date
		normalizedDataElement.save(failOnError: true)
		
		when:
		def value1 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		def date1 = value1.timestamp
		def value2 = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(KIVUYE), period, Status.VALID, v("1"))
		def date2 = value2.timestamp
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds == date1.seconds
		NormalizedDataElementValue.list()[1].timestamp.seconds == date2.seconds
		NormalizedDataElement.list()[0].lastValueChanged.equals(date)
	}
	
	def "test refresh normalized elements updates value timestamps"() {
		when:
		def period = newPeriod()
		setupLocationTree()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def normalizedDataElementValue = newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, Value.NULL_INSTANCE())
		def timestamp = normalizedDataElementValue.timestamp
		
		then:
		NormalizedDataElementValue.count() == 1
		
		when:
		Thread.sleep(1100)
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement);

		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].timestamp.seconds != timestamp.seconds	
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
		def ratio = newSum("1", CODE(2))
		
		then:
		SumPartialValue.count() == 0
		ratio.refreshed == null
		
		when:
		refreshValueService.refreshCalculation(ratio);

		then:
		SumPartialValue.count() == 8
		SumPartialValue.list()[0].timestamp != null
		ratio.refreshed != null
	}
	
	def "test refresh calculations refreshes dependencies first - with data element"() {
		when:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newRawDataElement(CODE(1), Type.TYPE_NUMBER())
		def ratio = newSum("\$"+dataElement.id, CODE(2))
		
		then:
		SumPartialValue.count() == 0
		RawDataElementValue.count() == 0
		ratio.refreshed == null
		
		when:
		refreshValueService.refreshCalculation(ratio);

		then:
		RawDataElementValue.count() == 0
		SumPartialValue.count() == 8
		SumPartialValue.list()[0].timestamp != null
		ratio.refreshed != null
	}
	
	def "test refresh calculations refreshes dependencies first - with normalized data element"() {
			when:
		setupLocationTree()
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"1"]]))
		def ratio = newSum("\$"+dataElement.id, CODE(2))
		
		then:
		SumPartialValue.count() == 0
		NormalizedDataElementValue.count() == 0
		dataElement.refreshed == null
		ratio.refreshed == null
		
		when:
		refreshValueService.refreshCalculation(ratio);

		then:
		NormalizedDataElementValue.count() == 2
		SumPartialValue.count() == 8
		SumPartialValue.list()[0].timestamp != null
		dataElement.refreshed != null
		ratio.refreshed != null
	}
	
	def "test refresh calculation updates when last value changed is set after refresh"() {
		setup:
		def date = new Date()
		setupLocationTree()
		def period = newPeriod()
		def sum = newSum("1", CODE(2))
		
		when:
		sum.refreshed = date
		sum.lastValueChanged = new Date();
		refreshValueService.refreshCalculation(sum);
		
		then:
		SumPartialValue.count() == 8
		Sum.list()[0].lastValueChanged.after(date)
	}
	
	def "test refresh calculations updates timestamps"() {
		when:
		def refreshed = new Date()
		setupLocationTree()
		def period = newPeriod()
		def ratio = newSum("1", CODE(2))
		ratio.refreshed = refreshed
		ratio.save(failOnError: true)
		def partialValue = newSumPartialValue(ratio, period, Location.findByCode(BURERA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), 1, v("1"))
		def timestamp = partialValue.timestamp
		
		then:
		SumPartialValue.count() == 1
		
		when:
		refreshValueService.refreshCalculation(ratio);

		then:
		SumPartialValue.count() == 8
		!SumPartialValue.list()[0].timestamp.equals(timestamp)	
		!ratio.refreshed.equals(refreshed)
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
//		normalizedDataElement1.save(failOnError: true)
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement2);
		
		then:
		NormalizedDataElementValue.count() == 4
		NormalizedDataElementValue.list()[0].value.isNull()
		NormalizedDataElementValue.list()[1].value.isNull()
		NormalizedDataElementValue.list()[2].value.isNull()
		NormalizedDataElementValue.list()[3].value.isNull()
		
	}
	
	def "test refresh with circular dependencies - with location and period"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement1 = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([(period.id+''):[:]]))
		def normalizedDataElement2 = newNormalizedDataElement(CODE(2), Type.TYPE_NUMBER(), e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement1.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement1.id]]))
		normalizedDataElement1.expressionMap = e([(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"\$"+normalizedDataElement2.id, (HEALTH_CENTER_GROUP):"\$"+normalizedDataElement2.id]])
//		normalizedDataElement1.save(failOnError: true)
		
		when:
		refreshValueService.refreshNormalizedDataElement(normalizedDataElement2, DataLocation.findByCode(KIVUYE), period);
		
		then:
		NormalizedDataElementValue.count() == 2
		NormalizedDataElementValue.list()[0].value.isNull()
		NormalizedDataElementValue.list()[1].value.isNull()
		
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
