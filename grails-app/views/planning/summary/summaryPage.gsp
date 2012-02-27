<%@ page import="org.chai.kevin.planning.PlanningSummaryPage" %>
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
			<div class="main">			
				<g:if test="${summaryPage == null}">
					<p class="help"><g:message code="planning.summary.selectplanningfacility.text" default="Please select a planning and a facility to get to the respective survey."/></p>
				</g:if>
				<g:else>
					<div>
						<table class="listing">
							<thead>
								<g:sortableColumn property="${PlanningSummaryPage.FACILITY_SORT}" title="${message(code: 'facility.label', default: 'Facility')}" params="${params}" defaultOrder="asc"/>
								<g:each in="${summaryPage.planningTypes}" var="planningType">
									<g:sortableColumn property="planning-${planningType.id}" title="${i18n(field:planningType.namesPlural)}" params="${params}" defaultOrder="desc"/>
								</g:each>
								<th></th>
							</thead>
							<tbody>
								<g:each in="${summaryPage.dataEntities}" var="dataEntity">
									<tr>
										<td><g:i18n field="${dataEntity.names}"/></td>
										<g:each in="${summaryPage.planningTypes}" var="planningType">
											<td>${summaryPage.getNumberOfEntries(dataEntity, planningType)}</td>
										</g:each>
										<td>
											<ul class="horizontal">
												<li>
													<a href="${createLink(controller:'editPlanning', action:'overview', params:[location: dataEntity.id, planning: currentPlanning.id])}">
														To planning tool
													</>
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