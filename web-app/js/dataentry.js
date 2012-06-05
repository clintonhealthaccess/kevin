function DataEntry (settings) {
	this.element = settings.element;
	this.settings = settings;
	
	this.initializeSurvey();
}

DataEntry.prototype.initializeSurvey = function() {
	var self = this;

	this.element.delegate('form .element-enum select', 'change', function() {
		var element = $(this).parents('.element').first();
		showEnumOptionDescription(element);
	});
	this.element.find('form .element-enum').each(function(){
		showEnumOptionDescription(this);
	});

	this.element.delegate('form .input', 'change', function(){
		var element = $(this).parents('.element').first();
		self.surveyValueChanged(element, $(element).find('.input'), self.settings.callback);
	});
	this.element.delegate('form .ajax-error .input', 'focus', function(){
		var element = $(this).parents('.element').first();
		self.surveyValueChanged(element, $(element).find('.input'), self.settings.callback);
	});
	this.element.delegate('form a.outlier-validation', 'click', function(){
		$(this).next().val($(this).data('rule'));						
		var element = $(this).parents('.element').first();
		self.surveyValueChanged(element, $(element).find('.input'), self.settings.callback);						
		return false;
	});
	this.element.delegate('form .element-list-add', 'click', function(){
		if (!$(this).hasClass('ajax-disabled')) {
			self.listAddClick(this, self.settings.callback);
		}
		return false;
	});
	this.element.delegate('form .element-list-remove', 'click', function(){
		if (!$(this).hasClass('ajax-disabled')) {
			var remove = confirm(self.settings.messages['default.link.delete.confirm.message']);
			if (remove) self.listRemoveClick(this);
		}
		return false;
	});
	this.element.delegate('form .element-list-minimize', 'click', function(){
		minimizeRows($(this).parents('.element-list-row'));
		return false;
	});
	this.element.delegate('form .element-list-maximize', 'click', function(){
		maximizeRow($(this).parents('.element-list-row').first());
		return false;
	});

	this.element.delegate('button[type=submit]', 'click', function(){
		if (self.ajaxCallsInProgress()) {
			alert(self.settings.messages['dataentry.exit.saving.alert.text']);
			return false;
		}
		else if (this.element.find('.incomplete').length > 0) {
			return confirm(self.settings.messages['dataentry.exit.incomplete.confirm.text']);
		}
	});
	this.element.delegate('button[type=cancel]', 'click', function(){
		$.manageAjax.clear(self.queueName, true);
		$('.ajax-in-process').find('.input').removeAttr('disabled');
		$('.ajax-in-process').addClass('ajax-error');
		$('.ajax-in-process').removeClass('.ajax-in-process')
		return false;
	});

	// stop all calls to external links while values are saving
	$(document).delegate('a', 'click', function(){
		if (self.ajaxCallsInProgress() || $('.element.ajax-error').length > 0) {
			return confirm(this.settings.messages['dataentry.exit.saving.confirm.text']);
		}
	});
	
	this.queueName = 'queue'+Math.floor(Math.random()*11);
	this.surveyQueue = $.manageAjax.create(this.queueName, {
		url : this.settings.url,
		type : 'POST',
		dataType: 'json',
		// ajax queue options
		queue: true,
		cacheResponse: false
	});


	minimizeRows(this.element.find('.element-list-row'));		
	this.enableAfterLoading();
}

DataEntry.prototype.enableAfterLoading = function() {
	this.element.find('.loading-disabled').each(function(index, disabled) {
		if (!$(disabled).parents('.element').first().hasClass('skipped')) {
			$(disabled).removeClass('loading-disabled').removeAttr('disabled');
		}
	})
}

DataEntry.prototype.surveyValueChanged = function(element, inputs, callback) {
	var elementId = $(element).data('element');
	var suffix = $(element).data('suffix');
	
	var data = '';
	data += 'element='+elementId;
	data += '&suffix='+suffix;
	
	// we send all the modified inputs
	$.each(inputs, function(i, input) {
		data += '&'+$(input).serialize();
	})
	// we send the fields that should always be sent
	this.element.find('.js_always-send').each(function(i, input) {
		data += '&'+$(input).serialize();
	});
	
	$(element).removeClass('ajax-error');
	$(element).addClass('ajax-in-process');
	$(element).find('.input').attr('disabled', 'disabled');
	
	var self = this;
	
	// we add the request to the queue
	$.manageAjax.add(this.queueName, {
		data : data,
		success : function(data, textStatus) {
			$(element).removeClass('ajax-in-process');
			$(element).find('.input').removeAttr('disabled');
			
			self.toggleControls($.queue(document, self.queueName).length > 0);
			
			if (data.status == 'success') {
				// we reset null elements
				$.each(data.elements, function(elementIndex, element) {
					$.each(element.nullPrefixes, function(prefixIndex, prefix) {
						var elementToCheck = escape('#element-'+element.id+'-'+prefix);
						if (!$(elementToCheck).hasClass('ajax-in-process') && !$(elementToCheck).hasClass('ajax-error')) {
							$(elementToCheck).find('textarea, input, select').attr('value', '');
						}
					});
				});
		
				// we go through all changed elements
				$.each(data.elements, function(index, element) {
					
					// we remove all the skips
					$('#element-'+element.id).find('.element').removeClass('skipped').find('.input').removeAttr('disabled');
					
					// we add them again
					$.each(element.skipped, function(index, skipped) {
						$('#element-'+element.id).find('#element-'+element.id+'-'+escape(skipped))
						.addClass('skipped').find('.input').attr('disabled', 'disabled');
					});
					
					// we remove all the errors
					$('#element-'+element.id).find('.element-list-row').removeClass('row-errors');
					$('#element-'+element.id).find('.element').removeClass('errors');
					$('#element-'+element.id).find('.element').children('.error-list').html('');
					
					// we add them again
					$.each(element.invalid, function(index, invalid) {
						if (!invalid.valid) $('#element-'+element.id).find('#element-'+element.id+'-'+escape(invalid.prefix)).addClass('errors');
						$('#element-'+element.id).find('#element-'+element.id+'-'+escape(invalid.prefix)).children('.error-list').html(invalid.errors);
						$('#element-'+element.id).find('#element-'+element.id+'-'+escape(invalid.prefix)).parents('.element-list-row').first().addClass('row-errors');
					});
					
				});
			}
			callback(self, data, element);
		},
		error: function() {
			$(element).removeClass('ajax-in-process');
			$(element).find('.input').removeAttr('disabled');
			
			$(element).addClass('ajax-error');
			self.toggleControls($.queue(document, self.queueName).length > 0);
		},
		complete: function() {
			if (self.settings.trackEvent) _gaq.push(['_trackEvent', 'survey', 'save']);
		}
	});
	
	this.toggleControls(this.ajaxCallsInProgress());
}

