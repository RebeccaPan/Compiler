grammar Yx;

// reserved words
BOOL: 'bool';
INT: 'int';
STRING: 'string';
VOID: 'void';
NULL: 'null';
TRUE: 'true';
FALSE: 'false';

IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';

BREAK: 'break';
CONTINUE: 'continue';
RETURN: 'return';

NEW: 'new';
CLASS: 'class';
THIS: 'this';

// val
NUM : [1-9][0-9]* | '0';
ID: [a-zA-Z_][a-zA-Z_0-9]*;
STR : '"' (ESC|.)*? '"';
ESC : '\\n' | '\\\\' | '\\"';

const: TRUE | FALSE | NUM | STR | NULL;

// comment
WS: [ \t]+ -> skip;
NEWLINE: ('\r' '\n'? | '\n') -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]*? -> skip;

// Top-down
program: (def)* EOF;

suite: '{' stmt*'}';

stmt
    : suite
    | varDef
    | IF '(' expr ')' trueStmt=stmt (ELSE falseStmt=stmt)?
    | FOR '(' init=expr ';' cond=expr ';' step=expr ')' stmt?
    | WHILE '(' expr ')' stmt?
    | BREAK ';'
    | CONTINUE ';'
    | RETURN expr? ';'
    | expr ';'
    | ';'
    ;

def: classDef | funcDef | varDef;

classDef: CLASS ID '{' (varDef | funcDef | constructorDef)* '}' ';';

funcDef: type ID '(' paraList? ')' suite;

varDef: type simpleVarDef (',' simpleVarDef)* ';';

simpleVarDef: ID | ID '=' expr;

constructorDef: ID '(' paraList ')' suite;

expr
    : primary                                   #atomExpr
    | expr op=('++' | '--')                     #postfixExpr
    | <assoc=right> op=('++' | '--') expr       #prefixExpr
    | <assoc=right> op=('+' | '-') expr         #prefixExpr
    | <assoc=right> op=('!' | '~') expr         #prefixExpr
    | <assoc=right> NEW creator                 #newExpr
    | expr '.' ID                               #classMemberExpr
    | expr '(' exprList? ')'                    #funcExpr
    | expr '[' expr ']'                         #subscriptExpr
    | expr op=('*' | '/' | '%') expr            #binaryExpr
    | expr op=('+' | '-') expr                  #binaryExpr
    | expr op=('<<' | '>>') expr                #binaryExpr
    | expr op=('<' | '>' | '<=' | '>=') expr    #binaryExpr
    | expr op=('==' | '!=') expr                #binaryExpr
    | expr op=('&' | '|' | '^') expr            #binaryExpr
    | expr op=('&&' | '||') expr                #binaryExpr
    | <assoc=right> expr '=' expr               #assignExpr
    ;

primary
    : '(' expr ')'
    | THIS
    | ID
    | const
    ;

exprList: expr (',' expr)*;

/*
className (dimension* ('(' ')')? );

dimension : '[' expr? ']';
*/

creator: simpleType ('[' expr? ']')* ('(' ')')?;

para: type ID;

paraList: para (',' para)*;

simpleType: BOOL | INT | STRING | ID;

type
    : simpleType '[' ']'
    | simpleType
    ;