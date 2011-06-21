package org.chai.kevin.dashboard

import org.quartz.JobExecutionContext;
import org.quartz.InterruptableJob;

class ExpressionJob implements InterruptableJob {

	static triggers = {
		simple name: 'trigger', startDelay: 10000, repeatInterval: 20000  
	}
	
	def sessionRequired = true
	def concurrent = false
	
	def expressionService
	
	void execute(JobExecutionContext context) {
		if (log.isInfoEnabled()) log.info('executing ExpressionJob');
	
		expressionService.refreshExpressions()
		expressionService.refreshCalculations()
	}
	
	void interrupt() {}
}
