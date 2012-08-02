package org.chai.kevin.dashboard

/*
* Copyright (c) 2011, Clinton Health Access Initiative.
*
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*     * Redistributions of source code must retain the above copyright
*       notice, this list of conditions and the following disclaimer.
*     * Redistributions in binary form must reproduce the above copyright
*       notice, this list of conditions and the following disclaimer in the
*       documentation and/or other materials provided with the distribution.
*     * Neither the name of the <organization> nor the
*       names of its contributors may be used to endorse or promote products
*       derived from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
* ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
* WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
* DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
* LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
* (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import org.chai.kevin.task.Progress;
import org.quartz.InterruptableJob
import org.quartz.JobExecutionContext

class RefreshJob implements InterruptableJob {

	// we have to have this otherwise the job seems
	// to be executed at each request
	static triggers = {
		cron name: 'refreshTrigger', startDelay: 10000, cronExpression: "0 0 0 * * ?"
	}
	
	def sessionRequired = true
	def concurrent = false

	def refreshValueService
	
	void execute(JobExecutionContext context) {
		if (log.isInfoEnabled()) log.info('executing RefreshJob');
	
		refreshValueService.refreshNormalizedDataElements(new DummyProgress())
		refreshValueService.refreshCalculations(new DummyProgress())
		
		refreshValueService.flushCaches()
	}
	
	void interrupt() {}
}

class DummyProgress implements Progress {
	
	public void incrementProgress(){}
	public void incrementProgress(Long increment){}
	public Double retrievePercentage(){return null;}
	public void setMaximum(Long max){}
	public void abort() {}
	public boolean isAborted() {return false;}
	
}