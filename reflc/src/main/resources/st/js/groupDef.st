groupDef(id, fields, layout, groups, setter, getter, cle, rules) ::= <<

	$groups; separator="\n"$
	
var $id$ = Group.extend({
	init: function(id, label) {
		this._super(id, label);		
		$fields$
	},
	render: function(el) {	
		if (el) {
			this.el = jQuery("<div class='container-fluid' style='padding: 0 0 0 0'></div>").appendTo(el);
		}
		var els = [];
		$if(layout)$
		$layout$
		$else$
		for ( var f in this ) {
			if (this[f] instanceof Field || this[f] instanceof Group) {
				els.push(this.el);
				els.push(jQuery("<div class='row-fluid'></div>").appendTo(els.pop())); 
				els.push(jQuery("<div class='span12'></div>").appendTo(els.pop()));
				this[f].render(els.pop());
			}
			if (Object.prototype.toString.call(this[f]) === '[object Array]' && (this[f][0] instanceof Field || this[f][0] instanceof Group)) {
				for (var i = 0; i < this[f].length; i++) {
					els.push(this.el);
					els.push(jQuery("<div class='row-fluid'></div>").appendTo(els.pop())); 
					els.push(jQuery("<div class='span12'></div>").appendTo(els.pop()));
					this[f][i].render(els.pop());
				}
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
	clearErrors: function() {
		$cle; separator="\n"$
	},
	checkRules: function() {
	    console.log("Checking rules: " + this.id);
		$rules$
		for ( var f in this ) {
			if (this[f] instanceof Field || this[f] instanceof Group) {
				this[f].checkRules();
			}			
			if (Object.prototype.toString.call(this[f]) === '[object Array]' && (this[f][0] instanceof Field || this[f][0] instanceof Group)) {
				for (var i = 0; i < this[f].length; i++) {
					this[f][i].checkRules();
				}
			}			
		}
	}
})

>>