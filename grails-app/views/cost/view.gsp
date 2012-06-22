<%@ page import="org.chai.kevin.cost.CostTarget.CostType" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="cost.title" /></title>
        
        <!-- for admin forms -->
        <shiro:hasPermission permission="cost:admin">
        	<r:require modules="form,cluetip"/>
        </shiro:hasPermission>
        
        <r:require modules="cost"/>
    </head>
    <body>
    	<div id="cost">
			<div class="heading1-bar">
				<g:periodFilter linkParams="${[location: currentLocation?.id, program: currentProgram?.id]}" selected="${currentPeriod}"/>
				<g:locationFilter linkParams="${[period: currentPeriod.id, program: currentProgram?.id]}" selected="${currentLocation}"/>
				<g:render template="/templates/programFilter" model="[linkParams:[period: currentPeriod.id, location: currentLocation?.id]]"/>
				
				<shiro:hasPermission permission="admin:cost">					
					<span>
						<a href="${createLinkWithTargetURI(controller:'costProgram', action:'create')}"><g:message code="costing.admin.add.program"/></a>
					</span>
					<span>
						<a href="${createLinkWithTargetURI(controller:'costTarget', action:'create')}">Add target</a>
					</span>
				</shiro:hasPermission>
			</div>
    		<div id="text-center" class="main">
    			<div id="values">
    				<g:if test="${costTable != null}">
						<table class="listing">
							<thead>
								<tr>
									<th class="empty">
										<g:i18n field="${currentProgram.names}"/>
										<span>
											<a class="edit-link" href="${createLinkWithTargetURI(controller:'costProgram', action:'edit', id:currentProgram.id)}">
												<g:message code="default.link.edit.label" />
											</a>
										</span>
										<span>
											<a class="delete-link" href="${createLinkWithTargetURI(controller:'costProgram', action:'delete', id:currentProgram.id)}" onclick="return confirm('\${message(code: 'default.link.delete.confirm.message')}');">
												<g:message code="default.link.delete.label" />
											</a>
										</span>
									</th>
									<g:each in="${costTable.years}" var="year">
										<th class="cell label col-${year}" data-col="${year}">${year}</th>
									</g:each>
								</tr>
							</thead>
							<tbody>
								<g:each in="${CostType.values()}" var="costType">
									<tr>
										<td class="header" colspan="${costTable.years.size() + 1}">${costType.name}</td>
									</tr>
									<g:if test="${costTable.getTargetsOfType(costType).empty}">
										<tr class="italic"><th colspan="${costTable.years.size() + 1}"><span>no target defined for this type</span></th></tr>
									</g:if>
									<g:each in="${costTable.getTargetsOfType(costType)}" var="target">
										<tr>
											<td class="cell label row-${target.id}" data-row="${target.id}">
												<span>
													<a class="no-link" href="${createLink(controller:'cost', action:'explain', params:[program: target.id, location: currentLocation?.id])}"><g:i18n field="${target.names}"/></a>
												</span>
												
												<shiro:hasPermission permission="admin:cost">		
													<span>
														<a class="edit-link" href="${createLinkWithTargetURI(controller:'costTarget', action:'edit', id: target.id)}">(edit)</a>
													</span>
												</shiro:hasPermission>
											</th>
											<g:each in="${costTable.years}" var="year">
												<g:set value="${costTable.getCost(target, year)}" var="cost"/>
												<td class="cell value row-${target.id} col-${year}" data-col="${year}" data-row="${target.id}">
													${cost.roundedValue}
												</td>
											</g:each>
										</tr>
										<tr class="explanation-row">
											<td colspan="${costTable.years.size()+1}">
												<div class="explanation-cell" id="explanation-target-${target.id}"></div>
											</td>
										</tr>
									</g:each>
								</g:each>
							</tbody>
						</table>
					
					</g:if>
					<g:else>
						<p class="nav-help">Please select an location / program</p>
					</g:else>
				</div>
				<div class="clear"></div>
    		</div>
    	</div>
    	
    	<r:script>
    		$(document).ready(function(){
    			$('.cell.label').bind('click', function() {
    				var target = $(this).data('row');
    				explanationClick(this, 'target-'+target, function(){});
    				return false;
    			});
    		});
    	</r:script>
    </body>
</html>