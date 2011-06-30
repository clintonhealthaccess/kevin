package org.chai.kevin.chart

import org.chai.kevin.AbstractReportController;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.Data;

class ChartController extends AbstractReportController {

	def chartService
	
	
	def chart = {
		if (log.isDebugEnabled()) log.debug("chart.chart, params:"+params)
	
		Organisation organisation = getOrganisation(false)
		Data data = getData()
		
		Chart chart = chartService.getChart(data, organisation)
		
		if (log.isDebugEnabled()) log.debug("displaying chart: "+chart)
		render(contentType:"text/json", text:'{"result":"success","chart":'+chart.toJson()+'}');
	}
	
}
