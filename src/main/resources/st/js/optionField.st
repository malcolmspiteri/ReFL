optionField(label, id, option, numSelectable) ::= <<
$id$ : new OptionField("$id$","$label$", [$option; separator=",\n "$] $if(numSelectable)$ ,$numSelectable$ $endif$),
>>