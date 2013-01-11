<%@page import="org.chai.kevin.util.DataUtils"%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title><g:message code="expression.test.page.title" /></title>
        
        <r:require modules="form,error"/>
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
						
						<g:render template="/templates/typeEditor" model="[bean: cmd, name: 'type']"/>
						
						<g:selectFromList name="typeCodes" label="${message(code:'entity.datalocationtype.label')}" bean="${cmd}" field="typeCodeString" 
							from="${types}" value="${cmd==null?types*.code:cmd*.typeCodes*.toString()}" values="${types.collect{i18n(field:it.names)}}" optionKey="code" multiple="true"/>
						
						<g:selectFromList name="periodId" label="${message(code:'expression.test.periods.label')}" bean="${cmd}" field="periodId" 
							from="${periods}" value="${cmd==null?(periods.size() > 0?periods[periods.size()-1].id:''):cmd?.periodId}" values="${periods.collect{DataUtils.formatDate(it.startDate)+' to '+DataUtils.formatDate(it.endDate)}}" optionKey="id"/>
						
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
