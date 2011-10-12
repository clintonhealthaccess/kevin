package org.chai.kevin;

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

public abstract class Gradient {

	public abstract Double getValue();
	
	public boolean isValid() {
		return getValue() != null && !getValue().isNaN() && !getValue().isInfinite() && getValue() != -1;
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
		if (!isValid()) return "#F6FDEB";
		
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
			return "#BBB";
		}
	}
	
}
