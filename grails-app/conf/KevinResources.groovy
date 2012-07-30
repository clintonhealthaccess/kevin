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
		dependsOn 'jquery,help,foldable'
		
		resource url: '/css/screen.css', bundle: 'core'
	}

	error {
		resource url: '/css/errors.css'
	}
	
	spinner {
		dependsOn 'jquery'

		resource url: '/js/spinner.js'
	}

	fliptext {
		dependsOn 'jquery'

		resource url: '/js/jquery/fliptext/jquery.mb.flipText.js', bundle: 'core'
		resource url: '/js/fliptext_init.js', bundle: 'core'
	}

	fieldselection {
		dependsOn 'jquery'

		resource url: '/js/jquery/fieldselection/jquery.fieldselection.js', bundle: 'admin'
	}

	cluetip {
		dependsOn 'jquery'

		resource url: '/js/jquery/cluetip/jquery.cluetip.js', bundle: 'core'
		resource url: '/js/jquery/cluetip/lib/jquery.hoverIntent.js', bundle: 'core'
		resource url: '/js/jquery/cluetip/jquery.cluetip.css', bundle: 'core'
		resource url: '/js/cluetip_init.js', bundle: 'core'
		//resource url: '/css/cluetip.css'
	}

	form {
		dependsOn 'jquery,cluetip,fieldselection'

		resource url: '/js/jquery/form/jquery.form.js', bundle: 'core'
		resource url: '/js/form-util.js', bundle: 'core'
		resource url: '/js/form_init.js', bundle: 'core'
		//resource url: '/css/form.css'
	}

	url {
		dependsOn 'jquery'

		resource url: '/js/jquery/url/jquery.url.js', bundle: 'core'
	}

	progressbar {
		dependsOn 'jquery'

		resource url: '/js/jquery/progressbar/jquery.progressbar.js', bundle: 'core'
		resource url: '/js/progressbar_init.js', bundle: 'core'
	}

	chosen {
		dependsOn 'jquery'

		resource url: '/js/jquery/chosen/chosen.jquery.js', bundle: 'admin'
		resource url: '/js/jquery/chosen/ajax-chosen.js', bundle: 'admin'
		resource url: '/js/jquery/chosen/chosen.css', bundle: 'admin'
	}

	datepicker {
		dependsOn 'jquery'

		resource url: '/js/jquery/datepicker/glDatePicker.js', bundle: 'core'
		resource url: '/js/jquery/datepicker/datepicker.css', bundle: 'core'
	}

	richeditor {
		resource url: '/js/richeditor/nicEdit.js', bundle: 'admin'
	}

	foldable {
		dependsOn 'jquery'

		resource url: '/js/foldable_init.js', bundle: 'core'
	}
	
	dropdown {
		dependsOn 'jquery'

		resource url: '/js/dropdown_init.js', bundle: 'core'
	}

	nicetable {
		dependsOn 'jquery'

		resource url: '/js/nicetable_init.js'
	}

	explanation {
		dependsOn 'jquery'

		resource url: '/js/explanation_init.js', bundle: 'core'
	}

	help {
		dependsOn 'jquery'

		resource url: 'js/help_init.js', bundle: 'core'
	}

	tipsy {
		dependsOn 'jquery'

		resource url: 'js/jquery/tipsy/src/javascripts/jquery.tipsy.js', bundle: 'core'
		resource url: 'js/tipsy_init.js', bundle: 'core'
	}

	ajaxmanager {
		dependsOn 'jquery'

		resource url: 'js/jquery/ajaxmanager/jquery.ajaxmanager.js', bundle: 'core'
	}
	
	dataentry {
		dependsOn 'jquery,ajaxmanager,datepicker'
		
		resource url: '/js/dataentry.js', bundle: 'core'
	}
	
	chartanimation {
		dependsOn 'jquery,tipsy'
		
		resource url: '/js/chartanimation_init.js', bundle: 'core'
	}
	
	comparefilter {
		dependsOn 'jquery'
		
		resource url: '/js/jquery/comparefilter/jquery.form.js', bundle: 'core'
		resource url: '/js/dashboard/comparefilter_init.js', bundle: 'core'
	}
	
	valuefilter {
		dependsOn 'jquery'
				
		resource url: '/js/valuefilter_init.js', bundle: 'core'
	}
	
	leaflet {
		dependsOn 'jquery'
		
		resource url: 'http://cdn.leafletjs.com/leaflet-0.4/leaflet.js'
	}
	
	// Start resources for pages
	list {
		dependsOn 'core,spinner,form,fieldselection,cluetip,dropdown,explanation,chosen'
	}

	survey {
		dependsOn 'core,dataentry'
	}
	
	planning {
		dependsOn 'core,dataentry'
	}

	dsr {
		dependsOn 'core,fliptext,cluetip,dropdown,nicetable,spinner,chosen,chartanimation, leaflet'
	}

	fct {
		dependsOn 'core,fliptext,cluetip,dropdown,nicetable,spinner,chosen,chartanimation,valuefilter, leaflet'
	}
	
	dashboard {
		dependsOn 'core,cluetip,dropdown,nicetable,explanation,spinner,chosen,chartanimation,comparefilter, leaflet'
	}

//	maps {
//		dependsOn 'core,url,dropdown,explanation,spinner,chosen'
//	}

	cost {
		dependsOn 'core,dropdown,nicetable,explanation,spinner,chosen'
	}

}
