package org.chai.kevin;


public abstract class Gradient {

	public abstract Double getValue();
	
	public boolean isValid() {
		return getValue() != null && !getValue().isNaN() && getValue() != -1;
	}
	
	public Integer getRoundedValue() {
		return new Double(getValue() * 100).intValue();
	}
	
	public String getColor() {
//		.targetna
//		  background-color: #AAA
//
//		.target0
//		  background-color: #fd7272
//
//		.target0:hover
//		  background-color: #fc0404
//
//		.target1
//		  background-color: #fd8472
//
//		.target1:hover
//		  background-color: #fc2704
//
//		.target2
//		  background-color: #fd9772
//
//		.target2:hover
//		  background-color: #fc4404
//
//		.target3
//		  background-color: #fdaa72
//
//		.target3:hover
//		  background-color: #fc6704
//
//		.target4
//		  background-color: #9afba5
//
//		.target4:hover
//		  background-color: #0af626
//
//		.target5
//		  background-color: #79fa88
//
//		.target5:hover
//		  background-color: #0af626
		if (!isValid()) return "#AAA";
		
		switch (new Double(getValue() / 0.2d).intValue()) {
		case 0:
			return "#fd7272";
		case 1:
			return "#fd8472";
		case 2:
			return "#fd9772";
		case 3:
			return "#fdaa72";
		case 4:
			return "#9afba5";
		case 5:
			return "#79fa88";
		default:
			return "#AAA";
		}
	}
	
}
