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

import grails.plugin.spock.UnitSpec;

class DomainUnitSpec extends UnitSpec {
   
	def "find strategic objective"() {
        setup:
		mockDomain(DashboardObjective)
		
        when:
        new DashboardObjective(code: "target", root: false).save()

        then:
        DashboardObjective.findByCode("target") != null
    }
	
	def "ordering strategic objective"() {
		setup:
		mockDomain(DashboardObjective)
		mockDomain(DashboardTarget)
		mockDomain(DashboardObjectiveEntry)
		
		when:
		def weightedObjective1 = new DashboardObjectiveEntry(entry: new DashboardTarget(code: "target1"), weight: 1, order: 10)
		def weightedObjective2 = new DashboardObjectiveEntry(entry: new DashboardTarget(code: "target2"), weight: 1, order: 3)
		def objective = new DashboardObjective(code: "objective", objectiveEntries: [])
		objective.save(failOnError: true)
		objective.addObjectiveEntry weightedObjective2
		objective.addObjectiveEntry weightedObjective1
		weightedObjective1.save(failOnError: true)
		weightedObjective2.save(failOnError: true)
		objective.save(failOnError: true)
		
		then:
		DashboardObjective.findByCode("objective").objectiveEntries.size() == 2
		new ArrayList(DashboardObjective.findByCode("objective").objectiveEntries)[index].entry.code == expectedCode
		new ArrayList(DashboardObjective.findByCode("objective").objectiveEntries)[index].order == expectedOrder
		
		where:
		index	| expectedCode	| expectedOrder
		0		| "target2"		| 3
		1		| "target1"		| 10
	}
	
//	def "dashboardTarget validation"() {
//		setup:
//		mockForConstraintsTests(DashboardTarget)
//		
//		when:
//		def target = new DashboardTarget();
//		target.validate()
//		
//		then:
//		target.errors.count == 1
//		target.errors["name"] == "blank"
//	}
	
//	def "dashboardObjective validation"() {
//		setup:
//		mockForConstraintsTests(DashboardTarget)
//		
//		when:
//		def target = new DashboardTarget();
//		target.validate()
//		
//		then:
//		target.errors.count == 1
//		target.errors["name"] == "blank"
//	}
	
}