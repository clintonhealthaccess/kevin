<div class="row">Type: <span class="type">${dataElement.type}</span></div>

<g:if test="${enume!=null}">
	<div class="row enum box">
		<h5><g:i18n field="${enume.names}"/></h5>
		<ul>
			<g:each in="${enume.enumOptions}" var="enumOption">
				<li>
					<div class="name"><g:i18n field="${enumOption.names}"/></div>
					<div class="value">${enumOption.value}</div>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>

<div class="row"><g:i18n field="${dataElement.descriptions}"/></div>

