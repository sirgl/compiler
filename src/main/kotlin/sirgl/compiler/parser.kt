package sirgl.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BufferedTokenStream
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.CompilationUnit
import java.io.ByteArrayInputStream
import java.io.InputStream
import LangLexer;
import LangParser;
import sirgl.compiler.ast.Node
import sirgl.compiler.ast.processing.TreeWalker
import sirgl.compiler.transform.toAst

class ParserException(val reason: String) : RuntimeException(reason)


fun lexerForText(text : String) = lexerForStream(ByteArrayInputStream(text.toByteArray()))

fun parserForLexer(lexer : LangLexer) = LangParser(BufferedTokenStream(lexer))

fun lexerForStream(inputStream : InputStream) = LangLexer(ANTLRInputStream(inputStream))

fun parserForText(text : String) = parserForLexer(lexerForText(text))

fun parserForStream(inputStream : InputStream) = parserForLexer(lexerForStream(inputStream))

fun fromFile(name: String): InputStream = ClassLoader.getSystemResourceAsStream(name)

fun parseClassDef(name: String): ClassDefinition {
    val parser = parserForStream(fromFile(name))
    val ast = parser.classDefinition().toAst()
    setParents(ast)
    return ast
}

fun parseCompilationUnit(name: String): CompilationUnit {
    val parser = parserForStream(fromFile(name))
    val ast = parser.compilationUnit().toAst()
    setParents(ast.classDefinition)
    return ast
}

fun setParents(node: Node) {
    val walker = TreeWalker()
    walker.addNodeEntranceListener(Node::class.java, { node ->
        node.findChildren().forEach { it.parent = node }
    })
    walker.walk(node)
}