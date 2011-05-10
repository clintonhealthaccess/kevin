class UrlMappings {

	static mappings = {
		"/dashboard/$action/$period?/$objective?/$organisation?"(controller:"dashboard")
		
		"/cost/$action/$period?/$objective?/$organisation?"(controller:"cost")
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		// temporary redirect to dashboard
		"/"(controller:"cost", action:"index")
		"500"(view:'/error')
	}
}
