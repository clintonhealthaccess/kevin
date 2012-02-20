<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<tr>
	<td>
		<span style="margin-left: ${margin};">
			<a class="js_budget-section-link" 
				href="${createLink(controller:'planning', action:'editPlanningSection', params:[location:location.id, planningType:planningType.id, lineNumber: budgetCost.planningEntry.lineNumber, section: budgetCost.planningCost.section])}">
				<g:i18n field="${budgetCost.planningCost.names}"/>
			</a>
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
	<td class="status" title="Help message"></td>
</tr>
