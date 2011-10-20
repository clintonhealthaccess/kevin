/**
 *  nice input fields  
 */
$(document).delegate('input[type="text"],textarea','focus',function() {
	$(this).removeClass("idle-field completed-field").addClass("focus-field");
    if (this.value == this.defaultValue && this.defaultValue==''){
    	this.value = '';
	}
    if(this.value != this.defaultValue){
    	this.select();
    }
});
$(document).delegate('input[type="text"],textarea','blur',function() {
	$(this).removeClass("focus-field").addClass("idle-field");
	if (this.value != this.defaultValue){
		this.value =$.trim(this.value);
		$(this).removeClass("focus-field").addClass("completed-field");
	}
});

/**
 * language switcher functionality on admin panes
 */
$(document).delegate('.togglable a.toggle-link', 'click', function(){
	var togglable = $(this).parents('.togglable');
	var toggle = $(this).data('toggle')
	var allId = $(this).attr('id');
	$(togglable).find('.toggle-entry').each(function(key, value){
		if(allId) 
			$(this).show();
		else{ 
			if (toggle != $(this).data('toggle')) 
				$(this).hide();
			else 
			 $(this).show();
	   }
	});
	$(togglable).find('.toggle-link').each(function(key, value){
		if (toggle != $(this).data('toggle')) $(this).removeClass('no-link');
		else $(this).addClass('no-link');
	});
	return false;
});