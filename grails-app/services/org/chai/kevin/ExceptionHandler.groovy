package org.chai.kevin

import grails.util.GrailsWebUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.groovy.grails.commons.ConfigurationHolder;
import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.ModelAndView;

class ExceptionHandler extends GrailsExceptionResolver {

	def mailService
	def grailsApplication
	
	ModelAndView resolveException(HttpServletRequest arg0, HttpServletResponse arg1, def arg2, Exception exception) {
		def adminEmail = ConfigurationHolder.config.site.admin.email
		def fromEmail = ConfigurationHolder.config.site.from.email;
		def g = grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.RenderTagLib')
		g.metaClass.prettyPrintStatus = { return '' }
		
		try {
			mailService.sendMail {
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
