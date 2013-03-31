arraySetter(id) ::= <<
for (var i = 0; i < ret.$id$.length; i++) {
	this.$id$[i].val(ret.$id$[i]);
}  
>>