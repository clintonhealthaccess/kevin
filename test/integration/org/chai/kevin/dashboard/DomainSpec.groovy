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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chai.kevin.Initializer;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.IntegrationTestInitializer;
import org.chai.kevin.data.Average;
import org.chai.kevin.data.Calculation;
import org.chai.kevin.data.Sum;
import org.chai.kevin.data.Type;

import grails.plugin.spock.UnitSpec;

class DomainSpec extends DashboardIntegrationTests {

	private static final Log log = LogFactory.getLog(DomainSpec.class)

	def "save target does not resave calculation"() {
		when:
		def calculation = newCalculation([:], CODE(3), Type.TYPE_NUMBER)
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
		def calculation = newCalculation([:], CODE(3), Type.TYPE_NUMBER)
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
		newObjective.objectiveEntries[2].order == 5
		newObjective.objectiveEntries[1].order == 4
	}
	
	def "objective delete cascade deletes objective entry"() {
		when:
		def root = new DashboardObjective(root: true, names:[:], code: ROOT).save(failOnError: true)
		def objective = new DashboardObjective(names:[:], code: OBJECTIVE).save(failOnError: true)
		root.addObjectiveEntry new DashboardObjectiveEntry(entry:objective, weight: 1, order: 5);

		root.objectiveEntries.remove(objective)
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
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardObjective.findByCode("STAFFING");
		objective.objectiveEntries.clear()
		objective.save(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount
		DashboardTarget.count() == dashboardTargetCount;
	}
	
	def "remove target deletes parent objective entry"() {
		when:
		def dashboardObjectiveEntryCount = DashboardObjectiveEntry.count()
		def dashboardTargetCount = DashboardTarget.count()
		def objective = DashboardTarget.findByCode("A1");
		objective.parent.parent.objectiveEntries.remove(objective.parent)
		objective.delete(flush: true)
		
		then:
		DashboardObjectiveEntry.count() == dashboardObjectiveEntryCount - 1
		DashboardTarget.count() == dashboardTargetCount - 1;
	}
	
	// integration test
	def "objective entries has parents"() {
		expect:
		DashboardObjectiveEntry.count() == 4
	}
	
	// integration test
	def "weighted objectives for target"() {
		when:
		def objective = DashboardObjective.findByCode("STAFFING");
		def objectiveEntry = DashboardObjectiveEntry.findByEntry(objective);
		
		then:
		objectiveEntry != null
		objectiveEntry.getParent() != null
		
	}
	
	
	
	def "get parent"() {
		
		when:
		def child = DashboardTarget.findByCode(childCode)
		def parent = child.parent.parent
		
		then:
		parent != null
		parent.code == parentCode
		
		where:
		childCode	| parentCode
		"TARGET"	| "OBJ"
//		"objective"	| "root"
	}
	
	def "get parent of root"() {
		
		when:
		def child = DashboardObjective.findByCode("ROOT")
		def parent = child.parent
		
		then:
		parent == null
	}
	
	def "exception when multiple parents"() {
		when:
		def root = new DashboardObjective(names:j(["en":"root2"]), code:"ROOT2", objectiveEntries: [])
		root.save()
		def entry = new DashboardObjectiveEntry(entry: DashboardObjective.findByCode("OBJ"), weight: 1, order: 10)
		root.addObjectiveEntry entry
		entry.save()
		root.save()
			
		then:
		thrown(DataIntegrityViolationException)
	}
}
