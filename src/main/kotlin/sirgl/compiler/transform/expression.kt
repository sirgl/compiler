package sirgl.compiler.transform

import LangParser
import org.antlr.v4.runtime.tree.TerminalNode
import sirgl.compiler.ParserException
import sirgl.compiler.ast.*
import sirgl.compiler.ast.context.Reference

fun LangParser.ExpressionContext.toAst(): Expression {
    return when (this) {
        is LangParser.FieldAccessContext -> toAst()
        is LangParser.MultiplyExprContext -> toAst()
        is LangParser.SumExprContext -> toAst()
        is LangParser.MethodCallContext -> toAst()
        is LangParser.MethodCallWithoutSourceContext -> toAst()
        is LangParser.MinusExprContext -> toAst()
        is LangParser.NegateExpressionContext -> toAst()
        is LangParser.ComparsionExprContext -> toAst()
        is LangParser.EqallityExprContext -> toAst()
        is LangParser.ArrayAccessContext -> toAst()
        is LangParser.AssignmentExprContext -> toAst()
        is LangParser.TypeConversionContext -> toAst()
        is LangParser.PrimaryExprContext -> toAst()
        is LangParser.ObjectCreationExprContext -> toAst()
        is LangParser.ArrayCreationContext -> toExprAst()
        else -> throw ParserException("Can't convert expression to AST")
    }
}

fun LangParser.ObjectCreationExprContext.toAst(): ObjectCreationExpression {
    val constructorCall = objectCreationExpression().constructorCall()
    return ObjectInstantiationExpression(constructorCall.functionCall().toAst(), start.line, start.charPositionInLine)
}

fun LangParser.MultiplyExprContext.toAst(): BinaryArithmeticExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val line = start.line
    val position = start.charPositionInLine
    return when (operator.text) {
        "*" -> MultiplyExpression(left, right, line, position)
        "/" -> DivideExpression(left, right, line, position)
        else -> throw ParserException("Can't convert multiply expression to AST")
    }
}

fun LangParser.SumExprContext.toAst(): BinaryArithmeticExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val line = start.line
    val position = start.charPositionInLine
    return when (operator.text) {
        "+" -> SumExpression(left, right, line, position)
        "-" -> SubtractExpression(left, right, line, position)
        "%" -> RemainderExpression(left, right, line, position)
        else -> throw ParserException("Can't convert multiply expression to AST")
    }
}

fun LangParser.FieldAccessContext.toAst() = FieldAccessExpression(
        expression().toAst(),
        Identifier().text,
        start.line,
        start.charPositionInLine
)

fun LangParser.MethodCallContext.toAst() = MethodCallExpression(
        expression().toAst(),
        functionCall().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.MethodCallWithoutSourceContext.toAst() = MethodCallExpression(
        null,
        functionCall().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.FunctionCallContext.toAst(): FunctionCall {
    return FunctionCall(
            Identifier().text,
            expressionList().toAst()
    )
}

fun LangParser.ExpressionListContext.toAst(): List<Expression> {
    return if (text.isEmpty()) {
        emptyList()
    } else {
        expression().map { it.toAst() }
    }
}

fun LangParser.MinusExprContext.toAst() = UnaryMinusExpression(
        expression().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.NegateExpressionContext.toAst() = NegateExpression(
        expression().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.ComparsionExprContext.toAst(): BinaryPredicateExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val line = start.line
    val position = start.charPositionInLine
    return when (operator.text) {
        ">" -> GreaterThanExpression(left, right, line, position)
        "<" -> LessThanExpression(left, right, line, position)
        "<=" -> LessThanOrEqualsExpression(left, right, line, position)
        ">=" -> GreaterThanOrEqualsExpression(left, right, line, position)
        else -> throw ParserException("Can't convert comparision operator to AST")
    }
}

fun LangParser.EqallityExprContext.toAst(): EqullityExpression {
    val left = expression(0).toAst()
    val right = expression(1).toAst()
    val line = start.line
    val position = start.charPositionInLine
    return EqullityExpression(left, right, line, position)
}

fun LangParser.ArrayAccessContext.toAst(): ArrayElementAccessExpression {
    val arrayExpr = expression(0).toAst()
    val index = expression(1).toAst()
    val line = start.line
    val position = start.charPositionInLine
    return ArrayElementAccessExpression(arrayExpr, index, line, position)
}

fun LangParser.AssignmentExprContext.toAst(): AssignmentExprssion {
    val reference = expression(0).toAst() as? Reference ?:
            throw ParserException("Expression must be reference in assignment")
    return AssignmentExprssion(reference, expression(1).toAst(), start.line, start.charPositionInLine)
}

fun LangParser.TypeConversionContext.toAst() = ClassCastExpression(
        Identifier().text,
        expression().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.ArrayCreationContext.toExprAst() : ArrayInstantiationExpression {
    return ArrayInstantiationExpression(parseIntLiteral(IntLiteral()))
}

fun LangParser.PrimaryContext.toAst(): Expression {
    if (expression() != null) {
        return expression().toAst()
    }
    val line = start.line
    val position = start.charPositionInLine
    if (StringLiteral() != null) {
        return StringLiteral(StringLiteral().text.substring(1, StringLiteral().text.lastIndex), line, position)
    }
    if (IntLiteral() != null) {
        return IntLiteral(parseIntLiteral(IntLiteral()), line, position)
    }
    if (Identifier() != null) {
        return Variable(Identifier().text, line, position)
    }
    if (THIS() != null) {
        return This(line, position)
    }
    if (NULL() != null) {
        return NullLiteral(line, position)
    }
    if (CharLiteral() != null) {
        return CharLiteral(CharLiteral().text[1], line, position)
    }
    if (TRUE() != null) {
        return BooleanLiteral(true, line, position)
    }
    if (FALSE() != null) {
        return BooleanLiteral(false, line, position)
    }
    throw UnsupportedOperationException()
}

fun parseIntLiteral(intLiteral: TerminalNode) = intLiteral.text.toInt()

fun LangParser.PrimaryExprContext.toAst() = primary().toAst()