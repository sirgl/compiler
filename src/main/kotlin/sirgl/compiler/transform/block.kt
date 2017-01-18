package sirgl.compiler.transform

import LangParser
import sirgl.compiler.ParserException
import sirgl.compiler.ast.*

fun LangParser.BlockContext.toAst() = Block(
        blockStatement().map { it.toAst() },
        start.line,
        start.charPositionInLine
)

fun LangParser.BlockStatementContext.toAst(): Statement {
    val child = children[0]
    return when (child) {
        is LangParser.VariableDeclarationContext -> child.toStatementAst()
        is LangParser.AssignableLineContext -> child.toStatementAst()
        is LangParser.ReturnStatementContext -> child.toStatementAst()
        is LangParser.ContinueStatementContext -> child.toStatementAst()
        is LangParser.BreakStatementContext -> child.toStatementAst()
        is LangParser.BlockExprContext -> child.toStatementAst()
        is LangParser.SuperConstructorCallContext -> child.toStatementAst()
        is LangParser.IfStatementContext -> child.toStatementAst()
        is LangParser.WhileStatementContext -> child.toStatementAst()
        else -> throw ParserException("Can't convert statement to AST $javaClass")
    }
}

fun LangParser.BreakStatementContext.toStatementAst()  = BreakStatement(start.line, start.charPositionInLine)

fun LangParser.BlockExprContext.toStatementAst() = expression().toAst()

fun LangParser.AssignableLineContext.toStatementAst(): VariableDeclarationStatement {
    return variableDeclaration().toStatementAst()
}

fun LangParser.VariableDeclarationContext.toStatementAst() = VariableDeclarationStatement(
        assignableType().toAst(),
        Identifier().text,
        expression().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.ReturnStatementContext.toStatementAst() = ReturnStatement(start.line, start.charPositionInLine)

fun LangParser.ContinueStatementContext.toStatementAst() = ContinueStatement(start.line, start.charPositionInLine)

fun LangParser.SuperConstructorCallContext.toStatementAst(): SuperConstructorCall = SuperConstructorCall(
        expressionList().expression().map { it.toAst() },
        start.line,
        start.charPositionInLine
)
