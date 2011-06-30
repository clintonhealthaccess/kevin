<%@ page import="org.chai.kevin.value.ExpressionValue.Status" %>

<div class="info">	
	<g:if test="${info.calculationValue.hasMissingValues}">
		<div class="red bold">Some values are missing.</div>
	</g:if>
	
	<div class="average">
		<h5>Average value:</h5>
		<span class="value">
			<g:if test="${info.number}">
				<g:formatNumber number="${info.calculationValue.average * 100}" format="#0.0"/>%
			</g:if>
			<g:else>N/A</g:else>
		</span>
		<div class="clear"></div>
	</div>
	
	<div>
		<h5><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></h5>
		<div class="box span">
			<g:if test="${info.groups != null}">
				<g:each in="${info.groups}" var="groupOrganisation">
<!-- 					<g:set var="" value=""/> -->
<!-- 					<g:if test=""> -->
						<a href="#" onclick="$(this).next().slideToggle(); return false;">
							${groupOrganisation.name}
						</a>
						<g:render template="/info/organisations" model="[expressionValues: info.getExpressionValuesForGroup(groupOrganisation)]"/>
						<div class="clear"></div>
<!-- 					</g:if> -->
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
			<h5>
				<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Expression for ${groups.getGroupByUuid(groupUuid).name}</a>
			</h5>
			<div class="box span hidden">
				<div class="expression">
					<a href="#" class="cluetip" title="${i18n(field: expression.names)}" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:expression.id)}"><g:i18n field="${expression.names}"/></a>
				</div>
				<div class="equation">
					<h5>Equation</h5>
					<div>
						<div class="formula"><g:expression expression="${expression}"/></div>
					</div>
				</div>
			</div>
			<div class="clear"></div>
		</g:each>
	</div>

	<div>
		<h5>
			<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Trend</a>
		</h5>
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
