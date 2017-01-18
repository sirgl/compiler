package sirgl.compiler.verification.scope

import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.context.ReferenceDeclaration

class ScopeCheckSession(val scope: Scope) {
    private val conflictingMap = mutableMapOf<String, MutableList<ReferenceDeclaration>>()
    private val errors = mutableListOf<VerificationError>()

    fun add(reference: Reference) {
        if (!scope.containsReference(reference)) {
            errors.add(UndefinedVariableUsageError(reference))
        }
    }

    fun add(referenceDeclaration: ReferenceDeclaration) {
        if (!scope.tryAddReferenceDeclaration(referenceDeclaration)) {
            var conflictingDeclarations = conflictingMap[referenceDeclaration.referenceName]
            if (conflictingDeclarations == null) {
                conflictingDeclarations = mutableListOf()
            }
            conflictingDeclarations.add(referenceDeclaration)
            conflictingMap[referenceDeclaration.referenceName] = conflictingDeclarations
        }
    }

    fun getErrors(): List<VerificationError> {
        return conflictingMap.entries
                .mapTo(errors) {
                    it.value.add(scope.findDeclaration(it.key)!!)
                    RedeclarationError(it.value)
                }
    }

    fun addAll(references: List<Reference>) = references.forEach { add(it) }
    fun addAllDeclarations(references: List<ReferenceDeclaration>) = references.forEach { add(it) }
}