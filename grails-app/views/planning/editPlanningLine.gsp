<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="planning">
			<div class="main">  
				<p class="help">
					Welcome to the planning tool
				</p>
			</div>
			
			<g:form url="[controller:'planning', action:'save', params: [location: location.id, planningType: planningType.id, period: period.id]]">
				<input class="always-send" type="hidden" name="lineNumber" value="${planningLine.lineNumber}"/>
				
				<g:each in="${planningType.sections}" var="section">
					<g:i18n field="${planningType.headers[section]}"/>
	
					<g:render template="/survey/element/${planningType.getSectionType(section).type.name().toLowerCase()}"  model="[
						value: planningLine.getValue(section),
						lastValue: null,
						type: planningType.getSectionType(section), 
						suffix: '['+planningLine.lineNumber+']'+section,
						headerSuffix: section,
						
						// get rid of those in the templates??
						element: planningType,
						validatable: planningLine.validatable,
						
						readonly: readonly
					]"/>
					
				</g:each>
			</g:form>
		</div>
		
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#planning'),
					callback: function() {},
					url: "${createLink(controller:'planning', action:'saveValue', params: [location: location.id, planningType: planningType.id, period: period.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
			});
		</r:script>
	</body>
</html>