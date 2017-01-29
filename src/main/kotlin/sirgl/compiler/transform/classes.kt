package sirgl.compiler.transform

import org.antlr.v4.runtime.tree.ParseTree
import sirgl.compiler.ParserException
import sirgl.compiler.ast.*
import java.awt.font.LayoutPath


fun LangParser.ClassDefinitionContext.toAst(): ClassDefinition {
    val fields = mutableListOf<FieldDeclaration>()
    val constructors = mutableListOf<ConstructorDefinition>()
    val methods = mutableListOf<MethodDefinition>()
    val nativeMethods = mutableListOf<NativeMethodDeclaration>()
    classDefinitionBlock().classDefinitionExpression().forEach {
        val node = it.children[0]
        when (node) {
            is LangParser.FieldDeclarationContext -> fields.add(node.toAst())
            is LangParser.ConstructorDefinitionContext -> constructors.add(node.toAst())
            is LangParser.MethodDefinitionContext -> methods.add(node.toAst())
            is LangParser.NativeMethodDeclarationContext -> nativeMethods.add(node.toAst())
            else -> throw ParserException("Unsupported class definition")
        }
    }
    val superClass = superClassClause()?.Identifier()?.text
    return ClassDefinition(Identifier().text, fields, methods, constructors, nativeMethods, superClass, start.line, start.charPositionInLine)
}

fun LangParser.FieldDeclarationContext.toAst(): FieldDeclaration {
    return FieldDeclaration(Identifier().text, assignableType().toAst(), start.line, start.charPositionInLine)
}

fun LangParser.ConstructorDefinitionContext.toAst(): ConstructorDefinition {
    return ConstructorDefinition(constructorDeclaration().toAst(), block().toAst())
}

fun LangParser.ConstructorDeclarationContext.toAst(): ConstructorDeclaration {
    return ConstructorDeclaration(Identifier().text, parameters().parameter().map { it.toAst() }, start.line, start.charPositionInLine)
}

fun LangParser.ParameterContext.toAst(): Parameter {
    return Parameter(Identifier().text, assignableType().toAst(), start.line, start.charPositionInLine)
}


fun LangParser.MethodDefinitionContext.toAst() = MethodDefinition(
        methodDeclaration().toAst(),
        block().toAst(),
        start.line,
        start.charPositionInLine
)

fun LangParser.MethodDeclarationContext.toAst() = MethodDeclaration(
        Identifier().text,
        parameters().parameter().map { it.toAst() },
        returnType().toAst()
)

fun LangParser.NativeMethodDeclarationContext.toAst() = NativeMethodDeclaration(
        methodDeclaration().toAst(),
        start.line,
        start.charPositionInLine
)
