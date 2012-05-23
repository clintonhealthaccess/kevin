<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<table>
	<tbody>
		<g:each in="${planningType.costs}" var="planningCost">
			<g:set var="budgetCost" value="${budgetPlanningEntry.getBudgetCost(planningCost)}"/>
			<g:if test="${!budgetCost.hidden}">
				<tr>
					<td>
						<span style="margin-left: ${margin}px;">
							<g:i18n field="${planningCost.names}"/>
						</span>
					</td>
					<g:if test="${planningCost.type == PlanningCostType.OUTGOING}">
						<td>-</td>
					</g:if>
					<td><g:formatNumber number="${budgetCost?.value}" format="#,###"/></td>
					<g:if test="${planningCost.type == PlanningCostType.INCOMING}">
						<td>-</td>
					</g:if>
					<td>-</td>
					<td class="status"></td>
				</tr>
			</g:if>
		</g:each>
	</tbody>
</table>