DataEntry.prototype.ajaxCallsInProgress = function() {
	return $.queue(document, this.queueName).length > 0 || this.surveyQueue.inProgress > 0;
}

DataEntry.prototype.toggleControls = function(hide) {
	if (hide) {
		this.element.find('.element-list-add, .element-list-remove, button[type=submit]').addClass('ajax-disabled').attr('disabled', 'disabled');
		this.element.find('button[type=cancel]').show();
	}
	else {
		this.element.find('.element-list-add, .element-list-remove, button[type=submit]').removeClass('ajax-disabled').removeAttr('disabled');
		this.element.find('button[type=cancel]').hide();
	}
}

DataEntry.prototype.listRemoveClick = function(toRemove) {
	var element = $(toRemove).parents('.element').first();
	var index = $(toRemove).parents('.element-list-row').first().data('index');
	$(toRemove).parents('.element-list-row').find('.js_list-input').remove();

	this.surveyValueChanged(element, $(element).find('.js_list-input'), function(dataEntry, data, element) {

		$(toRemove).parents('.element-list-row').first().remove();	
	});
}

DataEntry.prototype.listAddClick = function(list, callback) {
	var suffix = $(list).parents('.element').first().data('suffix');

	var clone = $(list).prev().clone(true);
	var index = $(list).prev().prev().data('index');
	if (index == null) index = "0";
	else index = parseInt(index)+1;

	// we change the html	
	$(clone).find(".js_list-input").first().val('['+index+']')
	$(clone).find(".js_list-input-indexes").first().val('['+index+']')
	var copyHtml = clone.html().replace(RegExp(escape(suffix)+'\\[_\\]', 'g'), suffix+'['+index+']')

	$(list).prev().before(copyHtml);
	$(list).prev().prev().data('index', index);
	
	var self = this;

	this.surveyValueChanged($(list).parents('.element').first(), $(list).parents('.element').first().find('.js_list-input'), function(dataEntry, data, element) {
		$(list).prev().prev().show();

		maximizeRow($(list).prev().prev());
		callback(self, data, element);
	});
}

function showEnumOptionDescription(element) {
	var enumId = $(element).find('select').data('enum');
	var optionId = $(element).find('option:selected').data('option');
	$(element).find('.option-description').hide();
	$(element).find('#enum-'+enumId+'-option-'+optionId).show();
}

function getId(array, id) {
	var result = null;
	$.each(array, function(index, value){
		if (value.id == id) result = value;
	});
	return result;
}

function escape(myid) { 
	return myid.replace(/(:|\.|\[|\])/g,'\\$1');
}

function minimizeRows(rows, keepOpen) {
	$.each(rows, function(i, element) {
		if (!$(element).hasClass('minimized') && $(element).data('index') != $(keepOpen).data('index')) {
			$(element).find('.input').each(function(i, input) {
				var elementInRow = $(input).parents('.element').first();
				if (!$(elementInRow).hasClass('skipped')) {
					var value = $(input).attr('value');
					if ($(input).prop('nodeName') == 'SELECT' && $(input).attr('value')) {
						value = $(input).find("option:selected").html();
					}
					$(input).after('<span class="minimized-input" onclick="maximizeRow($(this).parents(\'.element-list-row\')); return false;">'+value+'</span>');
				}
			});
			
			$(element).find('.input').hide();
			$(element).find('label, h5, h6').hide();
			$(element).find('.adv-form-section').hide();
			$(element).find('.adv-form-title').hide();
			$(element).find('.element-map').addClass('adv-form-mini');
			$(element).find('.element-list-minimize').hide();
			$(element).find('.element-list-maximize').show();
			$(element).addClass('minimized');
		}
	});
}

function maximizeRow(row) {
	$(row).find('.minimized-input').remove();
	$(row).find('.input').show();
	$(row).find('label, h5, h6').show();
	$(row).find('.adv-form-section').show();
	$(row).find('.adv-form-title').show();
	$(row).find('.element-map').removeClass('adv-form-mini');
	$(row).find('.element-list-minimize').show();
	$(row).find('.element-list-maximize').hide();
	$(row).removeClass('minimized');
	
	minimizeRows($(row).parents('.element-list').first().find('.element-list-row'), row);
}
