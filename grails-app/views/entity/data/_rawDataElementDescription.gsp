<%@ page import="org.chai.kevin.data.Enum" %>
<%@ page import="org.chai.kevin.data.Type.ValueType" %>

<div class="row">
	<span class="type"><g:message code="type.label" default="Type"/>:</span>
	<g:toHtml value="${rawDataElement.type.getDisplayedValue(2, null)}"/>
</div>

<g:if test="${rawDataElement.type.type == ValueType.ENUM}">
	<g:set var="enume" value="${Enum.findByCode(rawDataElement.type.enumCode)}"/>
	<div class="row enum box">
		<h5><g:i18n field="${enume.names}"/></h5>
		<ul>
			<g:each in="${enume.enumOptions}" var="enumOption">
				<table>
					<tr>
						<td><g:i18n field="${enumOption.names}"/>: </td>
						<td class="bold">${enumOption.value}</td>
					</tr>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>

<div class="row"><g:i18n field="${rawDataElement.descriptions}"/></div>