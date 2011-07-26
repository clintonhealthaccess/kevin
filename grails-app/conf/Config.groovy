import org.hisp.dhis.period.MonthlyPeriodType

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

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
grails.views.javascript.library="jquery"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

// validateable classes
//grails.validateable.packages = ['org.chai.kevin.survey']
//grails.validateable.classes = [org.chai.kevin.survey.SurveyElementValue]

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// set per-environment serverURL stem for creating absolute links
grails.serverURL = "http://localhost:8080/${appName}"

// log4j configuration
log4j = {
	appenders {
		environments {
			production {
				file name: 'log-error', file:'errors.log'
			}
		}
	}
	
    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons' // core / classloading
		   
    warn   'org.mortbay.log',
		   'org.hibernate',
		   'net.sf.ehcache.hibernate'
	
	info   'org.codehaus.groovy.grails.plugins', // plugins
		   'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		   'org.hsqldb.jdbc',
		   'org.springframework'
		   
	debug  'org.hisp.dhis',
		   'org.chai.kevin',
		   'grails.app'
		   
		   
//	trace  'org.hibernate.SQL',
//		   'org.hibernate.engine.query',
//		   'org.hibernate.type',
//		   'org.hibernate.jdbc'
		   

	environments {
		production {
			root {
//				error 'log-error'
			}
		}
    }

	//	trace  'org.hibernate.type'
}

cloudbees.api.url='https://api.cloudbees.com/api'
cloudbees.api.key=System.properties['bees.key']
cloudbees.api.secret=System.properties['bees.secret']

/**
 * Application specific config
 */
google.analytics.webPropertyID = "UA-xxxxxx-x"

site.languages=["en","fr","rw"]
site.fallback.language="en"
site.admin=true

facility.level=4
facility.type.group="Type"

info.group.level=3

dashboard.facility.checked=["District Hospital","Health Center"]
dashboard.skip.levels=[]

cost.skip.levels=[]

dsr.facility.checked=["District Hospital","Health Center"]

period.type = "Monthly"

/**
 * Configuration file override
 */
def locations = ["file:${userHome}/.grails/${appName}-config.groovy"]
if (System.properties['config']) locations.add("file:"+System.properties['config'])
environments {
	production {
		grails.config.locations = locations
	}
}