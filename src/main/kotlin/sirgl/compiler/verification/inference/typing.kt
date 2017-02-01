package sirgl.compiler.verification.inference

import sirgl.compiler.ast.*
import sirgl.compiler.ast.context.FunctionalDeclaration
import sirgl.compiler.ast.context.FunctionalReference
import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.processing.TreeWalker
import sirgl.compiler.dependency.CallSiteSignature
import sirgl.compiler.dependency.DependencyService
import sirgl.compiler.resolution.SubtypingService
import sirgl.compiler.verification.scope.VerificationError

interface Typed {
    var inferredType: Type?
}

class InferenceService(val dependencyService : DependencyService, val subtypingService : SubtypingService) {
    val errors = mutableListOf<InferenceError>()

    fun addError(inferenceError : InferenceError) {
        errors.add(inferenceError)
        throw InferenceException("Error occured when inferring types")
    }

    fun inferTypes(compilationUnit: CompilationUnit) {
        //TODO
        val walker = TreeWalker()
        walker.addNodeEntranceListener(Block::class.java) {
            it.statements.forEach { statement ->
                when (statement) {
                    is IfStatement -> inferTypes(statement)
                    is WhileStatement -> inferTypes(statement)
                }
            }
        }
    }

    fun inferTypes(ifStatement : IfStatement) {
        inferTypes(ifStatement.condition)
        ifStatement.condition.assertIsBoolean()
    }

    fun inferTypes(whileStatement : WhileStatement) {
        inferTypes(whileStatement.condition)
        whileStatement.condition.assertIsBoolean()
    }

    fun inferTypes(forStatement : ForStatement) {
        val forCondition = forStatement.forCondition
        val forIteration = forStatement.forIteration
        val forInitBlock = forStatement.forInitBlock
        if(forCondition != null) {
            inferTypes(forCondition)
            forCondition.assertIsBoolean()
        }
        if(forIteration != null) {
            inferTypes(forIteration)
        }
        if(forInitBlock != null) {
            when(forInitBlock) {
                is ExpressionInitBlock -> inferTypes(forInitBlock.expr)
                else -> throw UnsupportedOperationException("For init not supported ${forInitBlock.javaClass}")
            }
        }
    }

    fun Expression.assertIsBoolean() = assertIsOfType(BooleanType())

    fun Expression.assertIsOfType(type: Type) {
        if (inferredType != type) {
            addError(UnexpectedExpressionTypeError(this, listOf(type)))
        }
    }

    fun inferTypes(expression : Expression) {
        when(expression) {
            is IntLiteral -> expression.inferredType = IntegerType()
            is CharLiteral -> expression.inferredType = CharType()
            is StringLiteral -> expression.inferredType = stringType
            is BooleanLiteral -> expression.inferredType = BooleanType()
            is NullLiteral -> expression.inferredType = NullType()
            is BinaryExpression -> inferTypes(expression)
            is UnaryExpression -> inferTypes(expression)
            is This -> expression.inferredType = expression.findClassDefinition()?.getType()
            is Reference -> inferTypes(expression)
            is MethodCallExpression -> inferTypes(expression)
            is ObjectInstantiationExpression -> inferTypes(expression)
        }
    }

    fun inferTypes(objectInstantiationExpression : ObjectInstantiationExpression) {
        objectInstantiationExpression.functionCall.parameters.forEach { inferTypes(it) }
        val argumentTypes = objectInstantiationExpression.extractMethodArguments()
        val simpleName = objectInstantiationExpression.functionCall.name
        val imports = objectInstantiationExpression.findCompilationUnit()?.importDeclarations?.filter { it.simpleName == simpleName }
        val availableClassesCount = imports?.size ?: 0
        if(availableClassesCount > 1) {
            addError(AmbigiousClassError(objectInstantiationExpression))
        } else if (availableClassesCount == 0) {
            addError(UnresolvedClassError(objectInstantiationExpression))
        } else {
            val fullName = imports?.first()!!.className
            val availableSignatures = subtypingService.findMethodsWithSignature(fullName, CallSiteSignature(fullName, simpleName, argumentTypes))
            if(availableSignatures.size > 1) {
                addError(AmbigiousMethodResolutionError(objectInstantiationExpression, availableSignatures))
            } else if (availableSignatures.isEmpty()) {
                addError(UnresolvedMethodError(objectInstantiationExpression))
            } else {
                val objectType = ObjectType(fullName.takeWhile { it != '.' }, null)
                objectType.fullName = fullName
                objectInstantiationExpression.inferredType = objectType
            }
        }
    }

    fun Node.findFullClassNameImports(simpleName: String): List<ImportDeclaration>? {
        return findCompilationUnit()?.importDeclarations?.filter { it.simpleName == simpleName }
    }

