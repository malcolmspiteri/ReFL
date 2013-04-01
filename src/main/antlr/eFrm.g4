grammar eFrm;

form  			: formDecl fieldsSection layoutSection? rulesSection? END_FORM NL* ;

formDecl		: FORM labeledId NL ;

labeledId		: ID STRING? ;

fieldsSection	: FIELDS NL (fieldDecl|groupDecl|NL)+ ;

fieldDecl		: labeledId ':' (ARRAY '[' INT ']' 'OF')? type NL ;

type			: STRING_TYPE '[' INT ']' #StringType
				| lb=INT '..' ub=INT #NumberRangeType
				| OPTION ('[' INT ']')? '{' (optionDecl ',')* optionDecl NL* '}' #OptionType				
				| ID #groupType
				;

optionDecl		: NL* labeledId ;

groupDecl		: GROUP ID NL fieldsSection layoutSection? rulesSection? END_GROUP NL ;

rulesSection	: RULES NL (stat|NL)+ ;

layoutSection	: LAYOUT NL (layout NL|NL)+ ;

layout			: idRef #RenderStat 
				| HEADER INT STRING #HeaderStat
				| GRID num=INT+ #GridStat
				| INFO STRING #InfoStat
				| SKIP #SkipStat
				| NEWROW #NewRowStat
				;

stat			: expr '=' expr NL #AssignStat  
				| varDecl NL #VarDecStat
				| IF expr THEN NL+ stat+ elseBlock? 'END IF' NL #IfContStat
				| 'WHILE' expr NL+ stat+ 'END WHILE' NL #WhileStat
				| NOASK expr NL #NoAskStat
				| ASK expr NL #AskStat
				;

elseBlock		: ELSE NL stat+ ;

varDecl			: ID ':' varType ('=' expr)? ;

varType			: STRING_TYPE
				| NUMBER
				;
				
expr			: idRef ('.' idRef)* #IDExpr
				| '[' ID (',' ID)* ']' #OptionExpr
				| '(' expr ')' #BracketedExpr
				| expr '==' expr #EqualityExpr				
				| expr '<' expr #LessThanExpr
				| expr op=('+'|'-'|'*'|'/') expr #ArithmeticExpr
				| INT #IntegerLiteralExpr
				| STRING #StringLiteralExpr
				;

idRef			: ID ('[' expr ']')? ;


// Keywords
FORM			: 'FORM' ;
END_FORM		: 'END FORM' ;
FIELDS			: 'FIELDS' ;
STRING_TYPE		: 'STRING' ;
ARRAY			: 'ARRAY' ;
OPTION			: 'OPTION' ;
GROUP			: 'GROUP' ;
END_GROUP		: 'END GROUP' ;
RULES			: 'RULES' ;
NUMBER			: 'NUMBER' ;
PARAMETERS		: 'PARAMETERS' ;
IF				: 'IF' ;
THEN			: 'THEN' ;
ELSE			: 'ELSE' ;
NOASK	        : 'NOASK' ;
ASK	       		: 'ASK' ;

// Layout tokes
LAYOUT			: 'LAYOUT' ;
INFO            : 'INFO' ;
HEADER			: 'HEADER' ;
GRID			: 'GRID' ;
NEWROW			: 'NEWROW' ;
SKIP			: 'SKIP' ;

// Common tokens
ID  			: LETTER (LETTER | [0-9])* ; // match identifiers
LETTER 			: [a-zA-Z] ;
STRING			: '"' .*? '"' ; // match literal strings
INT				: [0-9]+ ; // match literal integers
NL 				: '\r'? '\n' ; 
WS 				: [ \t]+ -> skip ;

COMMENT			: '/*' .*? '*/'    -> channel(HIDDEN) ; // match anything between /* and */
LINE_COMMENT	: '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN) ;

