<%@ page import="org.chai.kevin.util.Utils" %>

<!-- Date type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-date ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${Utils.formatDate(lastValue?.dateValue)}" />
	</g:if>

	<input id="date-${surveyElement.id}-${suffix}" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} type="text" value="${Utils.formatDate(value?.dateValue)}" name="surveyElements[${surveyElement.id}].value${suffix}" class="${tooltipValue!=null?'tooltip':''} idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<shiro:hasPermission permission="admin">
		<div class="admin-hint">Element: ${surveyElement.id} - Prefix: ${suffix}</div>
	</shiro:hasPermission>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
<g:if test="${!print}">
	<script type="text/javascript">
		$(document).ready(
			function() {
				$(escape('#date-${surveyElement.id}-${suffix}')).glDatePicker({
					onChange : function(target, newDate) {
						target.val(newDate.getDate() + "-" + (newDate.getMonth() + 1) + "-" + newDate.getFullYear());
						target.trigger('change')
					},
					zIndex : "10"
				});
			}
		);
	</script>
</g:if>
