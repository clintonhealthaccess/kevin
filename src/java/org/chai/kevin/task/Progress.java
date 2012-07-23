package org.chai.kevin.task;

public interface Progress {

	public void incrementProgress();
	
	public Double retrievePercentage();
	
	public void setMaximum(Integer max);
	
}
