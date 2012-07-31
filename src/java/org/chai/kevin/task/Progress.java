package org.chai.kevin.task;

public interface Progress {

	public void incrementProgress();
	public void incrementProgress(Long increment);
	
	public Double retrievePercentage();
	
	public void setMaximum(Long max);
	
}
