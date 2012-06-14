<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push"/>
			<div class="wrapper">
				<div class="main">
				
					<g:render template="/planning/planningTabs" model="[planning: planningOutput.planning, location: location, selected: "output-"+planningOutput.id]"/>
			    	<g:render template="/templates/help" model="[content: i18n(field: planningOutput.helps)]"/>
			    	
			    	<div id="questions">
			    		<div class="question push-20">	
			    			<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								<g:i18n field="${planningOutput.names}"/>: <g:i18n field="${location.names}"/>
							</h4>
							<div>
								<div class="table-wrap left clear">
									<g:table table="${outputTable}" nullText="message(code:'table.tag.header.none.entered')"/>
						    	</div>
						    </div>
					    </div>
			    	</div>
				</div>	
			</div>
		</div>
	</body>
</html>