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

import org.chai.kevin.data.RawDataElement;
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType;
import org.chai.kevin.location.LocationEntity;
import org.chai.kevin.location.LocationLevel;
import org.chai.kevin.value.RawDataElementValue;
import org.hisp.dhis.period.Period;
import org.chai.kevin.reports.ReportProgram;

class DashboardServiceSpec extends DashboardIntegrationTests {

	def dashboardService	
	
	def "dashboard service works"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		def root = newReportProgram(CODE(1))
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		def calculation = newAverage("1", CODE(2))
		def target = newDashboardTarget(TARGET1, calculation, root, 1)
		def dashboard = null
		refresh()
		
		when:
		dashboard = dashboardService.getLocationDashboard(LocationEntity.findByCode(RWANDA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]), false)
		
		then:
		dashboard.locations.equals([LocationEntity.findByCode(NORTH)])
		
		when:
		dashboard = dashboardService.getLocationDashboard(LocationEntity.findByCode(BURERA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]), false)
		
		then:
		(dashboard.locations).equals([DataLocationEntity.findByCode(BUTARO), DataLocationEntity.findByCode(KIVUYE)])
		
		when:
		dashboard = dashboardService.getLocationDashboard(LocationEntity.findByCode(BURERA), root, period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP)]), false)
		
		then:
		dashboard.locations.equals([DataLocationEntity.findByCode(BUTARO)])
		
	}
	
	def "test dashboard with correct values"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getLocationDashboard(LocationEntity.findByCode(currentLocationName), ReportProgram.findByCode(currentProgramName), period, new HashSet(types.collect {DataEntityType.findByCode(it)}), false);
		def percentage = dashboard.getPercentage(getCalculationEntity(locationName), getDashboardEntity(programName))

		then:
		if (percentage == null) value == null
		else percentage.value == value

		where:
		currentLocationName	| currentProgramName	| locationName	| programName 	| types										    | value
		BURERA				| PROGRAM1				| BUTARO		| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA				| PROGRAM1				| BUTARO		| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|20.0d
		BURERA				| PROGRAM1				| KIVUYE		| TARGET1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|40.0d
		BURERA				| PROGRAM1				| KIVUYE		| TARGET2		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|null
		RWANDA				| ROOT					| NORTH			| PROGRAM1		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]|30.0d
		BURERA				| PROGRAM1				| BUTARO		| TARGET1		| [DISTRICT_HOSPITAL_GROUP]						|20.0d
		RWANDA				| ROOT					| NORTH			| PROGRAM1		| [DISTRICT_HOSPITAL_GROUP]						|20.0d
	}

	def "dashboard test program (compare) dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = dashboardService.getProgramDashboard(LocationEntity.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]));

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getLocationEntity(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {LocationEntity.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations	| expectedEntities  	| expectedLocationPath	| expectedProgramPath
		RWANDA			| ROOT			| [RWANDA]			| [PROGRAM1, PROGRAM2]	| []					| []
		BURERA			| PROGRAM1		| [BURERA]			| [TARGET1, TARGET2]	| [RWANDA, NORTH]		| [ROOT]
		BURERA			| ROOT			| [BURERA]			| [PROGRAM1, PROGRAM2]	| [RWANDA, NORTH]		| []		
	}
	
	def "get program dashboard with no dashboard entities"(){
		setup:
		setupLocationTree()
		refresh()
		
		when:
		def burera = LocationEntity.findByCode(BURERA)
		def root = newReportProgram(ROOT)
		def dashboardRoot = newDashboardProgram(ROOT, root, 0)
		def period = Period.list()[0]				
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		
		then:
		def dashboard = dashboardService.getProgramDashboard(burera, root, period, types)
		dashboard.locations.isEmpty() == false
		dashboard.dashboardEntities.isEmpty() == true
		dashboard.hasData() == false
	}
	
	def "dashboard test location dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard = 
		dashboardService.getLocationDashboard(LocationEntity.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]), false);

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getLocationEntity(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {LocationEntity.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations		| expectedEntities  | expectedLocationPath	| expectedProgramPath
		BURERA			| PROGRAM1		| [BUTARO, KIVUYE]		| [PROGRAM1]		| [RWANDA, NORTH]		| [ROOT]
		BURERA			| ROOT			| [BUTARO, KIVUYE]		| [ROOT]			| [RWANDA, NORTH]		| []
		RWANDA			| ROOT			| [NORTH]				| [ROOT]			| []					| []
	}
	
	def "dashboard test location compare dashboard"() {
		setup:
		def period = newPeriod()
		setupLocationTree()
		setupProgramTree()
		setupDashboardTree()
		refresh()

		when:
		def dashboard =
		dashboardService.getLocationDashboard(LocationEntity.findByCode(locationName), ReportProgram.findByCode(programCode), period, new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)]), true);

		then:
		dashboard.dashboardEntities.containsAll expectedEntities.collect {getDashboardEntity(it)}
		dashboard.locations.containsAll expectedLocations.collect {getLocationEntity(it)}
		dashboard.locationPath.containsAll expectedLocationPath.collect {LocationEntity.findByCode(it)}

		where:
		locationName	| programCode	| expectedLocations		| expectedEntities  | expectedLocationPath	| expectedProgramPath
		BURERA			| PROGRAM1		| [BURERA]				| [PROGRAM1]		| [RWANDA, NORTH]		| [ROOT]
		NORTH			| ROOT			| [NORTH]				| [ROOT]			| [RWANDA]				| []
	}
	
	def "get location dashboard with no location entities"(){
		setup:
		def period = newPeriod()
		setupProgramTree()
		setupDashboardTree()
		refresh()
		
		when:
		def country = newLocationLevel(COUNTRY, 1)
		def rwanda = newLocationEntity(j(["en":RWANDA]), RWANDA, country)		
		def root = ReportProgram.findByCode(ROOT)	
		def types = new HashSet([DataEntityType.findByCode(DISTRICT_HOSPITAL_GROUP), DataEntityType.findByCode(HEALTH_CENTER_GROUP)])
		
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

	def getLocationEntity(String code){
		def entity = LocationEntity.findByCode(code);
		if(entity == null) entity = DataLocationEntity.findByCode(code);
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