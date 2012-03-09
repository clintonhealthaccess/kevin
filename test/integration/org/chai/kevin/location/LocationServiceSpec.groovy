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
import org.chai.kevin.location.DataLocationEntity;
import org.chai.kevin.location.DataEntityType
import org.chai.kevin.location.LocationEntity
import org.chai.kevin.location.LocationLevel;

class LocationServiceSpec extends IntegrationTests {

	def locationService;
	
	def "get locations of level"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getChildrenOfLevel(LocationEntity.findByCode(location), LocationLevel.findByCode(level)).containsAll(expectedLocations.collect{LocationEntity.findByCode(it)})
		
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
		locationService.getParentOfLevel(LocationEntity.findByCode(entity), LocationLevel.findByCode(level)).equals(LocationEntity.findByCode(expectedLocation))

		where:
		entity	| level		| expectedLocation
		NORTH	| COUNTRY	| RWANDA
		BURERA	| COUNTRY	| RWANDA
		
	}
	
	def "get data entity by code"(){
		setup:
		setupLocationTree()
		
		when: 
		def dataEntity = DataLocationEntity.findByCode(BUTARO)
		def dataEntOne = locationService.findCalculationEntityByCode(BUTARO, DataLocationEntity.class);
		
		then:
		dataEntOne != null
		dataEntOne.equals(dataEntity) 	
		
		when:
		def dataEntTwo = locationService.findCalculationEntityByCode(BUTARO, LocationEntity.class);
		
		then:
		dataEntTwo == null
		!dataEntTwo.equals(dataEntity)
	}
	
	def "get location entity by code"(){
		setup:
		setupLocationTree()
		
		when:
		def locationEntity = LocationEntity.findByCode(BURERA)
		def locationEntOne = locationService.findCalculationEntityByCode(BURERA,LocationEntity.class)
		
		then:
		locationEntOne != null
		locationEntOne.equals(locationEntity)
		
		when:
		def locationEntTwo = locationService.findCalculationEntityByCode(BURERA,DataLocationEntity.class)
		
		then:
		locationEntTwo == null
		!locationEntTwo.equals(locationEntity)
				
	}
	
	def "search location"() {
		setup:
		setupLocationTree()
		
		when:
		def result = locationService.searchLocation(LocationEntity.class, text, [:])
		
		then:
		result.equals(expectedResult.collect{LocationEntity.findByCode(it)})
		
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
}
