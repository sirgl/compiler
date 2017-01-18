package sirgl.compiler.verification.scope

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.FieldDeclaration
import sirgl.compiler.ast.IntLiteral
import sirgl.compiler.ast.IntegerType
import sirgl.compiler.ast.VariableDeclarationStatement
import sirgl.compiler.parseCompilationUnit

class ScopeCheckerTests {
    @Test
    fun `valid example`() {
        val ast = parseCompilationUnit("compiler/transform/correct/1.lng")
        val checker = ScopeChecker(ast)
        assertThat(checker.check()).isEmpty()
    }

    @Test
    fun `variable redeclare field`() {
        val ast = parseCompilationUnit("compiler/verification/scope/variable_field_redeclaration.lng")
        val checker = ScopeChecker(ast)
        assertThat(checker.check()).containsOnly(RedeclarationError(listOf(
                VariableDeclarationStatement(IntegerType(), "a", IntLiteral(12)),
                FieldDeclaration("a", IntegerType())
        )))
    }

    @Test
    fun `variable redeclare variable`() {
        val ast = parseCompilationUnit("compiler/verification/scope/variable_redeclaration.lng")
        val checker = ScopeChecker(ast)
        assertThat(checker.check()).containsOnly(RedeclarationError(listOf(
                VariableDeclarationStatement(IntegerType(), "a", IntLiteral(12)),
                VariableDeclarationStatement(IntegerType(), "a", IntLiteral(13))
        )))
    }
}