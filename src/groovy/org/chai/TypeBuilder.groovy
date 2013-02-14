package org.chai

/* type {
  list type { map [
    "test": type { string }

    ]
  }

	for information about dsl :
	DSL tutorial : http://java.dzone.com/articles/groovy-dsl-simple-example
	DSL slideshow : http://glaforge.appspot.com/article/groovy-domain-specific-languages-in-chicago
	Imports in shell : http://mrhaki.blogspot.com/2011/06/groovy-goodness-add-imports.html
} */

import org.chai.kevin.data.Type;

class TypeBuilder {

	def getString() {
		return Type.TYPE_STRING();
	}
	
	def getNumber() {
		return Type.TYPE_NUMBER();
	}
	
	def getText() {
		return Type.TYPE_TEXT();
	}
	
	def getBool() {
		return Type.TYPE_BOOL();
	}
	
	def getDate() {
		return Type.TYPE_DATE();
	}

	def enume(def code) {
		return Type.TYPE_ENUM(code);
	}

	def list(def type) {
		return Type.TYPE_LIST(type);
	}

	def map(Map<String, Type> map) {
		return Type.TYPE_MAP(map)
	}

	/**
	 * example:
	   type { map box
			first_name: type {string}
		}
		is equivalent to
		type { map ( box, first_name: type {string}} )
	 */
	def map(Map<String, Type> map, boolean box) {
		return Type.TYPE_MAP(map, box)
	}
		
	def build(def closure) {
		// delegates all methods calls in the closure
		// to current object (this)
		closure.delegate = this
		def type = closure()
		return type
	}
	
}