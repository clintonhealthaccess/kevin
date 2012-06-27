function valueFilterChange(element) {
	var valueId = $(element).val();
	if(valueId == 0){
		$('.report-location-value').show();
		$('.report-location-percentage').hide();				
	} else if(valueId == 1) {
		$('.report-location-value').hide();
		$('.report-location-percentage').show();
	} else {
		
	}
}