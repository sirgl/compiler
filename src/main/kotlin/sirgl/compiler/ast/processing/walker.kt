package sirgl.compiler.ast.processing

import sirgl.compiler.ast.Node
import sirgl.compiler.util.getAllSuperclasses
import kotlin.reflect.memberProperties

@Suppress("UNCHECKED_CAST")
open class TreeWalker {
    val nodeEntranceListeners = mutableMapOf<Class<*>, MutableList<(Node) -> Unit>>()
    val nodeSubtreeTraverseStartListeners = mutableMapOf<Class<*>, MutableList<(Node) -> Unit>>()
    val nodeSubtreeTraverseEndListeners = mutableMapOf<Class<*>, MutableList<(Node) -> Unit>>()
    val finishPreconditions = mutableListOf<(Node) -> Boolean>()
    val finishPostconditions = mutableListOf<(Node) -> Boolean>()

    fun walk(node: Node) {
        if (finishPreconditions.any { it(node) }) {
            return
        }
        acceptListeners(node, nodeEntranceListeners)
        if (finishPostconditions.any { it(node) }) {
            return
        }
        traverseSubtree(node)
    }

    private fun traverseSubtree(node: Node) {
        acceptListeners(node, nodeSubtreeTraverseStartListeners)
        node.javaClass.kotlin.memberProperties
                .filter { it.name != "parent" }
                .map { it.get(node) }
                .forEach { value ->
                    when (value) {
                        is Node -> walk(value )
                        is Collection<*> -> value.forEach { if (it is Node) walk(it) }
                    }
                }
        acceptListeners(node, nodeSubtreeTraverseEndListeners)
    }

    private fun acceptListeners(node: Node, listenersMap: MutableMap<Class<*>, MutableList<(Node) -> Unit>>) {
        val classes = getAllSuperclasses(node.javaClass)
        classes.forEach {
            val listeners = listenersMap[it]
            listeners?.forEach {
                it(node)
            }
        }
    }

    private fun <T> addListeners(
            map: MutableMap<Class<*>, MutableList<(Node) -> Unit>>,
            nodeClass: Class<T>,
            action: (T) -> Unit) {
        var actionList = map[nodeClass]
        if (actionList == null) {
            actionList = mutableListOf()
        }
        actionList.add(action as (Node) -> Unit)
        map[nodeClass] = actionList
    }

    fun <T> addNodeEntranceListener(nodeClass: Class<T>, action: (T) -> Unit) {
        addListeners(nodeEntranceListeners, nodeClass, action)
    }

    fun <T> addSubtreeTraverseStartListener(nodeClass: Class<T>, action: (T) -> Unit) {
        addListeners(nodeSubtreeTraverseStartListeners, nodeClass, action)
    }

    fun <T> addSubtreeTraverseEndListener(nodeClass: Class<T>, action: (T) -> Unit) {
        addListeners(nodeSubtreeTraverseEndListeners, nodeClass, action)
    }

    fun addFinishPrecondition(predicate: (Node) -> Boolean) {
        finishPreconditions.add(predicate)
    }

    fun addFinishPoscondition(predicate: (Node) -> Boolean) {
        finishPostconditions.add(predicate)
    }
}
