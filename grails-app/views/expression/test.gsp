<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="expression.test.page.title" /></title>
        
        <r:require module="form"/>
    </head>
    <body>
    
	    <div class="entity-form-container">
			<div class="entity-form-header">
				<h3 class="title">
					<g:message code="expression.test.label"/>
				</h3>
			</div>
			<div class="forms-container">
				<div class="data-field-column">
					<g:form url="[controller:'expression', action:'doTest']" useToken="true">
						
						<g:textarea name="expression" label="${message(code:'expression.test.expression.label')}" bean="${cmd}" field="expression" value="${cmd?.expression}" height="130" />
						<g:textarea name="type" label="${message(code:'expression.test.type.label')}" bean="${cmd}" field="type" value="${cmd?.type}" height="70" />
						
						<div class="row">
							<button type="submit"><g:message code="expression.test.button.label"/></button>
						</div>
					</g:form>
				</div>
				<g:render template="/templates/searchDataElement" model="[element: 'textarea[name="expression"]', formUrl: [controller:'data', action:'getData', params:[class:'RawDataElement']]]"/>
			</div>
		</div>
    
    </body>
</html>
