/**
 * nice table functionality
 */
var selected = false;

function addClass(column, row, className) {
	if (column != null) {
		addClassByType('col', column, className);
	}
	if (row != null) {
		addClassByType('row', row, className);
	}
}

function addClassByType(type, id, className) {
	$('.'+type+'-'+id).addClass(className);
}    

$(document).ready(function(){

	/**
	 * nice tables
	 */
	$('.nice-table .cell').bind({
		mouseenter: function(){
			$('.cell').removeClass('highlighted');
			addClass($(this).data('col'), $(this).data('row'), 'highlighted');
		},
		mouseleave: function(){
			if (selected) {
				$('.cell').removeClass('highlighted');
			}
			else {
				$('.cell.value').addClass('highlighted');
				$('.cell.label').removeClass('highlighted');
			}
		},
		click: function(){
			var row = $(this).data('row');
			var col = $(this).data('col');
		
			if ($(this).hasClass('me-selected')) {
				deselect = true
				selected = false;
			}
			else {
				deselect = false;
				selected = true;
			}
			$('.cell').removeClass('me-selected');
			$('.cell').removeClass('selected');
		
			if (!deselect) {
				$(this).addClass('me-selected');
				addClass(col, row, 'selected');
			}
		}
	});
	
})