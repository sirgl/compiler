package sirgl.compiler.dependency

import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.Type
import sirgl.compiler.ast.context.FunctionalContext
import sirgl.compiler.verification.scope.VerificationError

class DependencyService(userSrc: List<CompilationUnit>, stdLib : List<CompilationUnit>) {
    private val roots : List<DependencyNode>
    val errors = mutableListOf<VerificationError>()
    private val nameToNode = mutableMapOf<String, DependencyNode>()

    init {
        val builder = DependencyGraphBuilder(userSrc, stdLib)
        roots = builder.findRoots()
        errors.addAll(builder.errors)
        fillNameMap()
    }

    private fun fillNameMap() = fillNameMap(roots)

    private fun fillNameMap(dependencies: List<DependencyNode>) {
        dependencies.forEach {
            nameToNode[it.qualifiedName] = it
            fillNameMap(it.subclasses)
        }
    }

    fun findClassByName(fullName: String) = findNode(fullName)?.compilationUnit

    private fun findNode(fullName: String) = nameToNode[fullName]


    fun findAllSuperclasses(fullName: String) = findNode(fullName)?.superclasses
                ?.toList()



}

class CallSiteSignature(val className: String, val methodName: String, val types: List<Type>)