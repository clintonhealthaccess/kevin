<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}</label>
	<input type="${type}" name="${name}" value="${fieldValue(bean:bean,field:field)}"></input>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>