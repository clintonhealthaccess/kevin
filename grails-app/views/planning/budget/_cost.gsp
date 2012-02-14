<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<tr>
	<td>
		<span style="margin-left: ${margin};">
			<g:i18n field="${budgetCost.planningCost.names}"/>
		</span>
	</td>
	<g:if test="${costType == PlanningCostType.INCOMING}">
		<td>-</td>
	</g:if>
	<td>${budgetCost.value}</td>
	<g:if test="${costType == PlanningCostType.OUTGOING}">
		<td>-</td>
	</g:if>
	<td>-</td>
	<td></td>
</tr>
