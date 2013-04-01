table(header) ::= <<
els = [this.el]; 
els.push(jQuery("<div class='row-fluid'></div>").appendTo(els[els.length-1]));
els.push(jQuery("<div class='span12'></div>").appendTo(els[els.length-1]));
els.push(jQuery("<table class='table table-striped'><thead><tr>$header$</tr></thead><tbody></tbody></table>").appendTo(els[els.length-1]).children("tbody"));
els.push(1) // Hack to correctly display new rows :-(
>>