<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}</label>
	<input type="${type}" class="idle-field" name="${name}"  ${active} ${readonly?'readonly="readonly"':''}/>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>