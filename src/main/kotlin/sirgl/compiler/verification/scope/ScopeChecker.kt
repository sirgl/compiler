package sirgl.compiler.verification.scope

import sirgl.compiler.ast.Block
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.context.FunctionalDeclaration
import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.context.ReferenceDeclaration
import sirgl.compiler.ast.processing.TreeWalker

class ScopeChecker(val compilationUnit: CompilationUnit) {
    private val errors = mutableListOf<VerificationError>()

    fun check(): List<VerificationError> {
        val classDefinition = compilationUnit.classDefinition
        injectFieldsToScope(classDefinition)
        injectParametersInFunctions(classDefinition)

        val blockWalker = TreeWalker()
        setupBlockListener(blockWalker)
        blockWalker.walk(classDefinition)
        return errors
    }

    private fun setupBlockListener(blockWalker: TreeWalker) {
        blockWalker.addNodeEntranceListener(Block::class.java, { block ->
            block.initScope()
            val scope = block.scope
            val session = ScopeCheckSession(scope)
            block.statements.forEach { statement ->
                val referenceWalker = TreeWalker()
                referenceWalker.addFinishPrecondition { it is Block }
                referenceWalker.addNodeEntranceListener(Reference::class.java, {
                    session.add(it)
                })
                referenceWalker.addNodeEntranceListener(ReferenceDeclaration::class.java, {
                    session.add(it)
                })
                referenceWalker.walk(statement)
            }
            errors.addAll(session.getErrors())
        })
    }

    private fun injectParametersInFunctions(classDefinition: ClassDefinition) {
        classDefinition.methods.forEach {
            addParametersToScope(it.methodDeclaration, it.block.scope)
        }
        classDefinition.constructors.forEach {
            addParametersToScope(it.constructorDeclaration, it.block.scope)
        }
    }

    private fun addParametersToScope(functionalDeclaration: FunctionalDeclaration, scope: Scope) {
        functionalDeclaration.paremeters.forEach { scope.tryAddReferenceDeclaration(it) }
    }

    private fun injectFieldsToScope(classDefinition: ClassDefinition) {
        val session = ScopeCheckSession(classDefinition.scope)
        session.addAllDeclarations(classDefinition.fields)
        errors.addAll(session.getErrors())
    }
}