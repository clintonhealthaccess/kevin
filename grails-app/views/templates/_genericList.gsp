<div class="entity-list">
	<div id="entities">
		<h5>
           	<g:message code="default.list.label" args="[entityName]" />
		</h5>
		<g:if test="${flash.message}">
           	<div class="message">${flash.message}</div>
           </g:if>
		
		<div class="float-right">
			<g:if test="${!addTemplate}">
				<a class="flow-add" href="${createLink(controller: params['controller'], action:'create', params: params)}">New ${entityName}</a>
			</g:if>
			<g:else>
				<g:render template="/survey/admin/${addTemplate}"/>
			</g:else>
		</div>
		<div class="clear"></div>
			
		<!-- Template goes here -->
		<g:if test="${!entities.isEmpty()}">
			<g:render template="${template}"/>
			
			<!-- End of template -->
			<div class="paginateButtons">
				<g:paginate total="${entityCount}" params="${params}"/>
			</div>
		</g:if>
		<g:else>
			<div>No ${entityName} available</div>
		</g:else>				
	</div>
	<div class="hidden flow-container"></div>
	<div class="clear"></div>
</div>

<r:script>
	$(document).ready(function() {
		$('#entities').flow({
			onSuccess : function(data) {
				if (data.result == 'success') {
					location.reload();
				}
			}
		});
	});
</r:script>