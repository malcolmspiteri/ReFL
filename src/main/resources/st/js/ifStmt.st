ifStmt(expr, stat1, stat2) ::= <<
if ($expr$) {
	$stat1; separator="\n"$
}
$if(stat2)$
else {
	$stat2; separator="\n"$
}
$endif$
>>