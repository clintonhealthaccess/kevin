<r:require module="foldable" />

<li class="${current?.id == objective?.id ? 'current':''} foldable">
	<g:if test="${objective.children != null && !objective.children.isEmpty()}">
		<a class="foldable-toggle" href="#">(toggle)</a>
	</g:if>
		<% def programLinkParams = new HashMap(linkParams) %>
		<% programLinkParams.remove("dashboardEntity") %>
		<% programLinkParams['objective'] = objective.id+"" %>	
		<a class="dropdown-link parameter" data-type="objective"
			data-organisation="${objective.id}"
			href="${createLinkByFilter(controller:controller, action:action, params:programLinkParams)}">
			<g:i18n field="${objective.names}"/> </a>
	<g:if test="${objective.children != null && !objective.children.isEmpty()}">
		<ul class="organisation-fold" id="organisation-fold-${objective.id}">
			<g:each in="${objective.children}" var="child">
				<g:render template="/templates/programTree"
					model="[controller: controller, 
					action: action,
					current: current, 
					objective: child,
					linkParams:linkParams]" />
			</g:each>
		</ul>
	</g:if></li>