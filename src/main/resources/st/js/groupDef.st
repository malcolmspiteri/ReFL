groupDef(id, fields, layout, groups, setter, getter, rules) ::= <<

	$groups; separator="\n"$
	
var $id$ = Group.extend({
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

>>