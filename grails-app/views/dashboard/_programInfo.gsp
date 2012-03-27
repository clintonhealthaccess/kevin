<div class="info">	
	<div>		
		<div class="average">
			<span class="bold"><g:message code="dashboard.info.program.average"/>:</span>
			<span class="value">
				<g:if test="${info.numberValue != null}">
					<g:formatNumber number="${info.numberValue * 100}" format="#0.0"/>%
				</g:if>
				<g:else><g:message code="report.value.na"/></g:else>
			</span>
			<div class="clear"></div>
		</div>
	
		<div>
			<span class="bold"><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></span>
			<div class="box span">
				<table class="listing">
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th><g:message code="dashboard.info.program.weight"/></th>
							<th><g:message code="dashboard.info.program.score"/></th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${info.values}" var="entry">
							<g:set var="program" value="${entry.key}"/>
							<g:set var="percentage" value="${entry.value}"/>
							<tr>
								<th><g:i18n field="${program.entry.names}"/></th>
								<td>${program.weight}</td>
								<td>
									<g:if test="${percentage.valid}">
										<g:formatNumber number="${percentage.value * 100}" format="#0.0"/>%
									</g:if>
									<g:else>
										<g:message code="report.value.na"/>
									</g:else>
								</td>
							</tr>
						</g:each>
					</tbody>
				</ul>
			</div>
		</div>
	</div>
</div>
