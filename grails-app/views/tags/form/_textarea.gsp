<div class="row ${hasErrors(bean:bean,field:field,'errors')}">
	<label for="${name}">${label}</label>
	<textarea class="input" type="${type}" name="${name}" rows="${rows}" ${readonly?'readonly="readonly"':''} style="height:${height}px;">${value}</textarea>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>