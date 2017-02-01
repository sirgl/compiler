grammar Lang;


//Lexems

CLASS : 'class';
PrimitiveType : 'int' | 'byte' | 'boolean' | 'char';
VoidType : 'void';
NULL: 'null';
THIS: 'this';
RETURN: 'return';
IF: 'if';
ELSE: 'else';
FOR: 'for';
WHILE: 'while';
NEW: 'new';
TRUE : 'true';
FALSE: 'false';
BREAK: 'break';
CONTINUE: 'continue';

PLUS : '+';
MINUS : '-';
MULTIPLY : '*';
DIVIDE : '/';
REMAINDER : '%';
ASSIGNMENT : '=';
GT : '>';
GTEQ : '>=';
LTEQ : '<=';
LT : '<';
EQ : '==';

Identifier : ('A'..'Z' | 'a'..'z')+;

IntLiteral : ('0'..'9')+;
StringLiteral
   : '"' (~ ["\\])* '"';
CharLiteral : '\'' (~ ["\\]) '\'';

WS  :   ( [ \t\r\n] | COMMENT) -> skip;

fragment
COMMENT
: '/*'.*'*/' /*single comment*/
| '//'~('\r' | '\n')* /* multiple comment*/
;




//ParserRules

//General symbols



returnType : assignableType | VoidType;

simpleType : PrimitiveType | Identifier;

arrayWrapper : ('[]');

assignableType : simpleType arrayWrapper*;

//compilation unit

packageDeclaration: 'package' qualifiedName ';';

importDeclaration: 'import' qualifiedName ';';

compilationUnit: packageDeclaration? importDeclaration* classDefinition;



// Class related rules

qualifiedName  : Identifier ('.' Identifier)*;

fieldDeclaration : assignableType Identifier ';';

parameter : assignableType Identifier;

parameters : (parameter)*;

methodDeclaration : returnType Identifier '(' parameters ')';

methodDefinition : methodDeclaration block;

constructorDeclaration : Identifier '(' parameters ')';

constructorDefinition : constructorDeclaration block;

nativeMethodDeclaration : 'native' methodDeclaration ';';

classDefinitionExpression :
    fieldDeclaration |
    constructorDefinition |
    methodDefinition |
    nativeMethodDeclaration;

classDefinitionBlock :
    '{' (classDefinitionExpression)* '}';

superClassClause : 'super' Identifier;

classDefinition : CLASS Identifier superClassClause? classDefinitionBlock;

superConstructorCall: 'super' '(' expressionList ')' ';';


//Expression

expressionList :   expression (',' expression)* ;

functionCall : Identifier '(' expressionList? ')';

arrayCreation : assignableType arrayWrapper* '(' IntLiteral ')';

constructorCall : 'new' functionCall;

objectCreationExpression : constructorCall | arrayCreation;

expression :
    primary                                                             #primaryExpr
    | expression '.' Identifier                                         #fieldAccess
    | expression '.' functionCall                                       #methodCall
    | functionCall                                                      #methodCallWithoutSource
    | expression operator=(DIVIDE | MULTIPLY)  expression               #multiplyExpr
    | expression operator=(PLUS | MINUS | REMAINDER)  expression        #sumExpr
    | MINUS expression                                                  #minusExpr
    | '~' expression                                                    #negateExpression
    | expression operator=(GT | GTEQ | LT | LTEQ)  expression           #comparsionExpr
    | expression operator=('||' | '&&')  expression                     #booleanExpr
    | expression (EQ)  expression                                       #eqallityExpr
    | expression '[' expression ']'                                     #arrayAccess
    | <assoc=right> expression ASSIGNMENT expression                    #assignmentExpr
    | '(' Identifier ')' expression                                     #typeConversion
    | objectCreationExpression                                          #objectCreationExpr
    ;

primary : '(' expression ')'
    | THIS
    | IntLiteral
    | StringLiteral
    | CharLiteral

    | TRUE
    | FALSE
    | NULL
    | Identifier
    |
;

//Control

breakStatement : BREAK ';';

continueStatement : CONTINUE ';';

ifStatement : IF '(' expression ')' block (ELSE block)?;

forInit : (variableDeclaration | expression);

forCondition : expression;

forIteration : expression;

forControl : (forInit? ';' forCondition? ';' forIteration?);

forStatement : FOR '(' forControl ')' block;

whileStatement : WHILE '(' expression ')' block;

//Block


variableDeclaration : assignableType Identifier ASSIGNMENT expression;

assignableLine : variableDeclaration ';';

returnStatement : RETURN expression? ';';

blockExpr : expression ';' ;

blockStatement: assignableLine | returnStatement | blockExpr | ifStatement | forStatement | whileStatement | breakStatement | continueStatement | superConstructorCall;

block : '{' (blockStatement)* '}';