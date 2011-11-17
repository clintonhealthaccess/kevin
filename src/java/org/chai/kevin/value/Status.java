package org.chai.kevin.value;

public enum Status {
	VALID, // OK
	MISSING_VALUE, // value is missing
	DOES_NOT_APPLY, // expression is missing for period or facility type
	MISSING_DATA_ELEMENT, // referenced data element in expression is missing
	ERROR // other error (typing, jaql error)
}