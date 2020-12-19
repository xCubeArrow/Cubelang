package com.cubearrow.cubelang.interpreter.library

import Main
import com.cubearrow.cubelang.compiler.NormalType
import com.cubearrow.cubelang.interpreter.Callable
import com.cubearrow.cubelang.interpreter.FunctionStorage
import com.cubearrow.cubelang.interpreter.VariableStorage
import kotlin.math.absoluteValue

class UtilLibrary {
    class Len : Callable {
        override val name = "len"
        override val args = mapOf("string" to NormalType("string"))

        override fun call(variableStorage: VariableStorage, functionStorage: FunctionStorage): Int {
            val any = variableStorage.getCurrentVariables()["string"]
            if (any?.value is String) {
                return any.value.length
            }
            Main.error(-1, -1, null, "The function len() can only be called on strings.")
            return -1
        }

    }

    class Abs : Callable {
        override val name = "abs"
        override val args = mapOf("string" to NormalType("string"))

        override fun call(variableStorage: VariableStorage, functionStorage: FunctionStorage): Double {
            val any = variableStorage.getCurrentVariables()["string"]
            if (any?.value is Double) {
                return any.value.absoluteValue
            }
            Main.error(-1, -1, null, "The function abs() can only be called on numbers.")
            return -1.0
        }

    }
}