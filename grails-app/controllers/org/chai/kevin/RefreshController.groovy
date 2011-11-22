package org.chai.kevin;

import org.chai.kevin.dashboard.RefreshJob;

class RefreshController {
	
	def index = {
		RefreshJob.triggerNow()
		
		render ("launched refresh job")
	}
	
}
