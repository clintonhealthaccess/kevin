<%@ page import="org.chai.kevin.dashboard.DashboardPercentage.Status" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Dashboard explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
		<div id="dashboard-explanation">
			<h3><g:i18n field="${explanation.entry.names}"/> in ${explanation.organisation.name}</h3>
			<g:if test="${explanation.average.status == Status.MISSING_EXPRESSION || (explanation.leaf && explanation.average.isHasMissingExpression())}">
				<div class="red bold">This group doesn't have any associated expression.</div>
			</g:if>
			<g:else>
				<g:if test="${explanation.average.isHasMissingValue()}">
					<div class="red bold">Some values are missing.</div>
				</g:if>
				
				<div id="dashboard-explanation-content">		
				
					<g:if test="${explanation.leaf?!explanation.target:true}">
						<div class="average">
							<g:if test="${explanation.target}">
								<h5>${explanation.level.name} average:</h5>
							</g:if>
							<g:else>
								<h5><g:i18n field="${explanation.entry.names}"/> average:</h5>
							</g:else>
							<a href="#" onclick="$('#values-${explanation.entry.id}-${explanation.organisation.id}').slideToggle(); return false;">(scores)</a>
							<span class="value">
								<g:if test="${explanation.average.valid}">
									<g:formatNumber number="${explanation.average.value * 100}" format="#0.0"/>%
								</g:if>
								<g:else>
									N/A
								</g:else>
							</span>
							<div class="clear"></div>
						</div>
	
						<div class="values">
							<h5><a href="#" onclick="$('#values-${explanation.entry.id}-${explanation.organisation.id}').slideToggle(); return false;">Scores</a></h5>
							<div id="values-${explanation.entry.id}-${explanation.organisation.id}" class="hidden">
								<div class="box float-left">
									<g:if test="${explanation.target}">
										<ul><g:each in="${explanation.values}">
											<g:set var="value" value="${it}"/>
											<li>
												<div class="organisation">${value.key.name}</div>
												<div class="value">
													<g:if test="${value.value.valid}">
														<g:formatNumber number="${value.value.value * 100}" format="#0.0"/>%
													</g:if>
													<g:else>
														N/A
													</g:else>
												</div>
											</li>
										</g:each></ul>
									</g:if>
									<g:else>
										<ul>
											<li class="header">
												<div class="objective">&nbsp;</div>
												<div class="weight">Weight</div>
												<div class="value">Score</div>
											</li>
											<g:each in="${explanation.objectives}">
												<g:set var="objective" value="${it}"/>
												<li>
													<div class="objective"><g:i18n field="${objective.key.entry.names}"/></div>
													<div class="weight">${objective.key.weight}</div>
													<div class="value">
														<g:if test="${objective.value.valid}">
															<g:formatNumber number="${objective.value.value * 100}" format="#0.0"/>%
														</g:if>
														<g:else>
															N/A
														</g:else>
													</div>
												</li>
											</g:each>
										</ul>
									</g:else>
								</div>
							</div>
						</div>
					</g:if>
						
					<g:if test="${explanation.target}">
						<div class="expressions">
						
							<g:each	in="${explanation.expressionExplanations}">
							<div class="expression-explanation">
								<g:set var="expressionExplanation" value="${it}"/>
								<g:set var="calculation" value="${expressionExplanation.calculation}"/>
								<h5>
									<g:if test="${!explanation.leaf}">
										<a href="#" onclick="$('#calculation-${calculation.id}').slideToggle(); return false;">Expression for ${groups.getGroupByUuid(calculation.groupUuid).name}</a>
									</g:if>
									<g:else>
										Expression for ${groups.getGroupByUuid(calculation.groupUuid).name}
									</g:else>
								</h5>
								<div id="calculation-${calculation.id}" class="left-pane ${!explanation.leaf?'hidden':''}">
									<div class="box float-left">
										<div class="expression">
											<div>
												<a href="#" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:calculation.expression.id)}"><g:i18n field="${calculation.expression.names}"/></a>
											</div>
										</div>
										<div class="equation">
											<h5>Equation</h5>
											<div>
												<div class="formula">
													${expressionExplanation.htmlFormula}
												</div>
												<g:if test="${explanation.leaf}">
													<div class="value">
														<g:if test="${expressionExplanation.percentage.valid}">
															<g:formatNumber number="${expressionExplanation.percentage.value * 100}" format="#0.0"/>%
														</g:if>
														<g:else>
															N/A
														</g:else>
													</div>
												</g:if>
											</div>
										</div>
									</div>
								</div>
								<g:if test="${explanation.leaf && !expressionExplanation.constant}">
									<div class="right-pane">
										<div class="box float-left">
											<h5>Data</h5>
											<ul><g:each in="${expressionExplanation.relevantDatas}">
												<g:set var="data" value="${it}"/>
												<li id="data-${explanation.organisation.id}-${explanation.entry.id}-${data.element.id}">
													<div class="id">[${data.element.id}]</div>
													<div class="name">
													<a	onclick="return false;" title="${i18n(field:data.element.names)}" class="cluetip"
														href="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: data.element.id])}"
														rel="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: data.element.id])}">
														<g:i18n field="${data.element.names}"/>
													</a>
													</div>
													<g:if test="${data.value == null}">
														<div class="value red">
															N/A
														</div>
													</g:if>
													<g:else>
														<div class="value">
															${data.value}
														</div>
													</g:else>
												</li>
											</g:each></ul>
										</div>
									</div>
								</g:if>
							</div>
							</g:each>
						</div>
					</g:if>
				</div>
			</g:else>
		</div>
    </body>
</html>