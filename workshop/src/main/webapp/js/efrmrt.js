/* Base types */

/* Simple JavaScript Inheritance
 * By John Resig http://ejohn.org/
 * MIT Licensed.
 */
// Inspired by base2 and Prototype
(function(){
  var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
 
  // The base Class implementation (does nothing)
  this.Class = function(){};
 
  // Create a new Class that inherits from this class
  Class.extend = function(prop) {
    var _super = this.prototype;
   
    // Instantiate a base class (but only create the instance,
    // don't run the init constructor)
    initializing = true;
    var prototype = new this();
    initializing = false;
   
    // Copy the properties over onto the new prototype
    for (var name in prop) {
      // Check if we're overwriting an existing function
      prototype[name] = typeof prop[name] == "function" &&
        typeof _super[name] == "function" && fnTest.test(prop[name]) ?
        (function(name, fn){
          return function() {
            var tmp = this._super;
           
            // Add a new ._super() method that is the same method
            // but on the super-class
            this._super = _super[name];
           
            // The method only need to be bound temporarily, so we
            // remove it when we're done executing
            var ret = fn.apply(this, arguments);        
            this._super = tmp;
           
            return ret;
          };
        })(name, prop[name]) :
        prop[name];
    }
   
    // The dummy class constructor
    function Class() {
      // All construction is actually done in the init method
      if ( !initializing && this.init )
        this.init.apply(this, arguments);
    }
   
    // Populate our constructed prototype object
    Class.prototype = prototype;
   
    // Enforce the constructor to be what we expect
    Class.prototype.constructor = Class;
 
    // And make this class extendable
    Class.extend = arguments.callee;
   
    return Class;
  };
})();

var Group = Class.extend({
	init: function(id, label) {
		this.id = id;
		this.label = label;		
	}
});

var Variable = Class.extend({
	init: function(value) {
		this.value = value;
	},
	value : null,
	val: function(val) {
		if (val) {
			this.value = val;
			return val;
		}
		return this.value;
	}
});
/*
var Array = Class.extend({
	init: function(id, label, values) {
		this.id = id;
		this.label = label;
		this.values = values;
	},
	render: function(apdel) {	
		for (var i = 0; i < this.values.length; i++) {
			this.values[i].render(apdel);
		}
	},
	val: function(val) {
		if (val) {
			for (var i = 0; i < val.length; i++) {
				this.values[i].val(val[i]);
			}
			return val;
		} else {
			val = [];
			for (var i = 0; i < this.values.length; i++) {
				val.push(this.values[i].val());
			}
			return val;
		}
	},
	checkRules: function() {
		for (var i = 0; i < this.values.length; i++) {
			val.push(this.values[i].val());
		}		
	}
});
*/

var Field = Class.extend({
	init: function(id, label) {
		this.id = id;
		this.label = label;
		this.errors = {};	
		this.prevVal = [];
	},
	template: function() {
		return "<div>" + this.id + "</div>";
	},
	$ : function(selector) {
		return jQuery(selector, this.el);
	},
	render: function(apdel) {	
		this.el = jQuery(this.template());
		if (apdel.prop("tagName") == "TD") { // We're in a table
			this.$("label.control-label").addClass("hidden");
		}
		if (apdel) {
			this.el.appendTo(apdel);
		}
		return this.el;
	},
	setInvalid: function() {
		this.el.addClass("error");
	},
	setValid: function() {
		this.el.removeClass("error");
	},	
	disable: function() {
		this.$("label").each(function() {
			$(this).addClass("muted");
		});
		this.$("input").each(function() {
			$(this).prop("disabled", true);
		});
		this.pushVal();
	},
	enable: function() {
		this.$("label").each(function() {
			$(this).removeClass("muted");
		});
		this.$("input").each(function() {
			$(this).prop("disabled", false);
		});
		this.popVal();
	},
	pushVal: function() {
		var currVal = this.val();
		if (currVal != null && currVal != "" && currVal != "undefined") {
			this.prevVal.push(currVal);
		}
		this.$("input").val("");
		this.setValid();
	},
	popVal: function() {
		if (this.prevVal.length > 0) {
			var pv = this.prevVal.pop(); 
			if (pv != null && pv != "" && pv != "undefined") {
				this.val(pv);
			}
			this.checkRules();
		} else {
			console.log("No values to pop");
		}
	},
	val: function(val) {
		if (val) {
			this.$("input").val(val);
			return val;
		} else {
			return this.$("input").val();
		}
	},
	checkRules: function() {
		var ne = 0;
		for (e in this.errors) {
			ne++;
		}
		if (ne > 0) {
			this.setInvalid();
		} else {
			this.setValid();
		}
	},
	isEmpty: function() {
		if (this.val() == "") {
			return true;
		}
		return false;
	},
	clearErrors: function() {
		this.errors = {};	
	},
	addError: function(errKey, errMsg) {
		this.errors[errKey] = errMsg;
	},
	removeError: function(errKey) {
		delete this.errors[errKey];
	},
	showErrors: function() {
		var html = "<ul>";
		html += "<li>There are errors</li>";
		html += "<ul>";
		this.$("input").popover({
			html: html,
			placement: "bottom",
			trigger: "hover",
			title: "Errors"				
		});
	}
});

