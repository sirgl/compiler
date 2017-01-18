package sirgl.compiler.ast.context

import sirgl.compiler.ast.Node


interface FunctionalReference : Node {
    var referenceContext: FunctionalContext?
}

interface FunctionalContext {
    var declaration: FunctionalDeclaration
}

interface FunctionalDeclaration : Node