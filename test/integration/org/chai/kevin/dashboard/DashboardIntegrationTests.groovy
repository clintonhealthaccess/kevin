package org.chai.kevin.dashboard

import org.chai.kevin.IntegrationTests;
import org.chai.kevin.reports.ReportIntegrationTests;
import org.chai.kevin.data.Type;
import org.chai.kevin.reports.ReportProgram;
import org.chai.kevin.value.Value
import org.hisp.dhis.period.Period;

abstract class DashboardIntegrationTests extends IntegrationTests {
	
	public static final String VALUE_STRING = "value";
	
	static def newDashboardPercentage(def value){
		def dashboardPercentage = new DashboardPercentage(new Value("{\""+VALUE_STRING+"\":"+value+"}"), null, null)
		return dashboardPercentage
	}	
	
	def getPercentage(def percentage) {
		if(percentage != null && percentage.isValid())
			return percentage.getRoundedValue();
		else
			return null;
	}
}