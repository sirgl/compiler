package sirgl.compiler.util

fun getAllSuperclasses(clazz : Class<*>) : MutableList<Class<*>> {
    if(clazz == Any::class.java){
        return mutableListOf()
    }
    val allSuperclasses = getAllSuperclasses(clazz.superclass)
    allSuperclasses.add(clazz)
    allSuperclasses.addAll(clazz.interfaces.flatMap(::getAllInterfaces))
    return allSuperclasses
}

private fun getAllInterfaces(interfaceClass: Class<*>) : MutableList<Class<*>> {
    val interfaces = mutableListOf<Class<*>>()
    interfaces.addAll(interfaceClass.interfaces.flatMap(::getAllInterfaces))
    interfaces.add(interfaceClass)
    return interfaces
}