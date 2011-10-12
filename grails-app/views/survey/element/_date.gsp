<%@ page import="org.chai.kevin.util.Utils" %>

<!-- Date type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-date ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<input id="date-${surveyElement.id}-${suffix} ${!readonly?'loading-disabled':''}" type="text" value="${Utils.formatDate(value?.dateValue)}" name="surveyElements[${surveyElement.id}].value${suffix}" class="idle-field input" disabled="disabled"/>
	<g:if test="${lastValue!=null}"><span class="survey-old-value">(${Utils.formatDate(lastValue?.dateValue)})</span></g:if>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}" suffix="${suffix}"/>
	</div>
</div>
<g:if test="${!print}">
	<script type="text/javascript">
		$(document).ready(
			function() {
				$('#date-${surveyElement.id}-${suffix}').glDatePicker({
					onChange : function(target, newDate) {
						target.val(newDate.getDate() + "-" + (newDate.getMonth() + 1) + "-" + newDate.getFullYear());
						target.trigger('change')
					}
				});
			}
		);
	</script>
</g:if>
