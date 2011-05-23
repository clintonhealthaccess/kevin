class UrlMappings {

	static mappings = {
		"/dashboard/$action/$period?/$objective?/$organisation?"(controller:"dashboard")
		
		"/cost/$action/$period?/$objective?/$organisation?"(controller:"cost")

		"/dsr/$action/$period?/$objective?/$organisation?"(controller:"dsr")
	
		"/maps/view"(controller:"maps", action:"view")
		
		"/maps/map/$period?/$organisation?/$level?/$target?"(controller:"maps", action: "map")
		
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
