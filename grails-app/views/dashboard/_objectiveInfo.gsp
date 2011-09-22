<div class="info">	
	<div>		
		<div class="average">
			<span class="bold">Average value:</span>
			<span class="value">
				<g:if test="${info.numberValue != null}">
					<g:formatNumber number="${info.numberValue * 100}" format="#0.0"/>%
				</g:if>
				<g:else>N/A</g:else>
			</span>
			<div class="clear"></div>
		</div>
	
		<div>
			<span class="bold"><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></span>
			<div class="box span">
				<table>
					<thead>
						<tr>
							<th>&nbsp;</th>
							<th>Weight</th>
							<th>Score</th>
						</tr>
					</thead>
					<tbody>
						<g:each in="${info.values}" var="entry">
							<g:set var="objective" value="${entry.key}"/>
							<g:set var="percentage" value="${entry.value}"/>
							<tr>
								<th><g:i18n field="${objective.entry.names}"/></th>
								<td>${objective.weight}</td>
								<td>
									<g:if test="${percentage.valid}">
										<g:formatNumber number="${percentage.value * 100}" format="#0.0"/>%
									</g:if>
									<g:else>
										N/A
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
