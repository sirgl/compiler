package sirgl.compiler.ast

import sirgl.compiler.ParserException
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
}

