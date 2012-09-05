package org.chai.kevin

import grails.util.GrailsUtil;
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ErrorController {

	def internalError = {
		if (request.exception) {
			// admin email is specified in the Config.groovy file
			// An email will be sent to the admin person whenever an internalError occurred.
			def adminEmail = ConfigurationHolder.config.site.admin.email
			def fromEmail = ConfigurationHolder.config.site.from.email;
			def exception = request.exception
			
			try {
				sendMail {
					multipart true
					to adminEmail
					from fromEmail
					subject "Unhandled exception in the production environment"
					html g.renderException(exception: exception)
				}
			} catch (Exception e) {
				log.error("could not send email after exception", e)
			}
			
			render (view: 'error')
		}
	}
}