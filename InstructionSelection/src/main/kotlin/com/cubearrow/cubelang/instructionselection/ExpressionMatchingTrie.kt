package com.cubearrow.cubelang.instructionselection

import com.cubearrow.cubelang.common.Expression
import com.cubearrow.cubelang.common.ASMEmitter
import java.util.*


/**
 * The trie used to match expressions to immediate representation values.
 */
class ExpressionMatchingTrie(private val rules: List<Rule>, var emitter: ASMEmitter) {
    private var trieEntries: MutableList<TrieEntry> = ArrayList()

    private val astGetSymbol = ASTGetSymbol()

    init {
        trieEntries.add(TrieEntry(' ', 0))
        buildTrieFromRules(rules)
    }

    private fun buildTrieFromRules(rules: List<Rule>) {
        for (i in rules.indices) {
            buildTrieFromExpression(rules[i].expression, i)
        }
        buildFailureFunctions()
    }

    private fun buildFailureFunctions() {
        val queue: PriorityQueue<Int> = PriorityQueue()
        queue.addAll(trieEntries[0].next)
        while (queue.isNotEmpty()) {
            val r = queue.poll()

            for (s in trieEntries[r].next) {
                queue.add(s)
                // state = f(r)?
                var state = trieEntries[r].failureState
                while (trieEntries[state].next.none { trieEntries[it].value == trieEntries[s].value } && state != 0)
                    state = trieEntries[state].failureState
                val newFailureState = trieEntries[state].next.firstOrNull { trieEntries[it].value == trieEntries[s].value } ?: 0
                trieEntries[s].failureState = newFailureState
                trieEntries[s].isAccepting = combineAcceptingStates(trieEntries[s].isAccepting, trieEntries[newFailureState].isAccepting)
            }

        }
    }

    private fun combineAcceptingStates(accepting: Array<Pair<Boolean, Int>>, accepting1: Array<Pair<Boolean, Int>>): Array<Pair<Boolean, Int>> {
        val newArray: Array<Pair<Boolean, Int>> = Array(accepting.size) {Pair(false, 0)}
        for(i in accepting.indices){
            if(accepting[i].first)
                newArray[i] = accepting[i]
            if(accepting1[i].first)
                newArray[i] = accepting1[i]
        }
        return newArray

    }

    private fun buildTrieFromExpression(expression: Expression, ruleIndex: Int, currentState: Int = 0) {
        val ruleChar = astGetSymbol.evaluate(expression)
        val newState = generateTrieEntryIfNeeded(currentState, ruleChar)
        val children = Utils.getChildren(expression)
        if (children.isEmpty())
            trieEntries[newState].isAccepting[ruleIndex] = Pair(true, trieEntries[newState].length)

        children.forEachIndexed { index, child ->
            val indexState = generateTrieEntryIfNeeded(newState, index.toChar())
            buildTrieFromExpression(child, ruleIndex, indexState)
        }
    }

    private fun generateTrieEntryIfNeeded(currentState: Int, newChar: Char): Int {
        return try {
            trieEntries[currentState].next.first { trieEntries[it].value == newChar }
        } catch (e: NoSuchElementException) {
            val originalLength = trieEntries.size
            val newTrieEntry = TrieEntry(newChar, trieEntries[currentState].length + 1)
            trieEntries[currentState].next.add(originalLength)
            trieEntries.add(newTrieEntry)
            originalLength
        }
    }

    /**
     * Uses the given [[ASMEmitter]] to emit the rules needed for the expression. This method both finds and executes the necessary rules.
     *
     * @return Returns the [[Expression]] returned from the [[Rule.constructString]] method.
     */
    fun emitCodeForExpression(expression: Expression): Expression {
        visit(expression)
        val rule = expression.match['r'] ?:
        TODO("NO RULE for ${expression::class}")

        emitSubRuleReductions(rules[rule].expression, expression)
        return rules[rule].constructString(expression, emitter, this)
    }

