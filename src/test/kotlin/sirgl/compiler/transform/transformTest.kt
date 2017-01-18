package sirgl.compiler.transform

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import sirgl.compiler.ast.*
import sirgl.compiler.parseCompilationUnit
import sirgl.compiler.parserForText

class TransformTest {
    @Test
    fun `correct file parsing`() {
        val ast = parseCompilationUnit("compiler/transform/correct/1.lng")
        val compilationUnit = CompilationUnit(
                ClassDefinition(
                        "MyClass",
                        fields = listOf(
                                FieldDeclaration("a", IntegerType),
                                FieldDeclaration("b", IntegerType),
                                FieldDeclaration("c", BooleanType),
                                FieldDeclaration("d", CharType),
                                FieldDeclaration("out", ObjectType("Out", null))
                        ),
                        methods = listOf(
                                MethodDefinition(MethodDeclaration(
                                        functionalName = "doX",
                                        paremeters = listOf(),
                                        returnType = stringType
                                ),
                                        block = Block(listOf(
                                                IfStatement(condition = BooleanLiteral(bool = true), block = Block(
                                                        listOf(
                                                                AssignmentExprssion(VariableAccess("a"), IntLiteral(23)),
                                                                MethodCallExpression(VariableAccess("out"), FunctionCall("println", listOf(StringLiteral("Correct"))))
                                                        )
                                                ), elseBlock = Block(
                                                        listOf(
                                                                MethodCallExpression(VariableAccess("out"), FunctionCall("println", listOf(StringLiteral("Impossible"))))
                                                        )
                                                )),
                                                WhileStatement(
                                                        condition = BooleanLiteral(true),
                                                        block = Block(
                                                                listOf(
                                                                        MethodCallExpression(VariableAccess("out"), FunctionCall("println", listOf(StringLiteral("Once")))),
                                                                        IfStatement(VariableAccess("c"), Block(
                                                                                listOf(
                                                                                        MethodCallExpression(VariableAccess("out"), FunctionCall("print", listOf(StringLiteral("Inner block"))))
                                                                                )
                                                                        ), null),
                                                                        BreakStatement()
                                                                )
                                                        )
                                                )
                                        ))
                                ),
                                MethodDefinition(
                                        MethodDeclaration("doY", listOf(), VoidType), Block(listOf(
                                        MethodCallExpression(VariableAccess("out"), FunctionCall("println", listOf(StringLiteral("Another function"))))
                                )
                                )
                                )
                        ),
                        constructors = listOf(
                                ConstructorDefinition(ConstructorDeclaration("MyClass", listOf()), Block(listOf(
                                        AssignmentExprssion(VariableAccess("a"), IntLiteral(12)),
                                        AssignmentExprssion(FieldAccessExpression(This(), "b"), IntLiteral(34)),
                                        AssignmentExprssion(FieldAccessExpression(This(), "c"), BooleanLiteral(true)),
                                        AssignmentExprssion(FieldAccessExpression(This(), "d"), CharLiteral('a')),
                                        AssignmentExprssion(FieldAccessExpression(This(), "out"), ObjectInstantiationExpression(FunctionCall("Out", listOf())))
                                        ))
                                )
                        ),
                        superClass = null
                ),
                packageDeclaration = PackageDeclaration("my.test.hello"),
                importDeclarations = listOf()
        )
        assertThat(ast).isEqualTo(compilationUnit)
    }

    @Test
    fun `assignment with this field`() {
        val ast = parserForText("this.x = 12;").blockExpr().toStatementAst()
        assertThat(ast).isEqualTo(AssignmentExprssion(FieldAccessExpression(This(), "x"), IntLiteral(12)))
    }
}