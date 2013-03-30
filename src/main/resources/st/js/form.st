form(id, label, fields, layout, groups, setter, getter, rules) ::= <<
<div class="container-fluid">
	<form id="$id$-form"/>
</div>
<script type="text/javascript">
	//<![CDATA[

	$groups; separator="\n"$
	
var $id$ = Class.extend({
	init: function() {
		this.id = "$id$";
		this.label = "$label$";		
	},
	$fields$
	render: function(el) {	
		if (el) {
			this.el = el;
		}
		var rowEl = null;
		var cellEl = null;
		$if(layout)$
		$layout$
		$else$
		for ( var f in this ) {
			if (this[f] instanceof Field || this[f] instanceof Group) {
				rowEl = jQuery("<div class='row-fluid'></div>").appendTo(this.el); 
				cellEl = jQuery("<div class='span12'></div>").appendTo(rowEl)
				this[f].render(rowEl);
			}			
		}
		$endif$
		
		var _this = this;
		jQuery("#$id$-form input[type='text']").focusout(function() {			
			_this.checkRules();
		});
		jQuery("#$id$-form input[type='radio']").click(function() {			
			_this.checkRules();
		});
		jQuery("#$id$-form input[type='checkbox']").click(function() {			
			_this.checkRules();
		});

	},
	val: function(obj) {
		var ret = obj;
		if (ret) {
			$setter; separator="\n"$
		} else {
			ret = {};
			$getter; separator="\n"$
		}
		return ret;
	},
	checkRules: function() {
		for ( var f in this ) {
			if (this[f] instanceof Field || this[f] instanceof Group) {
				this[f].checkRules();
			}			
		}
		$rules$
	}
})

console.log("Rendering form");

var frm = new $id$();
frm.render(jQuery("#$id$-form"));
frm.checkRules();
	
	//]]>
</script>
>>