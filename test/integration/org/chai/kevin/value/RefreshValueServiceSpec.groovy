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
import org.hisp.dhis.period.Period;

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
	
	
}
