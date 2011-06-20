
<%@ page import="org.chai.kevin.cost.CostRampUp" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'costRampUp.label', default: 'CostRampUp')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'costRampUp.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="name" title="${message(code: 'costRampUp.name.label', default: 'Name')}" />
                        
                            <th><g:message code="costRampUp.years.label" default="Years" /></th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${costRampUpInstanceList}" status="i" var="costRampUpInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${costRampUpInstance.id}">${fieldValue(bean: costRampUpInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: costRampUpInstance, field: "name")}</td>
                        
                            <td>${fieldValue(bean: costRampUpInstance, field: "years")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${costRampUpInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
