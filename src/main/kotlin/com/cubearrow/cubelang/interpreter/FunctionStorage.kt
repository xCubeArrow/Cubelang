package com.cubearrow.cubelang.interpreter

import com.cubearrow.cubelang.lexer.Token
import com.cubearrow.cubelang.library.Library
import com.cubearrow.cubelang.main.Main
import com.cubearrow.cubelang.parser.Expression

/**
 * Stores the instances of [Callable] defined in a program
 */
class FunctionStorage {
    class Function(override val name: String, override var args: List<String>, private var body: List<Expression>) : Callable {
        override fun call(variableStorage: VariableStorage, functionStorage: FunctionStorage): Any? {
            return Interpreter(this.body, variableStorage, functionStorage).returnedValue
        }
    }

    var functions = ArrayList<Callable>()

    fun addFunction(name: Token, args: List<String>, body: List<Expression>) {
        if (functions.stream().anyMatch { it.name == name.substring && it.args == args }) {
            Main.error(name.line, name.index, null, "A function with the specified name and argument size already exists")
        } else {
            functions.add(Function(name.substring, args, body))
        }
    }

    fun addFunction(callable: Callable) {
        if (functions.stream().anyMatch { it.name == callable.name && it.args == callable.args }) {
            Main.error(-1, -1, null, "A function with the specified name and argument size already exists")
        } else {
            functions.add(callable)
        }
    }

    fun getFunction(name: String, argsSize: Int): Callable? = functions.stream().filter { it.name == name && it.args.size == argsSize }.findFirst().orElse(null)
    fun addFunctions(functions: ArrayList<Callable>) {
        val result = ArrayList<Callable>()

        for(callable in functions){
            this.functions.stream().filter { it.name != callable.name && it.args != callable.args }.forEach{result.add(callable)}
        }
        this.functions.addAll(result)
    }

    init {
        functions.addAll(Library().classes)
    }
}