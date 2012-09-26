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
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
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
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
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
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		def time1 = normalizedDataElement.timestamp
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		!NormalizedDataElement.list()[0].timestamp.equals(time1)
	}
	
	def "saving normalized data element sets last value changed"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		normalizedDataElementController = new NormalizedDataElementController()
		def time1 = normalizedDataElement.lastValueChanged
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		Thread.sleep(1100)
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].lastValueChanged.after(time1)
		
	}
	
	def "saving normalized data element deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		newNormalizedDataElementValue(normalizedDataElement, DataLocation.findByCode(BUTARO), period, Status.VALID, v("1"))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.save()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElementValue.count() == 0
	}
	
	def "cannot delete normalized data element if there are associated calculations"() {
		setup:
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
		def calculation = newSum("\$"+normalizedDataElement.id, CODE(2))
		normalizedDataElementController = new NormalizedDataElementController()
		
		when:
		normalizedDataElementController.params.id = normalizedDataElement.id
		normalizedDataElementController.delete()
		
		then:
		NormalizedDataElement.count() == 1
		Sum.count() == 1
	}
	
	def "cannot delete normalized data element if there are associated planning costs"() {
		setup:
		def period = newPeriod()
		def normalizedDataElement = newNormalizedDataElement(CODE(1), Type.TYPE_NUMBER(), e([:]))
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
		def normalizedDataElement = newNormalizedDataElement(j(["en":"data element"]), CODE(1), Type.TYPE_NUMBER(), e([:]))
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
		def period2 = newPeriod()
		def type1 = newDataLocationType("type1")
		normalizedDataElementController = new NormalizedDataElementController()

		when:
		normalizedDataElementController.params.type = "{\"type\":\"number\"}"
		normalizedDataElementController.params.code = "code"
		normalizedDataElementController.params['expressionMap['+period1.id+']['+type1.code+']'] = '123'
		normalizedDataElementController.params['expressionMap['+period2.id+']['+type1.code+']'] = '456'
		normalizedDataElementController.saveWithoutTokenCheck()
		
		then:
		NormalizedDataElement.count() == 1
		NormalizedDataElement.list()[0].expressionMap.equals( [(period1.id+''):[(type1.code):'123'], (period2.id+''):[(type1.code):'456']] )
	}
	
}
