package sirgl.compiler.resolution

import sirgl.compiler.ast.AssignableType
import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.Parameter
import sirgl.compiler.ast.ReturnType

interface ClassContext {
    val className: String
    val functionSignatures: List<FunctionSignature>
    val fieldSignatures: List<FieldSignature>
    var superclass: String?
}

data class AstClassContext(
        override val className: String,
        override val functionSignatures: List<FunctionSignature>,
        override val fieldSignatures: List<FieldSignature>,
        val compilationUnit: CompilationUnit,
        override var superclass: String?
) : ClassContext

data class FunctionSignature(
        var methodName: String,
        var parameters: List<Parameter>,
        var returnType: ReturnType?
)

data class FieldSignature(
        var name: String,
        var type: AssignableType
)