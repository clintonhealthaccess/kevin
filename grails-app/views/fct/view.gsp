<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
 		<meta name="layout" content="main" />
		<title>Facility Count Tables</title>
		
		<!-- for admin forms -->
		<shiro:hasPermission permission="admin:fct">
        	<r:require modules="form"/>
        </shiro:hasPermission>        
        
        <r:require modules="fct"/>
        
	</head>
	<body>
		<div id="report">
			<div class="subnav">
				<g:iterationFilter linkParams="${params}" selected="${currentPeriod}"/>
				<g:locationFilter linkParams="${params << [filter:'location']}" selected="${currentLocation}"/>
				<g:render template="/templates/programFilter" model="[linkParams:params]"/>
				<g:levelFilter linkParams="${params << [filter:'level']}" selected="${currentLevel}"/>												
				
				<div class="right">
				<!-- ADMIN SECTION -->
				<shiro:hasPermission permission="admin:fct">
					<span> <a href="${createLinkWithTargetURI(controller:'fctProgram', action:'create')}">Add Program</a> </span>|
					<span> <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}">Add Target</a> </span>
				</shiro:hasPermission>
				<!-- ADMIN SECTION END -->
			</div>
		</div>
		
		<g:if test="${fctTable != null}">
			<g:locationTypeFilter linkParams="${linkParams}" selected="${currentLocationTypes}" />
		</g:if>
		<div id="center" class="main">
			<div id="values">
				<g:if test="${fctTable != null}">
					<g:if test="${!fctTable.targets.empty}">
						<table class="nice-table">
							<thead>
								<tr>
									<th class="object-name-box" rowspan="2">
										<div>
											<g:i18n field="${currentProgram.names}" />
										</div> <shiro:hasPermission permission="admin:fct">
											<span> 
												<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctProgram', action:'edit', id:currentProgram.id)}">
													<g:message code="default.link.edit.label" default="Edit" />
												</a> 
											</span>
											<span> 
												<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctProgram', action:'delete', id:currentProgram.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
													<g:message code="default.link.edit.label" default="Delete" />
												</a>
											</span>
										</shiro:hasPermission></th>
									<g:set var="i" value="${0}" />
									<g:each in="${fctTable.targets}" var="target">
										<th class="title-th" rowspan="2">
											<div class="bt">
												<g:i18n field="${target.names}" />
											</div>
											<shiro:hasPermission permission="admin:fct">
												<span> 
													<a class="edit-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'edit', id:target.id)}">
														<g:message code="default.link.edit.label" default="Edit" />
													</a> 
												</span>
												<span> 
													<a class="delete-link" href="${createLinkWithTargetURI(controller:'fctTarget', action:'delete', id:target.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message', default: 'Are you sure?')}');">
														<g:message code="default.link.edit.label" default="Delete" />
													</a>
												</span>
											</shiro:hasPermission>
										</th>
									</g:each>
								</tr>
							</thead>
							<tbody>								
								<g:each in="${fctTable.locations}" var="parent">
									<tr>
										<th colspan="${fctTable.targets.size()+1}" class="parent-row"><g:i18n field="${parent.names}"/></th>
									</tr>
									<g:each in="${fctTable.getLocationMap().get(parent)}" var="children">
										<g:each in="${children}" var="child">										
											<tr class="row location">
												<th class="box-report-location"><g:i18n field="${child.names}"/></th>
												<g:each in="${fctTable.targets}" var="target">
													<td class="box-report-value">
														<g:if test="${!fctTable.getReportValue(child, target) != null}">
															${fctTable.getReportValue(child, target).value}
														</g:if>
													</td>
												</g:each>
											</tr>
										</g:each>
									</g:each>
								</g:each>
								<tr>
									<th colspan="${fctTable.targets.size()+1}" class="parent-row">Total</th>
								</tr>
								<tr>
									<th class="box-report-location"><g:i18n field="${currentLocation.names}"/></th>
									<g:each in="${fctTable.targets}" var="target">
										<td class="box-report-value">
											<g:if test="${!fctTable.getTotalValue(target) != null}">
												${fctTable.getTotalValue(target).value}
											</g:if>
										</td>
									</g:each>
								</tr>
							</tbody>
						</table>
					</g:if>
					<g:else>
						<div>
							Please <a href="${createLinkWithTargetURI(controller:'fctTarget', action:'create')}"> Add Target </a>
						</div>
					</g:else>
				</g:if>
				<g:else>
					<div class="help">Please select a Location / Program</div>
				</g:else>
			</div>
		</div>
	</div>

</body>
</html>