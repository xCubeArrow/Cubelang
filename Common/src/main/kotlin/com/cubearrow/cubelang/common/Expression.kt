package com.cubearrow.cubelang.common


import com.cubearrow.cubelang.common.tokens.Token

abstract class Expression(var state: Int = 0) {

    class Assignment (val name: Token, var valueExpression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitAssignment(this)
        }
    }

    class VarInitialization (val name: Token, var type: Type, var valueExpression: Expression?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitVarInitialization(this)
        }
    }

    class Operation (var leftExpression: Expression, val operator: Token, var rightExpression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitOperation(this)
        }
    }

    class Call (val callee: VarCall, var arguments: List<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitCall(this)
        }
    }

    class Literal (val value: Any?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitLiteral(this)
        }
    }

    class VarCall (val varName: Token) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitVarCall(this)
        }
    }

    class FunctionDefinition (val name: Token, val args: List<Expression>, val type: Type, var body: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitFunctionDefinition(this)
        }
    }

    class Comparison (var leftExpression: Expression, val comparator: Token, var rightExpression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitComparison(this)
        }
    }

    class IfStmnt (val condition: Expression, var ifBody: Expression, var elseBody: Expression?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitIfStmnt(this)
        }
    }

    class ReturnStmnt (var returnValue: Expression?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitReturnStmnt(this)
        }
    }

    class WhileStmnt (var condition: Expression, var body: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitWhileStmnt(this)
        }
    }

    class ForStmnt (var inBrackets: List<Expression>, var body: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitForStmnt(this)
        }
    }

    class StructDefinition (val name: Token, val body: List<VarInitialization>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitStructDefinition(this)
        }
    }

    class InstanceGet (val expression: Expression, val identifier: Token) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitInstanceGet(this)
        }
    }

    class InstanceSet (val instanceGet: InstanceGet, val value: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitInstanceSet(this)
        }
    }

    class ArgumentDefinition (val name: Token, val type: Type) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitArgumentDefinition(this)
        }
    }

    class BlockStatement (var statements: List<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitBlockStatement(this)
        }
    }

    class Logical (var leftExpression: Expression, val logical: Token, var rightExpression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitLogical(this)
        }
    }

    class Unary (val identifier: Token, var expression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitUnary(this)
        }
    }

    class Grouping (var expression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitGrouping(this)
        }
    }

    class ArrayGet (val expression: Expression, val inBrackets: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitArrayGet(this)
        }
    }

    class ArraySet (val arrayGet: ArrayGet, val value: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitArraySet(this)
        }
    }

    class ImportStmnt (val identifier: Token) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitImportStmnt(this)
        }
    }

    class PointerGet (val varCall: VarCall) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitPointerGet(this)
        }
    }

    class ValueFromPointer (var expression: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitValueFromPointer(this)
        }
    }

    class Empty (val any: Any?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitEmpty(this)
        }
    }
    class FramePointer: Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            TODO("Not yet implemented")
        }
    }

    interface ExpressionVisitor<R> {
        fun visitAssignment(assignment: Assignment): R
        fun visitVarInitialization(varInitialization: VarInitialization): R
        fun visitOperation(operation: Operation): R
        fun visitCall(call: Call): R
        fun visitLiteral(literal: Literal): R
        fun visitVarCall(varCall: VarCall): R
        fun visitFunctionDefinition(functionDefinition: FunctionDefinition): R
        fun visitComparison(comparison: Comparison): R
        fun visitIfStmnt(ifStmnt: IfStmnt): R
        fun visitReturnStmnt(returnStmnt: ReturnStmnt): R
        fun visitWhileStmnt(whileStmnt: WhileStmnt): R
        fun visitForStmnt(forStmnt: ForStmnt): R
        fun visitStructDefinition(structDefinition: StructDefinition): R
        fun visitInstanceGet(instanceGet: InstanceGet): R
        fun visitInstanceSet(instanceSet: InstanceSet): R
        fun visitArgumentDefinition(argumentDefinition: ArgumentDefinition): R
        fun visitBlockStatement(blockStatement: BlockStatement): R
        fun visitLogical(logical: Logical): R
        fun visitUnary(unary: Unary): R
        fun visitGrouping(grouping: Grouping): R
        fun visitArrayGet(arrayGet: ArrayGet): R
        fun visitArraySet(arraySet: ArraySet): R
        fun visitImportStmnt(importStmnt: ImportStmnt): R
        fun visitPointerGet(pointerGet: PointerGet): R
        fun visitValueFromPointer(valueFromPointer: ValueFromPointer): R
        fun visitEmpty(empty: Empty): R
    }
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}
