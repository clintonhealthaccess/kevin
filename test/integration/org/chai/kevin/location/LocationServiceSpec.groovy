package org.chai.kevin.location

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

import java.sql.Types;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.DataLocationType
import org.chai.kevin.location.Location
import org.chai.kevin.location.LocationLevel;

class LocationServiceSpec extends IntegrationTests {

	def locationService;
	
	def "get locations of level"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getChildrenOfLevel(Location.findByCode(location), LocationLevel.findByCode(level)).containsAll(expectedLocations.collect{Location.findByCode(it)})
		
		where:
		location	| level		| expectedLocations
		RWANDA		| COUNTRY	| [RWANDA]
		RWANDA		| PROVINCE	| [NORTH]
		RWANDA		| DISTRICT	| [BURERA]
		NORTH		| DISTRICT	| [BURERA]
		BURERA		| DISTRICT	| [BURERA]
		BURERA		| COUNTRY	| []
	}
	
	def "get parent of level for location"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getParentOfLevel(Location.findByCode(location), LocationLevel.findByCode(level)).equals(Location.findByCode(expectedLocation))

		where:
		location| level		| expectedLocation
		NORTH	| COUNTRY	| RWANDA
		BURERA	| COUNTRY	| RWANDA
		
	}
	
	def "get data location by code"(){
		setup:
		setupLocationTree()
		
		when: 
		def dataLocation = DataLocation.findByCode(BUTARO)
		def dataEntOne = locationService.findCalculationLocationByCode(BUTARO, DataLocation.class);
		
		then:
		dataEntOne != null
		dataEntOne.equals(dataLocation) 	
		
		when:
		def dataEntTwo = locationService.findCalculationLocationByCode(BUTARO, Location.class);
		
		then:
		dataEntTwo == null
		!dataEntTwo.equals(dataLocation)
	}
	
	def "get location location by code"(){
		setup:
		setupLocationTree()
		
		when:
		def location = Location.findByCode(BURERA)
		def locationEntOne = locationService.findCalculationLocationByCode(BURERA,Location.class)
		
		then:
		locationEntOne != null
		locationEntOne.equals(location)
		
		when:
		def locationEntTwo = locationService.findCalculationLocationByCode(BURERA,DataLocation.class)
		
		then:
		locationEntTwo == null
		!locationEntTwo.equals(location)
				
	}
	
	def "search location"() {
		setup:
		setupLocationTree()
		
		when:
		def result = locationService.searchLocation(Location.class, text, [:])
		
		then:
		result.equals(expectedResult.collect{Location.findByCode(it)})
		
		where:
		text	| expectedResult
		"Bur"	| [BURERA]
		"Nor"	| [NORTH]
		"n/a"	| []
	}
	
	def "get list of levels with no skip levels"(){
		setup:
		setupLocationTree()
		def skipLevels = null
		
		when:		
		def levels = locationService.listLevels(skipLevels)
		def noSkipLevels = locationService.listLevels()		
		
		then:
		levels == noSkipLevels
		levels.size() == 4		
	}
	
	def "get list of levels with skip levels"(){
		setup:
		setupLocationTree()
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		
		when:
		def levels = locationService.listLevels(skipLevels)		
		def noSkipLevels = locationService.listLevels()
		
		then:
		levels != noSkipLevels
		levels.size() == 3
	}
	
	def "get level before with skip levels"(){
		setup:
		setupLocationTree()
		
		//levelBefore == null
		when:
		def level = LocationLevel.findByCode(PROVINCE)
		def skipLevels = new HashSet([LocationLevel.findByCode(COUNTRY)])
		def levelBefore = locationService.getLevelBefore(level, skipLevels)		
		
		then:
		levelBefore == null
		
		//levelBefore skips 1 level
		when:
		level = LocationLevel.findByCode(DISTRICT)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE)])
		levelBefore = locationService.getLevelBefore(level, skipLevels)
		
		then:
		levelBefore.equals(LocationLevel.findByCode(COUNTRY))
		
		//levelBefore skips 2 levels
		when:
		level = LocationLevel.findByCode(SECTOR)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE), LocationLevel.findByCode(DISTRICT)])
		levelBefore = locationService.getLevelBefore(level, skipLevels)
		
		then:
		levelBefore.equals(LocationLevel.findByCode(COUNTRY))
	}
	
	def "get level after with skip levels"(){
		setup:
		setupLocationTree()
		
		//levelAfter == null
		when:
		def level = LocationLevel.findByCode(DISTRICT)
		def skipLevels = new HashSet([LocationLevel.findByCode(SECTOR)])
		def levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter == null
		
		//levelAfter skips 1 level
		when:
		level = LocationLevel.findByCode(PROVINCE)
		skipLevels = new HashSet([LocationLevel.findByCode(DISTRICT)])
		levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter.equals(LocationLevel.findByCode(SECTOR))
		
		//levelAfter skips 2 levels
		when:
		level = LocationLevel.findByCode(COUNTRY)
		skipLevels = new HashSet([LocationLevel.findByCode(PROVINCE), LocationLevel.findByCode(DISTRICT)])
		levelAfter = locationService.getLevelAfter(level, skipLevels)
		
		then:
		levelAfter.equals(LocationLevel.findByCode(SECTOR))
	}
}
