<r:require module="foldable" />
<li class="${current?.id == program?.id ? 'current':''} js_foldable foldable">	
	<% def programLinkParams = new HashMap(linkParams) %>
	<% programLinkParams['program'] = program.id+"" %>
	<% linkParams = programLinkParams %>
	<g:if test="${program.children != null && !program.children.empty && !programTree.disjoint(program.children)}">
		<!-- program is a branch sub-program -->
		<a class="js_foldable-toggle foldable-toggle" href="#">(toggle)</a>
		<g:if test="${showProgramData}">
			<a class="dropdown-link js_dropdown-link parameter" data-type="program"
					data-location="${program.id}"
					href="${createLink(controller:controller, action:action, params:linkParams)}">
					<g:i18n field="${program.names}"/>
			</a>
		</g:if>
		<g:else>
			<g:i18n field="${program.names}"/>
		</g:else>
	</g:if>
	<g:else>
		<!-- program is a leaf sub-program -->
		<a class="dropdown-link js_dropdown-link parameter" data-type="program"
				data-location="${program.id}"
				href="${createLink(controller:controller, action:action, params:linkParams)}">
				<g:i18n field="${program.names}"/>
		</a>
	</g:else>
	<g:if test="${program.children != null && !program.children.empty}">					
		<g:each in="${program.children.sort({it.order})}" var="child">
			<g:if test="${programTree.contains(child)}">
				<ul class="js_foldable-container foldable-container">
					<g:render template="/tags/filter/programTree"
						model="[
						controller: controller, 
						action: action,
						current: current, 
						program: child,
						programTree: programTree,
						linkParams:linkParams]"
					/>
				</ul>
			</g:if>
		</g:each>
	</g:if>
</li>