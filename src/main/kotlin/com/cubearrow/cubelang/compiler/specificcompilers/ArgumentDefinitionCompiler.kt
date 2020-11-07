package com.cubearrow.cubelang.compiler.specificcompilers

import com.cubearrow.cubelang.compiler.Compiler
import com.cubearrow.cubelang.compiler.CompilerContext
import com.cubearrow.cubelang.compiler.CompilerUtils
import com.cubearrow.cubelang.parser.Expression

class ArgumentDefinitionCompiler(var context: CompilerContext): SpecificCompiler<Expression.ArgumentDefinition> {
    override fun accept(expression: Expression.ArgumentDefinition): String {
        val length: Int = Compiler.LENGTHS_OF_TYPES[expression.identifier2.substring]

        context.stackIndex.push(context.stackIndex.pop() + length)
        context.variables.peek()[expression.identifier1.substring] = Compiler.LocalVariable(context.stackIndex.peek(), expression.identifier2.substring, length)

        val register = Compiler.ARGUMENT_INDEXES[context.argumentIndex++]
        return if (length > 2) {
            "mov ${CompilerUtils.getASMPointerLength(length)}[rbp - ${context.stackIndex.peek()}], ${CompilerUtils.getRegister(register, length)}"
        } else {
            "mov eax, ${CompilerUtils.getRegister(register, 4)}\n" +
                    "mov ${CompilerUtils.getASMPointerLength(length)}[rbp - ${context.stackIndex.peek()}], ${CompilerUtils.getRegister("ax", length)}"
        }
    }
}