<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.label"/></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="import.label"/></h3>
				<div class="clear"></div>
			</div>
				<g:form url="[controller:'importerEntity', action:'uploader']" useToken="true" method="post" enctype="multipart/form-data">
					<g:selectFromList name="dataElement.id" label="${message(code:'dataelement.label')}" bean="${importerEntity}" field="dataElement" optionKey="id" multiple="false"
					ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
					from="${dataElements}" value="${importerEntity?.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
					
					<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${importerEntity}" field="period" optionKey="id" multiple="false"
					from="${periods}" value="${importerEntity?.period?.id}" optionValue="startDate" />
					
					<g:file name="file" label="${message(code:'import.file.label')}" bean="${importerEntity}" field="file"/>
					<div class="row">
						<button type="submit"><g:message code="default.button.import.label"/></button>
						<a href="${createLink(uri: targetURI)}"><g:message code="default.link.cancel.label"/></a>
					</div>
				</g:form>
		</div>

	</body>
</html>