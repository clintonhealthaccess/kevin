package org.chai.kevin;

public class ProgressListener {

	private int total;
	private int current;
	private boolean stop = false;
	
	public void stop() {
		this.stop = true;
	}
	
	public boolean isInterrupted() {
		return stop;
	}
	
	public int getCurrent() {
		return current;
	}
	
	public void increment() {
		current++;
	}
	
	public void setTotal(int total) {
		this.total = total;
	}
	
}
