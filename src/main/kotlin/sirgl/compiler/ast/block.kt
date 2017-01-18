package sirgl.compiler.ast

data class Block(var statements: List<Statement>) : Node {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(statements: List<Statement>, line : Int, position : Int) : this(statements) {
        this.line = line
        this.position = position
    }
}

data class VariableDeclarationStatement(var type : AssignableType, var fieldName: String, var expression : Expression) : Statement {
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