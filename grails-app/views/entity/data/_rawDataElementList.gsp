<table class="listing">
  	<thead>
  		<tr>
  			<th/>
  		    <g:sortableColumn property="id" title="${message(code: 'entity.id.label')}" />
  			<g:sortableColumn property="code" title="${message(code: 'entity.code.label')}" />  		    
  			<th><g:message code="entity.name.label"/></th>
  			<th><g:message code="entity.type.label"/></th>
  			<th><g:message code="rawdataelement.lastvaluechanged.label"/></th>
  			<th><g:message code="entity.list.manage.label"/></th>
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
  					<a class="cluetip"
  						href="${createLink(controller:'data', action:'getExplainer', params:[id: rawDataElement.id])}"
					 	rel="${createLink(controller: 'data', action:'getDescription', params:[id: rawDataElement.id])}">
  						${rawDataElement.code}
  					</a>
  				</td>
  				<td><g:i18n field="${rawDataElement.names}" /></td>  				
  				<td><g:toHtml value="${rawDataElement.type.getDisplayedValue(2, 2)}"/></td>
  				<td><g:formatDate format="yyyy-MM-dd HH:mm" date="${rawDataElement.lastValueChanged}"/></td>
  				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
  									<a href="${createLink(controller:'data', action:'dataValueList', params:[data:rawDataElement.id])}">
  										<g:message code="dataelement.viewvalues.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'addReferencingDataTasks', params:[data:rawDataElement.id])}">
  										<g:message code="dataelement.addreferencingdatatasks.label"/>
  									</a>
  								</li>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'deleteValues', params:[data:rawDataElement.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  										<g:message code="data.deletevalues.label"/>
  									</a>
  								</li>
  							</ul>
  						</div>
  					</div>
  				</td>
  			</tr>
  			<tr class="explanation-row">
  				<td colspan="7">
  					<div class="explanation-cell" id="explanation-${rawDataElement.id}"></div>
  				</td>
  			</tr>
  		</g:each>
  	</tbody>
</table>

<script type="text/javascript">
	
	$(document).ready(function() {
		$('.data-element-explainer').bind('click', function() {
			var rawDataElement = $(this).data('data');
			explanationClick(this, rawDataElement, function(){});
			return false;
		});
	});
	
</script>