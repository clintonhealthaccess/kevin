<div>
	<g:form class="search-form" url="[controller: controller, action: action]" method="GET">
		<g:each in="${hiddenParams}" var="entry">
			<input type="hidden" name="${entry.key}" value="${entry.value}"/>
		</g:each>
		<label for="q"><g:message code="entity.search.label" default="Search" args="[entityName]"/>: </label>
		<input name="q" value="${params.q}"></input>
		<button type="submit"><g:message code="default.button.search.label" default="Search"/></button>
	</g:form>
</div>