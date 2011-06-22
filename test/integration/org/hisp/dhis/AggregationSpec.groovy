package org.hisp.dhis

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

import java.util.Date;

import org.chai.kevin.ExpressionService;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.DataElement;
import org.chai.kevin.ValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;

class AggregationSpec extends IntegrationTests {

	ValueService valueService;
	
	def setup() {
		Initializer.createDummyStructure();
		IntegrationTestInitializer.createExpressions()
		IntegrationTestInitializer.createDashboard();
		IntegrationTestInitializer.createDataElements()
		IntegrationTestInitializer.addNonConstantData();
	}
	
	def "call twice in a row"() {
		
		when:
		def period = Period.list()[1]
		def dataElement = DataElement.findByCode(dataElementCode)
		def organisation = getOrganisation(organisationName)
		
		then:
		valueService.getDataValue(dataElement, period, organisation).value == value
				
		where:
		dataElementCode	| organisationName	| value
		"CODE"			| "Butaro DH"		| "40"
		
	}
	
//	def "ids"() {
//		
//		expect:
//		def periods = Period.list()
//		periods.size() == 1
//		periods[0].id == periodId
//		def dataElements = DataElement.list()
//		dataElements.size() == 1
//		dataElements[0].id == dataElementId
//		
//		where:
//		periodId	| dataElementId
//		1			| 1
//		2			| 2
//		3			| 3
//		4			| 4
//		
//	}
	
}
