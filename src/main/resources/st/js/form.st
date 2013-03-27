form(id, label, fieldsDecl, stats) ::= <<
<div class="container-fluid">
	<form id="$id$-form"/>
</div>
<script type="text/javascript">
	//<![CDATA[

var $id$ = Class.extend({
	init: function() {
		this.id = "$id$";
		this.label = "$label$";		
	},
	$fieldsDecl$
	render: function(el) {	
		if (el) {
			this.el = el;
		}
		var rowEl = null;
		var cellEl = null;
		$if(stats)$
		$stats$
		$else$
		for ( var f in this ) {
			if (this[f] instanceof Field) {
				rowEl = jQuery("<div class='row-fluid'></div>").appendTo(this.el); 
				cellEl = jQuery("<div class='span12'></div>").appendTo(rowEl)
				this[f].render(rowEl);
			}			
		}
		$endif$
	}
})

console.log("Rendering form");

var frm = new $id$();
frm.render(jQuery("#$id$-form"));

	
	//]]>
</script>
>>