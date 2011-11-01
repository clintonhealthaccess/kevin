package org.chai.kevin.security

import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.springframework.web.servlet.FlashMap;

class AuthController {
	
    def shiroSecurityManager

	def getFromEmail() {
		def fromEmail = ConfigurationHolder.config.site.from.email;
		return fromEmail
	}
	
    def index = { redirect(action: "login", params: params) }

	def register = {
		return [ ]
	}
	
	// this will disappear once we have a real registration mechanism
	def requestAccess = {
		if (params.email?.trim() != '' && params.email != null) {
			// TODO find by email instead
			User user = User.findByUsername(params.email)
			if (user == null) {
				log.info("creating user ${params.email}")
				new User(username: params.email, passwordHash:'', permissionString:'').save()
			} 
			
			
			def contactEmail = ConfigurationHolder.config.site.contact.email;
			
			// TODO internationalize email
			sendMail {
				to contactEmail
				from getFromEmail()
				subject "Access request from ${params.email}"
				body "Access request received from ${params.email}\nComments: ${params.comment}"
			}
			
			flash.message = message(code:'register.request.sent', default:'Thanks for submitting, we will send you the credentials shortly.')
		}
		else {
			// TODO check email address using command object ?
			flash.message = message(code:'register.wrong.username', default:'This is not a valid email address');
		}
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

            if (log.isInfoEnabled()) log.info "Redirecting to '${targetUri}'."
            redirect(uri: targetUri)
        }
        catch (AuthenticationException ex){
            // Authentication failed, so display the appropriate message
            // on the login page.
            if (log.isInfoEnabled()) log.info "Authentication failure for user '${params.username}'."
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
	
	def forgotPassword = {
		return [ ]
	}

	def retrievePassword = {
		if (log.isDebugEnabled()) log.debug("auth.retrievePassword, params:"+params)
		
		if (params.email?.trim() != '' && params.email != null) {
			// TODO find by email instead
			User user = User.findByUsername(params.email)
			if (user == null) {
				flash.message = message(code:'forgot.password.username.not.found', default:'This username does not exist.')
				redirect(action:'forgotPassword')
			}
			else {
				// TODO replace everything by email
				// create token
				String randomString = RandomStringUtils.randomAlphabetic(20)
				Token token = new Token(token: randomString, email: user.username).save()
				
				def url = createLink(controller:'auth', action:'newPassword', params:[token:token.token])
				if (log.isDebugEnabled()) log.debug("sending email to: ${user.username}, token: ${token.token}, url: ${url}")
				// send email
				sendMail {
					to user.username
					from getFromEmail()
					subject "Lost password?"
					body "Hello\n\nTo set a new password, please go to ${url}.\n\nYour DHSST Team."
				}
				flash.message = message(code:'forgot.password.email.sent', default:'An email has been sent with the instructions.')
				redirect(action:'forgotPassword')
			}
		}
	}
	
	def newPassword = {
		if (log.isDebugEnabled()) log.debug("auth.newPassword, params:"+params)
		
		// if token in URL
		if (params.token != null && params.token.trim() != '') {
			Token token = Token.findByToken(params.token);
			if (token == null) {
				flash.message = message(code:'new.password.token.not.found', default:'The request could not be completed.')
				redirect(action:'login')
			}
			else {
				return [token: token.token]
			}
		}
		// if user is logged in
		else if (SecurityUtils.subject.isAuthenticated()) {
			// TODO
		}
		else {
			redirect(action:'login')
		}
	}

	def setPassword = { PasswordCommand cmd ->
		if (log.isDebugEnabled()) log.debug("auth.setPassword, params:"+params)
		
		if (cmd.hasErrors()) {
			redirect (action: 'newPassword', params:[token: params.token])
		}
		else {
				
		}
	}
	
    def unauthorized = {
        render "You do not have permission to access this page."
    }
}

class PasswordCommand {
	String password
	String repeat

	static constraints = {
		password(blank: false, minSize: 4)
		repeat(blank: false, minSize: 4, validator: {val, obj ->
			val == obj.password
		})
	}
}