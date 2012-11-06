package org.chai.kevin.reports;

import org.chai.kevin.Exportable
import org.chai.kevin.util.Utils



class ReportProgram extends ReportEntity implements Exportable {

	ReportProgram parent;
	
	static hasMany = [children: ReportProgram]
	
	static mapping = {
		table 'dhsst_report_program'
		parent column: 'parent'
		children cache: true
	}
	
	static constraints =  {
		// TODO only one parent
		parent (nullable: true)
	}
	
	// this is for JAVA access, 
	// TODO find another way
	List<ReportProgram> getAllChildren() {
		return new ArrayList<ReportProgram>(children?:[]);
	}
	
	// TODO check this
	public <T extends ReportTarget> List<T> getReportTargets(Class<T> clazz) {
		def criteria = clazz.createCriteria()
		return criteria.list(cache: true){eq ("program", this)}
		if (log.debugEnabled) log.debug("collectReportTree(program="+program+",targets="+targets+")");
		return targets;
	}
	
	@Override
	public String toExportString() {
		return "[" + Utils.formatExportCode(getCode()) + "]";
	}
	
	@Override
	public String toString() {
		return "ReportProgram [code=" + getCode() + "]";
	}
	
}
