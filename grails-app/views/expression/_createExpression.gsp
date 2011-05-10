<div id="add-expression" class="entity-form-container">
	<div id="add-expression-col">
		<g:form url="[controller:'expression', action:'save']" useToken="true">
			<div class="row ${hasErrors(bean:expression,field:'name','errors')}">
				<label for="name">Name</label>		
				<input name="name" value="${fieldValue(bean:expression,field:'name')}"></input>
				<div class="error-list"><g:renderErrors bean="${expression}" field="name" /></div>
			</div>
			<div class="row ${hasErrors(bean:expression,field:'description','errors')}">
				<label for="description">Description</label>
				<textarea name="description" rows="5">${fieldValue(bean:expression,field:'description')}</textarea>
				<div class="error-list"><g:renderErrors bean="${expression}" field="description" /></div>
			</div>
			
			<div class="row">
				<div class="${hasErrors(bean:expression,field:'expression','errors')}">
					<label for="expression">Expression</label>
					<textarea name="expression" id="expression" rows="5">${fieldValue(bean:expression,field:'expression')}</textarea>
					<div class="error-list"><g:renderErrors bean="${expression}" field="expression" /></div>
				</div>
			</div>
			<g:if test="${expression.id != null}">
				<input type="hidden" name="id" value="${expression.id}"></input>
			</g:if>
			
			<input type="hidden" name="type" value="VALUE"></input>
			
			<div class="row">
				<button type="submit">Save Indicator</button>
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
		    	<label for="dataSetFilter">Categories: </label>
				<select name="dataSetFilter">
					<option value="all">-- all categories --</option>
					<g:each in="${dataSets}" var="dataSet">
						<option value="${dataSet.id}">${dataSet.name}</option>
					</g:each>
				</select>
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

	$('.search-form').bind('submit', function() {
		var element = this;
		$.ajax({
			type: 'GET', data: $(element).serialize(), url: $(element).attr('action'), 
			success: function(data, textStatus){
				if (data.result == 'success') {
					$(element).parent('div').find('.filtered').html(data.html);
					$(element).parent('div').find('.filtered a').cluetip(cluetipOptions);
				}
			}
		});
		return false;
	});


	$('.filtered li')
	.live('mousedown',
		function(event){
			if ($('.in-edition').size() == 1) {
				var edition = $('.in-edition')[0]
				$(edition).replaceSelection('['+$(this).data('code')+']');
			}
		}
	);
	$('#expression')
	.live('click keypress focus',
		function(){
			$(this).addClass('in-edition');
		}
	)
	.live('blur',
		function(){
			$(this).removeClass('in-edition');
		}
	);
});
</script>