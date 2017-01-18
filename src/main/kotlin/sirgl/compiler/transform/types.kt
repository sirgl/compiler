package sirgl.compiler.transform

import sirgl.compiler.ast.*


fun LangParser.AssignableTypeContext.toAst(): AssignableType {
    val line = start.line
    val position = start.charPositionInLine
    val baseType = when (simpleType().text) {
        "int" -> IntegerType(line, position)
        "char" -> CharType(line, position)
        "boolean" -> BooleanType(line, position)
        "byte" -> ByteType(line, position)
        else -> ObjectType(text, null, line, position)
    }
    var assignableType = baseType
    if (arrayWrapper() != null) {
        for (i in 1..arrayWrapper().size) {
            assignableType = ArrayType(assignableType, line, position)
        }
    }
    return assignableType
}

fun LangParser.ReturnTypeContext.toAst(): ReturnType {
    if(VoidType() != null) {
        return VoidType(start.line, start.charPositionInLine)
    }
    return assignableType().toAst()
}