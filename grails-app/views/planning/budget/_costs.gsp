<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<table>
	<tbody>
		<g:each in="${planningType.costs}" var="planningCost">
			<g:set var="budgetCost" value="${budgetPlanningEntry.getBudgetCost(planningCost)}"/>
			<g:if test="${budgetCost == null || !budgetCost.hidden}">
				<tr>
					<td>
						<span style="margin-left: ${margin}px;">
							<g:i18n field="${planningCost.names}"/>
						</span>
					</td>
					<g:if test="${planningCost.type == PlanningCostType.INCOMING}">
						<td>-</td>
					</g:if>
					<td>
						<g:if test="${budgetCost != null}">
							<g:formatNumber number="${budgetCost?.value}" format="#,###"/>
						</g:if>
						<g:else>
							<a href="#" class="tooltip" title="${message(code: 'planning.budget.error.tooltip')}" onclick="return false;">?</a>
						</g:else>
					</td>
					<g:if test="${planningCost.type == PlanningCostType.OUTGOING}">
						<td>-</td>
					</g:if>
					<td>-</td>
					<td class="status"></td>
				</tr>
			</g:if>
		</g:each>
	</tbody>
</table>
