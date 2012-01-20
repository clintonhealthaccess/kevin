$(document).ready(function() {
	/**
	 * facility type switcher
	 **/
	$('#facility-type-filter input').bind('click', function() {
		toggleFacilityType();
	});
	
	toggleFacilityType();
});

/**
 * facility type checkboxes
 */
function toggleFacilityType() {
	if ($('#facility-type-filter input').size() != 0) {
		var checked = [];
		$('#facility-type-filter input').each(function(){
			if (this.checked) checked.push($(this).val())
		});
		checked.push('Total');
		$('.row.location').each(function(){
			if($.inArray($(this).data('group'), checked) >= 0) 
				$(this).show();
			else 
				$(this).hide()
		});
	}
}