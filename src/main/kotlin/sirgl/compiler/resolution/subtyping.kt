package sirgl.compiler.resolution

import sirgl.compiler.ast.*
import sirgl.compiler.ast.context.FunctionalDeclaration
import sirgl.compiler.dependency.CallSiteSignature
import sirgl.compiler.dependency.DependencyService

class SubtypingService(val dependencyService: DependencyService) {

    fun getMaximalType(type: Type?, anotherType: Type?): Type? {
        if (type == null || anotherType == null) {
            return null
        }
        if (isSubtypeOf(type, anotherType)) {
            return anotherType
        } else if (isSubtypeOf(anotherType, type)) {
            return type
        }
        return null
    }

    fun isSubtypeOf(checkingType: Type, anotherType: Type): Boolean {
        return when (checkingType) {
            is VoidType -> false
            is NullType -> isSubtypeOf(checkingType, anotherType)
            is ArrayType -> isSubtypeOf(checkingType, anotherType)
            is SimpleType -> isSubtypeOf(checkingType, anotherType)
            is ObjectType -> isSubtypeOf(checkingType, anotherType)
            else -> throw UnsupportedOperationException("Type not supported ${checkingType.javaClass}")
        }
    }

    fun isSubtypeOf(checkingType: NullType, anotherType: Type): Boolean {
        return !(anotherType !is ObjectType || anotherType !is ArrayType)
    }

    fun isSubtypeOf(checkingType: ObjectType, anotherType: Type): Boolean {
        if (anotherType !is ObjectType) {
            return false
        }
        val fullName = checkingType.fullName ?: throw IllegalStateException("Object type must be already set")
        val superclasses = dependencyService.findAllSuperclasses(fullName)
        if (superclasses?.any { it.qualifiedName == anotherType.fullName } ?: false) {
            return true
        } else if (fullName == anotherType.fullName) {
            return true
        }
        return false
    }

    fun isSubtypeOf(checkingType: SimpleType, anotherType: Type): Boolean {
        if (anotherType !is SimpleType) {
            return false
        }
        return when (checkingType) {
            is ByteType -> anotherType is ByteType
                    || anotherType is IntegerType
                    || anotherType is CharType
            is CharType -> anotherType is CharType
                    || anotherType is IntegerType
            is IntegerType -> anotherType is IntegerType
            is BooleanType -> anotherType is BooleanType
            else -> throw UnsupportedOperationException("Simple type not supported")
        }
    }

    fun isSubtypeOf(checkingType: ArrayType, anotherType: Type): Boolean {
        return anotherType is ArrayType && anotherType.componentType == checkingType.componentType
    }

    fun findMethodsWithSignature(className: String, callSiteSignature: CallSiteSignature) : List<FunctionalDeclaration> {
        val definition = dependencyService.findClassByName(className)
                ?.classDefinition

        return definition?.allFunctionalDeclarations
                ?.filter { signatureAppliable(callSiteSignature, it) } ?: emptyList()

    }

    private fun signatureAppliable(callSiteSignature: CallSiteSignature, it: FunctionalDeclaration): Boolean {
        return it.functionalName == callSiteSignature.methodName &&
                it.parameters.zip(callSiteSignature.types) { param, actual -> TypePair(param.type, actual) }
                        .all { isSubtypeOf(it.actualType, it.parameterType) }
    }

    private data class TypePair(
            val parameterType: Type,
            val actualType: Type
    )
}

