package sirgl.compiler.ast

data class IntLiteral(var number: Int) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(number : Int, line: Int, position : Int) : this(number) {
        this.line = line
        this.position = position
    }
}

data class CharLiteral(var char: Char) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(char : Char, line: Int, position : Int) : this(char) {
        this.line = line
        this.position = position
    }
}

data class StringLiteral(var string : String) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(string : String, line: Int, position : Int) : this(string) {
        this.line = line
        this.position = position
    }
}

data class BooleanLiteral(var bool: Boolean) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(bool : Boolean, line: Int, position : Int) : this(bool) {
        this.line = line
        this.position = position
    }
}


class NullLiteral() : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(line: Int, position : Int) : this() {
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


