package sirgl.compiler.verification.scope

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.*

class ScopeCheckSessionTests {
    @Test
    fun `valid scope - no errors`() {
        val session = ScopeCheckSession(Scope())
        session.add(VariableDeclarationStatement(IntegerType(), "a", IntLiteral(211)))
        session.add(VariableAccess("a"))
        assertThat(session.getErrors()).isEmpty()
    }

    @Test
    fun `undefined variable leads to error`() {
        val session = ScopeCheckSession(Scope())
        session.add(VariableAccess("a"))
        session.add(VariableAccess("b"))
        assertThat(session.getErrors()).containsOnly(
                UndefinedVariableUsageError(VariableAccess("a")),
                UndefinedVariableUsageError(VariableAccess("b"))
        )
    }

    @Test
    fun `redeclaration variable leads to error`() {
        val session = ScopeCheckSession(Scope())
        val referenceDeclaration1 = VariableDeclarationStatement(IntegerType(), "a", IntLiteral(211))
        val referenceDeclaration2 = VariableDeclarationStatement(IntegerType(), "a", IntLiteral(123))
        val referenceDeclaration3 = VariableDeclarationStatement(IntegerType(), "a", IntLiteral(111))
        session.add(referenceDeclaration1)
        session.add(referenceDeclaration2)
        session.add(referenceDeclaration3)
        assertThat(session.getErrors()).containsOnly(
                RedeclarationError(listOf(referenceDeclaration2, referenceDeclaration3, referenceDeclaration1))
        )
    }

    @Test
    fun `redeclaration with parent scope`() {
        val referenceDeclaration2 = VariableDeclarationStatement(IntegerType(), "a", IntLiteral(123))
        val parentScope = Scope(Scope())
        parentScope.tryAddReferenceDeclaration(referenceDeclaration2)
        val scope = parentScope
        val session = ScopeCheckSession(scope)
        val referenceDeclaration1 = VariableDeclarationStatement(IntegerType(), "a", IntLiteral(211))
        session.add(referenceDeclaration1)
        val errors = session.getErrors()
        assertThat(errors).containsOnly(
                RedeclarationError(listOf(referenceDeclaration1, referenceDeclaration2))
        )
    }
}