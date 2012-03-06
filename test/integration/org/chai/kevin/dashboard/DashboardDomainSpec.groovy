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

import grails.validation.ValidationException;

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.data.Average
import org.chai.kevin.data.Type

class DashboardDomainSpec extends DashboardIntegrationTests {

	private static final Log log = LogFactory.getLog(DashboardDomainSpec.class)

	def "weight cannot be null on target"() {
		when:
		def calculation = newAverage("1", CODE(3))
		def program = newReportProgram(CODE(2))
		new DashboardTarget(code: CODE(1), program: program, calculation: calculation, weight: 1).save(failOnError: true)
		
		then:
		DashboardTarget.count() == 1
		
		when:
		new DashboardTarget(code: CODE(4), program: program, calculation: calculation).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "report program cannot be null on target"() {
		when:
		def calculation = newAverage("1", CODE(3))
		def program = newReportProgram(CODE(2))
		new DashboardTarget(code: CODE(1), program: program, calculation: calculation, weight: 1).save(failOnError: true)
		
		then:
		DashboardTarget.count() == 1
		
		when:
		new DashboardTarget(code: CODE(4), program: program, weight: 1).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "report program cannot be null on program"() {
		when:
		def calculation = newAverage("1", CODE(3))
		def program = newReportProgram(CODE(2))
		new DashboardProgram(code: CODE(1), program: program, weight: 1).save(failOnError: true)
		
		then:
		DashboardProgram.count() == 1
		
		when:
		new DashboardProgram(code: CODE(4), weight: 1).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "weight cannot be null on program"() {
		when:
		def calculation = newAverage("1", CODE(3))
		def program = newReportProgram(CODE(2))
		new DashboardProgram(code: CODE(1), program: program, weight: 1).save(failOnError: true)
		
		then:
		DashboardProgram.count() == 1
		
		when:
		new DashboardProgram(code: CODE(4), program: program).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
	def "save target does not resave calculation"() {
		when:
		def calculation = newAverage("1", CODE(3))
		def program = newReportProgram(CODE(2))
		def target = newDashboardTarget(CODE(1), calculation, program, 1)
		
		then:
		Average.count() == 1
		
		when:
		target.save(failOnError: true)
		
		then:
		Average.count() == 1
	}
	
	def "get parent"() {
		when:
		def program = newReportProgram(PROGRAM)
		def calculation = newAverage("1", CODE(3))
		def target1 = newDashboardTarget(TARGET1, calculation, program, 1)
		
		then:
		target1.program != null
	}
	
	def "get parent of root"() {
		when:
		def root = newReportProgram(ROOT)
		
		then:
		root.parent == null
	}
	
	def "target calculation cannot be a sum"() {
		when:
		new DashboardTarget(code: PROGRAM, calculation: newSum("1", CODE(1))).save(failOnError: true)
		
		then:
		thrown ValidationException
	}
	
}
