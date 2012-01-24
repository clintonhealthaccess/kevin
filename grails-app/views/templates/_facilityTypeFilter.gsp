<div class="filter">
	<g:each in="${linkParams}" var="param">
		<g:if test="${param.key != 'action' && param.key != 'controller' && param.key != 'typeCodes'}">
			<input type="hidden" name="${param.key}" value="${param.value}"/>
		</g:if>
	</g:each>
	
	<span class='dropdown subnav-dropdown'>
	    <a class='facility selected' data-period='3' data-type='period' href='#'>
	      <g:message code="filter.facility.type.label"/>
	    </a>
	    <g:if test="${locationTypes != null && !locationTypes.empty}">
		    <div class='hidden dropdown-list'>
		      <ul class="horizontal">
		      	<g:each in="${locationTypes}" var="type">
					<li class="check_filter">
						<input name="locationTypes" type="checkbox" id="${type.id}" value="${type.id}" 
						${currentLocationTypes.contains(type)?'checked="checked"':''}/>
						<label for="${type.id}"><g:i18n field="${type.names}"/></label>
					</li>
				</g:each>	        
		      </ul>
		    </div>
	    </g:if>
	    <g:else>
			<span class="italic"><g:message code="filter.facility.type.empty.label"/></span>
		</g:else>
		<button type="submit">Filter</button>
    </span>
</div>
