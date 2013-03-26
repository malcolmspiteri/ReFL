grammar eFrm;

form  			: formDecl fieldsAndRules END_FORM NL* ;

formDecl		: FORM labeledId NL ;

labeledId		: ID STRING? ;

fieldsAndRules	: fieldsSection (rulesSection)? ;

fieldsSection	: FIELDS NL (fieldDecl|groupDecl|NL)+ ;

fieldDecl		: labeledId ':' type NL ;

type			: STRING_TYPE '[' INT ']' #StringType
				| lb=INT '..' ub=INT { $lb.int < $ub.int }? #NumberRangeType
				| OPTION ('[' INT ']')? '{' (optionDecl ',')* optionDecl '}' #OptionType				
				| ID #groupType
				;

optionDecl		: labeledId ;

groupDecl		: GROUP ID NL fieldsAndRules END_GROUP NL ;

rulesSection	: RULES NL stat+ ;

stat			: 'STATEMENT' NL ;

// Keywords
FORM			: 'FORM' ;
END_FORM		: 'END FORM' ;
FIELDS			: 'FIELDS' ;
STRING_TYPE		: 'STRING' ;
OPTION			: 'OPTION' ;
GROUP			: 'GROUP' ;
END_GROUP		: 'END GROUP' ;
RULES			: 'RULES' ;

// Common tokens
ID  			: LETTER (LETTER | [0-9])* ; // match identifiers
LETTER 			: [a-zA-Z] ;
STRING			: '"' .*? '"' ; // match literal strings
INT				: [0-9]+ ; // match literal integers
NL 				: '\r'? '\n' ; 
WS 				: [ \t]+ -> skip ;

COMMENT			: '/*' .*? '*/'    -> channel(HIDDEN) ; // match anything between /* and */
LINE_COMMENT	: '//' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN) ;

