package sirgl.compiler.dependency

import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.verification.scope.VerificationError

class DependencyGraphBuilder(userSrc: List<CompilationUnit>, stdLib : List<CompilationUnit>) {
    private val resolvedSet = mutableSetOf<DependencyNode>()
    private val resolvingSet = mutableSetOf<DependencyNode>()
    private val librarySet = mutableSetOf<DependencyNode>()
    val errors = mutableListOf<VerificationError>()

    init {
        userSrc.mapTo(resolvingSet) { DependencyNode(it.fullName, it, true) }
        stdLib.mapTo(librarySet) { DependencyNode(it.fullName, it, false) }
        buildDependencyGraph()
    }

    private fun buildDependencyGraph() {
        while (resolvingSet.isNotEmpty()) {
            val node = resolvingSet.first()
            val compilationUnit = node.compilationUnit
            if(compilationUnit != null) {
                val superClassName = compilationUnit.classDefinition.superClass
                compilationUnit.importDeclarations.forEach {
                    val dependencyClassName = it.className
                    val dependencyNode : DependencyNode? = findNodeWithName(dependencyClassName)
                    if(dependencyNode == null) {
                        errors.add(ClassNotFoundError(dependencyClassName))
                        return
                    }
                    val dependencySimpleName = it.simpleName
                    if(dependencySimpleName == superClassName) {
                        node.superclassDependency = dependencyNode
                        dependencyNode.subclasses.add(node)
                    } else {
                        node.dependencies.add(dependencyNode)
                        dependencyNode.dependents.add(node)
                    }
                    if(!resolvedSet.contains(dependencyNode) && !resolvingSet.contains(dependencyNode)) {
                        resolvingSet.add(dependencyNode)
                    }
                }
            }
            resolvingSet.remove(node)
            resolvedSet.add(node)
        }
    }

    fun findRoots() = resolvedSet.filter { it.superclassDependency == null }

    private fun findNodeWithName(className: String): DependencyNode? {
        return resolvedSet.find { it.qualifiedName == className } ?:
                resolvingSet.find { it.qualifiedName == className } ?:
                librarySet.find { it.qualifiedName == className }
    }
}

data class DependencyNode(val qualifiedName: String, var compilationUnit : CompilationUnit? = null, val userSrc : Boolean = false) {
    val dependencies = mutableListOf<DependencyNode>()
    var superclassDependency : DependencyNode? = null
    val subclasses = mutableListOf<DependencyNode>()
    val dependents = mutableListOf<DependencyNode>()

    val superclasses : Sequence<DependencyNode> = generateSequence(superclassDependency) {it.superclassDependency}
}

class ClassNotFoundError(val className: String) : VerificationError() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ClassNotFoundError

        if (className != other.className) return false

        return true
    }

    override fun hashCode(): Int {
        return className.hashCode()
    }
}