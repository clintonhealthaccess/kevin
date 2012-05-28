package org.chai.kevin.value;

public enum Status {
	VALID, // OK
	MISSING_EXPRESSION, // expression is missing for period or data location type
	MISSING_DATA_ELEMENT, // referenced data element in expression is missing
	ERROR // other error (typing, jaql error)
}