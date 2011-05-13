class UrlMappings {

	static mappings = {
		"/dashboard/$action/$period?/$objective?/$organisation?"(controller:"dashboard")
		
		"/cost/$action/$period?/$objective?/$organisation?"(controller:"cost")
		
		"/maps/$action/$period?/$objective?/$organisation?"(controller:"maps")
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		// temporary redirect to dashboard
		"/"(controller:"maps", action:"index")
		"500"(view:'/error')
	}
}
