package sirgl.compiler.transformations

import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.ObjectType
import sirgl.compiler.ast.processing.TreeWalker


fun setObjectNames(compilationUnit: CompilationUnit) {
    val walker = TreeWalker()
    walker.addNodeEntranceListener(ObjectType::class.java) {
        it.fullName = compilationUnit.fullName
    }
    walker.walk(compilationUnit.classDefinition)
}
