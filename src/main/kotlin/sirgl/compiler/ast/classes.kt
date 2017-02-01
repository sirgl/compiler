package sirgl.compiler.ast

import sirgl.compiler.ast.context.FunctionalDeclaration
import sirgl.compiler.ast.context.ReferenceDeclaration
import sirgl.compiler.verification.scope.Scope
import sirgl.compiler.verification.scope.Scoped

data class ClassDefinition(
        var className: String,
        var fields: List<FieldDeclaration> = emptyList(),
        var methods: List<MethodDefinition> = emptyList(),
        var constructors: List<ConstructorDefinition> = emptyList(),
        var nativeMethodDeclarations : List<NativeMethodDeclaration> = emptyList(),
        var superClass: String? = null
) : Node, Scoped {
    override var scope: Scope = Scope()
    override var parent: Node? = null
    override var line: Int? = null
    override var position: Int? = null

    val allFunctionalDeclarations : List<FunctionalDeclaration>
    get() {
        val functionalDeclarations = mutableListOf<FunctionalDeclaration>().let {
            methods.mapTo(it) { it.methodDeclaration }
            nativeMethodDeclarations.mapTo(it) { it.methodDeclaration }
            constructors.mapTo(it) { it.constructorDeclaration } }
        return functionalDeclarations
    }

    constructor(className: String,
                fields: List<FieldDeclaration> = emptyList(),
                methods: List<MethodDefinition> = emptyList(),
                constructors: List<ConstructorDefinition> = emptyList(),
                nativeMethodDeclarations : List<NativeMethodDeclaration> = emptyList(),
                superClass: String? = null,
                line: Int,
                position: Int) : this(className, fields, methods, constructors, nativeMethodDeclarations, superClass) {
        this.line = line
        this.position = position
    }

    fun getType() : ObjectType {
        return ObjectType(className, findCompilationUnit()?.packageDeclaration?.fullName)
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

data class Parameter(override var referenceName: String, override var type: AssignableType) : ReferenceDeclaration {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(name: String, type : AssignableType, line: Int, position: Int) : this(name, type) {
        this.line = line
        this.position = position
    }
}


data class MethodDeclaration(override var functionalName: String, override var parameters: List<Parameter>, override var returnType: ReturnType?) : Node, FunctionalDeclaration {
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


data class ConstructorDeclaration(override var functionalName: String, override var parameters: List<Parameter>) : Node, FunctionalDeclaration {
    override var returnType: ReturnType? = null
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

data class FieldDeclaration(override var referenceName: String, override var type: AssignableType) : ReferenceDeclaration {
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