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
	
	def "get location dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportProgram(CODE(1))
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		def calculation = newSum("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def dashboard = null
		refresh()
		
		when:
		dashboard = dashboardService.getLocationDashboard(Location.findByCode(RWANDA), root, period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]), false)
		
		then:
		dashboard.locations.equals([Location.findByCode(NORTH)])
		
		when:
		dashboard = dashboardService.getLocationDashboard(Location.findByCode(BURERA), root, period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]), false)
		
		then:
		(dashboard.locations).equals([DataLocation.findByCode(BUTARO), DataLocation.findByCode(KIVUYE)])
		
		when:
		dashboard = dashboardService.getLocationDashboard(Location.findByCode(BURERA), root, period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP)]), false)
		
		then:
		dashboard.locations.equals([DataLocation.findByCode(BUTARO)])
		
	}
	
	def "get location dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getLocationDashboard(Location.findByCode(currentLocationName), ReportProgram.findByCode(currentProgramName), period, new HashSet(types.collect {DataLocationType.findByCode(it)}), false);
		def value = dashboard.getPercentage(getCalculationLocation(locationName), getDashboardEntity(currentProgramName))

		then:
		Utils.formatNumber("#.0", value.numberValue) == Utils.formatNumber("#.0", expectedValue)

		where:
		currentLocationName	| currentProgramName	| locationName	| types										    | expectedValue
		BURERA				| PROGRAM1				| BUTARO		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|30.0d
		BURERA				| PROGRAM1				| KIVUYE		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA				| PROGRAM1				| BUTARO		| [DISTRICT_HOSPITAL_GROUP]						|30.0d
		BURERA				| PROGRAM1				| KIVUYE		| [HEALTH_CENTER_GROUP]							|40.0d
		RWANDA				| ROOT					| NORTH			| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|15.0d
		RWANDA				| ROOT					| NORTH			| [DISTRICT_HOSPITAL_GROUP]						|50/3
		RWANDA				| ROOT					| NORTH			| [HEALTH_CENTER_GROUP]							|50/3
	}

	def "get program dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(currentLocationName), ReportProgram.findByCode(currentProgramName), period, new HashSet(types.collect {DataLocationType.findByCode(it)}));
		def value = dashboard.getPercentage(getCalculationLocation(currentLocationName), getDashboardEntity(programName))

		then:
		value.numberValue == expectedValue

		where:
		currentLocationName	| currentProgramName	| programName 	| types										    | expectedValue
		BURERA				| PROGRAM1				| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA				| PROGRAM1				| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|20.0d
		BURERA				| PROGRAM1				| TARGET1		| [DISTRICT_HOSPITAL_GROUP]						|40.0d
		BURERA				| PROGRAM1				| TARGET1		| [HEALTH_CENTER_GROUP]							|40.0d
		BURERA				| PROGRAM1				| TARGET2		| [DISTRICT_HOSPITAL_GROUP]						|20.0d
		BURERA				| PROGRAM1				| TARGET2		| [HEALTH_CENTER_GROUP]							|null
		BURERA				| ROOT					| PROGRAM1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|30.0d
		BURERA				| ROOT					| PROGRAM2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|10.0d
		BURERA				| PROGRAM3				| TARGET4		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|5.0d
		BURERA				| PROGRAM3				| TARGET4		| [DISTRICT_HOSPITAL_GROUP]						|10.0d
		BURERA				| PROGRAM3				| TARGET4		| [HEALTH_CENTER_GROUP]							|0.0d
		BURERA				| ROOT					| PROGRAM3		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|5.0d
	}
	
	def "get program dashboard with no partial values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(currentLocationName), ReportProgram.findByCode(currentProgramName), period, new HashSet(types.collect {DataLocationType.findByCode(it)}));
		def value = dashboard.getPercentage(getCalculationLocation(currentLocationName), getDashboardEntity(programName))

		then:
		value.isNull()

		where:
		currentLocationName	| currentProgramName	| programName 	| types										    
		BURERA				| PROGRAM1				| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		RWANDA				| ROOT					| PROGRAM1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]
		BURERA				| PROGRAM1				| TARGET1		| [DISTRICT_HOSPITAL_GROUP]						
		RWANDA				| ROOT					| PROGRAM1		| [DISTRICT_HOSPITAL_GROUP]						
	}
	
	def "get program dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getCalculationLocation(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {Location.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations	| expectedEntities  	| expectedLocationPath	| expectedProgramPath
		RWANDA			| ROOT			| [RWANDA]			| [PROGRAM1, PROGRAM2]	| []					| []
		BURERA			| PROGRAM1		| [BURERA]			| [TARGET1, TARGET2]	| [RWANDA, NORTH]		| [ROOT]
		BURERA			| ROOT			| [BURERA]			| [PROGRAM1, PROGRAM2]	| [RWANDA, NORTH]		| []
	}
	
	def "get program (compare) dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getCalculationLocation(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {Location.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations	| expectedEntities  	| expectedLocationPath	| expectedProgramPath
		RWANDA			| ROOT			| [RWANDA]			| [PROGRAM1, PROGRAM2]	| []					| []
		BURERA			| PROGRAM1		| [BURERA]			| [TARGET1, TARGET2]	| [RWANDA, NORTH]		| [ROOT]
		BURERA			| ROOT			| [BURERA]			| [PROGRAM1, PROGRAM2]	| [RWANDA, NORTH]		| []		
	}
	
	def "get program dashboard with sorted dashboard program entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(RWANDA), ReportProgram.findByCode(ROOT), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities[0].equals(DashboardProgram.findByCode(PROGRAM1))
		dashboard.dashboardEntities[1].equals(DashboardProgram.findByCode(PROGRAM2))
		
		when:
		DashboardProgram.findByCode(PROGRAM1).order = 2
		DashboardProgram.findByCode(PROGRAM2).order = 1		
		dashboard = dashboardService.getProgramDashboard(Location.findByCode(RWANDA), ReportProgram.findByCode(ROOT), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));
		
		then:
		dashboard.dashboardEntities[0].equals(DashboardProgram.findByCode(PROGRAM2))
		dashboard.dashboardEntities[1].equals(DashboardProgram.findByCode(PROGRAM1))
		
	}
	
	def "get program dashboard with sorted dashboard target entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()
		
		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(BURERA), ReportProgram.findByCode(PROGRAM1), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities[0].equals(DashboardTarget.findByCode(TARGET1))
		dashboard.dashboardEntities[1].equals(DashboardTarget.findByCode(TARGET2))
		
		when:
		DashboardTarget.findByCode(TARGET1).order = 2
		DashboardTarget.findByCode(TARGET2).order = 1
		dashboard = dashboardService.getProgramDashboard(Location.findByCode(BURERA), ReportProgram.findByCode(PROGRAM1), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));
		
		then:
		dashboard.dashboardEntities[0].equals(DashboardTarget.findByCode(TARGET2))
		dashboard.dashboardEntities[1].equals(DashboardTarget.findByCode(TARGET1))
		
	}
	
	def "get program dashboard with sorted dashboard program and target entities"(){
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		def dataElement4 = newNormalizedDataElement(CODE(10), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def ratio4 = newSum("\$"+dataElement4.id, CODE(11))
		def target4 = newDashboardTarget("Target 5", ratio4, ReportProgram.findByCode(ROOT), 1)
		def dataElement5 = newNormalizedDataElement(CODE(12), Type.TYPE_NUMBER(), [(period.id+''):[(DISTRICT_HOSPITAL_GROUP):"10",(HEALTH_CENTER_GROUP):"10"]])
		def ratio5 = newSum("\$"+dataElement5.id, CODE(13))
		def target5 = newDashboardTarget("Target 6", ratio5, ReportProgram.findByCode(ROOT), 1)
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(Location.findByCode(RWANDA), ReportProgram.findByCode(ROOT), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities[0].equals(DashboardProgram.findByCode(PROGRAM1))
		dashboard.dashboardEntities[1].equals(DashboardProgram.findByCode(PROGRAM2))
		dashboard.dashboardEntities[2].equals(DashboardProgram.findByCode(PROGRAM3))
		dashboard.dashboardEntities[3].equals(DashboardTarget.findByCode("Target 5"))
		dashboard.dashboardEntities[4].equals(DashboardTarget.findByCode("Target 6"))
		
		when:
		DashboardProgram.findByCode(PROGRAM1).order = 2
		DashboardProgram.findByCode(PROGRAM2).order = 1
		DashboardProgram.findByCode(PROGRAM3).order = 3
		DashboardTarget.findByCode("Target 5").order = 2
		DashboardTarget.findByCode("Target 6").order = 1
		dashboard = dashboardService.getProgramDashboard(Location.findByCode(RWANDA), ReportProgram.findByCode(ROOT), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]));
		
		then:
		dashboard.dashboardEntities[0].equals(DashboardProgram.findByCode(PROGRAM2))
		dashboard.dashboardEntities[1].equals(DashboardProgram.findByCode(PROGRAM1))
		dashboard.dashboardEntities[2].equals(DashboardProgram.findByCode(PROGRAM3))
		dashboard.dashboardEntities[3].equals(DashboardTarget.findByCode("Target 6"))
		dashboard.dashboardEntities[4].equals(DashboardTarget.findByCode("Target 5"))
		
	}
	
	def "get program dashboard with no dashboard entities"(){
		setup:
		setupLocationTree()
		refresh()
		
		when:
		def burera = Location.findByCode(BURERA)
		def root = newReportProgram(ROOT)
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		def period = Period.list([cache: true])[0]				
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		
		then:
		def dashboard = dashboardService.getProgramDashboard(burera, root, period, types)
		dashboard.locations.isEmpty() == false
		dashboard.dashboardEntities.isEmpty() == true
		dashboard.hasData() == false
	}
	
	def "get dashboard location dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = 
		dashboardService.getLocationDashboard(Location.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]), false);

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getCalculationLocation(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {Location.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations		| expectedEntities  | expectedLocationPath	| expectedProgramPath
		BURERA			| PROGRAM1		| [BUTARO, KIVUYE]		| [PROGRAM1]		| [RWANDA, NORTH]		| [ROOT]
		BURERA			| ROOT			| [BUTARO, KIVUYE]		| [ROOT]			| [RWANDA, NORTH]		| []
		RWANDA			| ROOT			| [NORTH]				| [ROOT]			| []					| []
	}
	
	def "get location compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard =
		dashboardService.getLocationDashboard(Location.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)]), true);

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getCalculationLocation(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {Location.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations		| expectedEntities  | expectedLocationPath	| expectedProgramPath
		BURERA			| PROGRAM1		| [BURERA]				| [PROGRAM1]		| [RWANDA, NORTH]		| [ROOT]
		NORTH			| ROOT			| [NORTH]				| [ROOT]			| [RWANDA]				| []
	}
	
	def "get location dashboard with no locations"(){
		setup:
		def period = newPeriod()
		setupProgramTree()
		setupDashboardTree()
		refresh()
		
		when:
		def country = newLocationLevel(NATIONAL, 1)
		def rwanda = newLocation(["en":RWANDA], RWANDA, country)		
		def root = ReportProgram.findByCode(ROOT)	
		def types = new HashSet([DataLocationType.findByCode(DISTRICT_HOSPITAL_GROUP), DataLocationType.findByCode(HEALTH_CENTER_GROUP)])
		
		then:
		def dashboard = dashboardService.getLocationDashboard(rwanda, root, period, types, false)
		dashboard.locations.isEmpty() == true
		dashboard.dashboardEntities.isEmpty() == false
		dashboard.hasData() == false
	}
	
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

	def getReportPrograms(List<String> codes) {
		def reportPrograms = []
		for (String code : codes) {
			def entity = DashboardProgram.findByCode(code);
			if(entity == null) DashboardTarget.findByCode(code);
			if(entity != null && entity.getProgram() != null) 
				reportPrograms.add(entity.getProgram());
		}
		return reportPrograms;
	}
}