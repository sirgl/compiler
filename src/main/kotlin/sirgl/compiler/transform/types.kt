package sirgl.compiler.transform

import sirgl.compiler.ast.*


fun LangParser.AssignableTypeContext.toAst(): AssignableType {
    val baseType = when (simpleType().text) {
        "int" -> IntegerType
        "char" -> CharType
        "boolean" -> BooleanType
        "byte" -> ByteType
        else -> ObjectType(text, null) //TODO package
    }
    var assignableType = baseType
    if (arrayWrapper() != null) {
        for (i in 1..arrayWrapper().size) {
            assignableType = ArrayType(assignableType)
        }
    }
    return assignableType
}

fun LangParser.ReturnTypeContext.toAst(): ReturnType {
    if(VoidType() != null) {
        return VoidType
    }
    return assignableType().toAst()
}