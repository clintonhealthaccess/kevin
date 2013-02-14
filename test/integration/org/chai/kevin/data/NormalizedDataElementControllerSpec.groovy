/**
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
package org.chai.kevin.data;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.data.NormalizedDataElement;
import org.chai.kevin.data.NormalizedDataElementController;
import org.chai.kevin.data.Type;
import org.chai.kevin.dsr.DsrIntegrationTests;
import org.chai.location.DataLocation;
import org.chai.location.Location;
import org.chai.kevin.planning.PlanningCost.PlanningCostType;
import org.chai.kevin.planning.PlanningIntegrationTests;
import org.chai.kevin.value.NormalizedDataElementValue;
import org.chai.kevin.value.Status;

class NormalizedDataElementControllerSpec extends IntegrationTests {

	def locationService
	def normalizedDataElementController

	def "deleting normalized data element deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 0
		NormalizedDataElementValue.count() == 0
	}
	
	def "saving normalized data element sets timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		normalizedDataElementController = new NormalizedDataElementController()
		def time1 = normalizedDataElement.timestamp
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.params['typeBuilderString'] = "type { number }"
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		!NormalizedDataElement.list()[0].timestamp.equals(time1)
	}
	
	def "saving normalized data element sets last value changed"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		normalizedDataElementController = new NormalizedDataElementController()
		def time1 = normalizedDataElement.lastValueChanged
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.params['typeBuilderString'] = "type { number }"
		Thread.sleep(1100)
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].lastValueChanged.after(time1)
		
	}
	
	def "saving normalized data element does not delete values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.params['typeBuilderString'] = "type { number }"
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElementValue.count() == 1
	}
	
	def "cannot delete normalized data element if there are associated calculations"() {
		setup:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		def calculation = newSum("\$"+normalizedDataElement.id, CODE(2))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
		Summ.count() == 1
	}
	
	def "cannot delete normalized data element if there are associated targets"() {
		setup:
		def program = newReportProgram(CODE(1))
		def targetCategory = DsrIntegrationTests.newDsrTargetCategory(CODE(2), program, 1)
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		def target = DsrIntegrationTests.newDsrTarget(CODE(3), normalizedDataElement, targetCategory)
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
	}
	
	def "cannot delete normalized data element if there are associated planning costs"() {
		setup:
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), [:])
		def dataElement = newRawDataElement(CODE(2),
			Type.TYPE_LIST(Type.TYPE_MAP(["key0":Type.TYPE_ENUM(CODE(1)), "key1":Type.TYPE_NUMBER()])))
		def planning = PlanningIntegrationTests.newPlanning(period, [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP])
		def formElement = newFormElement(dataElement)
		def planningType = PlanningIntegrationTests.newPlanningType(formElement, "[_].key1", planning)
		def planningCost = PlanningIntegrationTests.newPlanningCost(PlanningCostType.OUTGOING, normalizedDataElement, planningType)
		
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
	}
	
	def "search normalized data element"() {
		setup:
		def normalizedDataElement = newNormalizedDataElement(["en":"data element"], CODE(1), Type.TYPE_NUMBER(), [:])
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.q = "element"
		normalizedDataElementController.search()
		
		then:
		normalizedDataElementController.modelAndView.model.entities.size() == 1
		normalizedDataElementController.modelAndView.model.entities[0].equals(normalizedDataElement)
		normalizedDataElementController.modelAndView.model.entityCount == 1
	}
	
	def "create normalized element with expressions"() {
		setup:
		def period1 = newPeriod()
		def period2 = newPeriod(2006)
		def type1 = newDataLocationType([:], "type1")
		normalizedDataElementController = new NormalizedDataElementController()

		when:
		normalizedDataElementController.params['typeBuilderString'] = "type { number }"
		normalizedDataElementController.params.code = "code"
		normalizedDataElementController.params['expressionMap['+period1.id+']['+type1.code+']'] = '123'
		normalizedDataElementController.params['expressionMap['+period2.id+']['+type1.code+']'] = '456'
		normalizedDataElementController.saveWithoutTokenCheck()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].expressionMap.equals( [(period1.id+''):[(type1.code):'123'], (period2.id+''):[(type1.code):'456']] )
	}
	
	def "can change data element type if it has no values" () {
		setup:
		setupLocationTree()
		normalizedDataElementController = new NormalizedDataElementController()
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(["en":"data element"], CODE(1), Type.TYPE_NUMBER(), [:])

		when:
		normalizedDataElementController.params.id = dataElement.id
		normalizedDataElementController.params.code = dataElement.code
		normalizedDataElementController.params['typeBuilderString'] = 'type { bool }'
		normalizedDataElementController.saveWithoutTokenCheck()

		then:
		normalizedDataElementController.response.redirectedUrl.equals(normalizedDataElementController.getTargetURI())
		dataElement.type.equals(Type.TYPE_BOOL())
	}
		
	def "cannot change data element type if it has values" () {
		setup:
		setupLocationTree()
		normalizedDataElementController = new NormalizedDataElementController()
		def period = newPeriod()
		def dataElement = newNormalizedDataElement(["en":"data element"], CODE(1), Type.TYPE_NUMBER(), [:])

		when:
		newNormalizedDataElementValue(dataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		normalizedDataElementController.params.id = dataElement.id
		normalizedDataElementController.params.code = dataElement.code
		normalizedDataElementController.params['typeBuilderString'] = 'type { string }'
		normalizedDataElementController.saveWithoutTokenCheck()

		then:
		(Type.TYPE_NUMBER()).equals(dataElement.type)
	}
	
}
