<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="ajax" />
        <g:set var="entityName" value="${message(code: 'dashboard.explanation.label', default: 'Data element explanation')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    
    <body>

		<div class="box">
			<div><g:i18n field="${normalizedDataElement.names}"/></div>
			<div class="row">Type: <span class="type"><g:toHtml value="${normalizedDataElement.type.getDisplayedValue(2, null)}"/></span></div>
			<div><g:i18n field="${normalizedDataElement.descriptions}"/></div>
			<div class="clear"></div>
		</div>
		
		<g:if test="${!referencingData.isEmpty()}">
			<g:render template="/entity/data/referencingDataList" model="[referencingData: referencingData]"/>
		</g:if>
		
	</body>
</html>

