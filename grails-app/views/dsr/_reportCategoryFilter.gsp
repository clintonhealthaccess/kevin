<div class="selector">
	<span>Report Category:</span>
	<g:if test="${dsrTable.targetCategories != null && !dsrTable.targetCategories.empty}">
		<select id="report-category">
			<g:each in="${dsrTable.targetCategories}" var="category">
				<g:if test="${category.id == currentCategory?.id}">
					<option selected="selected" name="dsrTargetCategory" value="${category.id}">
						<g:i18n field="${category.names}" />
					</option>
				</g:if>
				<g:else>
					<option name="dsrTargetCategory" value="${category.id}">
						<g:i18n field="${category.names}" />
					</option>
				</g:else>
			</g:each>
		</select>
	</g:if>
	<g:else>
		<select id="report-category" disabled="disabled">
			<option value="0">None</option>
		</select>
	</g:else>
</div>