    fun inferTypes(methodCall: MethodCallExpression) {
        val caller: Expression = methodCall.extractCaller()
        inferTypes(caller)
        methodCall.functionCall.parameters.forEach { inferTypes(it) }
        val argumentTypes = methodCall.extractMethodArguments()
        val callerType = caller.inferredType!!
        if(callerType is ObjectType) {
            val callerClass = dependencyService.findClassByName(callerType.fullName!!)
            val callSiteSignature = CallSiteSignature(callerType.fullName!!, methodCall.functionCall.name, argumentTypes)
            val appliableSignatures = subtypingService.findMethodsWithSignature(callerClass?.fullName!!, callSiteSignature)
            if(appliableSignatures.size > 1) {
                addError(AmbigiousMethodResolutionError(methodCall, appliableSignatures))
            } else if (appliableSignatures.isEmpty()) {
                addError(UnresolvedMethodError(methodCall))
            } else {
                methodCall.inferredType = appliableSignatures.first().returnType
            }
        } else if (callerType is ArrayType) {
            if(methodCall.functionCall.name != "size") {
                addError(UnresolvedMethodError(methodCall))
            }
        } else {
            addError(UnexpectedCallerType(caller))
        }
    }

    fun inferTypes(arrayInstantiationExpression : ArrayInstantiationExpression) {
        val type = arrayInstantiationExpression.type
        TODO() // TODO!!!
    }

    fun FunctionalReference.extractMethodArguments(): List<Type> {
        return functionCall.parameters.map { it.inferredType }.requireNoNulls()
    }

    private fun HasCaller.extractCaller(): Expression {
        var caller: Expression = This()
        if (this.caller != null) {
            caller = this.caller!!
        }
        return caller
    }

    fun inferTypes(expression : Reference) {
        val declaration = expression.findUpperBlock()?.scope?.findDeclaration(expression.referenceName)!!
        expression.inferredType = declaration.type
    }

    fun inferTypes(expression : BinaryExpression) {
        inferTypes(expression.left)
        inferTypes(expression.right)
        when(expression) {
            is BinaryPredicateExpression -> inferTypes(expression)
            is BinaryArithmeticExpression -> inferTypes(expression)
            else -> throw UnsupportedOperationException("Binary expression not supported ${expression.javaClass}")
        }
    }

    private fun inferTypes(expression : BinaryPredicateExpression) {
        when (expression) {
            is AndExpression, is OrExpression -> inferTypesPredicate(expression)
            is EqullityExpression -> {}
            is ComparsionExpression -> inferTypes(expression)
            else -> throw UnsupportedOperationException("Unsupported binary predicate expression ${expression.javaClass}")
        }
        expression.inferredType = BooleanType()
    }


    private fun inferTypesPredicate(expression : BinaryPredicateExpression) {
        if(expression.left.inferredType !is BooleanType) {
            addError(UnexpectedExpressionTypeError(expression.left, BooleanType()))
        }
        if(expression.right.inferredType !is BooleanType) {
            addError(UnexpectedExpressionTypeError(expression.right, BooleanType()))
        }
    }

    private fun inferTypes(expression : ComparsionExpression) {
        childrenAreArithmetic(expression)
    }

    private fun childrenAreArithmetic(expression: BinaryExpression) {
        if (expression.left.inferredType?.isArithmetic() ?: false) {
            addError(UnexpectedExpressionTypeError(expression.left, listOf(ByteType(), CharType(), IntegerType())))
        }
        if (expression.right.inferredType?.isArithmetic() ?: false) {
            addError(UnexpectedExpressionTypeError(expression.right, listOf(ByteType(), CharType(), IntegerType())))
        }
    }

    private fun inferTypes(expression : BinaryArithmeticExpression) {
        childrenAreArithmetic(expression)
        expression.inferredType = subtypingService.getMaximalType(expression.left.inferredType, expression.right.inferredType)
        if(expression.inferredType == null) {
            addError(CastImpossibleError(expression.left, expression.right))
        }
    }

    fun inferTypes(expression : UnaryExpression) {
        inferTypes(expression.expr)
        when (expression) {
            is NegateExpression -> inferTypes(expression)
            is UnaryMinusExpression -> inferTypes(expression)
        }
    }

    private fun inferTypes(expression: NegateExpression) {
        if(expression.expr.inferredType !is BooleanType) {
            addError(UnexpectedExpressionTypeError(expression.expr, BooleanType()))
        }
        expression.expr.inferredType = BooleanType()
    }


    private fun inferTypes(expression: UnaryMinusExpression) {
        if(expression.expr.inferredType?.isArithmetic() ?: false) {
            addError(UnexpectedExpressionTypeError(expression.expr, listOf(ByteType(), CharType(), IntegerType())))
        }
        expression.expr.inferredType = BooleanType()
    }
}

class UnexpectedExpressionTypeError(val expression : Expression, val exectedTypes: List<Type>) : InferenceError() {
    constructor(expression : Expression, expectedType: Type) : this(expression, listOf(expectedType))
}

class CastImpossibleError(val expression1 : Expression, val expression2 : Expression) : InferenceError()

class UnexpectedCallerType(val caller: Expression) : InferenceError()

class AmbigiousMethodResolutionError(
        var methodCall : FunctionalReference,
        var declarations : List<FunctionalDeclaration>
) : InferenceError()


class AmbigiousClassError(val objectInstantiationExpression : ObjectInstantiationExpression) : InferenceError()

class UnresolvedMethodError(val methodCall : FunctionalReference) : InferenceError()

class UnresolvedClassError(val objectInstantiationExpression : ObjectInstantiationExpression) : InferenceError()

open class InferenceError : VerificationError()

class InferenceException(reason: String) : RuntimeException(reason)