<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <g:sortableColumn property="id" title="${message(code: 'entity.id.label')}" />
  			<th><g:message code="entity.name.label"/></th>
  			<th><g:message code="entity.type.label"/></th>
  			<g:sortableColumn property="code" title="${message(code: 'entity.code.label')}" />
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="rawDataElement"> 
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  					<ul class="horizontal">
  						<li>
  							<a class="edit-link" href="${createLinkWithTargetURI(controller:'rawDataElement', action:'edit', params:[id: rawDataElement.id])}">
  								<g:message code="default.link.edit.label" />
  							</a>
  						</li>
  						<li>
  							<a class="delete-link" href="${createLinkWithTargetURI(controller:'rawDataElement', action:'delete', params:[id: rawDataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  								<g:message code="default.link.delete.label" />
  							</a>
  						</li>
  					</ul>
  				</td>
  				<td>${rawDataElement.id}</td> 
  				<td class="data-element-explainer" data-data="${rawDataElement.id}">
  					<a href="${createLink(controller:'rawDataElement', action:'getExplainer', params:[id: rawDataElement.id])}"><g:i18n field="${rawDataElement.names}" /></a>
  				</td>
  				<td><g:toHtml value="${rawDataElement.type.getDisplayedValue(2, 2)}"/></td>
  				<td>${rawDataElement.code}</td>
  			</tr>
  			<tr class="explanation-row">
  				<td colspan="5">
  					<div class="explanation-cell" id="explanation-${rawDataElement.id}"></div>
  				</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>

<script type="text/javascript">
	
	$(document).ready(function() {
		$('.data-element-explainer').bind('click', function() {
			var rawDataElement = $(this).data('data');
			explanationClick(this, rawDataElement, function(){});
			return false;
		});
	});
	
</script>