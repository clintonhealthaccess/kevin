<div class="filter">
	<g:each in="${linkParams}" var="param">
		<g:if test="${param.key != 'action' && param.key != 'controller' && param.key != 'typeCodes'}">
			<input type="hidden" name="${param.key}" value="${param.value}"/>
		</g:if>
	</g:each>
	
	<span class="js_dropdown dropdown">
		<a class='facility selected' data-period='3' data-type='period' href='#'>
			<g:message code="filter.facility.type.label"/>
		</a>
		<g:if test="${locationTypes != null && !locationTypes.isEmpty()}">
			<div class='hidden dropdown-list' id='js_facility_filter'>
				<ul>
					<li><a id="js_checkall" class="dropdown-check" href="">Check all</a><a id="js_uncheckall" class="dropdown-check" href="">Uncheck all</a></li>
						<g:each in="${locationTypes}" var="type">
							<li class="check_filter">
								<input name="locationTypes" type="checkbox" id="${type.id}" value="${type.id}" ${currentLocationTypes.contains(type)?'checked="checked"':''}/>
								<label for="${type.id}"><g:i18n field="${type.names}"/></label>
							</li>
						</g:each>	        
					<li><button type="submit">Filter</button></li>
				</ul>
			</div>
		</g:if>
	    <g:else>
			<span class="italic"><g:message code="filter.facility.type.empty.label"/></span>
		</g:else>
		
    </span>
</div>
