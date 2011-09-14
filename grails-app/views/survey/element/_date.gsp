<!-- Date type question -->
<div id="element-${surveyElement.id}-${suffix}" class="element element-date ${enteredValue?.isSkipped(suffix)?'skipped':''} ${(enteredValue==null || enteredValue?.isValid(suffix))?'':'errors'}" data-element="${surveyElement.id}" data-suffix="${suffix}">
	<a name="element-${surveyElement.id}-${suffix}"></a>

	<input id="date-${surveyElement.id}-${suffix}" type="text" value="${value?.dateValue}" name="surveyElements[${surveyElement.id}].value" class="idle-field"  ${readonly?'disabled="disabled"':''}/>
	<g:if test="${lastValue!=null}"><span class="survey-old-value">(${surveyElementValue.lastValue})</span></g:if>

	<div class="error-list">
		<g:renderUserErrors element="${enteredValue}"/>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(
		function() {
			$('#date-${surveyElement.id}-${suffix}').glDatePicker({
				onChange : function(target, newDate) {
					target.val(newDate.getDate() + "-" + (newDate.getMonth() + 1) + "-" + newDate.getFullYear());
				}
			});
		}
	);
</script>
