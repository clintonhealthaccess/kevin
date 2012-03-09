/**
 * 
 */
package org.chai.kevin.security

import org.chai.kevin.AbstractController;

/**
 * @author JeanKahigiso
 *
 */
class UserListController  extends AbstractController {
	def userService;
	
	def list = {
		adaptParamsForList()
		
		List<User> users = User.list(params);

		render (view: '/entity/list', model:[
			template:"user/userList",
			entities: users,
			entityCount: User.count(),
			code: 'user.label',
			addTemplate: '/entity/user/addUser'
		])
	}
	

	def search={
		adaptParamsForList()
		
		List<User> users = userService.searchUser(params['q'], params);

		render (view: '/entity/list', model:[
			template:"user/userList",
			entities: users,
			entityCount: userService.countUser(params['q']),
			code: 'user.label',
			q:params['q'],
			addTemplate: '/entity/user/addUser'
		])
		
	}

}
