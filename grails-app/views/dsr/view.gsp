<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="dsrTable.view.label"
		default="District Summary Reports" /></title>
</head>
<body>
	<div id="dsr">
		<div id="top" class="box">
			<div class="filter">
				<h5>Iteration:</h5>
				<div class="dropdown">
					<a class="selected" href="#" data-period="${dsrTable.period.id}"
						data-type="period"><g:dateFormat format="yyyy"
							date="${dsrTable.period.startDate}" /></a>
					<div class="hidden dropdown-list">
						<ul>
							<g:each in="${periods}" var="period">
								<li><a href="${createLink(controller: "dsr", action:"view", params:[period:period.id, objective: dsrTable.objective.id, organisation: dsrTable.organisation.id])}">
										<span><g:dateFormat format="yyyy"
												date="${period.startDate}" />
									</span> </a></li>
							</g:each>
						</ul>
					</div>
				</div>
			</div>
			<div class="filter">
				<h5>Organisation:</h5>
				<div class="dropdown">
					<g:if test="${dsrTable.organisation != null}">
						<a class="selected" href="#"
							data-organisation="${dsrTable.organisation.id}"
							data-type="organisation">${dsrTable.organisation.name}</a>
					</g:if>
					<g:else>
						<a class="selected" href="#" data-type="organisation">Select
							Organisation Unit</a>
					</g:else>
					<div class="hidden dropdown-list">
						<ul>
							<g:render template="/templates/organisationTree"
								model="[controller: 'dsr', action: 'view', organisation: organisationTree, params:[period: periods.startDate, objective: objective?.id], displayLinkUntil: 3]" />
						</ul>
					</div>
				</div>
			</div>
			<div class="filter">
				<h5>Strategic Objective:</h5>
					<div class="dropdown">
						<g:if test="${dsrTable.objective != null}">
							<a class="selected" href="#"
							data-organisation="${dsrTable.objective.id}"
							data-type="objective"><g:i18n field="${dsrTable.objective.names}"/></a>
						</g:if>
						<g:else>
							<a href="#" class="selected" data-type="objective">
							Select Strategic Objective</a>
						</g:else>
						<div class="hidden dropdown-list">
							<g:if test="${!objectives.empty}">
								<ul>
									<g:each in="${objectives}" var="objective">
										<li>
											<span>
												<a href="${createLink(controller: "dsr", action:"view", params:[period: dsrTable.period.id, objective: objective.id, organisation: dsrTable.organisation.id])}">
													<g:i18n field="${objective.names}"/>
												</a>
											</span>
											<span>
												<g:link controller="dsrObjective" action="edit" id="${objective.id}" class="flow-edit">(Edit)</g:link>
											</span>
										</li>
									</g:each>
								</ul>
							</g:if>
							<g:else>
								<span>no objectives found</span>
							</g:else>
						</div>
					</div>
			</div>
				<g:if test="${true || user.admin}">
					<div>
						<a class="flow-add" id="add-dsr-objective-link" href="${createLink(controller:'dsrObjective', action:'create')}">Add Objective</a>
					</div>
				</g:if>
		</div>
		<div class="box">
			<div id="values">
				<table class="nice-table">
				<tbody>
					<tr>					
						<th class="objectNameBox"><g:i18n field="${dsrTable.objective.names}"/></th>
						<g:set var="i" value="${0}" />
						<g:each in="${dsrTable.targets}" var="target">
							<th class="titleTh">
							<g:if test="${target.category!=null}">
							${i++}<br/>
							<g:i18n field="${target.category.names}"/><br/>
							</g:if>							
							<g:i18n field="${target.names}"/>
							</th>
						</g:each>
					</tr>
					<g:each in="${dsrTable.values}" var="value">
						<tr>
						<th class="borderedBoxOrg">${value.key.name}</th>
							<g:each in="${value.value}" var="val">
								<td class="borderedBox">
									${val.value.value}
								</td>
							</g:each>
						</tr>
					</g:each>
					</tbody>
				</table>
			</div>
		</div>

	</div>
</body>
</html>