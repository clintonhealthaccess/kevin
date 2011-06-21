<%@ page import="org.chai.kevin.value.ExpressionValue.Status" %>

<div class="info">	
	<g:if test="${info.calculationValue.hasMissingValues}">
		<div class="red bold">Some values are missing.</div>
	</g:if>
	
	<div>		
		<div class="average">
			<h5>Average value:</h5>
			<span class="value">
				<g:if test="${info.number}">
					<g:formatNumber number="${info.calculationValue.average * 100}" format="#0.0"/>%
				</g:if>
				<g:else>N/A</g:else>
			</span>
			<div class="clear"></div>
		</div>
		
		<div class="values">
			<h5><a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Scores</a></h5>
			<div class="box float-left scores">
				<ul><g:each in="${info.expressionValues}" var="entry">
					<g:set var="organisation" value="${entry.key}"/>
					<g:set var="expressionValue" value="${entry.value}"/>
					<g:if test="${expressionValue != null}">
						<li>
							<div class="organisation">${organisation.name}</div>
							<div class="value">
								<g:if test="${expressionValue.numberValue != null}">
									<g:formatNumber number="${expressionValue.numberValue * 100}" format="#0.0"/>%
								</g:if>
								<g:else>
									N/A
								</g:else>
							</div>
						</li>
					</g:if>
				</g:each></ul>
			</div>
		</div>
	
		<div class="expression-explanation">
			<g:each in="${info.calculation.expressions}" var="entry">
				<g:set var="groupUuid" value="${entry.key}"/>
				<g:set var="expression" value="${entry.value}"/>
				<h5>
					<a href="#" onclick="$(this).parent().next().slideToggle(); return false;">Expression for ${groups.getGroupByUuid(groupUuid).name}</a>
				</h5>
				<div class="box float-left left-pane hidden">
					<div class="expression">
						<div>
							<a href="#" class="cluetip" title="${i18n(field: expression.names)}" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:expression.id)}"><g:i18n field="${expression.names}"/></a>
						</div>
					</div>
					<div class="equation">
						<h5>Equation</h5>
						<div>
							<div class="formula"><g:expression expression="${expression}"/></div>
						</div>
					</div>
				</div>
			</g:each>
		</div>
	
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
