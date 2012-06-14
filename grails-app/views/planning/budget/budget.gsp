<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.budget.title" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
		<div id="content" class="push">
			<div class="wrapper">
				<div class="main">
					<g:render template="/planning/planningTabs" model="[planning: planning, location: location, selected: "budget"]"/>
	        		<g:render template="/templates/help" model="[content: i18n(field: planning.budgetHelps)]"/>
	        		
					<div id="questions">
						<div class="question push-20">
							<h4 class="section-title">
								<span class="question-default">
									<r:img uri="/images/icons/star_small.png"/>
								</span>
								<g:message code="planning.budget.budget"/>: <g:i18n field="${location.names}"/>
							</h4>
							<div class="budget">
								<g:if test="${emptyBudget}">
									<p class="context-message warning">
										<a href="${createLink(controller:'editPlanning', action:'overview', params:[location:location.id, planning:planning.id])}"><g:message code="planning.budget.enteractivity.link"/></a> <g:message code="planning.budget.enteractivity.instructions"/>
									</p>
								</g:if>
								<div class="table-wrap left clear">
									<g:table table="${budgetTable}" nullText="message(code:'table.tag.header.none.entered')"/>
									<br />
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>