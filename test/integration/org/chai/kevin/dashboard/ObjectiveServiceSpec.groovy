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

import org.chai.kevin.Calculation;
import org.chai.kevin.Expression;
import org.chai.kevin.IntegrationTests;
import org.chai.kevin.dashboard.DashboardObjectiveService;
import org.chai.kevin.dashboard.DashboardTarget;
import org.chai.kevin.dashboard.DashboardObjective;
import org.chai.kevin.dashboard.DashboardObjectiveEntry;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;
import grails.test.GrailsUnitTestCase;

class ObjectiveServiceSpec extends IntegrationTests {
    
	def setup() {
		def calculation1 = new Calculation(expressions: [:], timestamp:new Date())
		calculation1.save(flush: true)
		def target = new DashboardTarget(names:j(["en":"target"]), code:"TARGET", calculation: calculation1)
		target.save()
		def objective = new DashboardObjective(names:j(["en":"objective"]), code:"OBJ", objectiveEntries: [])
		objective.addObjectiveEntry new DashboardObjectiveEntry(entry: target, weight: 1, order: 10)
		objective.save()
		def root = new DashboardObjective(names:j(["en":"root"]), code:"ROOT", objectiveEntries: [])
		root.addObjectiveEntry new DashboardObjectiveEntry(entry: objective, weight: 1, order: 10, parent: root)
		root.save()
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
