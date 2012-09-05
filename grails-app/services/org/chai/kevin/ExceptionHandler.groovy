package org.chai.kevin

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

class ExceptionHandler extends GrailsExceptionResolver {

	ModelAndView resolveException(HttpServletRequest arg0, HttpServletResponse arg1, def arg2, Exception exception) {
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
		
		return super.resolveException(arg0, arg1, arg2, exception)
	}
	
}
