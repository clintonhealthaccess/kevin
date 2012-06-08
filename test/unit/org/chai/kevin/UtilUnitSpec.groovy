package org.chai.kevin;

import java.text.ParseException;

import grails.plugin.spock.UnitSpec

import org.chai.kevin.data.Type;
import org.chai.kevin.util.Utils;
import org.chai.kevin.value.Value;

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
	
	def "test contains id"() {
		expect:
		Utils.containsId("\$123", 123)
		!Utils.containsId("\$1234", 123)
		Utils.containsId("\$1 + \$2", 1)
		Utils.containsId("\$1 + \$2", 2)
		Utils.containsId("\$1+\$2", 1)
		Utils.containsId("\$1+\$2", 2)
		!Utils.containsId("1+2", 2)
		!Utils.containsId("1+2", 2)
		
		Utils.containsId("\$10218[_].test\n", 10218)
		Utils.containsId("\$10218\n", 10218)
		Utils.containsId(
			"(\$10218[_].basic.monthly_targets.july + \n" +
			"\$10218[_].basic.monthly_targets.august + \n" +
			"\$10218[_].basic.monthly_targets.september + \n" +
			"\$10218[_].basic.monthly_targets.october + \n" +
			"\$10218[_].basic.monthly_targets.november + \n" +
			"\$10218[_].basic.monthly_targets.december + \n" +
			"\$10218[_].basic.monthly_targets.january + \n" +
			"\$10218[_].basic.monthly_targets.february + \n" +
			"\$10218[_].basic.monthly_targets.march + \n" +
			"\$10218[_].basic.monthly_targets.april+ \n" +
			"\$10218[_].basic.monthly_targets.may + \n" +
			"\$10218[_].basic.monthly_targets.june) == \n" +
			"\$10218[_].basic.targets.target_number_of_cases", 10218)
	}
	
	def "test parse date"() {
		when:
		Utils.parseDate("12-rr-2011")
		
		then:
		thrown ParseException
		
	}
	def "test getStringValue"(){
		setup:
		boolean boolValue= true;
		def nowDate = new Date();
		
		def typeString = Type.TYPE_STRING();
		def typeDate = Type.TYPE_DATE();
		def typeNumber = Type.TYPE_NUMBER();
		def typeBool = Type.TYPE_BOOL();
		def typeEnum = Type.TYPE_ENUM();
	
		def valueBool = Value.VALUE_BOOL(boolValue);
		def valueString = Value.VALUE_STRING("Value Text");
		def valueNumber = Value.VALUE_NUMBER(100);
		def valueDate = Value.VALUE_DATE(nowDate);
		
		when:
		def string = Utils.getValueString(typeString,valueString);
		def number = Utils.getValueString(typeString,valueNumber);
		def date = Utils.getValueString(typeString,valueDate);
		def bool = Utils.getValueString(typeString,valueBool);
		def enumValue= Utils.getValueString(typeEnum,valueString);
		
		then:
		string.equals("Value Text");
		number.equals("100");
		bool.equals(boolValue.toString());
		enumValue.equals("Value Text")
		date.equals(Utils.formatDate(nowDate));	
		
			
	}
}
