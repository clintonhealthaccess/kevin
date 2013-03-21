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

// pooled connection config
dataSource {
	pooled = true

	properties {
		maxActive = 50
		maxIdle = 25
		minIdle = 5
		initialSize = 5
		timeBetweenEvictionRunsMillis=1000 * 60 * 30
		numTestsPerEvictionRun=3
		minEvictableIdleTimeMillis=1000 * 60 * 30
		maxWait = 10000
		testOnBorrow = true
		testWhileIdle = true
		testOnReturn = true
		validationQuery = "SELECT 1"
	}
}
hibernate {
	cache.use_second_level_cache = true //specified in ehcache.xml
	cache.use_query_cache = true
	cache.provider_class = 'net.sf.ehcache.hibernate.SingletonEhCacheProvider'
	
	naming_strategy = org.hibernate.cfg.DefaultNamingStrategy
	// performance improvement, but keep in mind that it might 
	// affect data consistency
	flush.mode = 'commit'
	// show_sql = true
}
naming_strategy = org.hibernate.cfg.DefaultNamingStrategy

// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			driverClassName = "org.h2.Driver"
			url = "jdbc:h2:mem:devDB;mvcc=true"
			username = "sa";
			password = "";
		}
		hibernate {
//			dialect = "org.hibernate.dialect.HSQLDialect"
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "org.h2.Driver"
			url = "jdbc:h2:mem:testDb;mvcc=true"
			username = "sa";
			password = "";

		}
		hibernate {
//			dialect = "org.hibernate.dialect.HSQLDialect"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
			driverClassName = "com.mysql.jdbc.Driver"
			// configuration defined in ${home}/.grails/kevin-config.groovy
		}
		hibernate {
//			dialect = "org.hibernate.dialect.MySQLDialect"
		}
	}
	demo {
		dataSource {
			dbCreate = "create-drop"
			driverClassName = "com.mysql.jdbc.Driver"
			// configuration overriden by cloudfoundry
			dataSource.url="jdbc:mysql://127.0.0.1:8889/kevin_demo"
			dataSource.username="root"
			dataSource.password="root"
		}
		hibernate {
//			dialect = "org.hibernate.dialect.MySQLDialect"
		}
	}
}
