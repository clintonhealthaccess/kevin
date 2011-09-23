<%@ page import="org.chai.kevin.value.ExpressionValue.Status" %>

<div class="info">	
	<g:if test="${info.calculationValue.hasMissingValues}">
		<div class="red bold">Some values are missing.</div>
	</g:if>
	
	<div class="average">
		<span class="bold">Average value:</span>
		<span class="value">
			<g:if test="${info.value.numberValue}">
				<g:formatNumber number="${info.value.numberValue * 100}" format="#0.0"/>%
			</g:if>
			<g:else>N/A</g:else>
		</span>
		<div class="clear"></div>
	</div>
	
	<div>
		<span class="bold"><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></span>
		<div class="box span ${info.expressionValues.size()>10?'hidden':''}">
			<g:if test="${info.groups != null}">
				<g:each in="${info.groups}" var="groupOrganisation">
					<g:set var="expressionValues" value="${info.getExpressionValuesForGroup(groupOrganisation)}"/>
					<g:if test="${!expressionValues.isEmpty()}">
						<a href="#" onclick="$(this).next().slideToggle(); return false;">
							${groupOrganisation.name}
						</a>
						<g:render template="/info/organisations" model="[expressionValues: expressionValues]"/>
						<div class="clear"></div>
					</g:if>
				</g:each>
			</g:if>
			<g:else>
				<g:render template="/info/organisations" model="[expressionValues: info.expressionValues]"/>
			</g:else>
		</div>
	</div>

	<div>
		<g:each in="${info.calculation.expressions}" var="entry">
			<g:set var="groupUuid" value="${entry.key}"/>
			<g:set var="expression" value="${entry.value}"/>
			<span class="bold">
				<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Expression for ${groups.getGroupByUuid(groupUuid).name}</a>
			</span>
			<div class="box span hidden">
				<div class="expression">
					<a href="#" class="cluetip" title="${i18n(field: expression.names)}" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:expression.id)}"><g:i18n field="${expression.names}"/></a>
				</div>
				<div class="equation">
					<span class="bold">Equation</span>
					<div>
						<div class="formula"><g:expression expression="${expression}"/></div>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</g:each>
	</div>

	<div>
		<span class="bold">
			<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Trend</a>
		</span>
		<div class="span box hidden">
			<g:render template="/chart/chart" model="[data: info.calculation.id, organisation: info.organisation.id]"/>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
