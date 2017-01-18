package sirgl.compiler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.Node
import sirgl.compiler.ast.processing.TreeWalker

class ParserTests {
    @Test
    fun `parents must be set`() {
        val ast = parseCompilationUnit("compiler/transform/correct/1.lng")
        val walker = TreeWalker()
        walker.addNodeEntranceListener(Node::class.java, {
            if(it !is ClassDefinition) {
                assertThat(it.parent)
                        .describedAs("parent of object with class ${it.javaClass} must not be null")
                        .isNotNull()
            }
        })
        walker.walk(ast.classDefinition)
    }
}