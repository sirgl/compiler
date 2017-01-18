package sirgl.compiler.ast.context

import sirgl.compiler.ast.Node

interface ReferenceContext {
    var declaration: ReferenceDeclaration?
}

interface ReferenceDeclaration : Node {
    var referenceName: String
}


interface Reference : Node {
    var referenceContext:  ReferenceContext?
    var referenceName: String
}
