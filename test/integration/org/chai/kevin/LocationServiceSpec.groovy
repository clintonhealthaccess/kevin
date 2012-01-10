package org.chai.kevin

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

import org.chai.kevin.location.DataEntity
import org.chai.kevin.location.DataEntityType
import org.chai.kevin.location.LocationEntity
import org.chai.kevin.location.LocationLevel;

class LocationServiceSpec extends IntegrationTests {

	def locationService;
	
	def "get organisations of level"() {
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
	
	def "get data entities for location"() {
		setup:
		setupLocationTree()
		
		expect:
		def typeList = types.collect{DataEntityType.findByCode(it)}
		locationService.getDataEntities(LocationEntity.findByCode(location), typeList.toArray(new DataEntityType[typeList.size()])).containsAll(expectedEntities.collect{DataEntity.findByCode(it)})
		
		where:
		location	| types												| expectedEntities
		RWANDA		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]	| [BUTARO, KIVUYE]
		RWANDA		| [DISTRICT_HOSPITAL_GROUP]							| [BUTARO]
		NORTH		| [DISTRICT_HOSPITAL_GROUP, HEALTH_CENTER_GROUP]	| [BUTARO, KIVUYE]
		NORTH		| [DISTRICT_HOSPITAL_GROUP]							| [BUTARO]
	}
	
	def "get data entities for data entity"() {
		
	}
	
	def "get parent of level for organisation for location"() {
		setup:
		setupLocationTree()
		
		expect:
		locationService.getParentOfLevel(LocationEntity.findByCode(entity), LocationLevel.findByCode(level)).equals(LocationEntity.findByCode(expectedLocation))

		where:
		entity	| level		| expectedLocation
		NORTH	| COUNTRY	| RWANDA
		BURERA	| COUNTRY	| RWANDA
		
	}
	
	def "get parent of level for organisation for data entity"() {
		
	}

		
}
