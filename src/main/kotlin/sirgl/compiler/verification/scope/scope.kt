package sirgl.compiler.verification.scope

import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.context.ReferenceDeclaration

class Scope(var parentScope: Scope? = null) {
    var references = mutableListOf<ReferenceDeclaration>()

    /**
     * @return true if successfully added, false if it already contains declaration
     */
    fun tryAddReferenceDeclaration(referenceDeclaration : ReferenceDeclaration) : Boolean {
        if(containsReference(referenceDeclaration.referenceName)) {
            return false
        }
        references.add(referenceDeclaration)
        return true
    }

    fun findDeclaration(name : String) : ReferenceDeclaration? {
        return references.find { it.referenceName == name }
                ?: parentScope?.findDeclaration(name)
    }

    private fun containsReference(name: String): Boolean {
        return references.any { it.referenceName == name } ||
                parentScope?.containsReference(name) ?: false
    }

    fun containsReference(reference : Reference) = containsReference(reference.referenceName)
}

interface Scoped {
    var scope: Scope
}