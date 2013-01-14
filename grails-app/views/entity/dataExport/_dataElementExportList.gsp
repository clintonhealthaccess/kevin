<%@page import="org.chai.kevin.util.DataUtils"%>

<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />  		    
  			<g:sortableColumn property="${i18nField(field: 'descriptions')}" params="[q:params.q]" title="${message(code: 'entity.description.label')}" />
  			<g:sortableColumn property="typeCodeString" params="[q:params.q]" title="${message(code: 'exporter.location.types')}" />
  			<g:sortableColumn property="date" params="[q:params.q]" title="${message(code: 'exporter.create.on')}" />  		    
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="export">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
            		<ul class="horizontal">
		           		<li>
		           			<a class="edit-link" href="${createLinkWithTargetURI(controller:'dataElementExport', action:'edit', params:[id: export.id])}"><g:message code="default.link.edit.label" /></a>
						</li>
		           		<li>
		           			<a class="delete-link" href="${createLinkWithTargetURI(controller:'dataElementExport', action:'delete', params:[id:export.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
	           		</ul>
				</td>
				<td>${export.code}</td>
				<td><g:i18n field="${export.descriptions}"/></td>
  				<td>
  					${export.typeCodeString}
  				</td>
  				<td>${DataUtils.formatDateWithTime(export.date)}</td>
  				<td>
	  				<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
	  								<a href="${createLinkWithTargetURI(controller:'task', action:'create', params:['class': 'DataExportTask', 'exportId': export.id])}"><g:message code="exporter.exporttask.label" /></a>
	  							</li>
	  							<li>
	  								<a href="${createLinkWithTargetURI(controller:'dataElementExport', action:'clone', params:['export.id': export.id, method: method])}"><g:message code="exporter.clone.label" /></a>
	  							</li>
	  						</ul>
  						</div>
 					</div>
  				</td>
			</tr>
		</g:each>
	</tbody>
</table>
