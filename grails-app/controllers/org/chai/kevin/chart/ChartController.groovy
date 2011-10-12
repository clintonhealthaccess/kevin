package org.chai.kevin.chart

import org.chai.kevin.AbstractController;
import org.chai.kevin.Organisation;
import org.chai.kevin.data.Data;

class ChartController extends AbstractController {

	def chartService
	def dataService
	
	def chart = {
		if (log.isDebugEnabled()) log.debug("chart.chart, params:"+params)
	
		Organisation organisation = organisationService.getOrganisation(params.int('organisation'))
		Data data = dataService.getData(Long.parseLong(params['data']))
		
		Chart chart = chartService.getChart(data, organisation)
		
		if (log.isDebugEnabled()) log.debug("displaying chart: "+chart)
		render(contentType:"text/json", text:'{"result":"success","chart":'+chart.toJson()+'}');
	}
	
}
