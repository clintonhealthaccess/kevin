<!-- TODO fix this -->

<%@ page import="org.chai.kevin.cost.CostRampUp" %>
<table class="listing">
    <thead>
        <tr>
        	<th/>
            <g:sortableColumn property="name" title="${message(code: 'costRampUp.name.label', default: 'Name')}" />
            <th><g:message code="costRampUp.years.label" default="Years" /></th>
        </tr>
    </thead>
    <tbody>
    <g:each in="${entities}" status="i" var="costRampUp">
        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
            <td>
            	<ul class="horizontal">
	            	<li>
	            		<a class="edit-link" href="${createLinkWithTargetURI(action:'edit', id:costRampUp.id)}"><g:message code="general.text.edit" default="Edit" /></a>
	            	</li>
	            	<li>
	            		<a class="delete-link" href="${createLinkWithTargetURI(action:'delete', id:costRampUp.id)}"><g:message code="general.text.delete" default="Delete" /></a>
	            	</li>
            	</ul>
            </td>
            <td><g:i18n field="${costRampUp.names}" /></td>
            <td>${fieldValue(bean: costRampUp, field: "years")}</td>
        </tr>
    </g:each>
    </tbody>
</table>
