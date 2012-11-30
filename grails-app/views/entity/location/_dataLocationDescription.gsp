<div class="row">
	<div class="type"><g:message code="datalocation.changes.label"/>:</div>
	
	<g:each in="${dataLocation.changes}" var="change">
		<div>
			<g:if test="${!change.messages.empty}">
				${change.messages.join(', ')}, 
			</g:if>
			on ${change.dateCreated}, needs review: ${change.needsReview}
		</div>
	</g:each>
</div>