<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
<<<<<<< HEAD
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
					
				<g:if test="${planningEntry.submitted}">
	  				<a class="next gray medium" href="${createLink(controller:'planning', action:'unsubmit', params: [location: location.id, planningType: planningType.id, lineNumber: planningEntry.lineNumber, targetURI: targetURI])}">
	  					Remove from budget
	  				</a>
				</g:if>
					
				<div id="questions">
					<g:form url="[controller:'planning', action:'submit', params: [location: location.id, planningType: planningType.id, targetURI: targetURI]]">
		  				<input class="js_always-send" type="hidden" name="lineNumber" value="${planningEntry.lineNumber}"/>
		
		  				<g:each in="${planningType.sections}" var="section" status="i">
		  					<div id="section-${section}" class="${planningEntry.invalidSections.contains(section)?'invalid':''} ${planningEntry.incompleteSections.contains(section)?'incomplete':''}">
			  					<div class="section-title-wrap">
			  						<h4 class="section-title"> 
			  							<span class="question-default">${i+1}</span>
			  							<g:i18n field="${planningType.headers[section]}"/>
			  						</h4>
			  					</div>
			
			  					<g:render template="/survey/element/${planningType.getType(section).type.name().toLowerCase()}"  model="[
									value: planningEntry.getValue(section),
									lastValue: null,
									type: planningType.getType(section), 
									suffix: planningEntry.getPrefix(section),
									headerSuffix: section,
									
									// get rid of those in the templates??
									element: planningType,
									validatable: planningEntry.validatable,
									
									readonly: readonly,
									enums: planningEntry.enums
								]"/>
								
								<div class="adv-aside question-help-container">
									<div class="question-help"><g:i18n field="${planningType.sectionDescriptions[section]}"/></div>
								</div>
							</div>
		  				</g:each>
              			<div class="clearfix">
  		  					<button type="submit" class="loading-disabled">
  		  						<g:if test="${!planningEntry.submitted}">
  		  							Accept in budget
  		  						</g:if>
  		  						<g:else>
  		  							Update budget
  		  						</g:else>
  		  					</button>
  		  					
  		  					<button type="cancel" class="hidden">
								<g:message code="survey.section.cancel.label" default="Cancel"/>
							</button>
  		  					<a class="next gray medium" href="${createLink(uri: targetURI)}">
  		  						Return to listing
  		  					</a>
		  				</div>
	  				</g:form>
				</div>
=======
		<div id="planning">
			<div class="main" id="questions">  
				<g:form url="[controller:'planning', action:'save', params: [location: location.id, planningType: planningType.id, period: period.id]]">
  				<input class="always-send" type="hidden" name="lineNumber" value="${planningLine.lineNumber}"/>

  				<g:each in="${planningType.sections}" var="section">
  					<h4 class='section-title'> <span class='question-default'> 1 </span><g:i18n field="${planningType.headers[section]}"/></h4>

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
					
					<div>
						<g:i18n field="${planningType.sectionDescriptions[section]}"/>
					</div>
  				</g:each>
  				
  			</g:form>
>>>>>>> importer
			</div>
		</div>
		
		<r:script>
			$(document).ready(function() {
				${render(template:'/templates/messages')}
			
				new DataEntry({
					element: $('#planning'),
<<<<<<< HEAD
					callback: function(dataEntry, data, element) {
						$.each(data.sections, function(index, value) {
							if (value.complete == true) $(escape('#section-'+value.section)).removeClass('incomplete')
							else $(escape('#section-'+value.section)).addClass('incomplete')
							
							if (value.invalid == false) $(escape('#section-'+value.section)).removeClass('invalid')
							else $(escape('#section-'+value.section)).addClass('invalid')
						});
					},
					url: "${createLink(controller:'planning', action:'saveValue', params: [location: location.id, planningType: planningType.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
				
=======
					callback: function() {},
					url: "${createLink(controller:'planning', action:'saveValue', params: [location: location.id, planningType: planningType.id, period: period.id])}", 
					messages: messages,
					trackEvent: ${grails.util.Environment.current==grails.util.Environment.PRODUCTION}
				});
>>>>>>> importer
			});
		</r:script>
	</body>
</html>