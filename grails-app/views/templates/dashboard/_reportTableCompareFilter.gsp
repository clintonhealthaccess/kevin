<div class="selector right">
	<span>Compare</span> 
	<select id="${table}-compare">
		<option>Please select</option>
		<g:each in="${dashboard.locationPath}" var="location">
			<option value="${location.id}">
				<g:i18n field="${location.names}" />
			</option>
		</g:each>
		<option value="${currentLocation.id}">
			<g:i18n field="${currentLocation.names}" />
		</option>
	</select>
</div>
