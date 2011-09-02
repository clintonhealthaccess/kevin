<!-- Date type question -->
<div class="element element-date element-${surveyElement.id} ${surveyEnteredValue?.skipped?'skipped':''} ${(surveyEnteredValue==null || surveyEnteredValue?.valid)?'':'errors'}" data-element="${surveyElement.id}">
	<a name="element-${surveyElement.id}"></a>
	<input type="hidden" value="${surveyElement.id}" name="surveyElements"/>
	
	<input type="hidden" value="${surveyElement.id}" name="surveyElements[${surveyElement.id}].surveyElement.id"/>
	<input type="text" value="${surveyEnteredValue?.value}" name="surveyElements[${surveyElement.id}].value" class="idle-field date-${surveyElement.id}"  ${readonly?'disabled="disabled"':''}/>
	<div class="error-list">
		<g:renderUserErrors element="${surveyElementValue}"/>
	</div>
</div>
<script type="text/javascript">

	$(document).ready(
			function() {
				$('.date-${surveyElement.id}').glDatePicker(
						{
							onChange : function(target, newDate) {
								target.val(newDate.getDate() + "-"
										+ (newDate.getMonth() + 1) + "-"
										+ newDate.getFullYear());
							}
						})
			})
</script>