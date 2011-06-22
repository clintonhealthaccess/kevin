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

import grails.plugin.spock.UnitSpec;

import java.util.Date;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitLevel;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;

abstract class UnitTests extends UnitSpec {

	static Date mar01 = Initializer.getDate( 2005, 3, 1 );
	static Date mar31 = Initializer.getDate( 2005, 3, 31 );
	
	def addBasicData() {
		def monthly = new MonthlyPeriodType();
		mockDomain(MonthlyPeriodType, [monthly])
		def period = new Period(periodType: monthly, startDate: mar01, endDate: mar31)
		mockDomain(Period, [period])
		
		// organisations
		def burera = new OrganisationUnit(name: "Burera", shortName:"RW,N,BU")
		def butaro = new OrganisationUnit(name: "Butaro DH", shortName:"RW,N,BU,BUDH", parent: burera)
		def kivuye = new OrganisationUnit(name: "Kivuye HC", shortName:"RW,N,BU,KIHC", parent: burera)
		burera.children = [butaro, kivuye]
				
		// organisation groups
		def dh = new OrganisationUnitGroup(name: "District Hospital", uuid: "District Hospital", members: [butaro])
		def hc = new OrganisationUnitGroup(name: "Health Center", uuid: "Health Center", members: [kivuye])
		butaro.groups = [dh]
		kivuye.groups = [hc]
		mockDomain(OrganisationUnit, [burera, butaro, kivuye])
		mockDomain(OrganisationUnitGroup, [dh, hc])
		
		def level1 = new OrganisationUnitLevel(level: 0, name: "Country")
		def level2 = new OrganisationUnitLevel(level: 1, name: "Province")
		def level3 = new OrganisationUnitLevel(level: 2, name: "District")
		def level4 = new OrganisationUnitLevel(level: 3, name: "Facility")
		mockDomain(OrganisationUnitLevel, [level1, level2, level3, level4])
	}
	
	static def j(def map) {
		return Initializer.j(map);
	} 
	
}
