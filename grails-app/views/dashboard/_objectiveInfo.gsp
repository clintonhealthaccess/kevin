<div class="info">	
	<div>		
		<div class="average">
			<h5>Average value:</h5>
			<span class="value">
				<g:if test="${info.number}">
					<g:formatNumber number="${info.numberValue * 100}" format="#0.0"/>%
				</g:if>
				<g:else>N/A</g:else>
			</span>
			<div class="clear"></div>
		</div>
	
		<div class="values">
			<h5><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></h5>
			<div class="box float-left scores">
				<ul>
					<li class="header">
						<div class="objective">&nbsp;</div>
						<div class="weight">Weight</div>
						<div class="value">Score</div>
					</li>
					<g:each in="${info.values}" var="entry">
						<g:set var="objective" value="${entry.key}"/>
						<g:set var="percentage" value="${entry.value}"/>
						<li>
							<div class="objective"><g:i18n field="${objective.entry.names}"/></div>
							<div class="weight">${objective.weight}</div>
							<div class="value">
								<g:if test="${percentage.valid}">
									<g:formatNumber number="${percentage.value * 100}" format="#0.0"/>%
								</g:if>
								<g:else>
									N/A
								</g:else>
							</div>
						</li>
					</g:each>
				</ul>
			</div>
		</div>
	</div>
</div>
