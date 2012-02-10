<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" default="District Health System Portal" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push">
			<div class="wrapper">
				<div id="report">

					<div class="main">	
						<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: 'undertakings']"/>
						<g:render template="/planning/undertakingsTabs" model="[planning: planning, location: location, selected: planningType.id]"/>
        
        				<!-- TODO tips could go into a template -->
						<p class="show-question-help moved"><a href="#">Show Tips</a></p>
						<div class="question-help-container">
							<div class="question-help push-20"> <a class="hide-question-help" href="#">Close tips</a> Some help information for the Performance tab </div>
						</div>
        
						<ul class="clearfix" id="questions">
							<li class="question push-20">
								<a href="${createLink(controller:'planning', action:'editPlanningEntry', params:[planningType: planningType.id, location: location.id, lineNumber: planningList.nextLineNumber])}" class="next medium gray right">
									Create New <g:i18n field="${planningType.names}"/>
								</a>
								<h4 class="section-title">
									<span class="question-default">
										<r:img uri="/images/icons/star_small.png"/>
									</span>
									Manage <g:i18n field="${planningType.namesPlural}"/>
								</h4>
								
								<div class="table-wrap">
									<table class="listing">
										<thead>
											<tr>
												<th></th>
												<th><g:i18n field="${planningType.names}"/></th>
												<g:each in="${planningType.getValuePrefixes(section)}" var="prefix">
													<th><g:i18n field="${planningType.getHeaders()[prefix]}"/></th>
												</g:each>
												<th></th>
											</tr>
										</thead>
										<tbody>
										
											<g:each in="${planningList.planningEntries}" var="entry">
										
												<tr>
													<td class="status pos neg"></td>
													<td>
														<a href="#"><g:i18n field="${entry.names}"/></a>
													</td>
													<g:each in="${planningType.getValuePrefixes(section)}" var="prefix">
														<td>${entry.getValue(prefix)}</td>
													</g:each>
													
													<td><a class="edit-link" href="#">edit</a><a class="delete-link" href="#">delete</a></td>
												</tr>
											
											</g:each>
										</tbody>
									</table>

									<ul class="table-nav">
										<g:each in="${planningType.sections}" var="sectionIt" status="i">
											<li>
												<a class="${section==sectionIt?'selected':''}" href="${createLink(controller:'planning', action:'planningList', params:[location:location.id, section:i, planningType: planningType.id])}">
													<g:i18n field="${planningType.headers[sectionIt]}"/>
												</a>
											</li>
										</g:each>
									</ul>
								</div>
							</li>
						</ul>
        
					</div>
				</div>
			</div>
		</div>

	</body>
</html>