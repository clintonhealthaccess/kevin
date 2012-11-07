<div class="main">
  <table class="listing">
  	<thead>
  		<tr>
  			<th/>
  			<th><g:message code="entity.id.label"/></th>
  			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
  			<g:sortableColumn property="typeString" params="[q:params.q]" title="${message(code: 'entity.type.label')}" />
  			<g:sortableColumn property="refreshed" params="[q:params.q]" title="${message(code: 'normalizeddataelement.lastrefreshed.label')}" />
  			<g:sortableColumn property="lastValueChanged" params="[q:params.q]" title="${message(code: 'normalizeddataelement.lastvaluechanged.label')}" />
  			<th><g:message code="entity.list.manage.label"/></th>
  		</tr>
  	</thead>
  	<tbody>
  		<g:each in="${entities}" status="i" var="normalizedDataElement">
  			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
  				<td>
  	            	<ul class="horizontal">
  			           	<li>
  			           		<a class="edit-link" href="${createLinkWithTargetURI(action:'edit', id: normalizedDataElement.id)}"><g:message code="default.link.edit.label" /></a>
  						</li>
  			           	<li>
  			           		<a class="delete-link" href="${createLinkWithTargetURI(controller:'normalizedDataElement', action:'delete', params:[id:normalizedDataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
  						</li>
  		           	</ul>
  				</td>
  				<td>${normalizedDataElement.id}</td>
  				<td class="data-element-explainer" data-data="${normalizedDataElement.id}">
  					<a class="cluetip"
  						href="${createLink(controller:'data', action:'getExplainer', params:[id: normalizedDataElement.id])}"
  						rel="${createLink(controller: 'data', action:'getDescription', params:[id: normalizedDataElement.id])}">
  						${normalizedDataElement.code}
  					</a>
  				</td>
  				<td><g:i18n field="${normalizedDataElement.names}"/></td>
  				<td><g:toHtml value="${normalizedDataElement.type.getDisplayedValue(2, 2)}"/></td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${normalizedDataElement.refreshed}"/></td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${normalizedDataElement.lastValueChanged}"/></td>
  				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
  									<a href="${createLink(controller:'data', action:'dataValueList', params:[data:normalizedDataElement.id])}">
  										<g:message code="dataelement.viewvalues.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'task', action:'create', params:[class:'CalculateTask', dataId:normalizedDataElement.id])}">
  										<g:message code="dataelement.calculatevalues.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'addReferencingDataTasks', params:[data:normalizedDataElement.id])}">
  										<g:message code="dataelement.addreferencingdatatasks.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'deleteValues', params:[data:normalizedDataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  										<g:message code="data.deletevalues.label"/>
  									</a>
  								</li>
  							</ul>
  						</div>
  					</div>
  				</td>
  			</tr>
  			<tr class="explanation-row">
  				<td colspan="9">
  					<div class="explanation-cell" id="explanation-${normalizedDataElement.id}"></div>
  				</td>
  			</tr>
  		</g:each>
  	</tbody>
  </table>
</div>

<script type="text/javascript">
	$(document).ready(function() {
		$('.data-element-explainer').bind('click', function() {
			var normalizedDataElement = $(this).data('data');
			explanationClick(this, normalizedDataElement, function(){});
			return false;
		});
	});
</script>