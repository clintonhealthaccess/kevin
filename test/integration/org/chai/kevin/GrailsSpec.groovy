package org.chai.kevin

import javax.persistence.Basic;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Entity;
import org.hibernate.cfg.Configuration;

import test.Domain;

import grails.plugin.spock.IntegrationSpec;
import grails.plugin.spock.UnitSpec;

class GrailsSpec extends IntegrationSpec {

	def "weird map bug"() {
		setup:
		def domain = new Domain(name:"test").save(failOnError: true)
		
		when:
		domain = Domain.findByName("test")
		domain.properties = ["names[en]":"New name"]
		domain.save()
				
		then:
		Expression.count() == 1
	}
		
}
