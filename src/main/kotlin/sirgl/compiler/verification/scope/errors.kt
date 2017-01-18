package sirgl.compiler.verification.scope

import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.context.ReferenceDeclaration

open class VerificationError

class UndefinedVariableUsageError(val reference : Reference) : VerificationError() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as UndefinedVariableUsageError

        if (reference != other.reference) return false

        return true
    }

    override fun hashCode(): Int {
        return reference.hashCode()
    }
}

class RedeclarationError(val referenceDeclarations: List<ReferenceDeclaration>) : VerificationError() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as RedeclarationError

        if (referenceDeclarations != other.referenceDeclarations) return false

        return true
    }

    override fun hashCode(): Int {
        return referenceDeclarations.hashCode()
    }

        override fun toString(): String {
        return "RedeclarationError(referenceDeclarations=$referenceDeclarations)"
    }


}

class UnknownClassUsageError(val className: String) : VerificationError() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as UnknownClassUsageError

        if (className != other.className) return false

        return true
    }

    override fun hashCode(): Int {
        return className.hashCode()
    }
}