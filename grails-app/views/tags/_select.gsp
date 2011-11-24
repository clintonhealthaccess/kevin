<g:set var="multiple" value="${multiple!=null&&multiple=='true'}"/>

<div class="row ${hasErrors(bean:target,field:field,'errors')}">
	<g:if test="${multiple}"><input type="hidden" name="${name}" value=""/></g:if>
	<label for="${name}">${label}</label>
	<select name="${name}" ${multiple?'multiple':''}>
		<g:if test="${!multiple}"><option value="">-- Please select from the list --</option></g:if>
		<g:each in="${from}" var="item" status="i">
			<option value="${item[optionKey]}" ${(multiple?value.contains(item[optionKey]):item[optionKey].equals(value))?'selected':''}>
				<g:if test="${values!=null}">
					${values[i]}
				</g:if>
				<g:else>
					${item[optionValue]}
				</g:else>
			</option>
		</g:each>
	</select>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>