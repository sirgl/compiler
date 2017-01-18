package sirgl.compiler.ast.context

class ClassContext {

}

interface ReferenceContext

class VariableContext : ReferenceContext {

}

class FieldContext : ReferenceContext {

}

interface Reference {
    var referenceContext:  ReferenceContext?
}
