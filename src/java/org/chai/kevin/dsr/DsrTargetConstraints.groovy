package org.chai.kevin.dsr

import org.chai.kevin.Expression;
import org.chai.kevin.dsr.DsrObjective;
import org.chai.kevin.dsr.DsrTargetCategory;

constraints = {
	code(nullable: false, blank: false, unique: true)
	expression (nullable: false)
	objective (nullable: false)
}
