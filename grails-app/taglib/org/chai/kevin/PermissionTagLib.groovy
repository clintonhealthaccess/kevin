package org.chai.kevin

import org.codehaus.groovy.grails.commons.ConfigurationHolder;

class PermissionTagLib {

	def ifAdmin = {attrs, body ->
//		if (ConfigurationHolder.config.site.admin) {
//			out << body()
//		}
		if (g.cookie(name: "admin") == "true") out << body()
	}
}
