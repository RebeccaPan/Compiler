grammar Mx;

// keywords
INT: 'int';
BOOL: 'bool';
STRING: 'string';
NULL: 'null';
VOID: 'void';
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

// value
NUM : [1-9][0-9]* | '0';
ESC : '\\n' | '\\\\' | '\\"';
STR : '"' (ESC|.)*? '"';
ID: [a-zA-Z][a-zA-Z_0-9]*;

literal: TRUE | FALSE | NUM | STR | NULL;

// comment
WS: [ \t]+ -> skip;
NEWLINE: ('\r' '\n'? | '\n') -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;
LINE_COMMENT: '//' ~[\r\n]* -> skip;

// Top-down
program: (def)* EOF;

suite: '{' stmt* '}';

stmt
    : suite                                                             #blockStmt
    | varDef                                                            #varDefStmt
    | IF '(' expr? ')' (trueStmt=stmt)? (ELSE falseStmt=stmt)?          #ifStmt
    | FOR '(' (init=expr)? ';' (cond=expr)? ';' (step=expr)? ')' stmt?  #forStmt
    | WHILE '(' expr ')' stmt                                           #whileStmt
    | BREAK ';'                                                         #breakStmt
    | CONTINUE ';'                                                      #continueStmt
    | RETURN expr? ';'                                                  #returnStmt
    | expr ';'                                                          #exprStmt
    | ';'                                                               #emptyStmt
    ;

def: classDef | funcDef | varDef;

classDef: CLASS ID '{' (varDef | funcDef | constructorDef)* '}' ';';

funcDef: (type | VOID) ID '(' paraList? ')' suite;

varDef: type simpleVarDef (',' simpleVarDef)* ';';

simpleVarDef: ID | ID '=' expr;

constructorDef: ID '(' paraList? ')' suite;

expr
    : primary                                       #atomExpr
    | expr op=('++' | '--')                         #postfixExpr
    | <assoc=right> NEW creator                     #newExpr
    | expr '.' ID                                   #classMemberExpr
    | expr '(' exprList? ')'                        #callFuncExpr
    | expr '[' expr ']'                             #subscriptExpr
    | <assoc=right> op=('++' | '--') expr           #prefixExpr
    | <assoc=right> op=( '+' | '-' ) expr           #prefixExpr
    | <assoc=right> op=( '!' | '~' ) expr           #prefixExpr
    | expr op=('*' | '/' | '%') expr                #binaryExpr
    | expr op=('+' | '-') expr                      #binaryExpr
    | expr op=('<<' | '>>') expr                    #binaryExpr
    | expr op=('<' | '>' | '<=' | '>=') expr        #binaryExpr
    | expr op=('==' | '!=') expr                    #binaryExpr
    | expr op=('&' | '|' | '^') expr                #binaryExpr
    | expr op=('&&' | '||') expr                    #binaryExpr
    | <assoc=right> expr '=' expr                   #assignExpr
    ;

primary
    : '(' expr ')'
    | THIS
    | ID
    | literal
    ;

exprList: expr (',' expr)*;

creator
    : simpleType ('[' expr ']')+ ('[' ']')+ ('[' expr ']')  #wrongCreator
    | simpleType ('[' expr ']')+ ('[' ']')*                 #arrayCreator
    | simpleType '(' ')'                                    #classCreator
    | simpleType                                            #simpleCreator
    ;

para: type ID;

paraList: para (',' para)*;

simpleType: BOOL | INT | STRING | ID;

type
    : simpleType ('[' ']')+
    | simpleType
    ;