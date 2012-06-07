/**
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
package org.chai.kevin.imports;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean Kahigiso M.
 *
 */
public class ImporterErrorManager {
	
	String currentFileName;
	Integer numberOfSavedRows = 0;
	Integer numberOfUnsavedRows = 0;
	Integer numberOfRowsSavedWithError = 0;
	List<ImporterError> errors = new ArrayList<ImporterError>();

	public String getCurrentFileName() {
		return currentFileName;
	}

	public void setCurrentFileName(String currentFileName) {
		this.currentFileName = currentFileName;
	}
	
	public Integer getNumberOfSavedRows() {
		return numberOfSavedRows;
	}
	
	public void setNumberOfSavedRows(Integer numberOfSavedRows) {
		this.numberOfSavedRows = numberOfSavedRows;
	}
	public Integer getNumberOfUnsavedRows() {
		return numberOfUnsavedRows;
	}
	public void setNumberOfUnsavedRows(Integer numberOfUnsavedRows) {
		this.numberOfUnsavedRows = numberOfUnsavedRows;
	}
	public Integer getNumberOfRowsSavedWithError() {
		return numberOfRowsSavedWithError;
	}
	public void setNumberOfRowsSavedWithError(Integer numberOfRowsSavedWithError) {
		this.numberOfRowsSavedWithError = numberOfRowsSavedWithError;
	}
	public List<ImporterError> getErrors() {
		return errors;
	}
	public void setErrors(List<ImporterError> errors) {
		this.errors = errors;
	}
	
	public void incrementNumberOfSavedRows (){
		this.setNumberOfSavedRows(this.getNumberOfSavedRows()+1);
	}
	public void incrementNumberOfUnsavedRows(){
		this.setNumberOfUnsavedRows(this.getNumberOfUnsavedRows()+1);
	}
	public void incrementNumberOfRowsSavedWithError(Integer value){
		this.setNumberOfRowsSavedWithError(this.getNumberOfRowsSavedWithError()+value);
	}
	

}
