<table class="listing">
	<thead>
		<tr>
		    <g:sortableColumn property="id" title="${message(code: 'dataelement.id.label', default: 'Id')}" />
			<th>Name</th>
			<th>Type</th>
			<g:sortableColumn property="code" title="${message(code: 'dataelement.code.label', default: 'Code')}" />
			<th>Manage</th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="dataElement"> 
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>${dataElement.id}</td> 
				<td class="data-element-explainer" data-data="${dataElement.id}">
					<a  href="${createLink(controller:'dataElement', action:'getExplainer', params:[dataElement: dataElement.id])}"><g:i18n field="${dataElement.names}" /></a>
				</td>
				<td><g:toHtml value="${dataElement.type.getDisplayedValue(2, 2)}"/></td>
				<td>${dataElement.code}</td>
				<td>
					<div class="dropdown subnav-dropdown"> 
						<a class="selected" href="#" data-type="section">Manage</a>
						<div class="hidden dropdown-list">
							<ul>
								<li>
									<a href="${createLinkWithTargetURI(controller:'dataElement', action:'edit', params:[id: dataElement.id])}">
										<g:message code="general.text.edit" default="Edit" />
									</a>
								</li>
								<li>
									<a href="${createLinkWithTargetURI(controller:'dataElement', action:'delete', params:[id: dataElement.id])}" onclick="return confirm('\${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">
										<g:message code="general.text.delete" default="Delete" />
									</a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
			<tr>
				<td colspan="5" class="explanation-row">
					<div class="explanation-cell" id="explanation-${dataElement.id}"></div>
				</td>
			</tr>
		</g:each>
	</tbody>
</table>
<script type="text/javascript">
	
	$(document).ready(function() {
		$('.data-element-explainer').bind('click', function() {
			var dataElement = $(this).data('data');
			explanationClick(this, dataElement, function(){});
			return false;
		});
	});
	
</script>