<div class="row ${hasErrors(bean:bean, field:field, 'errors')}">
	<label for="${name}">${label}</label> 
	<input type="hidden" name="${name}" value=""/>
	<g:select optionKey="${optionKey}" optionValue="${optionValue}" name="${name}" from="${from}" value="${value}" />
	<div class="error-list">
		<g:renderErrors bean="${bean}" field="${field}" />
	</div>
</div>