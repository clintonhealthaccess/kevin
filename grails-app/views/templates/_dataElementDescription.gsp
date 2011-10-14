<%@ page import="org.chai.kevin.data.Enum" %>
<%@ page import="org.chai.kevin.data.Type.ValueType" %>

<div class="row">Type: <span class="type"><g:toHtml value="${dataElement.type.getDisplayedValue(2, null)}"/></span></div>

<g:if test="${dataElement.type.type == ValueType.ENUM}">
	<g:set var="enume" value="${Enum.findByCode(dataElement.type.enumCode)}"/>
	<div class="row enum box">
		<h5><g:i18n field="${enume.names}"/></h5>
		<ul>
			<g:each in="${enume.enumOptions}" var="enumOption">
				<li>
					<div class="name"><g:i18n field="${enumOption.names}"/></div>
					<div class="value">${enumOption.value}</div>
				</li>
			</g:each>
		</ul>
	</div>
</g:if>

<div class="row"><g:i18n field="${dataElement.descriptions}"/></div>

