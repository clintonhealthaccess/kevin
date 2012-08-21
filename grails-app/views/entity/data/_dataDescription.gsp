<%@ page import="org.chai.kevin.data.Enum" %>
<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<%@ page import="org.chai.kevin.util.Utils"%>

<div class="row">
	<span class="type"><g:message code="entity.type.label"/>:</span>
	<g:toHtml value="${data.type.getDisplayedValue(2, null)}"/>
</div>

<g:each in="${periodValues}" status="i" var="periodValue">
	<div class="row box">
		<span>${Utils.formatDate(periodValue.key.startDate)} - ${Utils.formatDate(periodValue.key.endDate)}</span>:
		<span class="bold">${periodValue.value} values</span> 
	</div>
</g:each>

<g:if test="${data.type.type == ValueType.ENUM}">
	<g:set var="enume" value="${Enum.findByCode(data.type.enumCode)}"/>
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

<div class="row"><g:i18n field="${data.descriptions}"/></div>