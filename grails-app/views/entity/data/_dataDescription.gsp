<%@ page import="org.chai.kevin.data.Enum" %>
<%@ page import="org.chai.kevin.data.Source" %>
<%@ page import="org.chai.kevin.data.Type.ValueType" %>
<%@ page import="org.chai.kevin.util.DataUtils"%>

<div class="row">
	<span class="type"><g:message code="entity.type.label"/>:</span>
	<g:toHtml value="${data.type.getDisplayedValue(2, null)}"/>
</div>

<div class="row">
	<span class="type"><g:message code="source.label"/>:</span>
	<g:each in="${data.sources}" var="sourceCode" status="i">
		<g:set var="source" value="${Source.findByCode(sourceCode)}"/>
		<g:if test="${source}">
			<g:i18n field="${source.names}"/>
			<g:if test="${i < data.sources.size() - 1}">,</g:if>
		</g:if>
	</g:each>
</div>

<g:each in="${periodValues}" status="i" var="periodValue">
	<div class="row box">
		<span>${DataUtils.formatDate(periodValue.key.startDate)} - ${DataUtils.formatDate(periodValue.key.endDate)}</span>:
		<span class="bold">${periodValue.value} values</span>
		<g:if test="${valuesWithError.containsKey(periodValue.key)}">
			, with error: <span class="bold">${valuesWithError[periodValue.key]} values</span>
		</g:if> 
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