$(document).ready(function() {	
	categoryFilter();
	
	$('#report-category').bind('change', function() {
			categoryFilter();
	});
});	

function categoryFilter() {
	var reportCategoryId = $('#report-category').val();
	if (reportCategoryId > 0) {
		$('.dsr-target').hide();
		$('.dsr-target').each(function() {
			dsrTargetCategoryId = $(this).data('category');
			if (dsrTargetCategoryId == reportCategoryId) {
				$(this).show();
			}		
		});
	} else {
		$('.dsr-target').show();
	}
}