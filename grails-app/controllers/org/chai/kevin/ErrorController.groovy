import grails.util.GrailsUtil;

import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ErrorController {

	def internalError = {
		if (request.exception) {
			// admin email is specified in the Config.groovy file
			// An email will be sent to the admin person whenever an internalError occurred.
			def adminEmail = ConfigurationHolder.config.site.admin.email
			def exception = request.exception
			
			sendMail {
				multipart true
				to adminEmail
				subject "Unhandled exception in the production environment"
				html g.renderException(exception: exception)
			}
			
			render (view: 'error')
		}
	}
}