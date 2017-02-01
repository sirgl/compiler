package sirgl.compiler.ast

import sirgl.compiler.ast.context.FunctionalContext
import sirgl.compiler.ast.context.FunctionalReference
import sirgl.compiler.ast.context.Reference
import sirgl.compiler.ast.context.ReferenceContext
import sirgl.compiler.verification.inference.Typed

interface Expression : Statement, Typed

interface BinaryExpression  : Expression {
    var left: Expression
    var right: Expression
}

interface UnaryExpression : Expression {
    var expr: Expression
}

interface PredicateExpression : Expression

interface BinaryPredicateExpression : BinaryExpression, PredicateExpression
interface UnaryPredicateExpression : UnaryExpression, PredicateExpression

interface HasCaller {
    var caller: Expression?
}


class This() : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null

    constructor(line: Int, position : Int) : this() {
        this.line = line
        this.position = position
    }

    override fun equals(other: Any?): Boolean {
        return this.javaClass == other?.javaClass
    }

    override fun hashCode(): Int {
        return this.javaClass.hashCode()
    }
}

data class VariableAccess(override var referenceName: String) : Expression, Reference {
    override var referenceContext: ReferenceContext? = null
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(name: String, line: Int, position : Int) : this(name) {
        this.line = line
        this.position = position
    }
}

data class FunctionCall(var name: String, var parameters: List<Expression>)


data class MethodCallExpression(override var caller: Expression?, override var functionCall : FunctionCall) : FunctionalReference, Expression, HasCaller {
    override var referenceContext: FunctionalContext? = null
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(caller: Expression?, functionCall : FunctionCall, line: Int, position : Int) : this(caller, functionCall) {
        this.line = line
        this.position = position
    }
}

data class ObjectInstantiationExpression(override var functionCall : FunctionCall) : FunctionalReference, Expression {
    override var referenceContext: FunctionalContext? = null
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(functionCall : FunctionCall, line: Int, position : Int) : this(functionCall) {
        this.line = line
        this.position = position
    }
}

data class ArrayInstantiationExpression(var size: Int, var type: AssignableType, val level: Int) : Expression  {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(size : Int, type : AssignableType, level : Int, line: Int, position : Int) : this(size, type, level) {
        this.line = line
        this.position = position
    }
}

data class FieldAccessExpression(override var caller: Expression?, override var referenceName: String) : Expression, Reference, HasCaller {
    override var referenceContext: ReferenceContext? = null
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(caller: Expression?, fieldName: String, line: Int, position : Int) : this(caller, fieldName) {
        this.line = line
        this.position = position
    }
}

interface ArithmeticExpression
interface BinaryArithmeticExpression : BinaryExpression, ArithmeticExpression
interface UnaryArithmeticExpression : UnaryExpression, ArithmeticExpression

data class DivideExpression(override var left: Expression, override var right: Expression) : BinaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class MultiplyExpression(override var left: Expression, override var right: Expression) : BinaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class SumExpression(override var left: Expression, override var right: Expression) : BinaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class SubtractExpression(override var left: Expression, override var right: Expression) : BinaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class RemainderExpression(override var left: Expression, override var right: Expression) : BinaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class UnaryMinusExpression(override var expr: Expression) : UnaryArithmeticExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(expr : Expression, line : Int, position : Int) : this(expr) {
        this.line = line
        this.position = position
    }
}

data class NegateExpression(override var expr: Expression) : UnaryPredicateExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(expr : Expression, line : Int, position : Int) : this(expr) {
        this.line = line
        this.position = position
    }
}

interface ComparsionExpression : BinaryPredicateExpression

data class GreaterThanExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression, ComparsionExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}


data class GreaterThanOrEqualsExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression, ComparsionExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class LessThanExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression, ComparsionExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}


data class LessThanOrEqualsExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression, ComparsionExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class AndExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class OrExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class EqullityExpression(override var left: Expression, override var right: Expression) : BinaryPredicateExpression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null



    constructor(left:Expression, right : Expression, line: Int, position: Int) : this(left, right) {
        this.line = line
        this.position = position
    }
}

data class ArrayElementAccessExpression(override var caller: Expression?, var index: Expression) : Expression, HasCaller {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(caller : Expression?, index : Expression, line : Int, position : Int) : this(caller, index) {
         this.line = line
        this.position = position
    }
}

data class AssignmentExprssion(var reference : Reference, var expr : Expression) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(reference : Reference, expr : Expression, line : Int, position : Int) : this(reference, expr) {
        this.line = line
        this.position = position
    }
}

data class ClassCastExpression(var className: String, var expr : Expression) : Expression {
    override var line: Int? = null
    override var position: Int? = null
    override var parent: Node? = null
    override var inferredType: Type? = null


    constructor(className : String, expr : Expression, line : Int, position : Int) : this(className, expr) {
        this.line = line
        this.position = position
    }
}