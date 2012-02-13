<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="survey.summaryPage.label" default="District Health System Portal" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>

		<div>
			<div class="subnav">
				<g:render template="/planning/summary/planningFilter"/>
				<g:locationFilter linkParams="${[planning: currentPlanning?.id, order:'desc']}" selected="${currentLocation}"/>
			</div>
			
			
		</div>
	</body>
</html>