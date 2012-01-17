<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.form.label" default="Data Import" /></title>
	</head>
	<body>
		<div class="entity-form-container togglable">
		<div class="entity-form-header">
			<h3 class="title">Import Data</h3>
			<div class="clear"></div>
		</div>
			<g:form url="[controller:'importerEntity', action:'uploader']" useToken="true" method="post" enctype="multipart/form-data">
				<g:selectFromList name="period.id" label="Period" bean="${importerEntity}" field="period" optionKey="id" multiple="false"
				from="${periods}" value="${period?.id}" optionValue="startDate" />
				<g:file name="file" label="File" bean="${importerEntity}" field="file"/>
				<div class="row">
					<button type="submit"><g:message code="default.button.import.label" default="Import"/></button>&nbsp;&nbsp;
					<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label" default="Cancel"/></a>
				</div>
			</g:form>
		</div>
	</body>
</html>