modules = {
	
	core {
		dependsOn 'jquery, spinner'
		
		resource url: '/css/screen.css'
		resource url: '/css/print.css', attrs: [media: 'print']
	}
	
	spinner {
		dependsOn 'jquery'
		
		resource url: '/js/spinner.js'
	}
	
	fliptext {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/fliptext/jquery.mb.flipText.js'
		resource url: '/js/fliptext_init.js'
	}
	
	fieldselection {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/fieldselection/jquery.fieldselection.js'
	}
	
	cluetip {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/cluetip/jquery.cluetip.js'
		resource url: '/js/jquery/cluetip/lib/jquery.hoverIntent.js'
		resource url: '/js/jquery/cluetip/jquery.cluetip.css'
		resource url: '/js/cluetip_init.js'
		resource url: '/css/cluetip.css'
	}
	
	form {
		dependsOn 'jquery,cluetip'
		
		resource url: '/js/jquery/form/jquery.form.js'
		resource url: '/js/form-util.js'
		resource url: '/js/form_init.js'
		resource url: '/css/form.css'
	}
	
	url {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/url/jquery.url.js'
	}
	
	progressbar {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/progressbar/jquery.progressbar.js'
	}
	
	chosen {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/chosen/chosen.jquery.js'
		resource url: '/js/jquery/chosen/ajax-chosen.js'
		resource url: '/js/jquery/chosen/chosen.css'
	}
	
	datepicker {
		dependsOn 'jquery'

		resource url: '/js/jquery/datepicker/glDatePicker.js'
		resource url: '/js/jquery/datepicker/datepicker.css'
	}
	
	richeditor {
		resource url: '/js/richeditor/nicEdit.js'
	}
	
	foldable {
		resource url: '/js/foldable_init.js'
		resource url: '/css/foldable.css'
	}
	
	dropdown {
		resource url: '/js/dropdown_init.js'
		resource url: '/css/dropdown.css'
	}
	
	nicetable {
		resource url: '/js/nicetable_init.js'
		resource url: '/css/nicetable.css'
	}
	
	explanation {
		resource url: '/js/explanation_init.js'
		resource url: '/css/explanation.css'
	}

	report {
		resource url: '/js/report_init.js'
	}
	
}

// reports (DSR)
//<g:javascript src="jquery/fliptext/jquery.mb.flipText.js" />
// reports (MAPS)
//<g:javascript src="jquery/url/jquery.url.js" />
// reports (MAPS)
//<g:javascript src="jquery/url/jquery.url.js" />
// survey (summary page)
//<g:javascript src="jquery/progressbar/jquery.progressbar.js" />
// reports (maps + charts, in maps and dashboard)
//<script type="text/javascript" src="https://www.google.com/jsapi"></script>
// all application
//<g:javascript src="spinner.js"/>
// survey
//<g:javascript src="jquery/datepicker/glDatePicker.js" />
//<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/datepicker',file:'datepicker.css')}"/ >

// admin forms (only survey)
//<g:javascript src="jquery/chosen/chosen.jquery.js" />
//<g:javascript src="jquery/chosen/ajax-chosen.js" />
//<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/chosen',file:'chosen.css')}"/ >

// admin forms (survey only)
//<g:javascript src="richeditor/nicEdit.js" />
// admin forms (expression + skip rule + validation rule)
//<g:javascript src="jquery/fieldselection/jquery.fieldselection.js" />

// ajax forms (dsr, dashboard, maps, cost + admin lists)
//<g:javascript src="jquery/form/jquery.form.js" />

// reports + admin list screens + admin forms
//<g:javascript src="jquery/cluetip/jquery.cluetip.js" />
//<g:javascript src="jquery/cluetip/lib/jquery.hoverIntent.js" />
//<link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery/cluetip',file:'jquery.cluetip.css')}"/ >



