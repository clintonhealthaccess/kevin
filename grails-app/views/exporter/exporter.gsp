<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="export.form.label" default="Data Export" /></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="export.title" default="Data Export" /></h3>
				<div class="clear"></div>
			</div>
				<g:form url="[controller:'exporter', action:'export', params:[targetURI: targetURI]]]" useToken="true">
					<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${exporter}" field="period" optionKey="id" multiple="true"
					from="${periods}" value="${exporter?.period?.id}" optionValue="startDate" />
					<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${exporter}" field="period" optionKey="id" multiple="true"
					from="${periods}" value="${exporter?.period?.id}" optionValue="startDate" />
					
					<div class="row">
						<button type="submit"><g:message code="export.button.import.label"/></button>
					</div>
				</g:form>
		</div>

	</body>
</html>