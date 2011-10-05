<div>
	<g:form class="search-form" url="[controller: controller, action: action]" method="GET">
		<g:each in="${hiddenParams}" var="entry">
			<input type="hidden" name="${entry.key}" value="${entry.value}"/>
		</g:each>
		<label for="q">Search ${entityName}: </label>
		<input name="q" value="${params.q}"></input>
		<button type="submit">Search</button>
	</g:form>
</div>