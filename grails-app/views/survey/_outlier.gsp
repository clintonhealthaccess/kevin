${message}
<a href="#" onclick="$(this).next().val(${rule.id}); surveyValueChanged($(this).parents('.element').data('element'), this, true);">Yes</a>
<input type="hidden" name="surveyElements[${surveyElement.id}].surveyEnteredValue.acceptedWarnings" value="-1"/>
