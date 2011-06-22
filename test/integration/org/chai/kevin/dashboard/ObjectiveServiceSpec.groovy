package org.chai.kevin.dashboard

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
