package com.cubearrow.cubelang.parser

import com.cubearrow.cubelang.lexer.Token

/**
 * This class is generated automatically by the [ASTGenerator]
 **/
abstract class Expression {

    class Assignment (var identifier1: Token, var expression1: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitAssignment(this)
        }
    }

    class VarInitialization (var identifier1: Token, var expression1: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitVarInitialization(this)
        }
    }

    class Operation (var expression1: Expression, var operator1: Token, var expression2: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitOperation(this)
        }
    }

    class Call (var identifier1: Token, var expressionLst1: MutableList<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitCall(this)
        }
    }

    class Literal (var any1: Any?) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitLiteral(this)
        }
    }

    class VarCall (var identifier1: Token) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitVarCall(this)
        }
    }

    class FunctionDefinition (var identifier1: Token, var expressionLst1: MutableList<Expression>, var expressionLst2: MutableList<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitFunctionDefinition(this)
        }
    }

    class Comparison (var expression1: Expression, var comparator1: Token, var expression2: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitComparison(this)
        }
    }

    class IfStmnt (var expression1: Expression, var expressionLst1: MutableList<Expression>, var expressionLst2: MutableList<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitIfStmnt(this)
        }
    }

    class ReturnStmnt (var expression1: Expression) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitReturnStmnt(this)
        }
    }

    class WhileStmnt (var expression1: Expression, var expressionLst1: MutableList<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitWhileStmnt(this)
        }
    }

    class ForStmnt (var expressionLst1: MutableList<Expression>, var expressionLst2: MutableList<Expression>) : Expression() {
        override fun <R> accept(visitor: ExpressionVisitor<R>): R {
            return visitor.visitForStmnt(this)
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
    }
    abstract fun <R> accept(visitor: ExpressionVisitor<R>): R
}
