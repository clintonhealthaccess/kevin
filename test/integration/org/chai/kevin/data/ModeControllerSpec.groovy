package org.chai.kevin.data

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.value.ModePartialValue;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;

class ModeControllerSpec extends IntegrationTests {

	def modeController
	
	def "save works"() {
		setup:
		modeController = new ModeController()
		
		when:
		modeController.params.code = CODE(1)
		modeController.params.expression = "1"
		modeController.params.type = Type.TYPE_NUMBER()
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 1
		Mode.list()[0].code == CODE(1)
		Mode.list()[0].expression == "1"
		
	}

	def "save validates"() {
		setup:
		modeController = new ModeController()
		
		when: //code = null
		modeController.params.code = null
		modeController.params.expression = "1"
		modeController.params.type = Type.TYPE_NUMBER()
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
		
		when: //expression = null
		modeController.params.code = CODE(1)
		modeController.params.expression = null
		modeController.params.type = Type.TYPE_NUMBER()
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
		
		when: //type = null
		modeController.params.code = CODE(1)
		modeController.params.expression = "1"
		modeController.params.type = null
		modeController.saveWithoutTokenCheck()
		
		then:
		Mode.count() == 0
	}
	
	def "delete mode deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		newModePartialValue(mode, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1")) 
		modeController = new ModeController()
		
		when:
		modeController.params.id = mode.id
		modeController.delete()
		
		then:
		Mode.count() == 0
		ModePartialValue.count() == 0 
	}

	def "save mode deletes values"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		newModePartialValue(mode, period, Location.findByCode(RWANDA), DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), v("1"))
		modeController = new ModeController()
		
		when:
		modeController.params.id = mode.id
		modeController.save()
		
		then:
		Mode.count() == 1
		ModePartialValue.count() == 0
	}	
	
	def "save mode updates timestamp"() {
		setup:
		setupLocationTree()
		def period = newPeriod()
		def mode = newMode("1", CODE(1), Type.TYPE_NUMBER())
		modeController = new ModeController()
		def time1 = mode.timestamp
		
		when:
		modeController.params.id = mode.id
		modeController.save()
		
		then:
		Mode.count() == 1
		!Mode.list()[0].timestamp.equals(time1)
	}	
}
