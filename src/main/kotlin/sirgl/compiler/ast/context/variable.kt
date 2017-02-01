package sirgl.compiler.ast.context

import sirgl.compiler.ast.AssignableType
import sirgl.compiler.ast.Expression
import sirgl.compiler.ast.Node

interface ReferenceContext {
    var declaration: ReferenceDeclaration?
}

class ReferenceContextImpl(override var declaration: ReferenceDeclaration?) : ReferenceContext

interface ReferenceDeclaration : Node {
    var referenceName: String
    var type: AssignableType
}


interface Reference : Expression {
    var referenceContext:  ReferenceContext?
    var referenceName: String
}
