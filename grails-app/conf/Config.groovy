/*
//* Copyright (c) 2011, Clinton Health Access Initiative.
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

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true

// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

//LOGGING
// set per-environment serverURL stem for creating absolute links
environments {
	development {
		// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
		grails.logging.jul.usebridge = true
	}
	production {
		// whether to install the java.util.logging bridge for sl4j. Disable for AppEngine!
		grails.logging.jul.usebridge = false
	}
}

//MAIL
environments {
	production {
		grails.mail.host = "smtp.gmail.com"
		grails.mail.port = 465
		// configuration defined in ${home}/.grails/kevin-config.groovy
//		grails.mail.username = "youracount@gmail.com"
//		grails.mail.password = "yourpassword"
		grails.mail.props = [
			"mail.smtp.auth":"true",
			"mail.smtp.socketFactory.port":"465",
			"mail.smtp.socketFactory.class":"javax.net.ssl.SSLSocketFactory",
			"mail.smtp.socketFactory.fallback":"false"
		]
	}
	development {
		grails.mail.disabled = true
	}
	test {
		grails.mail.disabled = true
	}
	demo {
		grails.mail.disabled = true
	}
}

//RESOURCES
environments {
	production {
		grails.resources.cdn.enabled = true
		// grails.resources.cdn.url = "http://static.mydomain.com/"
		// grails.resources.work.dir="/static/directory/"
		grails.resources.processing.enabled = true
		
		//NEVER COMMIT THIS !!!
		//grails.gsp.reload.enable = true
	}
	development {
		grails.resources.cdn.enabled = false
		grails.resources.processing.enabled = false
		grails.resources.mappers.yuicssminify.disable=true
		grails.resources.mappers.yuijsminify.disable=true
	}
	test {
		grails.resources.cdn.enabled = false
		grails.resources.processing.enabled = false
		grails.resources.mappers.yuicssminify.disable=true
		grails.resources.mappers.yuijsminify.disable=true
	}
	demo {
		grails.serverURL = 'http://kevin.cloudfoundry.com'
		grails.resources.cdn.enabled = false
		grails.resources.processing.enabled = true
		grails.resources.mappers.yuicssminify.disable=false
		grails.resources.mappers.yuijsminify.disable=false
	}
}

security.shiro.authc.required = false

// rabbitmq
rabbitmq {
	connectionfactory {
		username = 'guest'
		password = 'guest'
		hostname = '127.0.0.1'
		consumers = 1
	}
	queues = {
		adminQueue durable: true, autoDelete: false
	}
}
rabbitmq.concurrentConsumers = 1
rabbitmq.retryPolicy.maxAttempts = 1

// log4j configuration
log4j = {
	// Example of changing the log pattern for the default console
	// appender:
	//
	//appenders {
	//    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
	//}

	error  'grails.app.services.org.grails.plugin.resource',
		   'grails.app.resourceMappers.org.grails.plugin.resource',
		   'grails.app.taglib.org.grails.plugin.resource',
		   'grails.app.resourceMappers.org.grails.plugin.cachedresources',
		   'grails.app.services.grails.plugin.springcache',
		   'grails.plugin.springcache'
	
	error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
		   'org.codehaus.groovy.grails.web.pages', //  GSP
		   'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		   'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		   'org.codehaus.groovy.grails.web.mapping', // URL mapping
		   'org.codehaus.groovy.grails.commons', // core / classloading
		   'org.codehaus.groovy.grails.plugins', // plugins
		   'net.sf.ehcache.hibernate',
		   'grails.app.services.org.chai.kevin.survey.SurveyElementService',
		   'org.chai.kevin.JaqlService',
		   'org.chai.kevin.ExpressionService',
		   'org.springframework',
		   'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		   'org.hibernate'

    info   'org.chai.kevin.importer.GeneralDataImporter',
	       'org.chai.kevin.importer.NormalizedDataImporter',
		   'org.chai.kevin.importer.Importer',
		   'org.chai.kevin.value.ValueService'
		   				   
	debug  'grails.app',
		   'org.chai.kevin'
		   
	trace 'org.chai.kevin.service.imports.GeneralDataImporter',
		   'org.chai.kevin.service.imports.NominativeDataImporter',
		   'org.chai.kevin.service.imports.DataImporter',
		   'org.chai.kevin.service.imports.DataImporterSpec'
//		   'org.hibernate.type'
		   
}

environments {
	test {
		log4j = {
			root {
				error
			}
			
			off 'ExpressionLog'
		}
	}
	demo {
		log4j = {
			root {
				error
			}
			
			off 'ExpressionLog'
		}
	}
}

/**
 * Application specific config
 */
google.analytics.webPropertyID = "UA-xxxxxx-x"

// internationalization
i18nFields {
	locales = ["en","fr"]
	withlocale.disabled = true
}

site.fallback.language="en"
site.entity.list.max=40

site.admin.email="admin@dhsst.org"
site.contact.email="contact@dhsst.org"
site.from.email="no-reply@dhsst.org"
site.tagline.en="Ministry of Health"
site.tagline.fr="Ministère de la santé"
site.icon="http://localhost:8080/kevin/images/apple-touch-icon.png"
site.bugtracker.url = "#"

// TODO put in database on datalocation and period
site.datalocationtype.checked=["district_hospital","health_center"]
site.period=0

report.skip.levels=["sector"]
dashboard.skip.levels=[]
fct.skip.levels=[]
dsr.skip.levels=[]
dsr.view.map.skip.levels=["country", "province"]
survey.skip.levels=["sector"]
survey.export.skip.levels=["country", "sector"]

task.temp.folder='files/'

file.upload.available.charset=["UTF-8", "ISO-8859-1"]
file.upload.delimiter=","

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