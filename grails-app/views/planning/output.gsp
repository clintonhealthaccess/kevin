<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div class="main">
		
			<g:render template="/planning/planningTabs" model="[planning: planningOutput.planning, location: location, selected: "output-"+planningOutput.id]"/>
	    	<g:render template="/templates/help" model="[content: i18n(field: planningOutput.helps)]"/>
	    	
	    	<div>
	    		<div class="push-20">	
	    			<h4 class="nice-title">
						<span class="nice-title-image">
							<r:img uri="/images/icons/star_small.png"/>
						</span>
						<g:i18n field="${planningOutput.names}"/>: <g:i18n field="${location.names}"/>
					</h4>
					<div>
						<div class="left clear">
							<g:table table="${outputTable}" nullText="${message(code:'table.tag.header.none.entered')}"/>
				    	</div>
				    </div>
			    </div>
	    	</div>
		</div>	
	</body>
</html>