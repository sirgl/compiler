package sirgl.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.BufferedTokenStream
import java.io.ByteArrayInputStream
import java.io.InputStream
import LangLexer
import LangParser
import sirgl.compiler.ast.*
import sirgl.compiler.ast.context.FunctionalDeclaration
import sirgl.compiler.ast.processing.TreeWalker
import sirgl.compiler.resolution.AstClassContext
import sirgl.compiler.resolution.ClassContext
import sirgl.compiler.resolution.FieldSignature
import sirgl.compiler.resolution.FunctionSignature
import sirgl.compiler.transform.toAst
import sirgl.compiler.verification.scope.ScopeChecker
import sirgl.compiler.verification.scope.VerificationError
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class ParserException(val reason: String) : RuntimeException(reason)


fun lexerForText(text: String) = lexerForStream(ByteArrayInputStream(text.toByteArray()))

fun parserForLexer(lexer: LangLexer) = LangParser(BufferedTokenStream(lexer))

fun lexerForStream(inputStream: InputStream) = LangLexer(ANTLRInputStream(inputStream))

fun parserForText(text: String) = parserForLexer(lexerForText(text))

fun parserForStream(inputStream: InputStream) = parserForLexer(lexerForStream(inputStream))

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

fun MethodDeclaration.toFunctionSignature(): FunctionSignature {
    return FunctionSignature(functionalName, parameters, returnType)
}

fun ConstructorDeclaration.toFunctionSignature(): FunctionSignature {
    val className = findClassDefinition()?.className!!
    return FunctionSignature(functionalName, parameters, ObjectType(className, null))
}

fun CompilationUnit.toClassContext(): ClassContext {
    val funcSignatures = mutableListOf<FunctionSignature>()
    classDefinition.methods.mapTo(funcSignatures) { it.methodDeclaration.toFunctionSignature() }
    classDefinition.constructors.mapTo(funcSignatures) { it.constructorDeclaration.toFunctionSignature() }
    classDefinition.nativeMethodDeclarations.mapTo(funcSignatures) { it.methodDeclaration.toFunctionSignature() }
    val fieldSignatures = classDefinition.fields.map { FieldSignature(it.referenceName, it.type) }
    return AstClassContext(
            classDefinition.className,
            funcSignatures,
            fieldSignatures,
            this,
            classDefinition.superClass
    )
}

class ParsingPipeline {
    fun parse(fileName: String) = parse(FileInputStream(fileName))

    fun parse(inputStream: InputStream): ParsingResult {
        val ast = parserForStream(inputStream).compilationUnit().toAst()
        setParents(ast.classDefinition)
        val scopeChecker = ScopeChecker(ast)
        val errors = scopeChecker.check()
        return ParsingResult(errors, ast.toClassContext())
    }
}

data class ParsingResult(
        val errors: List<VerificationError>,
        val classContext: ClassContext
)

class Parser(val files: List<String>, threadCount: Int = 4) {
    val threadPool: ExecutorService = Executors.newFixedThreadPool(threadCount)

    fun parse(): List<ParsingResult> {
        return files.map { threadPool.submit { ParsingPipeline().parse(it) } }
                .map { it.get() as ParsingResult }
    }
}