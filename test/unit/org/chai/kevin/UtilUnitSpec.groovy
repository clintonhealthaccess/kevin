package org.chai.kevin;

import java.text.ParseException;

import grails.plugin.spock.UnitSpec

import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;
import org.codehaus.groovy.control.MultipleCompilationErrorsException

public class UtilUnitSpec extends UnitSpec {

	def "test for strip html"(){
		when:
		String htmlString = "Vaccination<br>";
		String noHtmlString = "Vaccination";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
		
		when:
		htmlString = "<p class=\"p1\">CHW Supervision</p>";
		noHtmlString = "CHW Supervision";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
		
		when:
		htmlString = "<div>Accountant</div><div><br></div>";
		noHtmlString = "Accountant";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
		
		when:
		htmlString = "Eye Problems&nbsp;(trauma, cataract, other)";
		noHtmlString = "Eye Problems (trauma, cataract, other)";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
		
		when:
		htmlString = "Bone &amp; Joint Diseases";
		noHtmlString = "Bone & Joint Diseases";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
		
		when:
		htmlString = "Children 14 Years<br>Old &amp; Younger";
		noHtmlString = "Children 14 Years Old & Younger";
		htmlString = Utils.stripHtml(htmlString);
		
		then:
		htmlString.equals(noHtmlString);
	}
	
	def "get string value for value with number, percentage format, and rounded to 2 places"() {
		
		when:
		def value = Value.VALUE_NUMBER(0.579)
		def type = Type.TYPE_NUMBER()
		def format = "#%"
		def rounded = 2
		def stringValue = Utils.getStringValue(value, type, null, format, null, rounded)
		
		then:
		stringValue == "58%"
		
	}
	
	def "type builder"() {
		
		expect:
		Utils.buildType("type {string}") == Type.TYPE_STRING()
		Utils.buildType("type {enume 'TheGreatEnum'}") == Type.TYPE_ENUM('TheGreatEnum')
		
		Utils.buildType('''
			type { list type { string } }
		''') == Type.TYPE_LIST(Type.TYPE_STRING())
		Utils.buildType('''
			type { map 
				first_name: type {string} 
			}
		''') == Type.TYPE_MAP(['first_name': Type.TYPE_STRING()])
		Utils.buildType('''
			type { list type { map 
				first_name: type {string}, birthday: type {date} 
			}}
		''') == Type.TYPE_LIST(Type.TYPE_MAP(['first_name': Type.TYPE_STRING(), 'birthday': Type.TYPE_DATE()]))
		Utils.buildType("type {enume 'TheGreatEnum'}") == Type.TYPE_ENUM('TheGreatEnum')
		Utils.buildType('''
			type { map 
				first_name: type { string }, 
				birthday: type { date },
				box 
			}
		''') == Type.TYPE_MAP(['first_name': Type.TYPE_STRING(), 'birthday': Type.TYPE_DATE()], true)
		Utils.buildType('''
			type { map box,
				first_name: type { string },
				birthday: type { date }
			}
		''') == Type.TYPE_MAP(['first_name': Type.TYPE_STRING(), 'birthday': Type.TYPE_DATE()], true)
	}
	
	def "type builder with non-map argument first throws exception"() {
		when:
		Utils.buildType('''
			type { map box first_name: type { string } }
		''')
		
		then:
		thrown MultipleCompilationErrorsException
		
		expect:
		Utils.buildType('''
			type { map box, 
				first_name: type { string },
				last_name: type { string }
			}
		''') == Type.TYPE_MAP(['first_name': Type.TYPE_STRING(), 'last_name': Type.TYPE_STRING()], true)
	}
	
	def "type builder with non existing method call"() {
		when:
		Utils.buildType('''
			type { no_method }
		''')
		
		then:
		thrown MissingPropertyException
	}
	
}
