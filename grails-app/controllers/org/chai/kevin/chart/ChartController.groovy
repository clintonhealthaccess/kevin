package org.chai.kevin.chart

import org.chai.kevin.AbstractController;
import org.chai.kevin.data.Data;
import org.chai.kevin.location.DataLocationEntity;

class ChartController extends AbstractController {

	def chartService
	def dataService
	
	def chart = {
		if (log.isDebugEnabled()) log.debug("chart.chart, params:"+params)
	
		DataLocationEntity entity = DataLocationEntity.int('entity')
		Data data = dataService.getData(params.long('data'))
		
		Chart chart = chartService.getChart(data, location)
		
		if (log.isDebugEnabled()) log.debug("displaying chart: "+chart)
		render(contentType:"text/json", text:'{"result":"success","chart":'+chart.toJson()+'}');
	}
	
}
