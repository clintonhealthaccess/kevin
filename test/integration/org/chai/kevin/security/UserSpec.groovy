package org.chai.kevin.security

import grails.validation.ValidationException;

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.location.DataLocation;
import org.chai.kevin.location.Location;
import org.chai.kevin.security.UserType;

class UserSpec extends IntegrationTests {

	def "user must have a type"() {
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test'
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
		).save(failOnError: true)
		
		then:
		User.count() == 1
	}
	
	def "planning user must have DataLocation location"() {
		setup:
		setupLocationTree()
		
		when:
		def location = Location.findByCode(BURERA)
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '', userType: UserType.PLANNING, 
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', locationId: location.id 
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '', userType: UserType.PLANNING,
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', locationId: DataLocation.findByCode(BUTARO).id
		).save(failOnError: true)
		
		then:
		User.count() == 1
	}
	
	def "planning user must have location"() {
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', userType: UserType.PLANNING	
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
		).save(failOnError: true)
		
		then:
		User.count() == 1
	}
	
	def "survey user must have location"() {
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', userType: UserType.SURVEY	
		).save(failOnError: true)
		
		then:
		thrown ValidationException
		
		when:
		new User(
			username: 'test', code: 'test', uuid: 'test', permissionString: '',
			passwordHash: '', email: 'test@test.com', firstname: 'test', lastname: 'test',
			phoneNumber: '123', organisation: 'test', userType: UserType.OTHER
		).save(failOnError: true)
		
		then:
		User.count() == 1	
	}
	
	def "add default role adds the default roles"() {
		setup:
		new Role(name: 'report-all-readonly', permissionString: '').save(failOnError: true)
		
		when:
		def user = new User(userType: UserType.SURVEY)
		user.setDefaultRoles()
		
		then:
		user.roles*.name == ['report-all-readonly']
	}
	
	def "add default role when role does not exist"() {
		
		when:
		def user = new User(userType: UserType.SURVEY)
		user.setDefaultRoles()
		
		then:
		user.roles.empty
	}
	
}
