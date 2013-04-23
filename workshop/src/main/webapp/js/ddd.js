var exampleForm = Class.extend({			
	init : function() {
		this.id = "exampleForm";
		this.label = "exampleForm";
		this.a = new NumberRangeField("a", "Value A", 1, 10);
		this.b = new NumberRangeField("b", "Value B", 1, 10);
		this.c = new NumberRangeField("c", "Addition Result", 2, 20);
	},
	render : function(el) {
		if (el) {
			this.el = jQuery(
					"<div class='container-fluid' style='padding: 0 0 0 0'></div>")
					.appendTo(el);
		}
		var els = [];
		for ( var f in this) {
			if (this[f] instanceof Field || this[f] instanceof SubForm) {
				els.push(this.el);
				els.push(jQuery("<div class='row-fluid'></div>")
						.appendTo(els.pop()));
				els.push(jQuery("<div class='span12'></div>").appendTo(
						els.pop()));
				this[f].render(els.pop());
			}
			if (Object.prototype.toString.call(this[f]) === '[object Array]'
					&& (this[f][0] instanceof Field || this[f][0] instanceof SubForm)) {
				for ( var i = 0; i < this[f].length; i++) {
					els.push(this.el);
					els.push(jQuery("<div class='row-fluid'></div>")
							.appendTo(els.pop()));
					els.push(jQuery("<div class='span12'></div>")
							.appendTo(els.pop()));
					this[f][i].render(els.pop());
				}
			}
		}
		var _this = this;
		jQuery("#exampleForm-form input[type='text']").focusout(
				function() {
					_this.checkRules();
				});
		jQuery("#exampleForm-form input[type='radio']").click(
				function() {
					_this.checkRules();
				});
		jQuery("#exampleForm-form input[type='checkbox']").click(
				function() {
					_this.checkRules();
				});
	},
	val : function(obj) {
		var ret = obj;
		if (ret) {
			this.a.val(ret.a);
			this.b.val(ret.b);
			this.c.val(ret.c);
		} else {
			ret = {};
			ret.a = this.a.val();
			ret.b = this.b.val();
			ret.c = this.c.val();
		}
		return ret;
	},
	clearErrors : function() {
		this.a.clearErrors();
		this.b.clearErrors();
		this.c.clearErrors();
	},
	checkRules : function() {
		console.log("Checking rules: " + this.id);
		this.clearErrors();
		this.c.val(this.a.val() + this.b.val());
		for ( var f in this) {
			if (this[f] instanceof Field || this[f] instanceof SubForm) {
				this[f].checkRules();
			}
			if (Object.prototype.toString.call(this[f]) === '[object Array]'
							&& (this[f][0] instanceof Field || this[f][0] instanceof SubForm)) {
						for ( var i = 0; i < this[f].length; i++) {
							this[f][i].checkRules();
						}
					}
				}
			}
		});
console.log("Rendering form");
var frm = new exampleForm();
frm.render(jQuery("#exampleForm-form"));
frm.checkRules();