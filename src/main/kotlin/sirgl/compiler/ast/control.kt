package sirgl.compiler.ast

interface Statement : Node

class BreakStatement() : Statement {
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

class ContinueStatement() : Statement {
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

data class IfStatement(var condition: Expression, var block: Block, var elseBlock: Block?) : Statement{
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(condition : Expression, block : Block, elseBlock : Block?, line : Int, position : Int) : this(condition, block, elseBlock) {
        this.line = line
        this.position = position
    }
}

data class WhileStatement(var condition: Expression, var block : Block) : Statement {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(condition : Expression, block : Block, line : Int, position : Int) : this(condition, block) {
        this.line = line
        this.position = position
    }
}

interface ForInitBlock : Node

data class ExpressionInitBlock(var expr : Expression): ForInitBlock {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(expr : Expression, line : Int, position : Int) : this(expr) {
        this.line = line
        this.position = position
    }
}

data class VarDeclarationForInitBlock(var variableDeclaration: VariableDeclarationStatement): ForInitBlock {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(variableDeclaration : VariableDeclarationStatement, line : Int, position : Int) : this(variableDeclaration) {
        this.line = line
        this.position = position
    }
}

data class ForStatement(var forInitBlock : ForInitBlock?, var forCondition: Expression?, var forIteration: Expression?) : Statement {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    constructor(forInitBlock : ForInitBlock?, forCondition : Expression?, forIteration : Expression?, line : Int, position : Int) : this(forInitBlock, forCondition, forIteration) {
        this.line = line
        this.position = position
    }
}
