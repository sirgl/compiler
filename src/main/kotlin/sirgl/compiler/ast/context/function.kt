package sirgl.compiler.ast.context

import sirgl.compiler.ast.FunctionCall
import sirgl.compiler.ast.Node
import sirgl.compiler.ast.Parameter
import sirgl.compiler.ast.ReturnType


interface FunctionalReference : Node {
    var referenceContext: FunctionalContext?
    var functionCall: FunctionCall
}

interface FunctionalContext {
    var declaration: FunctionalDeclaration
}

interface FunctionalDeclaration : Node {
    var functionalName : String
    var parameters: List<Parameter>
    var returnType: ReturnType?
}