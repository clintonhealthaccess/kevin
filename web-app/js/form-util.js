/**
 * data element Search
 */
function getDataElement(callback){
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
					filtered.find('li').bind('mousedown', callback);
					filtered.find('li')
				}
			}
		});
		return false;
	});
}
 
/**
 * rich text content retrieve
 */
function getRichTextContent(){
	$('.rich-textarea-form').bind('click',function(){
		$('.toggle-entry textarea').each(function(){
			$(this).val($(this).prev('div').children().html())
		})
	});
}

/**
 * edition pane functionality
 */
(function($) {
	var defaults = {
		container: '.flow-container',
		addLinks: '.flow-edit,.flow-add,.flow-preview',
		deleteLinks: '.flow-delete',
		onSuccess: function(){}
	}
	
	$.fn.flow = function(options) {
	    this.config = $.extend({}, defaults, options || {}); 

		var values = this;
		var container = this.next(this.config.container).first();
		
		var self = this;
		$(document).find(this.config.addLinks).each(function(index, element){
			$(element).bind('click', function() {
				$.ajax({
					type : 'GET',
					dataType: 'json',
					url : $(this).attr('href'),
					success : function(data) {
						if (data.result == 'success') {
							$(values).slideUp(function() {
								$(container).html(data.html);
								$(container).slideDown('slow', function(){
									bindEvents(container, values, self.config.onSuccess);
								});
							});
						}
					}
				});
				return false;
			});
		});
		
		$(document).find(this.config.deleteLinks).each(function(index, element) {
			$(element).bind('click', function() {
				var del = confirm('Are you sure you want to delete this?')
				if (del) {
					$.ajax({
						type : 'POST',
						dataType: 'json',
						url : $(this).attr('href'),
						success : function() {
							location.reload();
						}
					});
				}
				return false;
			});
		});
	}
	
	function bindEvents(container, values, onSuccess) {
		var cancel = $(container).find("#cancel-button").first();
		var form = $(container).find("form").first();
		
		$(cancel).bind('click', function() {
			// $('#add-expression-cancel').click();
			clearForm(form);
			$(container).slideUp('slow', function() {
				$(values).slideDown();
			});
			return false;	
		});

		$(form).find("button[type=submit]").bind('click', function() {$(this).submit(); return false;})				
		$(form).bind('submit', function() {
			$.ajax({
				type : 'POST',
				dataType: 'json',
				data : $(this).serialize(),
				url : $(this).attr('action'),
				success : function(data, textStatus) {
					onSuccess(data);
					if (data.result == 'error') {
						$(container).html(data.html);
						bindEvents(container, values, onSuccess);
					} 
					else {
						$(container).slideUp('slow', function() {
							$(values).slideDown();
						});
						clearForm(form);
					}
				}
			});
			return false;
		});
	}

	function clearForm(form) {
		$(form).clearForm();
		$(form).find('.errors').removeClass('errors');
		$(form).find('.error-list ul').remove();
	}
	
})(jQuery);
// end of edition pane functionality
