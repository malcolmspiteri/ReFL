tableHeader(header) ::= <<
<td>$first(header)$</td>$tableHeader(rest(header))$
>>