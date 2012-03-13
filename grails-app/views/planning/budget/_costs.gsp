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
			<g:each in="${budgetPlanningEntry.getBudgetCosts(costType, section)}" var="budgetCost">
				<g:render template="/planning/budget/cost" model="[budgetCost: budgetCost, margin: '40px']"/>
			</g:each>
		</g:if>
		<g:else>
			<tr class="tree-sign js_foldable">
				<td class="js_foldable-toggle">
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
				<td class="status" title="Help message"></td>
			</tr>
			<tr class="sub-tree js_foldable-container hidden">
				<td colspan="7" class="bucket">
					<table class="condensed">
						<tbody>
							<g:each in="${budgetPlanningEntry.getBudgetCosts(costType, section)}" var="budgetCost">
								<g:render template="/planning/budget/cost" model="[budgetCost: budgetCost, margin: '50px']"/>
							</g:each>	
						</tbody>
					</table>
				</td>
			</tr>
		</g:else>
	</g:each>
</g:if>