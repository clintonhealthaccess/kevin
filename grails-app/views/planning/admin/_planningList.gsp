<%@page import="org.chai.kevin.util.DataUtils"%>

<table class="listing">
	<thead>
		<tr>
			<th/>
			<g:sortableColumn property="${i18nField(field: 'names')}" params="[q:params.q]" title="${message(code: 'entity.name.label')}" />
			<g:sortableColumn property="typeCodeString" params="[q:params.q]" title="${message(code: 'entity.datalocationtype.label')}" />
			<g:sortableColumn property="active" params="[q:params.q]" title="${message(code: 'planning.active.label')}" />
			<g:sortableColumn property="period.startDate" params="[q:params.q]" title="${message(code: 'period.label')}" />
			<th><g:message code="default.number.label" args="[message(code:'planning.planningtype.label')]"/></th>
			<th><g:message code="entity.list.manage.label"/></th>
		</tr>
	</thead>
	<tbody>
		<g:each in="${entities}" status="i" var="planning">
			<tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
				<td>
					<ul class="horizontal">
						<li>
							<a class="edit-link" href="${createLinkWithTargetURI(controller:'planning', action:'edit', params:[id: planning.id])}">
								<g:message code="default.link.edit.label" /> 
							</a>
						</li>
						<li>
							<a class="delete-link" href="${createLinkWithTargetURI(controller:'planning', action:'delete', params:[id: planning.id])}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
								<g:message code="default.link.delete.label" /> 
							</a>
						</li>
					</ul>
				</td>
				<td><g:i18n field="${planning.names}" /></td>
				<td>${planning.typeCodeString}</td>
				<td>${planning?.active?'\u2713':''}</td>
				<td>${DataUtils.formatDate(planning.period.startDate)}</td>
				<td>${planning.planningTypes.size()}</td>
				<td>
					<div class="js_dropdown dropdown"> 
						<a class="js_dropdown-link with-highlight" href="#"><g:message code="entity.list.manage.label"/></a>
						<div class="dropdown-list js_dropdown-list">
							<ul>
								<li>
									<a href="${createLink(controller:'planningType', action:'list', params:['planning.id':planning.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningtype.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningOutput', action:'list', params:['planning.id':planning.id])}"><g:message code="default.list.label" args="[message(code:'planning.planningoutput.label')]" /></a>
								</li>
								<li>
									<a href="${createLink(controller:'planningSkipRule', action:'list', params:['planning.id': planning?.id])}"><g:message code="default.list.label" args="[message(code:'planning.skiprule.label')]" /></a>
								</li>
							</ul>
						</div>
					</div> 		
				</td>
			</tr>
		</g:each>
	</tbody>
</table>