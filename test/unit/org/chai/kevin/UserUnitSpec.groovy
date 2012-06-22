package org.chai.kevin

import org.chai.kevin.security.User;
import org.chai.kevin.security.UserType;

import grails.plugin.spock.UnitSpec;

class UserUnitSpec extends UnitSpec {

	def "set default permissions adds the default permissions"() {
		setup:
		def user
		
		when:
		user = new User(userType: UserType.SURVEY, locationId: 12)
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editSurvey:view','editSurvey:*:12','menu:survey']))
		
		when:
		user = new User(userType: UserType.PLANNING, locationId: 12)
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editPlanning:view','editPlanning:*:12','menu:planning']))
		
		when:
		user = new User(userType: UserType.OTHER, locationId: 12)
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['home:*']))
	}
	
	def "set default permissions keeps existing permissions"() {
		setup:
		def user
		
		when:
		user = new User(userType: UserType.SURVEY, locationId: 12, permissionString: 'test:test1,test:test2,test:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editSurvey:view','editSurvey:*:12','menu:survey','test:test1','test:test2','test:*']))
		
		when:
		user = new User(userType: UserType.PLANNING, locationId: 12, permissionString: 'test:test1,test:test2,test:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editPlanning:view','editPlanning:*:12','menu:planning','test:test1','test:test2','test:*']))
		
		when:
		user = new User(userType: UserType.OTHER, locationId: 12, permissionString: 'test:test1,test:test2,test:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['home:*','test:test1','test:test2','test:*']))
	}
	
	def "set default permissions erases existing permissions of other types"() {
		setup:
		def user
		
		when:
		user = new User(userType: UserType.SURVEY, locationId: 12, permissionString: 'editSurvey:view,editSurvey:*:100,menu:survey,home:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editSurvey:view','editSurvey:*:12','menu:survey']))
		
		when:
		user = new User(userType: UserType.SURVEY, locationId: 12, permissionString: 'editPlanning:view,editPlanning:*:100,menu:planning,home:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['editSurvey:view','editSurvey:*:12','menu:survey']))
		
		when:
		user = new User(userType: UserType.OTHER, locationId: 12, permissionString: 'editPlanning:view,editPlanning:*:100,menu:planning,home:*')
		user.setDefaultPermissions()
		
		then:
		user.permissions.equals(new HashSet(['home:*']))
		
	}
	
}
