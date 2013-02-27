package org.chai.kevin.dashboard

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

import java.util.List

import org.chai.kevin.Period;
import org.chai.kevin.data.RawDataElement;
import org.chai.location.CalculationLocation;
import org.chai.location.DataLocation;
import org.chai.location.DataLocationType;
import org.chai.location.Location;
import org.chai.location.LocationLevel;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.RawDataElementValue;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.data.Type;

class DashboardServiceSpec extends DashboardIntegrationTests {

	def dashboardService	
	
	def "get dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportProgram(CODE(1))
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def dashboard = null
		refresh()
		def types
		
		when:
		types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(RWANDA), root, dashboardRoot, period, types, false)
		
		then:
		dashboard.getLocations(Location.findByCode(RWANDA), [], types).equals([Location.findByCode(NORTH)])
		dashboard.getIndicators(dashboardRoot).equals([target])
		
		when:
		types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(BURERA), root, dashboardRoot, period, types, false)
		
		then:
		dashboard.getLocations(Location.findByCode(BURERA), [], types).equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		dashboard.getIndicators(dashboardRoot).equals([target])
		
		when:
		types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)])
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(BURERA), root, dashboardRoot, period, types, false)
		
		then:
		dashboard.getLocations(Location.findByCode(BURERA), [], types).equals([DataLocation.findByCode(BUTARO)])
		dashboard.getIndicators(dashboardRoot).equals([target])
	}
	
	def "get dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboardProgram = DashboardProgram.findByCode(currentProgramName)
		def types = new HashSet(typeCodes.collect {DataLocationType.findByCode(it)})
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(currentLocationName), dashboardProgram.program, dashboardProgram, period, types, false);
		def value = dashboard.getPercentage(getCalculationLocation(locationName), DashboardProgram.findByCode(currentProgramName))

		then:
		Utils.formatNumber("#.0", value.numberValue) == Utils.formatNumber("#.0", expectedValue)

		where:
		currentLocationName	| currentProgramName	| locationName			| typeCodes										| expectedValue
		BURERA				| DASHBOARD_PROGRAM1	| BUTARO				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|30.0d
		BURERA				| DASHBOARD_PROGRAM1	| KIVUYE				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA				| DASHBOARD_PROGRAM1	| BUTARO				| [DISTRICT_HOSPITAL_GROUP]						|30.0d
		BURERA				| DASHBOARD_PROGRAM1	| KIVUYE				| [HEALTH_CENTER_GROUP]							|40.0d
		RWANDA				| DASHBOARD_ROOT		| NORTH					| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|15.0d
		RWANDA				| DASHBOARD_ROOT		| NORTH					| [DISTRICT_HOSPITAL_GROUP]						|50/3
		RWANDA				| DASHBOARD_ROOT		| NORTH					| [HEALTH_CENTER_GROUP]							|50/3
	}

	def "get dashboard with no partial values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()

		when:
		def program = ReportProgram.findByCode(currentProgramName)
		def dashboardProgram = getDashboardEntity(programName)
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(currentLocationName), program, dashboardProgram, period, new HashSet(typeCodes.collect {DataLocationType.findByCode(it)}), false);
		def value = dashboard.getPercentage(getCalculationLocation(currentLocationName), getDashboardEntity(programName))

		then:
		value.isNull()

		where:
		currentLocationName	| currentProgramName	| programName		 	| typeCodes										    
		BURERA				| PROGRAM1				| TARGET1				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET2				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET1				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET2				| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		RWANDA				| ROOT					| DASHBOARD_PROGRAM1	| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET1				| [DISTRICT_HOSPITAL_GROUP]						
		RWANDA				| ROOT					| DASHBOARD_PROGRAM1	| [DISTRICT_HOSPITAL_GROUP]						
	}
	
	def "get dashboard entities"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboardProgram = DashboardProgram.findByCode(programCode)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(locationName), dashboardProgram.program, dashboardProgram, period, types, false);

		then:
		dashboard.getIndicators(DashboardProgram.findByCode(programCode)).containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.getLocations(Location.findByCode(locationName), [], types).containsAll expectedLocations.collect {getCalculationLocation(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {Location.findByCode(it)}

		where:
		locationName	| programCode		| expectedLocations	| expectedEntities  						| expectedLocationPath	| expectedProgramPath
		RWANDA			| DASHBOARD_ROOT	| [NORTH]			| [DASHBOARD_PROGRAM1, DASHBOARD_PROGRAM2]	| []					| []
		NORTH			| DASHBOARD_ROOT	| [BURERA]			| [DASHBOARD_PROGRAM1, DASHBOARD_PROGRAM2]	| [RWANDA]				| []
		BURERA			| DASHBOARD_PROGRAM1| [BUTARO, KIVUYE]	| [TARGET1, TARGET2]						| [RWANDA, NORTH]		| [ROOT]
		BURERA			| DASHBOARD_ROOT	| [BUTARO, KIVUYE]	| [DASHBOARD_PROGRAM1, DASHBOARD_PROGRAM2]	| [RWANDA, NORTH]		| []
	}
	
	def "get program dashboard with sorted dashboard program entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dashboardEntity = dashboardService.getDashboardProgram(ReportProgram.findByCode(ROOT))
		refresh()

		when:
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(RWANDA), dashboardEntity.program, dashboardEntity, period, types, false);

		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[0].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[1].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM2))
		
		when:
		DashboardProgram.findByCode(DASHBOARD_PROGRAM1).order = 2
		DashboardProgram.findByCode(DASHBOARD_PROGRAM2).order = 1		
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(RWANDA), dashboardEntity.program, dashboardEntity, period, types, false);
		
		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[0].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM2))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[1].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))
		
	}
	
	def "get program dashboard with sorted dashboard target entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dashboardEntity = dashboardService.getDashboardProgram(ReportProgram.findByCode(ROOT))
		refresh()
		
		when:
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(BURERA), dashboardEntity.program, dashboardEntity, period, types, false);

		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))[0].equals(DashboardTarget.findByCode(TARGET1))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))[1].equals(DashboardTarget.findByCode(TARGET2))
		
		when:
		DashboardTarget.findByCode(TARGET1).order = 2
		DashboardTarget.findByCode(TARGET2).order = 1
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(BURERA), dashboardEntity.program, dashboardEntity, period, types, false);
		
		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))[0].equals(DashboardTarget.findByCode(TARGET2))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))[1].equals(DashboardTarget.findByCode(TARGET1))
		
	}
	
	def "get program dashboard with sorted dashboard program and target entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		def dataElement4 = newNormalizedDataElement(CODE(10), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def ratio4 = newSum("\$"+dataElement4.id, CODE(11))
		def target4 = newDashboardTarget("Target 5", ratio4, ReportProgram.findByCode(ROOT), 1, 4)
		def dataElement5 = newNormalizedDataElement(CODE(12), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def ratio5 = newSum("\$"+dataElement5.id, CODE(13))
		def target5 = newDashboardTarget("Target 6", ratio5, ReportProgram.findByCode(ROOT), 1, 5)
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		def dashboardEntity = dashboardService.getDashboardProgram(ReportProgram.findByCode(ROOT))
		refresh()

		when:
		def dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(RWANDA), dashboardEntity.program, dashboardEntity, period, types, false);

		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[0].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[1].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM2))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[2].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM3))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[3].equals(DashboardTarget.findByCode("Target 5"))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[4].equals(DashboardTarget.findByCode("Target 6"))
		
		when:
		DashboardProgram.findByCode(DASHBOARD_PROGRAM1).order = 2
		DashboardProgram.findByCode(DASHBOARD_PROGRAM2).order = 5
		DashboardProgram.findByCode(DASHBOARD_PROGRAM3).order = 4
		DashboardTarget.findByCode("Target 5").order = 3
		DashboardTarget.findByCode("Target 6").order = 1
		dashboard = dashboardService.getDashboard(CalculationLocation.findByCode(RWANDA), dashboardEntity.program, dashboardEntity, period, types, false);
		
		then:
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[0].equals(DashboardTarget.findByCode("Target 6"))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[1].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM1))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[2].equals(DashboardTarget.findByCode("Target 5"))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[3].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM3))
		dashboard.getIndicators(DashboardProgram.findByCode(DASHBOARD_ROOT))[4].equals(DashboardProgram.findByCode(DASHBOARD_PROGRAM2))
		
	}
	
	// TODO this one does not work because percentage service never returns NULL
	// def "get program dashboard with no dashboard entities"(){
	// 	setup:
	// 	setupLocationTree()
	// 	refresh()
		
	// 	when:
	// 	def burera = Location.findByCode(BURERA)
	// 	def root = newReportProgram(ROOT)
	// 	def dashboardRoot = newDashboardProgram(DASHBOARD_ROOT, root, 0)
	// 	def period = newPeriod()
	// 	def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		
	// 	then:
	// 	def dashboard = dashboardService.getDashboard(burera, root, period, types, false)
	// 	dashboard.hasData() == false
	// }
	
	// TODO this one does not work because percentage service never returns NULL
	// def "get location dashboard with no locations"(){
	// 	setup:
	// 	def period = newPeriod()
	// 	setupProgramTree()
	// 	setupDashboardTree()
	// 	refresh()
		
	// 	when:
	// 	def country = newLocationLevel(NATIONAL, 1)
	// 	def rwanda = newLocation(["en":RWANDA], RWANDA, country)		
	// 	def root = ReportProgram.findByCode(ROOT)	
	// 	def types = new HashSet([])
		
	// 	then:
	// 	def dashboard = dashboardService.getDashboard(rwanda, root, period, types, false)
	// 	dashboard.hasData() == false
	// }
	
	def "get dashboard skip levels"(){
		setup:
		setupLocationTree()
		
		when:
		Set<LocationLevel> dashboardSkipLevels = dashboardService.getSkipLocationLevels()
		
		then:
		dashboardSkipLevels.equals( s([LocationLevel.findByCode(SECTOR)]) )
	}
	
	def getDashboardEntity(String code) {
		def entity = DashboardProgram.findByCode(code);
		if(entity == null) entity = DashboardTarget.findByCode(code);
		return entity
	}

}