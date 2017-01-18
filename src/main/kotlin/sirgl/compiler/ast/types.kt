package sirgl.compiler.ast

interface Type

interface ReturnType : Type
interface AssignableType : ReturnType
interface SimpleType : AssignableType

object ByteType : SimpleType
object IntegerType : SimpleType
object BooleanType : SimpleType
object CharType : SimpleType
object VoidType : ReturnType

interface BaseObjectType : AssignableType

data class ObjectType(val className: String, var packageName: String?) : BaseObjectType

object NullType : BaseObjectType // surrogate type


data class ArrayType(val componentType: AssignableType) : AssignableType


//Predefined classes

val stringType = ObjectType("String", null)
val objectType = ObjectType("Object", null)