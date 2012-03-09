/**
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
package org.chai.kevin.security

import java.util.Map;

import org.apache.commons.lang.StringUtils
import org.hibernate.Criteria;
import org.chai.kevin.util.Utils
import org.hibernate.criterion.MatchMode
import org.hibernate.criterion.Order
import org.hibernate.criterion.Projections
import org.hibernate.criterion.Restrictions

/**
 * @author Jean Kahigiso M.
 *
 */
class UserService {
	static transactional = true
	def sessionFactory;
	
	public List<User> searchUser(String text, Map<String, String> params) {
		def criteria = getSearchCriteria(text)
		
		if (params['offset'] != null) criteria.setFirstResult(params['offset'])
		if (params['max'] != null) criteria.setMaxResults(params['max'])
		
		List<User> users =[];
		
		if(params['sort']!=null)
			users= criteria.addOrder(Order.asc(params['sort'])).list()
		else
			users= criteria.addOrder(Order.asc("id")).list()
		
		StringUtils.split(text).each { chunk ->
			users.retainAll { user ->
				// we look in "info" if it is a data element
				Utils.matches(chunk, user.username) ||
				Utils.matches(chunk, user.email) ||
				Utils.matches(chunk, user.firstname) ||
				Utils.matches(chunk, user.lastname) ||
				Utils.matches(chunk, user.location)

			}
		}
		return users
		
	}
	
	public Integer countUser(String text) {
		return getSearchCriteria(text).setProjection(Projections.count("id")).uniqueResult()
	}
	
	private Criteria getSearchCriteria(String text) {
		def criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
		
		def textRestrictions = Restrictions.conjunction()
		StringUtils.split(text).each { chunk ->
			def disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.ilike("username", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("email", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("firstname", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("lastname", chunk, MatchMode.ANYWHERE))
			disjunction.add(Restrictions.ilike("location", chunk, MatchMode.ANYWHERE))
			
			textRestrictions.add(disjunction)
		}
		criteria.add(textRestrictions)
		return criteria
		
	}
	
	
}
