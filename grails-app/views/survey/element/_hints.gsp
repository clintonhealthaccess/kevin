<%@ page import="org.chai.kevin.data.Type.ValueType" %>

<g:if test="${showHints}">
	<div class="admin-hint">
	Element: ${element.id}
	-
	Prefix: ${suffix}
	-
	Data Element: <a href="${createLink(controller:element.dataElement.class.simpleName, action:'edit', params:[id: element.dataElement.id])}">${element.dataElement.code}</a>
	<g:if test="${element.dataElement.type.getType(suffix).type == ValueType.ENUM}">
		<g:set var="code" value="${element.dataElement.type.getType(suffix).enumCode}"/>
		-
		<g:if test="${code != null}">
			<g:set var="enume" value="${enums?.get(code)}"/>
			Enum: 
			<g:if test="${enume != null}">
				<a href="${createLink(controller:'enumOption', action:'list', params:['enume.id': enume.id])}">${code}</a>
			</g:if>
			<g:else>
				${code}
			</g:else>
		</g:if>
		<g:else>
			No ENUM code.
		</g:else>
	</g:if>
	</div>
</g:if>