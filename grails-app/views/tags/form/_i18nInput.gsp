<div class="row ${hasErrors(bean:bean,field:field, 'errors')}">
	<g:each in="${locales}" var="locale" status="i">
		<div class="toggle-entry ${i!=0?'hidden':''}" data-toggle="${locale}">
			<g:set var="fieldLang" value="${field+'_'+locale}"/>
			<g:if test="${bean.hasProperty(fieldLang)}">
				<label for="${name+'_'+locale}">${label} (${locale})</label>	
				<input type="${type}" class="idle-field" name="${name+'_'+locale}" value='${bean?."$fieldLang"}' ${active}></input>
			</g:if>
			<g:else>
				<label for="${name+'.'+locale}">${label} (${locale})</label>	
				<input type="${type}" class="idle-field" name="${name+'.'+locale}" value="${value?.get(locale)}" ${active}></input>
			</g:else>
		</div>
	</g:each>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}"/></div>
</div>