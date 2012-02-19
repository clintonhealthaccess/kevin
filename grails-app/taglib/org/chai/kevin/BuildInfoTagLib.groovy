package org.chai.kevin

import org.apache.commons.io.FileUtils;

import grails.util.GrailsUtil;
import grails.util.Metadata;

class BuildInfoTagLib {

	static def build 
	
	static {
		File file = new File("build.info")
		if (file.exists()) build = Metadata.getInstance(new File("build.info"))
		else build = null
	}
	
	def buildInfo = {attrs, body ->
		if (GrailsUtil.environment == 'production') {
			if (build == null) {
				out << "no build info found"
			}
			else {
				out << render(template:'/tags/buildInfo/buildInfo', model:[
					buildDate: build.'app.buildDate',
					gitCommit: build.'app.gitCommit',
					systemName: build.'app.systemName',
					timezone: build.'app.timezone'
				])
			}
		}
		else {
			out << "no build info in ${GrailsUtil.environment} environment"
		}
	}
	
}
