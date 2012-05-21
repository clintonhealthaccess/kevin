<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<g:set var="budgetCost" value="${budgetPlanningEntry.getBudgetCost(planningCost)}"/>
<tr>
	<td>
		<span style="margin-left: 40px;">
			<g:i18n field="${planningCost.names}"/>
		</span>
	</td>
	<g:if test="${planningCost.type == PlanningCostType.INCOMING}">
		<td>-</td>
	</g:if>
	<td><g:formatNumber number="${budgetCost?.value}" format="#,###"/></td>
	<g:if test="${planningCost.type == PlanningCostType.OUTGOING}">
		<td>-</td>
	</g:if>
	<td>-</td>
	<td class="status"></td>
</tr>
