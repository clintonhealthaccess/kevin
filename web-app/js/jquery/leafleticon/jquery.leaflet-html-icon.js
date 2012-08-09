/**
 * Plugin for adding arbitrary HTML markers to a Leaflet map
 * https://github.com/dwnoble/LeafletHtmlIcon
 * Public domain
 */

L.HtmlIcon = L.DivIcon.extend({
	options: {
		/*
		html: (String) (required)
		//slister start
		color: (String)
		fontSize: (String)
		fontWeight: (String)
		//slister end
		iconSize: (Point)
		iconAnchor: (Point)
		className: (String)
		*/
	},

	initialize: function (options) {
		L.Util.setOptions(this, options);
	},

	createIcon: function () {
		var div = document.createElement('div');
		div.innerHTML = this.options.html;
		//slister start
		div.style.color = this.options.color;
		div.style.font-size = this.options.fontSize;
		div.style.font-weight = this.options.fontWeight;
		//slister end
		return div;
	},

	createShadow: function () {
		return null;
	}
});