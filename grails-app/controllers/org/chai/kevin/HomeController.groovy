package org.chai.kevin

import org.apache.shiro.SecurityUtils;
import org.chai.kevin.security.SurveyUser;
import org.chai.kevin.security.User;

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

class HomeController {
	
	def languageService
	
	def index = {
		if (log.isDebugEnabled()) log.debug("home.index, params:"+params)
		User user = User.findByUuid(SecurityUtils.subject.principal)
		
		if (user instanceof SurveyUser) {
			redirect (controller: "editSurvey", action: "view")
		}
		else {
			redirect (controller: "dashboard", action: "view")
		}
	}
	
	def upgrade = {render (view:'upgrade_'+languageService.currentLanguage)}
	def about = {render (view:'about_contact_'+languageService.currentLanguage)}
	def contact = {render (view:'about_contact_'+languageService.currentLanguage)}
	def helpdesk = {render (view:'helpdesk_'+languageService.currentLanguage)}
}
