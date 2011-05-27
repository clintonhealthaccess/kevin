<div id="add-expression" class="entity-form-container togglable">
	
	<div class="entity-form-header">
		<h3 class="title">Expression</h3>
		<g:locales/>
		<div class="clear"></div>
	</div>
	
	<div id="add-expression-col">
		<g:form url="[controller:'expression', action:'save']" useToken="true">
			<g:i18nInput name="names" bean="${expression}" value="${expression.names}" label="Name" field="names"/>
			<g:i18nTextarea name="descriptions" bean="${expression}" value="${expression.descriptions}" label="Description" field="descriptions"/>
			<g:input name="code" label="Code" bean="${expression}" field="code"/>
			<g:textarea name="expression" label="Expression" bean="${expression}" field="expression" rows="5"/>
			
			<g:if test="${expression.id != null}">
				<input type="hidden" name="id" value="${expression.id}"></input>
			</g:if>
			
			<input type="hidden" name="type" value="VALUE"></input>
			
			<div class="row">
				<button type="submit">Save Expression</button>
				<button id="cancel-button">Cancel</button>
			</div>
		</g:form>
	</div>
	<div id="data-col">
		<g:form name="search-data-form" class="search-form" url="[controller:'expression', action:'getData']">
			<div class="row">
				<label for="searchText">Search: </label>
		    	<input name="searchText"></input>
		    </div>
			<div class="row">
				<label for="type">Search for: </label>
				<input class="radio" type="radio" name="type" value="data-element" checked="checked"/>Data elements
				<input class="radio" type="radio" name="type" value="constant"/>Constants
			</div>
			<div class="row">
				<button type="submit">Search</button>
				<div class="clear"></div>
			</div>
		</g:form>
		
	    <ul class="filtered" id="data"></ul>
	</div>
	
	<div class="clear"></div>
</div>
<script type="text/javascript">
$(document).ready(function() {

	$('.search-form button').bind('click', function(){$(this).submit(); return false;});
	$('.search-form').bind('submit', function() {
		var element = this;
		$.ajax({
			type: 'GET', data: $(element).serialize(), url: $(element).attr('action'), 
			success: function(data, textStatus){
				if (data.result == 'success') {
					var filtered = $(element).parent('div').find('.filtered');
					
					filtered.html(data.html);
					filtered.find('a.cluetip').cluetip(cluetipOptions);
					
					filtered.find('li').bind('mousedown',
						function(event){
							if ($('.in-edition').size() == 1) {
								var edition = $('.in-edition')[0]
								$(edition).replaceSelection('['+$(this).data('code')+']');
							}
						}
					);
					
					filtered.find('li')
				}
			}
		});
		return false;
	});

	$('#add-expression textarea')
	.bind('click keypress focus',
		function(){
			$(this).addClass('in-edition');
		}
	)
	.bind('blur',
		function(){
			$(this).removeClass('in-edition');
		}
	);
});
</script>