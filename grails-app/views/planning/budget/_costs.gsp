<%@ page import="org.chai.kevin.planning.PlanningCost.PlanningCostType" %>

<g:if test="${!budgetPlanningEntry.getGroupSections(costType).empty}">
	<tr>
		<td colspan="100">
			<span style="margin-left: 30px;" class="sub-title">
				<g:message code="${costType.code}"/>
			</span>
		</td>
	</tr>
	<g:each in="${budgetPlanningEntry.getGroupSections(costType)}" var="section">
		<!-- 
			Title row: not displayed if the section is null
		-->
		<g:if test="${section == null}">
			<g:each in="${budgetPlanningEntry.getPlanningCosts(costType, section)}" var="planningCost">
				<g:render template="/planning/budget/cost" model="[budgetCost: budgetPlanningEntry.getBudgetCost(planningCost), margin: '40px']"/>
			</g:each>
		</g:if>
		<g:else>
			<tr class="tree_sign_minus">
				<td>
					<span style="margin-left: 40px;">
						<g:i18n field="${planningType.headers[section]}"/>
					</span>
				</td>
				<g:if test="${costType == PlanningCostType.INCOMING}">
					<td>-</td>
				</g:if>
				<td>${budgetPlanningEntry.getGroupTotal(costType, section)}</td>
				<g:if test="${costType == PlanningCostType.OUTGOING}">
					<td>-</td>
				</g:if>
				<td>-</td>
				<td></td>
				<td class="status invalid tooltip" title="Help message"></td>
			</tr>
			<tr style="display: table-row" class="sub_tree">
				<td colspan="7" class="bucket">
					<table class="condensed">
						<tbody>
							<g:each in="${budgetPlanningEntry.getPlanningCosts(costType, section)}" var="planningCost">
								<g:render template="/planning/budget/cost" model="[budgetCost: budgetPlanningEntry.getBudgetCost(planningCost), margin: '50px']"/>
							</g:each>	
						</tbody>
					</table>
				</td>
			</tr>
		</g:else>
	</g:each>
</g:if>