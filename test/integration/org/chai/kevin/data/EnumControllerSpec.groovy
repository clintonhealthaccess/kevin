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
package org.chai.kevin.data

/**
 * @author JeanKahigiso
 *
 */

import org.chai.kevin.IntegrationTests;
import org.hibernate.exception.ConstraintViolationException;

class EnumControllerSpec extends IntegrationTests {
	def enumController
	
	//TODO grails 2.0.0 bug needs to be fixed in order for these to validate
	//this will fail grails 2 bug has to be fixed so this can pass
	//def "enum code has to be unique (this will fail grails bug)"(){
	//setup:
	//def enume = newEnume(CODE("code"), "My Enum two", "Enum two for test kap");
	//enumController = new EnumController();
	//when:
	//enumController.params.code="code";
	//enumController.save()
	//then:
	//thrown ConstraintViolationException
	//Enum.count()==1
	//	}
	
	def "search and list enum"(){
		setup:
		def enumeTwo = newEnume(CODE("the code two"), "My Enum two", "Enum two for test kap");
		def enumeOne = newEnume(CODE("the code one"), "My Enum one", "Enum one for test one kap");
		def enumeThree= newEnume(CODE("the code three"), "My Enum one", "Enum one for test one");
		enumController = new EnumController();
		
		when:
		enumController.params.sort="code"
		enumController.list()
		then:
		enumController.modelAndView != null
		enumController.modelAndView.model.entities.equals([enumeOne,enumeThree,enumeTwo])
		enumController.modelAndView.model.entityCount== 3
		
		when:
		enumController.params.q="kap"
		enumController.params.sort="code"
		enumController.search()
		then:
		enumController.modelAndView != null
		enumController.modelAndView.model.entities.equals([enumeOne,enumeTwo])
		enumController.modelAndView.model.entityCount== 2
		
	}


}
