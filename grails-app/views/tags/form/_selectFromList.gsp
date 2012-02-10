<g:set var="multiple" value="${multiple!=null&&multiple=='true'}"/>
<g:set var="random" value="${org.apache.commons.lang.math.RandomUtils.nextInt()}"/>

<div class="row ${hasErrors(bean:target,field:field,'errors')}">
	<g:if test="${multiple}"><input type="hidden" name="${name}" value=""/></g:if>
	<label for="${name}">${label}</label>
	<select id="options-${random}" name="${name}" ${multiple?'multiple':''}>
		<g:if test="${!multiple}"><option value="">-- Please select from the list --</option></g:if>
		<g:each in="${from}" var="item" status="i">
			<option value="${item[optionKey]}" ${(multiple?value?.contains(item[optionKey]):item[optionKey].equals(value))?'selected':''}>
				<g:if test="${values!=null}">
					${values[i]}
				</g:if>
				<g:else>
					${item[optionValue]}
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