<%@ page import="org.apache.shiro.SecurityUtils" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="planning">
			<div class="main">  
			
			<g:render template="/planning/planningTabs" model="[planning: planningType.planning, location: location, selected: "undertakings"]"/>
	    	<g:render template="/templates/help" model="[content: i18n(field: planningType.newHelps)]"/>
				
			<div>
				<shiro:hasPermission permission="admin">
					<div class="right"><a href="#" onclick="$('.admin-hint').toggle();return false;">Toggle element information</a></div>
				</shiro:hasPermission>
			
				<g:form url="[controller:'editPlanning', action:'save', params: [location: location.id, planningType: planningType.id, targetURI: targetURI]]">
	  				<input class="js_always-send" type="hidden" name="lineNumber" value="${planningEntry.lineNumber}"/>
	
					<div id="element-${planningType.formElement.id}">
		  				<g:each in="${planningType.sections}" var="section" status="i">
		  					<div id="section-${section}" class="question ${planningEntry.invalidSections.contains(section)?'invalid':''} ${planningEntry.incompleteSections.contains(section)?'incomplete':''}">
			  					<div class="clearfix">
			  						<h4 class="nice-title"> 
			  							<span class="nice-title-image">${i+1}</span>
			  							<g:i18n field="${planningType.formElement.headers[section]}"/>
			  						</h4>
			  					</div>
								
			  					<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
									value: planningEntry.getValue(section),
									lastValue: null,
									type: planningType.getType(section), 
									suffix: planningEntry.getPrefix(section),
									headerSuffix: section,
									
									// get rid of those in the templates??
									element: planningType.formElement,
									validatable: planningEntry.validatable,
									
									readonly: readonly,
									enums: planningEntry.enums,
									showHints: SecurityUtils.subject.isPermitted('admin')
								]"/>
								
								<div class="adv-aside help-container">
									<div class="help"><g:i18n field="${planningType.sectionDescriptions[section]}"/></div>
								</div>
							</div>
		  				</g:each>
	  				</div>
					<ul class=" form-actions clearfix">
						<li>
		  					<button type="submit" class="loading-disabled">
		  						<g:message code="planning.new.save"/>
		  					</button>
	  					</li>
	  					<li>
		  					<button type="cancel" class="hidden">
							    <g:message code="survey.section.cancel.label"/>
						    </button>
						  </li>
						  <li>
		  					<a class="go-back" href="${createLink(uri: targetURI)}">
		  						<g:message code="planning.new.backtolisting"/>
		  					</a>
		  				</li>
	  				</div>
	  				<br />
  				</g:form>
			</div>
		</div>
		
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#planning'),
					callback: function(dataEntry, data, element) {
						$.each(data.sections, function(index, value) {
							if (value.complete == true) $(escape('#section-'+value.section)).removeClass('incomplete')
							else $(escape('#section-'+value.section)).addClass('incomplete')
							
							if (value.invalid == false) $(escape('#section-'+value.section)).removeClass('invalid')
							else $(escape('#section-'+value.section)).addClass('invalid')
						});
					},
					url: "${createLink(controller:'editPlanning', action:'saveValue', params: [location: location.id, planningType: planningType.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		</r:script>
	</body>
</html>