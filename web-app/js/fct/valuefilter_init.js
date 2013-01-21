function valueFilterChange(element) {
	var valueId = $(element).val();
	if(valueId == 0){
		$('.report-value-number').show();
		$('.report-value-percentage').hide();				
	} else if(valueId == 1) {
		$('.report-value-number').hide();
		$('.report-value-percentage').show();
	} else {
		
	}
}