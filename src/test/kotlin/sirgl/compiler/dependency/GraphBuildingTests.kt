package sirgl.compiler.dependency

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.ClassDefinition
import sirgl.compiler.ast.CompilationUnit
import sirgl.compiler.ast.ImportDeclaration
import sirgl.compiler.ast.PackageDeclaration

class GraphBuildingTests {
    @Test
    fun `empty graph finds no roots`() {
        val roots = DependencyGraphBuilder(listOf(), listOf()).findRoots()
        assertThat(roots).isEmpty()
    }

    @Test
    fun `single node graph`() {
        val unit = CompilationUnit(ClassDefinition("myClass"), PackageDeclaration("my.package"), emptyList())
        val roots = DependencyGraphBuilder(listOf(unit), listOf()).findRoots()
        assertThat(roots).containsExactly(DependencyNode("my.package.myClass", unit, true))
    }

    @Test
    fun `class and parent graph`() {
        val unit = CompilationUnit(
                ClassDefinition("myClass", superClass = "parentClass"),
                PackageDeclaration("my.package"),
                listOf(ImportDeclaration("my.package.parentClass"))
        )
        val parentUnit = CompilationUnit(ClassDefinition("parentClass"), PackageDeclaration("my.package"), emptyList())
        val roots = DependencyGraphBuilder(listOf(unit, parentUnit), listOf()).findRoots()
        assertThat(roots).containsExactly(parentUnit.toNode())
        val actualParent = roots.first()
        assertThat(actualParent.subclasses).containsExactly(unit.toNode())
    }

    @Test
    fun `error when parent class not found`() {
        val unit = CompilationUnit(
                ClassDefinition("myClass", superClass = "parentClass"),
                PackageDeclaration("my.package"),
                listOf(ImportDeclaration("my.package.parentClass"))
        )
        val builder = DependencyGraphBuilder(listOf(unit), listOf())
        builder.findRoots()
        assertThat(builder.errors).containsExactly(ClassNotFoundError("my.package.parentClass"))
    }


    @Test
    fun `2 class-parent pairs and another node`() {
        val child1 = CompilationUnit(
                ClassDefinition("child1", superClass = "parent1"),
                PackageDeclaration("my.package"),
                listOf(ImportDeclaration("my.package.parent1"))
        )
        val child2 = CompilationUnit(
                ClassDefinition("child2", superClass = "parent2"),
                PackageDeclaration("my.package"),
                listOf(ImportDeclaration("my.package.parent2"))
        )
        val child3 = CompilationUnit(
                ClassDefinition("child3", superClass = "parent2"),
                PackageDeclaration("my.package"),
                listOf(ImportDeclaration("my.package.parent2"))
        )
        val parent1 = CompilationUnit(ClassDefinition("parent1"), PackageDeclaration("my.package"), emptyList())
        val parent2 = CompilationUnit(ClassDefinition("parent2"), PackageDeclaration("my.package"), emptyList())
        val parent3 = CompilationUnit(ClassDefinition("parent3"), PackageDeclaration("my.package"), emptyList())
        val roots = DependencyGraphBuilder(listOf(child1, child2, child3, parent1, parent2, parent3), listOf()).findRoots()
        assertThat(roots).containsOnly(
                DependencyNode(parent1.fullName, parent1, true),
                DependencyNode(parent2.fullName, parent2, true),
                DependencyNode(parent3.fullName, parent3, true)
        )
        assertThat(roots.first { it.qualifiedName == "my.package.parent1" }.subclasses)
                .containsExactly(child1.toNode())
        assertThat(roots.first { it.qualifiedName == "my.package.parent2" }.subclasses)
                .containsExactly(child2.toNode(), child3.toNode())
    }

    private fun CompilationUnit.toNode()  = DependencyNode(fullName, this, true)

}
