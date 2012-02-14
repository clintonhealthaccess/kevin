<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push"/>
			<div id="planning">
				<div class="main">  
	
				<ul class="horizontal" id="tab-nav">
					<li><a class="${selected=='undertakings'?'selected':''}" href="${createLink(controller:'planning', action:'overview', params:[planning: planningType.planning.id, location: location.id])}">Undertakings</a></li>
					<li><a class="selected" href="#">New <g:i18n field="${planningType.names}"/></a></li>
					<li><a class="${selected=='budget'?'selected':''}" href="${createLink(controller:'planning', action:'budget', params:[planning: planningType.planning.id, location: location.id])}">Projected Budget</a></li>
				</ul>
				    
		    	<!-- TODO tips could go into a template -->
				<p class="show-question-help moved"><a href="#">Show Tips</a></p>
				<div class="question-help-container">
					<div class="question-help push-20">
						<a class="hide-question-help" href="#">Close tips</a>Some help information for the Performance tab
					</div>
				</div>
					
				<div id="questions">
					<g:form url="[controller:'planning', action:'save', params: [location: location.id, planningType: planningType.id, period: period.id]]">
		  				<input class="always-send" type="hidden" name="lineNumber" value="${planningLine.lineNumber}"/>
		
		  				<g:each in="${planningType.sections}" var="section" status="i">
		  					<div class="section-title-wrap"><h4 class='section-title'> <span class='question-default'> ${i+1} </span><g:i18n field="${planningType.headers[section]}"/></h4></div>
		
		  					<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
								value: planningLine.getValue(section),
								lastValue: null,
								type: planningType.getType(section), 
								suffix: planningLine.getPrefix(section),
								headerSuffix: section,
								
								// get rid of those in the templates??
								element: planningType,
								validatable: planningLine.validatable,
								
								readonly: readonly,
								enums: planningLine.enums
							]"/>
							
							<div class="adv-aside question-help-container">
								<div class="question-help"><g:i18n field="${planningType.sectionDescriptions[section]}"/></div>
							</div>
		  				</g:each>
		  				
		  				<input type="submit" value="Submit">
		  				<a class="next gray medium" href="#">
		  					Return to listing
		  				</a>
	  				</g:form>
				</div>
			</div>
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