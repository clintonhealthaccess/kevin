<!-- Enum type question -->
<select name="select-enum-${dataElement?.id}" id="select-enum-${dataElement?.id}">
           <option value="">Select</option>
   <g:each in="${dataElement.enume?.enumOptions}" var="option">
        <option value="${option.value}"  ${option?.value==value?.value ? 'selected':''} id="select-option-${dataElement?.id}-${option?.id}">
			<g:i18n field="${option?.names}" />
		</option>
   </g:each>
</select>