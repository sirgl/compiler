package sirgl.compiler

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.Node
import sirgl.compiler.ast.processing.TreeWalker

class ParserTests {
    @Test
    fun `parents must be set`() {
        val ast = parseCompilationUnit("compiler/transform/correct/1.lng")
        val walker = TreeWalker()
        walker.addNodeEntranceListener(Node::class.java, {
            assertThat(it.parent).isNotNull()
        })
        walker.walk(ast.classDefinition)
    }
}