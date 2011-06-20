class UrlMappings {

	static mappings = {
		"/dashboard/$action/$period?/$objective?/$organisation?"(controller:"dashboard")
		
		"/cost/$action/$period?/$objective?/$organisation?"(controller:"cost")

		"/dsr/$action/$period?/$objective?/$organisation?"(controller:"dsr")
		
		"/survey/$action/$period?/$section?/$subsection?/$organisation?"(controller:"survey")
	
		"/maps/view"(controller:"maps", action:"view")
		
		"/maps/map/$period?/$organisation?/$level?/$target?"(controller:"maps", action: "map")
		
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		// temporary redirect to dsr
		//"/"(controller:"dsr", action:"index")
		"/"(controller:"survey", action:"index")
		"500"(view:'/error')
	}
}