var StringField = Field.extend({
	init: function(id, label, maxlength) {
		this._super(id, label);
		this.maxlength = maxlength;
	},
	template: function() {
		return "<div class='control-group'>" + 
			"<label class='control-label' for='" + this.id + "'>" + this.label + "</label>" +
			"<div class='controls'>" + 
			"<input class='input-block-level' type='text' id='" + this.id + "' placeholder='" + this.label + "' maxlength='" + this.maxlength + "'>" + 
			"</div>" +
			"</div>";
	}
});

var NumberRangeField = Field.extend({
	init: function(id, label, min, max) {
		this._super(id, label);
		this.min = min;
		this.max = max;
	},
	template: function() {
		return "<div class='control-group'>" + 
			"<label class='control-label' for='" + this.id + "'>" + this.label + "</label>" +
			"<div class='controls'>" + 
			"<input class='input-block-level' type='text' id='" + this.id + "' placeholder='" + this.label + "'>" + 
			"</div>" +
			"</div>";
	},
	render: function(apdel) {	
		this._super(apdel);
		
		var _this = this;
		this.$("#" + this.id).focusout(function() {			
			_this.checkRules();
		});
		this.$("#" + this.id).keydown(function(event) {
	        // Allow: backspace, delete, tab, escape, and enter
	        if ( event.keyCode == 46 || event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 27 || event.keyCode == 13 || 
	             // Allow: Ctrl+A
	            (event.keyCode == 65 && event.ctrlKey === true) || 
	             // Allow: home, end, left, right
	            (event.keyCode >= 35 && event.keyCode <= 39)) {
	                 // let it happen, don't do anything
	                 return;
	        }
	        else {
	            // Ensure that it is a number and stop the keypress
	            if (event.shiftKey || (event.keyCode < 48 || event.keyCode > 57) && (event.keyCode < 96 || event.keyCode > 105 )) {
	                event.preventDefault(); 
	            }   
	        }
	    });		

		return this.el;
	},
	checkRules: function() {
		var val = this.val();
		if (val != null && val != "" && val != "undefined") {
			if (val < this.min || val > this.max) { 
				this.addError("oor", "Value is not within range from " + this.min + " to " + this.max);
			}		
		}
		this._super();
	},
	val: function(val) {
		return parseInt(this._super(val));
	},
	isEmpty: function() {
		return isNaN(this.val());
	}
});

var OptionField = Field.extend({
	init: function(id, label, options, numSelectable) {
		this._super(id, label);
		this.options = options;
		if (numSelectable) {
			this.numSelectable = numSelectable;
		}
	},
	numSelectable: 1,
	template: function() {
		var optionsHtml = "";
		for (var i = 0; i < this.options.length; i++) {
		    var o = this.options[i];
		    var type = "radio";
		    if (this.numSelectable > 1) {
			    type = "checkbox";
		    }
			optionsHtml = optionsHtml + "<label class='" + type + "'>" +
			"<input type='" + type + "' name='" + this.id + "' id='" + o.id + "' value='" + o.id + "'>" +
			o.label +
			"</label>";
		}
		return "<div class='control-group'>" + 
			"<label class='control-label' for='" + this.id + "'>" + this.label + "</label>" +
			"<div class='controls'>" + 
			optionsHtml + 
			"</div>" +
			"</div>";
	},	
	pushVal: function() {
		var currVal = this.val();
		if (currVal != null && currVal != "" && currVal != "undefined") {
			this.prevVal.push(currVal);
		}
		this.$("input:checked").each(function() {
			$(this).prop("checked", false);			
		});
		this.setValid();		
	},
	render: function(apdel) {
		this._super(apdel);
		
		var _this = this;
		this.$("input[name='" + this.id + "']").each(function() {
			$(this).click(function() {
				_this.checkRules();
			});			
		});
				
		return this.el;
	},
	val: function(val) {
		if (val) {
			this.$("input:checked").each(function() {
				$(this).prop("checked", false);
			});
			for (var i = 0; i < val.length; i++) {
			    var opt = val[i];
			    this.$("input#" + opt).prop("checked", true);
			}
			return val;
		} else {
			var ret = [];
			this.$("input:checked").each(function() {
				ret.push($(this).val());
			});
			return ret;
		}
	},
	checkRules: function() {
		var noChecked = this.$("input:checked").size();
		if (noChecked > this.numSelectable) { 
			this.addError("tmo", "Number of selected options exceeds the allowable " + this.numSelectable);
		}
		this._super();
	},
	isEmpty: function() {
		if (this.val().length == 0) {
			return true;
		}
		return false;
	}
});

function areEqual(val1, val2) {
	if( Object.prototype.toString.call( val1 ) === '[object Array]' ||
		Object.prototype.toString.call( val2 ) === '[object Array]') {
		// comparing arrays
		if (val1.length != val2.length) {
			return false;
		} else {
			var hits = 0;
			for (var i = 0; i < val1.length; i++) {
			    for (var j = 0; j < val2.length; j++) {
			        if (val1[i] == val2[j]) {
			            hits++;
			            break;
			        }
			    }
			}
			if (hits == val1.length) {
				return true;
			} else {
				return false;
			}
		}
	} else {
		return (val1 == val2);
	}
		
}

function lessThan(val1, val2) {
	return (val1 < val2);
}

function greaterThan(val1, val2) {
	return (val1 > val2);
}
