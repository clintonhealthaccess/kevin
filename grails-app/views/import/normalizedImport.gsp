<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<meta name="layout" content="main" />
		<title><g:message code="import.title"/></title>
		<!-- for admin forms -->
        <r:require modules="form,chosen"/>
	</head>
	<body>
		<div class="entity-form-container togglable">
			<div class="entity-form-header">
				<h3 class="title"><g:message code="import.title"/></h3>
				<div class="clear"></div>
			</div>
				<g:form url="[controller:'normalizedImporter', action:'uploader']" useToken="true" method="post" enctype="multipart/form-data">
					<g:selectFromList name="dataElement.id" label="${message(code:'dataelement.label')}" bean="${normalizedImporter}" field="dataElement" optionKey="id" multiple="false"
					ajaxLink="${createLink(controller:'data', action:'getAjaxData', params:[class:'DataElement'])}"
					from="${dataElements}" value="${normalizedImporter?.dataElement?.id}" values="${dataElements.collect{i18n(field:it.names)+' ['+it.code+'] ['+it.class.simpleName+']'}}" />
					
					<g:selectFromList name="period.id" label="${message(code:'period.label')}" bean="${normalizedImporter}" field="period" optionKey="id" multiple="false"
					from="${periods}" value="${normalizedImporter?.period?.id}" optionValue="startDate" />
					
					<g:file bean="${normalizedImporter}"/>
					<div class="row">
						<button type="submit"><g:message code="import.button.import.label"/></button>
					</div>
				</g:form>
		</div>
	</body>
</html>