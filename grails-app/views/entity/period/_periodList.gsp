<%@page import="org.chai.kevin.util.DataUtils"%>
<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="code" params="[q:params.q]" title="${message(code: 'entity.code.label')}" />
			<g:sortableColumn property="startDate" params="[q:params.q]" title="${message(code: 'period.startdate.label')}" />
			<g:sortableColumn property="endDate" params="[q:params.q]" title="${message(code: 'period.enddate.label')}" />
			<g:sortableColumn property="defaultSelected" params="[q:params.q]" title="${message(code: 'period.defaultselected.label')}" />
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="period">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'period', action:'edit', params:[id: period.id])}">
								<g:message code="default.link.edit.label" />
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'period', action:'delete', params:[id: period.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');"><g:message code="default.link.delete.label" /></a>
						</li>
						
					</ul>
				</td>
				<td>${period.code}</td>
				<td>${DataUtils.formatDate(period.startDate)}</td>
				<td>${DataUtils.formatDate(period.endDate)}</td>
				<td>${period?.defaultSelected?'\u2713':''}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
  								<li>
  									<a href="${createLinkWithTargetURI(controller:'data', action:'deleteValues', params:[period:period.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
  										<g:message code="data.deletevalues.label"/>
  									</a>
  								</li>
  							</ul>
  						</div>
  					</div>
  				</td>
			</tr>
		</g:each>
	</tbody>
</table>
