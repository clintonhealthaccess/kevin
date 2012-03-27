<%@ page import="org.chai.kevin.value.Status" %>

<div class="info">
	<g:if test="${info.expressionValue.status == Status.MISSING_VALUE}">
		<div class="red bold"><g:message code="info.normalizeddataelement.missingvalue"/></div>
	</g:if>
	<div class="average">
		<span class="bold">Value:</span>
		<span class="value">
			<g:if test="${info.value.numberValue != null}">
				<g:formatNumber number="${info.value.numberValue * 100}" format="#0.0"/>%
			</g:if>
			<g:else><g:message code="info.normalizeddataelement.na"/></g:else>
		</span>
		<g:if test="${info.maxValue != null}">
			<span class="bold"><g:message code="info.normalizeddataelement.range"/>:</span>
			<span>0 - ${info.maxValue}</span>
		</g:if>
		<div class="clear"></div>
	</div>
	
	<div class="span box">
		<div>
			<a href="#" class="cluetip" title="${i18n(field: info.expression.names)}" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:info.expression.id)}"><g:i18n field="${info.expression.names}"/></a>
		</div>
		<div>
			<span class="bold"><g:message code="info.normalizeddataelement.expression"/></span>
			<div>
				<div><g:expression expression="${info.expression}"/></div>
			</div>
		</div>
	</div>
	<g:if test="${info.valuesForLocation != null}">
		<div class="span box">
			<span class="bold"><g:message code="info.normalizeddataelement.data"/></span>
			<table class="listing"><g:each in="${info.valuesForLocation}" var="data">
				<g:set var="dataElement" value="${data.key}"/>
				<g:set var="dataValue" value="${data.value}"/>
				
				<tr class="data-${dataElement.id} data">
					<th>[${data.key.id}]</th>
					<th>
					<a	onclick="return false;" title="${i18n(field:dataElement.names)}" class="cluetip"
						href="${createLink(controller:'dataElement', action:'getDescription', params:[dataElement: dataElement.id])}"
						rel="${createLink(controller:'dataElement', action:'getDescription', params:[dataElement: dataElement.id])}">
						<g:i18n field="${dataElement.names}"/>
					</a>
					</th>
					<g:if test="${dataValue == null}">
						<td class="red">
							<g:message code="info.normalizeddataelement.na"/>
						</td>
					</g:if>
					<g:else>
						<td>
							${dataElement.type.getDisplayValue(dataValue.value)}
						</td>
					</g:else>
				</tr>
			</g:each></table>
		</div>
	</g:if>
	
	<div>
		<span class="bold">
			<a href="#" onclick="$(this).parent().next().slideToggle(); return false;"><g:message code="info.normalizeddataelement.trend"/></a>
		</span>
		<div class="span box hidden">
			<g:render template="/chart/chart" model="[data: info.expression.id, location: info.location.id, maxValue: info.maxValue]"/>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
