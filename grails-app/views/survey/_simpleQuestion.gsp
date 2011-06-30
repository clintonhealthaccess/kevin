<%@ page import="org.chai.kevin.ValueType" %>
<g:i18n field="${question.names}" />
<span> 
	<g:each in="${dataValues}" var="value">
					<g:render template="/survey/${value.key.type}" model="[value: value.value, dataElement: value.key]" />
		</g:each>		
</span>

