<%@ page import="org.chai.kevin.util.Utils" %>

<!-- Date type question -->
<div id="element-${element.id}-${suffix}" class="element element-date ${validatable?.isSkipped(suffix)?'skipped':''} ${(validatable==null || validatable?.isValid(suffix))?'':'errors'}" data-element="${element.id}" data-suffix="${suffix}">
	<a name="element-${element.id}-${suffix}"></a>

	<g:if test="${lastValue!=null}">
		<g:set var="tooltipValue" value="${Utils.formatDate(lastValue?.dateValue)}" />
	</g:if>

	<input id="date-${element.id}-${suffix}" ${tooltipValue!=null?'title="'+tooltipValue+'"':''} type="text" value="${Utils.formatDate(value?.dateValue)}" name="elements[${element.id}].value${suffix}" class="${tooltipValue!=null?'tooltip':''} idle-field input ${!readonly?'loading-disabled':''}" disabled="disabled"/>

	<g:if test="${showHints}">
		<div class="admin-hint">Element: ${element.id} - Prefix: ${suffix}</div>
	</g:if>

	<div class="error-list">
		<g:renderUserErrors element="${element}" validatable="${validatable}" suffix="${suffix}" location="${location}"/>
	</div>
</div>
<g:if test="${!print}">
	<script type="text/javascript">
		$(document).ready(
			function() {
				$(escape('#date-${element.id}-${suffix}')).glDatePicker({
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
