<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}</label>
	<input type="${type}" class="idle-field" name="${name}" value="${fieldValue(bean:bean,field:field)}" ${active}></input>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>