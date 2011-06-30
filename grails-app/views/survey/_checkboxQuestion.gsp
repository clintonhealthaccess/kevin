<g:i18n field="${question.names}" />
<span> 
	<ul>
		<g:each in="${question.options}" var="option">
		    <li>
			   <g:render template="/survey/${option.dataElement.type}" model="[value: dataValues[option.dataElement],dataElement: option.dataElement]" />
			   <g:i18n field="${option.names}"/>
		    </li>
		</g:each>
	</ul>		
</span>