    private fun emitSubRuleReductions(rule: Expression, actual: Expression) {
        // Expects rule and actual to have the same arity
        val ruleChildren = Utils.getChildren(rule)
        val actualChildren = Utils.getChildren(actual)

        for (i in ruleChildren.indices) {
            if (ruleChildren[i]::class != actualChildren[i]::class) {
                // Update new child
                val ruleToApply = actualChildren[i].match[astGetSymbol.evaluate(ruleChildren[i])] ?: TODO("Can this be reached?")

                emitSubRuleReductions(rules[ruleToApply].expression, actualChildren[i])
                val newExpression = rules[ruleToApply].constructString(actualChildren[i], emitter, this)
                Utils.setNthChild(i, newExpression, actual)
            } else {
                emitSubRuleReductions(ruleChildren[i], actualChildren[i])
            }
        }
    }


    private fun visit(expression: Expression, previous: Int = 0, index: Int = -1) {
        if (index != -1) {
            val indexState = succ(previous, index.toChar())
            val finalState = succ(indexState, astGetSymbol.evaluate(expression))
            expression.state = finalState
        } else {
            // Expression is the root
            val newState = succ(previous, astGetSymbol.evaluate(expression))
            expression.state = newState
        }

        val children = Utils.getChildren(expression)
        children.forEachIndexed { index, it -> visit(it, expression.state, index) }
        postProcess(expression, children, previous, index)
    }

    private fun postProcess(expression: Expression, children: List<Expression>, previousState: Int = 0, index: Int = -1) {
        setPartial(expression, expression.state)
        if (children.isNotEmpty()) {
            for (i in rules.indices) {
                var product = children[0].b[i] / 2
                for (child in children.subList(1, children.size)) {
                    product = product and child.b[i] / 2
                }

                expression.b[i] = expression.b[i] or product
            }
        }
        doReduce(expression, previousState, index)
    }

    private fun doReduce(expression: Expression, previousState: Int = 0, index: Int = -1) {
        for (i in rules.indices) {
            // If there is a rule matching exactly
            if (expression.b[i] % 2 == 1) {
                val possibleNewCost = rules[i].getCost(expression, astGetSymbol, rules)
                if (possibleNewCost < (expression.cost[rules[i].resultSymbol] ?: Int.MAX_VALUE)) {
                    expression.cost[rules[i].resultSymbol] = possibleNewCost
                    expression.match[rules[i].resultSymbol] = i

                    // Expression is root
                    val x: Int = if (index == -1) {
                        succ(0, rules[i].resultSymbol)
                    } else {
                        succ(succ(previousState, index.toChar()), rules[i].resultSymbol)
                    }
                    setPartial(expression, x)
                }
            }
        }
    }

    private fun setPartial(expression: Expression, state: Int) {
        for (rule in rules.indices) {
            if (trieEntries[state].isAccepting[rule].first) {
                expression.b[rule] = expression.b[rule] or twoToThePowerOf(getTreeLength(trieEntries[state].isAccepting[rule].second))
            }
        }
    }


    private fun twoToThePowerOf(n: Int): Int {
        val base = 2
        var result = 1
        var temp = n

        while (temp != 0) {
            result *= base
            --temp
        }
        return result
    }

    private fun getTreeLength(pathStringLength: Int): Int = (pathStringLength - 1) / 2

    private fun succ(from: Int, to_value: Char): Int {
        return try {
            trieEntries[from].next.first { trieEntries[it].value == to_value }
        } catch (e: NoSuchElementException) {
            if (trieEntries[from].failureState == from)
                return 0
            val newState = succ(trieEntries[from].failureState, to_value)
            newState
        }
    }

    private class TrieEntry(val value: Char, val length: Int) {
        var next: MutableList<Int> = ArrayList()
        var isAccepting: Array<Pair<Boolean, Int>> = Array(Rule.RULE_COUNT) { Pair(false, 0) }
        var failureState: Int = 0
    }
}