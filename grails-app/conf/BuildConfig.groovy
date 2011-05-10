grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // uncomment to disable ehcache
        // excludes 'ehcache'
		excludes 'hibernate'
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal()
        mavenCentral()
        mavenRepo "http://snapshots.repository.codehaus.org"
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://mirrors.ibiblio.org/pub/mirrors/maven2/"
        mavenRepo "http://repository.jboss.org/nexus/"
        mavenRepo "http://www.intalio.org/public/maven2/"

	/**
	 * Configure our resolver.
	 */
	def libResolver = new org.apache.ivy.plugins.resolver.URLResolver();
	libResolver.addArtifactPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/[type]s/[artifact]-[revision].[ext]")
	libResolver.addIvyPattern("https://github.com/fterrier/repository/raw/master/[organisation]/[module]/ivys/ivy-[revision].xml")
	libResolver.name = "github"
	libResolver.settings = ivySettings
	resolver libResolver

    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		compile ('org.aspectj:aspectjrt:1.6.8')
		compile ('org.aspectj:aspectjweaver:1.6.8')
		compile ('javassist:javassist:3.11.0.GA')
		
		compile ('org.hisp.dhis:dhis-service-core:2.2-SNAPSHOT') {
			excludes "xml-apis" ,
			         "hibernate-core",
					 "hibernate-annotations",
					 "velocity"
			         //"dhis-support-hibernate" 
					 //"dhis-support-external" 
					 //"dhis-support-jdbc"
					 //"dhis-support-system"
		}
		compile ('org.hisp.dhis:dhis-service-aggregationengine-default:2.2-SNAPSHOT')
		compile ('org.hisp.dhis:dhis-api:2.2-SNAPSHOT') {
			excludes "xml-apis"
		}
				
		compile( "org.quartz-scheduler:quartz:1.8.4" ) {
			excludes "slf4j-api"
		}
		
		// runtime 'org.springframework.security:spring-security-core:3.0.5.RELEASE'
        runtime 'mysql:mysql-connector-java:5.1.13'
		
//		test("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
		test("org.seleniumhq.selenium:selenium-chrome-driver:2.0b3")
		test("org.seleniumhq.selenium:selenium-htmlunit-driver:latest.release") {
			excludes "xml-apis"
		}
		test 'org.gmock:gmock:0.8.1'
		
		// those are for the migration script to work
//		compile 'org.hisp.dhis:dhis-service-importexport:2.2-SNAPSHOT'
//		compile 'org.supercsv:SuperCSV:1.52'
    }
	
}
