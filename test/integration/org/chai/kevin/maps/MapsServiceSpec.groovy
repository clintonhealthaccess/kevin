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
package org.chai.kevin.maps

import org.chai.kevin.data.Type
import org.chai.kevin.maps.MapsTarget.MapsTargetType
import org.hisp.dhis.period.Period

class MapsServiceSpec extends MapsIntegrationTests {

	def mapsService;
	
	def "calculation gives correct values"() {
		setup:
		setupOrganisationUnitTree()
		def period = newPeriod()
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "10")
		def calculation = newAverage([(DISTRICT_HOSPITAL_GROUP): expression, (HEALTH_CENTER_GROUP): expression], CODE(2), Type.TYPE_NUMBER())
		def mapsTarget = newMapsTarget(CODE(3), MapsTargetType.AVERAGE, calculation)
		refresh()
		
		when:
		def maps = mapsService.getMap(period, getOrganisation(RWANDA), 2, mapsTarget)
		
		then:
		maps.polygons.size() == 1
		def hasValues = false
		for (def polygon : maps.polygons) {
			if (polygon.value != null) hasValues = true
		}
		hasValues
	}
	
	
}