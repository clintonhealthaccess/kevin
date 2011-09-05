package org.chai.kevin;

import org.chai.kevin.dashboard.ExpressionJob;

class RefreshController {
	
	def index = {
		ExpressionJob.triggerNow()
		
		render ("launched refresh job")
	}
	
}
