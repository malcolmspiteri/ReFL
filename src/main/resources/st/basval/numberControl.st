numberControl(id, lb, ub) ::= <<
jQuery("#$id$").focusout(function() {
	var val = jQuery(this).val();
	if (val < $lb$ || val > $ub$) { 
		jQuery(this).parentsUntil("fieldset").addClass("error");
	} else {
		jQuery(this).parentsUntil("fieldset").removeClass("error");
	}
});
>>