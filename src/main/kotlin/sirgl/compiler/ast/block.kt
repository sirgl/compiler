package sirgl.compiler.ast

import sirgl.compiler.ast.context.ReferenceDeclaration
import sirgl.compiler.verification.scope.Scope
import sirgl.compiler.verification.scope.Scoped

data class Block(var statements: List<Statement>) : Node, Scoped {
    override var scope: Scope = Scope()
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    var _scopeInited = false
    var scopeInited: Boolean
        get() = _scopeInited
        private set(value) {
            _scopeInited = value
        }

    constructor(statements: List<Statement>, line : Int, position : Int) : this(statements) {
        this.line = line
        this.position = position
    }

    fun initScope() {
        if(scopeInited) {
            return
        }
        val parentScope = findUpperScoped()?.scope
        scope.parentScope = parentScope
        scopeInited = true
    }

    fun findUpperScoped() = findUpper(Scoped::class.java)
}

data class VariableDeclarationStatement(override var type : AssignableType, override var referenceName: String, var expression : Expression) : ReferenceDeclaration, Statement {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null


    constructor(type : AssignableType, fieldName: String, expression : Expression, line : Int, position : Int) : this(type, fieldName, expression) {
        this.line = line
        this.position = position
    }
}

class ReturnStatement() : Statement {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(line : Int, position : Int) : this() {
        this.line = line
        this.position = position
    }

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

data class SuperConstructorCall(var parameters: List<Expression>) : Statement {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(parameters: List<Expression>, line : Int, position : Int) : this(parameters) {
        this.line = line
        this.position = position
    }
}