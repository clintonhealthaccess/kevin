<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<title><g:message code="dsrTable.view.label" default="District Summary Reports" /></title>
</head>
<body>
	<div>
		<table width="100%">
			<tr>
				<td width="20%">
					<table>
						<tr>
							<td>
								<div id="top" class="box">
									<h5 class="float">Iteration</h5>
									<span><g:dateFormat format="yyyy"
											date="${dsrTable.period.startDate}" /> </span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div id="top" class="box">
									<h5 class="float">Organisations</h5>
									<ul>
										<g:each in="${dsrTable.organisations}" var="org">
											<li>${org.name}</li>

										</g:each>
									</ul>
								</div></td>
						</tr>
					</table>
				</td>
				<td>
					<div id="top" class="box">
						<table>
							<tr>
								<td><h5 class="float">Objctives:</h5><g:i18n field="${dsrTable.objective.names}"/></td>
							</tr>
						</table>
					</div>
					<div id="right" class="box">
						<table>
							<tr>
								<td><g:i18n field="${dsrTable.objective.names}"/></td>
								<g:each in="${dsrTable.targets}" var="target">
									<td><g:i18n field="${target.names}"/></td>
								</g:each>
							</tr>
							<tr>
								<g:each in="${dsrTable.values}" var="value">
									<td>${value.key.children.name}</td>
									<g:each in="${value.value}" var="val">
										<td>${val.value.target.expression.expression}</td>
									</g:each>
								</g:each>
							</tr>
						</table>
					</div>
				</td>
			</tr>
		</table>
	</div>
</body>
</html>