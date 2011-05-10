package org.chai.kevin

import geb.Page;

abstract class KevinPage extends Page {

	static content = {
		header { module HeaderModule }
	}
	
}
