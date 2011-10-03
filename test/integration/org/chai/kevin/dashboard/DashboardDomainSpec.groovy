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

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.chai.kevin.data.Average
import org.chai.kevin.data.Type

class DashboardDomainSpec extends DashboardIntegrationTests {

	private static final Log log = LogFactory.getLog(DashboardDomainSpec.class)

	def "save target does not resave calculation"() {
		when:
		def calculation = newAverage([:], CODE(3), Type.TYPE_NUMBER())
		def objective = newDashboardObjective(CODE(2))
		def target = newDashboardTarget(CODE(1), calculation, objective, 1)
		
		then:
		Average.count() == 1
		
		when:
		target.save(failOnError: true)
		
		then:
		Average.count() == 1
	}
	
	def "call twice in a row"() {
		when:
		def calculation = newAverage([:], CODE(3), Type.TYPE_NUMBER())
		def objective = newDashboardObjective(OBJECTIVE)
		def target1 = newDashboardTarget(TARGET1, calculation, objective, 1)
		def target2 = newDashboardTarget(TARGET2, calculation, objective, 2 )
		
		then:
		DashboardObjective.findByCode(objectiveCode).objectiveEntries.size() == expectedSize
		
		where:
		objectiveCode	| expectedSize
		OBJECTIVE		| 2
		OBJECTIVE		| 2
	}
	
	def "objective entry order can be null"() {
		when:
		def objective = new DashboardObjective(root: true, names:[:], code: CODE(1)).save(failOnError: true)
		def entry = new DashboardObjectiveEntry(entry: objective, weight: 1).save(failOnError: true)
		
		then:
		DashboardObjective.count() == 1
		DashboardObjectiveEntry.count() == 1
	}
	
	def "objective save preserves order"() {
		when:
		def objective = new DashboardObjective(root: true, names:[:], code: OBJECTIVE).save(failOnError: true)
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:[:], code: CODE(2)), weight: 1, order: 5);
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: new DashboardObjective(names:[:], code: CODE(3)), weight: 1, order: 4);
		objective.save(flush: true)
		
		then:
		def newObjective = DashboardObjective.findByCode(OBJECTIVE);
		newObjective.objectiveEntries.size() == 2
		newObjective.objectiveEntries[1].order == 5
		newObjective.objectiveEntries[0].order == 4
	}
	
	def "objective delete cascade deletes objective entry"() {
		when:
		def root = new DashboardObjective(root: true, names:[:], code: ROOT).save(failOnError: true)
		def objective = new DashboardObjective(names:[:], code: OBJECTIVE).save(failOnError: true)
		def objectiveEntry = new DashboardObjectiveEntry(entry:objective, weight: 1, order: 5);
		root.addObjectiveEntry objectiveEntry
		root.save(failOnError: true)
		
		root.objectiveEntries.remove(objectiveEntry)
		new ArrayList(root.objectiveEntries).each { 
			it.parent = null
		}
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == 0
		DashboardObjective.count() == 1
	}

	def "objective save does not cascade objective entries"() {
		when:
		def root = new DashboardObjective(root: true, names:[:], code: ROOT).save(failOnError: true)
		def objective = new DashboardObjective(names:[:], code: OBJECTIVE)
		root.addObjectiveEntry new DashboardObjectiveEntry(entry:objective, weight: 1, order: 5);
		root.save(failOnError: true)
		
		then:
		DashboardObjectiveEntry.count() == 0
		DashboardObjective.count() == 1
	}
	
	def "remove target deletes parent objective entry"() {
		when:
		def root = new DashboardObjective(root: true, names:[:], code: ROOT).save(failOnError: true)
		def target = new DashboardTarget(names:[:], code: OBJECTIVE, calculation: newAverage([:], CODE(1), Type.TYPE_NUMBER())).save(failOnError: true)
		def objectiveEntry = new DashboardObjectiveEntry(entry:target, weight: 1, order: 5);
		root.addObjectiveEntry objectiveEntry
		root.save(failOnError: true)
		
		root.objectiveEntries.remove(target)
		target.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == 0
		DashboardTarget.count() == 0
	}
	
	
	def "get parent"() {
		when:
		def objective = newDashboardObjective(OBJECTIVE)
		def calculation = newAverage([:], CODE(3), Type.TYPE_NUMBER())
		def target1 = newDashboardTarget(TARGET1, calculation, objective, 1)
		
		then:
		target1.parent != null
//		target1.parent.parent != null
	}
	
	def "get parent of root"() {
		when:
		def root = newDashboardObjective(ROOT)
		
		then:
		root.parent == null
	}
	
}
