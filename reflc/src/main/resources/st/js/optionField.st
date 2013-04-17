optionField(label, id, option, numSelectable) ::= <<
new OptionField("$id$","$label$", [$option; separator=",\n "$] $if(numSelectable)$ ,$numSelectable$ $endif$)
>>