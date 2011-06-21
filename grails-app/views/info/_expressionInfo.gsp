<%@ page import="org.chai.kevin.value.ExpressionValue.Status" %>
<div class="info">
	<g:if test="${info.expressionValue.status == Status.MISSING_VALUE}">
		<div class="red bold">Some values are missing.</div>
	</g:if>
	<g:if test="${info.value != null && !info.number}">
		<div class="red bold">The expression is invalid.</div>
	</g:if>
	<div>
		<div class="left-pane">
			<div class="box float-left">
				<div class="expression">
					<div>
						<a href="#" class="cluetip" title="${i18n(field: info.expression.names)}" onclick="return false;" rel="${createLink(controller:'expression', action:'getDescription', id:info.expression.id)}"><g:i18n field="${info.expression.names}"/></a>
					</div>
				</div>
				<div class="equation">
					<h5>Equation</h5>
					<div>
						<div class="formula"><g:expression expression="${info.expression}"/></div>
						<div class="value">
							<g:if test="${info.number}">
								<g:formatNumber number="${info.expressionValue.numberValue * 100}" format="#0.0"/>%
							</g:if>
							<g:else>N/A</g:else>
						</div>
					</div>
				</div>
			</div>
		</div>
		<g:if test="${info.valuesForOrganisation != null}">
			<div class="right-pane">
				<div class="box float-left">
					<h5>Data</h5>
					<ul><g:each in="${info.valuesForOrganisation}" var="data">
						<li class="data-${data.key.id} data">
							<div class="id">[${data.key.id}]</div>
							<div class="name">
							<a	onclick="return false;" title="${i18n(field:data.key.names)}" class="cluetip"
								href="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: data.key.id])}"
								rel="${createLink(controller:'expression', action:'getDataElementDescription', params:[dataElement: data.key.id])}">
								<g:i18n field="${data.key.names}"/>
							</a>
							</div>
							<g:if test="${data.value == null}">
								<div class="value red">
									N/A
								</div>
							</g:if>
							<g:else>
								<div class="value">
									${data.value.value}
								</div>
							</g:else>
						</li>
					</g:each></ul>
				</div>
			</div>
		</g:if>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function(){
	$(this).find('a.cluetip').cluetip(cluetipOptions);	
})
</script>
