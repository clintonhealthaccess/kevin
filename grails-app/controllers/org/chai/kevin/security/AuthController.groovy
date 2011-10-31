package org.chai.kevin.security

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class AuthController {
    def shiroSecurityManager

    def index = { redirect(action: "login", params: params) }

	
	def register = {
		return [ ]
	}

	// this will disappear once we have a real registration mechanism
	def requestAccess = {
		if (params.email?.trim() != '') {
			User user = User.findByUsername(params.email)
			if (user == null) {
				log.info("creating user ${params.email}")
				new User(username: params.email, passwordHash:'', permissionString:'').save()
			} 
		}
		
		def contactEmail = ConfigurationHolder.config.site.contact.email;
		def fromEmail = ConfigurationHolder.config.site.from.email;
		
		sendMail {
			to contactEmail
			from fromEmail
			subject "Access request from ${params.email}"
			body "Access request received from ${params.email}\nComments: ${params.comment}"
		}
		
		flash.message = message(code:'register.request.sent', default:'Thanks for submitting, we will send you the credentials shortly.')
		redirect(action: "login", params: params)
	}
	
	def login = {
		return [ username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri ]
	}

    def signIn = {
        def authToken = new UsernamePasswordToken(params.username, params.password as String)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }
        
        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri ?: "/"
        
        // Handle requests saved by Shiro filters.
        def savedRequest = WebUtils.getSavedRequest(request)
        if (savedRequest) {
            targetUri = savedRequest.requestURI - request.contextPath
            if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
        }
        
        try{
            // Perform the actual login. An AuthenticationException
            // will be thrown if the username is unrecognised or the
            // password is incorrect.
            SecurityUtils.subject.login(authToken)

            log.info "Redirecting to '${targetUri}'."
            redirect(uri: targetUri)
        }
        catch (AuthenticationException ex){
            // Authentication failed, so display the appropriate message
            // on the login page.
            log.info "Authentication failure for user '${params.username}'."
            flash.message = message(code: "login.failed")

            // Keep the username and "remember me" setting so that the
            // user doesn't have to enter them again.
            def m = [ username: params.username ]
            if (params.rememberMe) {
                m["rememberMe"] = true
            }

            // Remember the target URI too.
            if (params.targetUri) {
                m["targetUri"] = params.targetUri
            }

            // Now redirect back to the login page.
            redirect(action: "login", params: m)
        }
    }

    def signOut = {
        // Log the user out of the application.
        SecurityUtils.subject?.logout()

        // For now, redirect back to the home page.
        redirect(uri: "/")
    }

    def unauthorized = {
        render "You do not have permission to access this page."
    }
}
