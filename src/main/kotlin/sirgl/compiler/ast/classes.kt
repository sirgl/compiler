package sirgl.compiler.ast

import sirgl.compiler.ast.context.FunctionalDeclaration

data class ClassDefinition(
        var className: String,
        var fields: List<FieldDeclaration>,
        var methods: List<MethodDefinition>,
        var constructors: List<ConstructorDefinition>,
        var superClass: String?) : Node {
    override var parent: Node? = null
    override var line: Int? = null
    override var position: Int? = null

    constructor(className: String,
                fields: List<FieldDeclaration>,
                methods: List<MethodDefinition>,
                constructors: List<ConstructorDefinition>,
                superClass: String?,
                line: Int,
                position: Int) : this(className, fields, methods, constructors, superClass) {
        this.line = line
        this.position = position
    }
}

data class SuperClause(var className: String) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(className: String, line: Int, position: Int) : this(className) {
        this.line = line
        this.position = position
    }
}

data class Parameter(var name: String, var type: AssignableType) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(name: String, type : AssignableType, line: Int, position: Int) : this(name, type) {
        this.line = line
        this.position = position
    }
}

data class MethodDeclaration(var methodName: String, var paremeters: List<Parameter>, var returnType: ReturnType) : Node, FunctionalDeclaration {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(methodName: String, paremeters: List<Parameter>, returnType: ReturnType, line: Int, position: Int) : this(methodName, paremeters, returnType) {
        this.line = line
        this.position = position
    }
}

data class MethodDefinition(var methodDeclaration: MethodDeclaration, var block: Block) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(methodDeclaration: MethodDeclaration, block: Block, line: Int, position: Int) : this(methodDeclaration, block) {
        this.line = line
        this.position = position
    }
}


data class ConstructorDeclaration(var constructorName: String, var paremeters: List<Parameter>) : Node, FunctionalDeclaration {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(constructorName: String, paremeters: List<Parameter>, line: Int, position: Int) : this(constructorName, paremeters) {
        this.line = line
        this.position = position
    }
}

data class ConstructorDefinition(var constructorDeclaration: ConstructorDeclaration, var block: Block) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(constructorDeclaration: ConstructorDeclaration, block: Block, line: Int, position: Int) : this(constructorDeclaration, block) {
        this.line = line
        this.position = position
    }
}

data class FieldDeclaration(var fieldName: String, var type: AssignableType) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(fieldName: String, type: AssignableType, line: Int, position: Int) : this(fieldName, type) {
        this.line = line
        this.position = position
    }
}

data class NativeMethodDeclaration(var methodDeclaration: MethodDeclaration) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(methodDeclaration: MethodDeclaration, line: Int, position: Int) : this(methodDeclaration) {
        this.line = line
        this.position = position
    }
}