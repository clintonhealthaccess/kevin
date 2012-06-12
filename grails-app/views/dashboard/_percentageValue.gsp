<td>
	<g:if test="${percentageValue != null}">
		${percentageValue}%
	</g:if>
	<g:else>
		<g:message code="report.value.na"/>
	</g:else>
</td>
<td>
	<!-- percentage value -->
	<g:if test="${percentageValue == null}">
		<div class="js_bar_horizontal tooltip horizontal-bar" 
			data-percentage="${percentageValue}"
			style="width:0%"							 
			original-title="${percentageValue}"></div>
	</g:if>
	<g:elseif test="${percentageValue <= 100}">
		<div class="js_bar_horizontal tooltip horizontal-bar" 
			data-percentage="${percentageValue}"
			style="width:${percentageValue}%"							 
			original-title="${percentageValue}%"></div>
	</g:elseif>
	<g:else>
		<div class="js_bar_horizontal tooltip horizontal-bar expand-bar" 
			data-percentage="${percentageValue}"
			style="width:100%"							 
			original-title="${percentageValue}%"></div>
	</g:else>						
	<!-- comparison value -->
	<div id="compare-dashboard-entity-${id}" 
	class="js_bar_horizontal tooltip horizontal-bar-avg" 							
		data-percentage="45" 
		style="width:45%;" 
		original-title="45%"></div>
</td>