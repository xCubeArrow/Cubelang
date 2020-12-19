import com.cubearrow.cubelang.compiler.Compiler
import com.cubearrow.cubelang.interpreter.Interpreter
import com.cubearrow.cubelang.lexer.TokenSequence
import com.cubearrow.cubelang.parser.Parser
import com.cubearrow.cubelang.utils.ConsoleColor
import com.cubearrow.cubelang.utils.IOUtils.Companion.readAllText
import kotlin.system.exitProcess
import kotlin.system.getTimeMicros

fun main(args: Array<String>) {
    if (args.size == 1) {
        Main().compileFile(args[0])
    } else {
        println("No source file was provided")
        exitProcess(64)
    }
}
@ThreadLocal
var containsError = false
@ThreadLocal
var exitAfterError = false

class Main {
    companion object {
        const val useCompiler = true


        /**
         * Prints an error in the console while specifying the line and character index.
         *
         * If [exitAfterError] is active, this will exit the process.
         *
         * @param line The line at which the error is located
         * @param index The character index at which the error is located/starts
         * @param fullLine The full line of code, if available
         * @param message The error message itself
         */
        fun error(line: Int, index: Int, fullLine: String?, message: String) {
            if (fullLine != null) {
                val indicator = " ".repeat(index - 1) + "^"
                println(
                    """
                ${ConsoleColor.ANSI_RED}${fullLine}
                $indicator
                Error [$line:$index]: $message ${ConsoleColor.ANSI_WHITE}
            """.trimIndent()
                )
            } else {
                println(
                    """
                ${ConsoleColor.ANSI_RED}Error [$line:$index]: $message ${ConsoleColor.ANSI_WHITE}
            """.trimIndent()
                )
            }
            containsError = true
            if (exitAfterError)
                exitProcess(65)
        }
    }


    fun compileFile(sourceFile: String) {
//        ASTGenerator("src/nativeMain/kotlin/com/cubearrow/cubelang/parser/", "src/nativeMain/resources/SyntaxGrammar.txt")
        val startTime = getTimeMicros()
        val sourceCode = readAllText(sourceFile)
        val tokenSequence = TokenSequence(sourceCode)
        val tokenSequenceTime = getTimeMicros()
        val expressions = Parser(tokenSequence.tokenSequence).parse()
        val parserMillis = getTimeMicros()
        if (containsError)
            exitProcess(65)
        exitAfterError = true
        if (useCompiler) {
            Compiler(expressions, "src/nativeMain/resources/output.asm")
            val compilerTime = getTimeMicros()
            println("Start - TokenSequence: ${tokenSequenceTime - startTime}μs")
            println("TokenSequence - Parsed: ${parserMillis - tokenSequenceTime}μs")
            println("Parsed - Compiled: ${compilerTime - parserMillis}μs")
        } else {
            Interpreter(expressions)
        }
    }
}