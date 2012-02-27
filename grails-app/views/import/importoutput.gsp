<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.form.label" default="Data Import" /></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="main">
			<div class="main">
				<ul>
					<li><g:message code="result.message.saved.rows" default="Number of saved rows" />: ${errorManager.numberOfSavedRows}</li>
					<li><g:message code="result.message.unsaved.rows" default="Number of unsaved rows" />: ${errorManager.numberOfUnsavedRows}</li>
					<li><g:message code="result.message.error.saved.rows" default="Number of rows saved with error" />: ${errorManager.numberOfRowsSavedWithError}</li>
				</ul>
			</div>	
			<div class="main">
				<ul class="import-error-list-group">
					<g:set var="j" value="${0}"/>
					<g:each in="${errorManager.errors}" var="error" status="i">
						<g:if test="${j!=error.lineNumber}">
							<g:set var="j" value="${error.lineNumber}"/>
							<li><g:message code="label.line.number" default="Line Number" />: ${error.lineNumber}<li>
						</g:if> 
						<li>
							<ul class="import-error-list">
								<li><g:message code="${error.messageCode}"/> <g:message code="label.column" default="Column" />: ${error.header}</li>
							</ul>
						</li>	
					</g:each>
				</ul>
			</div>
		</div>
	</body>
</html>