<div class="row ${hasErrors(bean:bean,field:'type','errors')}">
	<label for="${name}">${message(code:'expression.test.type.label')}</label>
	
	<div class="push-10 js_type-editor">
		<ul class="horizontal">
			<li>
				<a href="#">number</a>
				<span class="hidden">type { number }</span>
			</li>
			<li>
				<a href="#">bool</a>
				<span class="hidden">type { bool }</span>
			</li>
			<li>
				<a href="#">string</a>
				<span class="hidden">type { string }</span>
			</li>
			<li>
				<a href="#">text</a>
				<span class="hidden">type { text }</span>
			</li>
			<li>
				<a href="#">date</a>
				<span class="hidden">type { date }</span>
			</li>
			
			<li>
				<a href="#">enum</a>
				<span class="hidden">type { enume 'insertEnumCode' }</span>
			</li>
			<li>
				<a href="#">map</a>
				<span class="hidden">type { map 
				    insertName : insertValue
				}</span>
			</li>
			<li>
				<a href="#">nominative table</a>
				<span class="hidden">type { list  
				    type { map
					    insertName : insertValue
					}
				}</span>
			</li>
		</ul>
	</div>
	<textarea class="input" type="text" name="${name}" rows="30" 
		${readonly?'readonly="readonly"':''} style="height:70px;">${params.typeBuilderError!=null?params.typeBuilderString:bean?.type?.getDisplayedValue(4, null)}</textarea>
	<div class="error-list"><g:renderErrors bean="${bean}" field="type" /></div>
</div>
<script type="text/javascript">
	$(document).ready(function() {
		 $('.js_type-editor a').bind('click', function() {
		 	$('textarea[name=${name.replaceAll('\\.','\\\\\\\\.')}]').replaceSelection($(this).next().html());
		 	return false;
		 });
	});
</script>