package sirgl.compiler.ast.processing

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.MethodCallExpression
import sirgl.compiler.parseCompilationUnit

class WalkerTests {
    @Test
    fun `walk method calls`() {
        val ast = parseCompilationUnit("compiler/transform/correct/1.lng")
        val walker = TreeWalker()
        val methodNames = mutableListOf<String>()
        walker.addNodeEntranceListener(MethodCallExpression::class.java, { node : MethodCallExpression ->
            methodNames.add(node.functionCall.name)
        })
        walker.walk(ast.classDefinition)
        assertThat(methodNames).containsOnly("print", "println")
    }
}