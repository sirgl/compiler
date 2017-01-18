package sirgl.compiler.ast

import sirgl.compiler.ParserException
import sirgl.compiler.util.getAllSuperclasses
import kotlin.reflect.memberProperties

interface SourcePosition {
    var line : Int?
    var position : Int?
}

interface Node : SourcePosition {
    var parent: Node?

    fun findChildren()  = javaClass.kotlin.memberProperties
            .filter { it.name != "parent" }
            .map { it.get(this) }
            .filterNotNull()
            .flatMap {
                val children = mutableListOf<Node>()
                when (it) {
                    is Node -> children.add(it)
                    is Collection<*> -> it
                            .filter {it is Node}
                            .map { it as Node }
                            .mapTo(children) {it}
                    else -> {}
                }
                children
            }

    fun <T> findUpper(targetClass: Class<T>) : T? = findUpper<T, Nothing>(targetClass, null)

    fun <T, R : Node> findUpper(targetClass: Class<T>, limitClass : Class<R>?) : T? {
        val parent = parent ?: return null
        val parentClasses: List<Class<*>> = getAllSuperclasses(parent.javaClass)
        if(limitClass != null && parentClasses.contains(limitClass)) {
            return null
        }
        @Suppress("UNCHECKED_CAST")
        if(parentClasses.contains(targetClass)) {
            return parent as T
        }
        return parent.findUpper(targetClass, limitClass)
    }

    fun findClassDefinition()  = findUpper(ClassDefinition::class.java)

    fun findUpperBlock(): Block? {
        return findUpper(Block::class.java, ConstructorDefinition::class.java)
    }
}

