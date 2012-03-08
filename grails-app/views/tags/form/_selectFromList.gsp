<g:set var="multiple" value="${multiple!=null&&multiple=='true'}"/>
<g:set var="random" value="${org.apache.commons.lang.math.RandomUtils.nextInt()}"/>

<div class="row ${hasErrors(bean:bean, field:field, 'errors')}">
	<g:if test="${multiple}"><input type="hidden" name="${name}" value=""/></g:if>
	<label for="${name}">${label}</label>
	<select id="options-${random}" name="${name}" ${multiple?'multiple':''} style="min-width:300px">
		<g:if test="${!multiple}"><option value="">-- Please select from the list --</option></g:if>
		<g:each in="${from}" var="item" status="i">
			<g:set value="${optionKey!=null?item[optionKey]:item}" var="option"/>
			<option value="${option}" ${(multiple?value?.contains(option):option.equals(value))?'selected':''}>
				<g:if test="${values!=null}">
					${values[i]}
				</g:if>
				<g:elseif test="${optionValue!=null}">
					${item[optionValue]}
				</g:elseif>
				<g:else>
					${item}
				</g:else>
			</option>
		</g:each>
	</select>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>
<g:if test="${ajaxLink}">
	<script type="text/javascript">
		$(document).ready(function() {
			<!-- TODO change ID -->	
			$("#options-${random}").ajaxChosen({
				type : 'GET',
				dataType: 'json',
				url : "${ajaxLink}"
			}, function (data) {
				var terms = {};
				$.each(data.elements, function (i, val) {
					terms[val.key] = val.value;
				});
				return terms;
			});
		});
	</script>
</g:if>