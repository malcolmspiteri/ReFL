arrayGetter(id) ::= <<
var $id$ArrVal = [];
for (var i = 0; i < this.$id$.length; i++) {
	$id$ArrVal.push(this.$id$[i].val());
}  
ret.$id$ = $id$ArrVal; 
>>