package sirgl.compiler.transform

import sirgl.compiler.ast.*

fun LangParser.IfStatementContext.toStatementAst() : IfStatement {
    var elseBlock : Block? = null
    if(block().size == 2) {
        elseBlock = block(1).toAst()
    }
    return IfStatement(
            expression().toAst(),
            block(0).toAst(),
            elseBlock,
            start.line,
            start.charPositionInLine
    )
}

fun LangParser.WhileStatementContext.toStatementAst()  = WhileStatement(
        expression().toAst(),
        block().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.ForStatementContext.toStatementAst() : ForStatement {
    val forControl = forControl()
    val condition = forControl.forCondition()?.expression()?.toAst()
    val iteration = forControl.forIteration()?.expression()?.toAst()
    val forInit = forControl.forInit()
    var forInitBlock : ForInitBlock? = null
    if(forInit.expression() != null) {
        forInitBlock = ExpressionInitBlock(forInit.expression().toAst())
    } else if (forInit.variableDeclaration() != null) {
        forInitBlock = VarDeclarationForInitBlock(forInit.variableDeclaration().toStatementAst())
    }
    return ForStatement(forInitBlock, condition, iteration, start.line, start.charPositionInLine)
}
