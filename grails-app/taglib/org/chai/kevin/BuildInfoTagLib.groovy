package org.chai.kevin

import org.apache.commons.io.FileUtils;

import grails.util.GrailsUtil;
import grails.util.Metadata;

class BuildInfoTagLib {

	def grailsApplication
	
	def buildInstance
	def buildInitialized = false

	def getBuild() {
		if (!buildInitialized) {
			def resource = grailsApplication.parentContext.getResource("classpath:build.info")
			if (resource.exists()) buildInstance = Metadata.getInstance(resource.inputStream)
			else buildInstance = null
			buildInitialized = true
		}
		return buildInstance
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
