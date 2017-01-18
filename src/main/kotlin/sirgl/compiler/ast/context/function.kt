package sirgl.compiler.ast.context

import sirgl.compiler.ast.Node
import sirgl.compiler.ast.Parameter
import sirgl.compiler.ast.ReturnType


interface FunctionalReference : Node {
    var referenceContext: FunctionalContext?
}

interface FunctionalContext {
    var declaration: FunctionalDeclaration
}

interface FunctionalDeclaration : Node {
    var functionalName : String
    var paremeters: List<Parameter>
    var returnType: ReturnType?
}