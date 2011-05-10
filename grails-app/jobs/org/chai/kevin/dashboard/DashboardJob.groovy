package org.chai.kevin.dashboard

import org.hisp.dhis.period.Period;
import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;

class DashboardJob implements InterruptableJob {
	static triggers = {}
	
	def sessionRequired = true
	
	def dashboardService
	def organisationService
	def progress
	
	void execute(JobExecutionContext context) {
		if (log.isInfoEnabled()) log.info('executing DashboardJob, jobmap: '+context.mergedJobDataMap);
				
		def organisation = organisationService.getOrganisation(context.mergedJobDataMap.get('organisation'));
		def objective = DashboardObjective.get(context.mergedJobDataMap.get('objective'));
		def period = Period.get(context.mergedJobDataMap.get('period'));
		progress = context.mergedJobDataMap.get('progress');
		
		if (log.isInfoEnabled()) log.info('about to refresh dashboard, period: '+period+', objective: '+objective+', organisation: '+organisation)
		dashboardService.refreshDashboard(
			organisation,
			objective,
			period,
			progress
		)
	}
	
	void interrupt() {
		progress.stop();
	}
}
