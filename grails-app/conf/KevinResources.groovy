modules = {

	// special module for print, let's see if we move
	// that somewhere else later
	print2 {
		resource url: '/css/print.css', attrs:[media:'screen, print']
	}

	// overrides, let's put jquery in the core bundle
	overrides {
		jquery {
			defaultBundle 'core'
		}
	}

	// modules
	core {
		dependsOn 'jquery'
		
		resource url: '/css/screen.css', bundle: 'core'
	}

	error {
		resource url: '/css/errors.css'
	}
	
	spinner {
		dependsOn 'jquery'

		resource url: '/js/spinner.js'
		//resource url: '/css/spinner.css', bundle: 'core'
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
		//resource url: '/css/cluetip.css'
	}

	form {
		dependsOn 'jquery,cluetip'

		resource url: '/js/jquery/form/jquery.form.js'
		resource url: '/js/form-util.js'
		resource url: '/js/form_init.js'
		//resource url: '/css/form.css'
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
		dependsOn 'jquery'

		resource url: '/js/foldable_init.js', bundle: 'core'
		//resource url: '/css/foldable.css', bundle: 'core'
	}

	dropdown {
		dependsOn 'jquery'

		resource url: '/js/dropdown_init.js', bundle: 'core'
		//resource url: '/css/dropdown.css', bundle: 'core'
	}

	nicetable {
		dependsOn 'jquery'

		resource url: '/js/nicetable_init.js', bundle: 'core'
		//resource url: '/css/nicetable.css', bundle: 'core'
	}

	explanation {
		dependsOn 'jquery'

		resource url: '/js/explanation_init.js', bundle: 'core'
	}

	report {
		dependsOn 'jquery'

		resource url: '/js/report_init.js', bundle: 'core'
	}
	
	questionhelp {
		dependsOn 'jquery'

		resource url: 'js/questionhelp_init.js', bundle: 'core'
	}

	tipsy {
		dependsOn 'jquery'

		resource url: 'js/jquery/tipsy/src/javascripts/jquery.tipsy.js', bundle: 'core'
		resource url: 'js/tipsy_init.js', bundle: 'core'
	}

	ajaxmanager {
		dependsOn 'jquery'

		resource url: 'js/jquery/ajaxmanager/jquery.ajaxmanager.js'
	}

	// Start resources for pages
	list {
		dependsOn 'core,spinner'

		//resource url: '/css/list.css'
	}

	survey {
		dependsOn 'core,ajaxmanager,questionhelp,datepicker'

		//resource url: '/css/survey.css'
	}

	dsr {
		dependsOn 'core,fliptext,cluetip,dropdown,nicetable,report,spinner'

		//resource url: '/css/dsr.css'
	}

	fct {
		dependsOn 'core,cluetip,dropdown,nicetable,report,spinner'

		//resource url: '/css/dsr.css'
	}
	
	dashboard {
		dependsOn 'core,cluetip,dropdown,nicetable,explanation,report,spinner'

		//resource url: '/css/dashboard.css'
	}

	maps {
		dependsOn 'core,url,dropdown,explanation,spinner'

		//resource url: '/css/maps.css'
	}

	cost {
		dependsOn 'core,dropdown,nicetable,explanation,spinner'

		//resource url: '/css/cost.css'
	}

}
