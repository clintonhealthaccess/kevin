<h4 class="nice-title">
	<span class="nice-title-image">
		<img src="${resource(dir:'images/icons',file: file)}" />
	</span>
	${title}
	<g:if test="${entity != null}">
	&nbsp;
		<g:render template="/templates/help_tooltip" 
			model="[names: i18n(field: entity.names), descriptions: i18n(field: entity.descriptions)]" />
	</g:if>
</h4>
