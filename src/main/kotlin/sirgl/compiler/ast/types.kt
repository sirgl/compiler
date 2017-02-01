package sirgl.compiler.ast

interface Type : Node {
    fun isArithmetic(): Boolean {
        return this is IntegerType || this is CharType || this is ByteType
    }
}

interface ReturnType : Type
interface AssignableType : ReturnType
interface SimpleType : AssignableType

class ByteType() : SimpleType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}

class IntegerType() : SimpleType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}

class BooleanType() : SimpleType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}

class CharType() : SimpleType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}

class VoidType() : ReturnType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}

interface BaseObjectType : AssignableType

data class ObjectType(val className: String, var packageName: String?) : BaseObjectType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    var fullName: String?
        get() {
            if (packageName != null) {
                return packageName + "." + className
            }
            return className
        }
        set(value) {
            if(value == null) {
                throw IllegalArgumentException("value must not be null")
            }
            val lastIndex = value.indexOfLast { it == '.' }
            if(lastIndex == -1) {
                return
            }
            val simpleName = value.substring(lastIndex + 1) // TODO
            packageName = value.substring(0, lastIndex)

        }

    constructor(className: String, packageName: String?, line: Int, position: Int) : this(className, packageName) {
        this.line = line
        this.position = position
    }
}

class NullType() : BaseObjectType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(line: Int, position: Int) : this() {
        this.line = line
        this.position = position
    }
}


data class ArrayType(val componentType: AssignableType) : AssignableType {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }

    constructor(componentType: AssignableType, line: Int, position: Int) : this(componentType) {
        this.line = line
        this.position = position
    }
}

val stringType = ObjectType("String", "lang")