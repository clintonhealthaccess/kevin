<%@ page import="org.chai.kevin.planning.PlanningSummaryPage" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.summary.title" /></title>
		
		<r:require modules="progressbar,dropdown,explanation,survey"/>
	</head>
	<body>

		<div>
			<div class="filter-bar">
				<g:render template="/planning/summary/planningFilter"/>
				<g:locationFilter linkParams="${[planning: currentPlanning?.id, order:'desc']}" selected="${currentLocation}"/>
			</div>
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="nav-help"><g:message code="planning.summary.selectplanninglocation.text"/></p>
				</g:if>
				<g:else>
					<div>
						<table class="listing">
							<thead>
								<g:sortableColumn property="${PlanningSummaryPage.LOCATION_SORT}" title="${message(code: 'location.label')}" params="${params}" defaultOrder="asc"/>
								<g:each in="${summaryPage.planningTypes}" var="planningType">
									<g:sortableColumn property="planning-${planningType.id}" title="${i18n(field:planningType.namesPlural)+' '}" params="${params}" defaultOrder="desc"/>
								</g:each>
								<th></th>
							</thead>
							<tbody>
								<g:each in="${summaryPage.dataLocations}" var="dataLocation">
									<tr>
										<td><g:i18n field="${dataLocation.names}"/></td>
										<g:each in="${summaryPage.planningTypes}" var="planningType">
											<td>${summaryPage.getNumberOfEntries(dataLocation, planningType)}</td>
										</g:each>
										<td>
											<ul class="horizontal">
												<li>
													<a href="${createLink(controller:'editPlanning', action:'overview', params:[location: dataLocation.id, planning: currentPlanning.id])}">
														<g:message code="planning.summary.link"/>
													</a>
												</li>
											</ul>
										</td>
									</tr>			
								</g:each>
							</tbody>
						</table>
					</div>
				</g:else>
			</div>			
		</div>
	</body>
</html>