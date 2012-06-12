<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<table>
	<tbody>
		<g:each in="${planningType.getGroups(currentGroups)}" var="group">
			<g:set var="newGroups" value="${currentGroups.clone() << group}"/>
			<tr class="tree-sign js_foldable">
				<td class="js_foldable-toggle">
					<span style="margin-left: ${margin}px;">
						${group} 
					</span>
				</td>
				<td>
					<g:formatNumber number="${budgetPlanningEntry.getIncoming(planningType.getPlanningCosts(newGroups))}" format="#,###"/>
				</td>
				<td>
					<g:formatNumber number="${budgetPlanningEntry.getOutgoing(planningType.getPlanningCosts(newGroups))}" format="#,###"/>
				</td>
				<td>
					<g:formatNumber number="${budgetPlanningEntry.getDifference(planningType.getPlanningCosts(newGroups))}" format="#,###"/>
				</td>
				<td class="status"></td>
			</tr>
		
			<tr class="sub-tree js_foldable-container hidden">
				<td colspan="7" class="bucket">
					<g:render template="/planning/budget/costs" model="[budgetPlanningEntry: budgetPlanningEntry, planningType: planningType, margin: margin+20, currentGroups: newGroups]"/>
				</td>
			</tr>
		</g:each>

		<g:each in="${planningType.getPlanningCostsInGroup(currentGroups)?}" var="planningCost">
			<g:set var="budgetCost" value="${budgetPlanningEntry.getBudgetCost(planningCost)}"/>
			<g:if test="${budgetCost == null || !budgetCost.hidden}">
				<tr>
					<td>
						<span style="margin-left: ${margin}px;">
							${budgetPlanningEntry.getDisplayName(planningCost)}
						</span>
					</td>
					<g:if test="${planningCost.type == PlanningCostType.OUTGOING}">
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
