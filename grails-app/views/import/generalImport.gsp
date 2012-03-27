<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.form.label" default="Data Import" /></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="import.label"/></h3>
				<div class="clear"></div>
			</div>
				<g:form url="[controller:'generalImporterEntity', action:'uploader']" useToken="true" method="post" enctype="multipart/form-data">
					<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${importerEntity}" field="period" optionKey="id" multiple="false"
					from="${periods}" value="${importerEntity?.period?.id}" optionValue="startDate" />
					
					<g:file name="file" label="${message(code:'import.file.label')}" bean="${importerEntity}" field="file"/>
					<div class="row">
						<button type="submit"><g:message code="import.button.import.label"/></button>
					</div>
				</g:form>
		</div>

	</body>
</html>