/**
 * @author Daewon on 07,August,2023
 *
 */

package com.example.domain

object Expression {

    private val currentOperandList = mutableListOf<String>()
    var lastInputState = InputState.Init

    fun currentInitStateInput(input: String): Result<Unit> {
        if (input.isOperand()) {
            addElement(input)
            lastInputState = InputState.Operand
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException(NEED_OPERAND))
    }

    fun currentOperandStateInput(input: String): Result<Unit> {
        if (input.isOperand()) {
            currentOperandList[currentOperandList.lastIndex] =
                currentOperandList.last() + input
            return Result.success(Unit)
        } else if (input.isOperator()) {
            addElement(input)
            lastInputState = InputState.Operator
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException(NEED_OPERAND_AND_OPERATOR))
    }

    fun currentOperatorStateInput(input: String): Result<Unit> {
        if (input.isOperand()) {
            addElement(input)
            lastInputState = InputState.Operand
            return Result.success(Unit)
        } else if (input.isOperator()) {
            currentOperandList[currentOperandList.lastIndex] = input
            return Result.success(Unit)
        }
        return Result.failure(IllegalArgumentException(NEED_OPERAND_AND_OPERATOR))
    }

    private fun addElement(input: String) = currentOperandList.add(input)

    fun removeLastInput() {
        if (currentOperandList.isNotEmpty()) {
            when(lastInputState) {
                InputState.Init -> return
                InputState.Operand -> removeLastOperand()
                InputState.Operator -> removeLastOperator()
            }
            updateCurrentInputState()
        }
    }

    private fun removeLastOperand() {
        currentOperandList[currentOperandList.lastIndex] = currentOperandList.last().dropLast(1)
        if (currentOperandList.last().isEmpty()) {
            currentOperandList.removeLast()
        }
    }

    private fun removeLastOperator() {
        currentOperandList.removeLast()
    }

    private fun String.isOperand(): Boolean = this.toBigDecimalOrNull() != null

    private fun String.isOperator(): Boolean =
        runCatching { OperatorFinder.findOperator(this) }.getOrNull() != null

    fun clearCurrentOperandList() {
        currentOperandList.clear()
        updateCurrentInputState()
    }

    private fun updateCurrentInputState() {
        if(currentOperandList.isEmpty()) {
            lastInputState = InputState.Init
        } else if(currentOperandList.last().isOperand()) {
            lastInputState = InputState.Operand
        } else if(currentOperandList.last().isOperator()) {
            lastInputState = InputState.Operator
        }
    }

    fun isValidExpression() {
        require(lastInputState == InputState.Operand && currentOperandList.size > 2) { "완성되지 않은 수식입니다." }
    }

    fun showExpression() = currentOperandList.joinToString(" ")
}

const val NEED_OPERAND = "숫자를 먼저 입력해주세요."
const val NEED_OPERAND_AND_OPERATOR = "숫자 또는 연산자를 입력해주세요."
