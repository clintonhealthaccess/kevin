<div class="entity-list">
	<div id="entities">
		<h5 class="subnav">
	     	<g:message code="default.list.label" args="[entityName]" />
	     	<g:if test="${!search}">
		     	<span class="right">
					<g:if test="${!addTemplate}">
		  				<a href="${createLinkWithTargetURI(controller: params['controller'], action:'create', params: params)}">
		  					<g:message code="default.new.label" args="[entityName]"/>
		  				</a>
		  			</g:if>
		  			<g:else>
		  				<g:render template="/survey/admin/${addTemplate}"/>
		  			</g:else>
		     	</span>
	     	</g:if>
		</h5>
		
		<g:if test="${flash.message}">
			<div class="message">${flash.message}</div>
        </g:if>
			
		<!-- Template goes here -->
		<g:if test="${grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName).hasProperty('search')}">
			<g:searchBox action="search"/>
		</g:if>
		<g:if test="${!entities.isEmpty()}">
			  <g:render template="${template}"/>
			<!-- End of template -->
			<div class="paginateButtons main">
				<g:paginate total="${entityCount}" params="${params}" action="${actionName}"/>
			</div>
		</g:if>
		<g:else>
			<div><g:message code="entity.list.empty.label" args="[entityName]"/></div>
		</g:else>				
	</div>
</div>
