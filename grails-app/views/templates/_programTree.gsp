<r:require module="foldable" />
<li class="${current?.id == objective?.id ? 'current':''} foldable">	
	<% def programLinkParams = new HashMap(linkParams) %>
	<% programLinkParams.remove("dashboardEntity") %>		
	<% programLinkParams['objective'] = objective.id+"" %>
	<% linkParams = programLinkParams %>		
	<g:if test="${objective.children != null && !objective.children.empty && !objectiveTree.disjoint(objective.children)}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
	<a class="dropdown-link js_dropdown-link parameter" data-type="objective"
			data-location="${objective.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:linkParams)}">
			<g:i18n field="${objective.names}"/>
	</a>
	<g:if test="${objective.children != null || !objective.children.empty}">					
		<g:each in="${objective.children}" var="child">
			<g:if test="${objectiveTree.contains(child)}">
				<ul class="location-fold" id="location-fold-${objective.id}">
					<g:render template="/templates/programTree"
						model="[
						controller: controller, 
						action: action,
						current: current, 
						objective: child,
						objectiveTree: objectiveTree,
						linkParams:linkParams]" />
				</ul>
			</g:if>
		</g:each>
	</g:if>
</li>