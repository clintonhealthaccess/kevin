<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="planning.new.label" /></title>
		
		<r:require module="planning"/>
	</head>
	<body>
	
		<table>
			<g:each in="${budgetPlanningTypes}" var="budgetPlanningType">
				<tr>
					<td><g:i18n field="${budgetPlanningType.names}"/></td>
					<td>${budgetPlanningType.outgoing}</td>
					<td>${budgetPlanningType.incoming}</td>
					<td>${budgetPlanningType.difference}</td>
				</tr>
				
				<g:each in="${budgetPlanningType.budgetPlanningLines}" var="budgetPlanningLine">
					<tr>
						<td>${budgetPlanningLine.names}</td>
						<td>${budgetPlanningLine.outgoing}</td>
						<td>${budgetPlanningLine.incoming}</td>
						<td>${budgetPlanningLine.difference}</td>
					</tr>
					
					<!-- OUTGOING -->
					<tr>
						<td>Outgoing</td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<g:each in="${budgetPlanningLine.getGroupSections(PlanningCostType.OUTGOING)}" var="section">
						<tr>
							<td>${section}</td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					
						<g:each in="${budgetPlanningLine.getBudgetCosts(PlanningCostType.OUTGOING, section)}" var="cost">
							<tr>
								<td>${cost.names}</td>
								<td></td>
								<td>${cost.value}</td>
								<td></td>
							</tr>
						</g:each>
					</g:each>
					
					<!-- INCOMING -->
				</g:each>
			</g:each>
		</table>
	
	</body>
</html>