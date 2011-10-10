package org.chai.kevin.maps;

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

import grails.validation.ValidationException

import org.chai.kevin.data.Expression
import org.chai.kevin.data.Type
import org.chai.kevin.maps.MapsTarget.MapsTargetType

public class MapsDomainSpec extends MapsIntegrationTests {

	def "target constraint: type cannot be null"() {
		when: 
		new MapsTarget(code: CODE(1)).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: code cannot be null"() {
		when:
		def expression = newExpression(CODE(1), Type.TYPE_NUMBER(), "1")
		new MapsTarget(code:CODE(1), expression: expression, type: MapsTargetType.AGGREGATION).save(failOnError:true)
		
		then:
		MapsTarget.count() == 1
		
		when:
		new MapsTarget(expression: expression, type: MapsTargetType.AGGREGATION).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: expression cannot be null"() {
		when:
		new MapsTarget(code:CODE(1), expression: newExpression(CODE(1), Type.TYPE_NUMBER(), "1"), type: MapsTargetType.AGGREGATION).save(failOnError:true)
		
		then:
		MapsTarget.count() == 1
		
		when:
		new MapsTarget(code:CODE(1), type: MapsTargetType.AGGREGATION).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
	def "target constraint: calculation cannot be null"() {
		when:
		def calculation = newAverage([:], CODE(1), Type.TYPE_NUMBER())
		new MapsTarget(code:CODE(1) , calculation: calculation, type: MapsTargetType.AVERAGE).save(failOnError:true)
		
		then:
		MapsTarget.count() == 1
		
		when:
		new MapsTarget(code:CODE(1), type: MapsTargetType.AVERAGE).save(failOnError:true)
		
		then:
		thrown ValidationException
	}
	
}