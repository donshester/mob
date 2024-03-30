package com.example.calculator.presenter

import com.example.calculator.model.CalculatorModel
import com.example.calculator.view.CalculatorView

class CalculatorPresenter(private val view: CalculatorView) {

    private val calculatorModel = CalculatorModel()
    private val MAX_RESULT_LENGTH = 12
    private val MAX_EXPRESSION_LENGTH = 20
    private val OPERATORS = "+-*/^"
    private val PARENTHESES = "()"

    private var currentExpression = StringBuilder()
    private var currentResult = 0.0

    fun onClearClicked() {
        clearExpression()
        updateUI()
    }

    fun onDotClicked() {
        appendDot()
        updateUI()
    }

    fun onOperatorClicked(operator: String) {
        handleOperator(operator)
    }

    fun onEqualClicked() {
        calculateResult()
    }

    fun onSqrtClicked() {
        currentExpression.append("sqrt(")
        updateUI()
    }

    fun onPowerClicked() {
        currentExpression.append("^2")
        updateUI()
    }

    fun onSinClicked() {
        currentExpression.append("sin(")
        updateUI()
    }

    fun onCosClicked() {
        currentExpression.append("cos(")
        updateUI()
    }

    fun onLnClicked() {
        currentExpression.append("ln(")
        updateUI()
    }

    fun onParenthesesClicked() {
        val openParenthesesNeeded = currentExpression.count { it == '(' } > currentExpression.count { it == ')' }
        val parenthesesToAdd = if (!openParenthesesNeeded) "(" else ")"
        val lastOperatorIndex = currentExpression.lastIndexOfAny(OPERATORS.toCharArray())
        val lastParenthesesIndex = currentExpression.lastIndexOfAny(PARENTHESES.toCharArray())
        val lastSymbolIndex = maxOf(lastOperatorIndex, lastParenthesesIndex)
        val isLastSymbolOperator = lastSymbolIndex >= 0 && currentExpression[lastSymbolIndex] in OPERATORS

        if (currentExpression.isEmpty() || currentExpression.endsWith('(') || isLastSymbolOperator) {
            currentExpression.append(parenthesesToAdd)
        } else {
            currentExpression.append(parenthesesToAdd)
        }
        updateUI()
    }

    fun onPercentageClicked() {
        currentExpression.append("%")
        updateUI()
    }

    fun onDigitClicked(digit: String) {
        appendDigit(digit)
    }

    private fun appendDot() {
        val lastChar = currentExpression.lastOrNull()
        if (currentExpression.isEmpty() || lastChar!! in OPERATORS.toCharArray()) {
            currentExpression.append("0.")
        } else if (lastChar != '.') {
            currentExpression.append(".")
        }
    }

    private fun handleOperator(operator: String) {
        val lastChar = if (currentExpression.isNotEmpty()) currentExpression.last() else ' '
        val isLastCharOperator = lastChar in listOf('+', '-', '*', '/')
        if (!isLastCharOperator) {
            currentExpression.append(" $operator ")
            updateUI()
        } else {
            currentExpression.setLength(currentExpression.length - 1)
            currentExpression.append(" $operator ")
            updateUI()
        }
    }

    private fun clearExpression() {
        currentExpression.clear()
        currentResult = 0.0
    }

    private fun calculateResult() {
        val expression = currentExpression.toString()
        val expressionWithPercentConverted = expression.replace("%", "* 0.01")

        try {
            val result = calculatorModel.calculateResult(expressionWithPercentConverted)
            if(calculatorModel.isValidInput(result)){
                clearExpression()
                updateUI()
                view.showToast("Слишком большое число")
                return
            }

            val formattedResult = if (result.toString().length > MAX_RESULT_LENGTH) {
                result.toString().substring(0, MAX_RESULT_LENGTH)
            } else {
                result.toString()
            }

            currentExpression.clear()
            currentExpression.append(formattedResult)
            currentResult = formattedResult.toDouble()

            updateUI()
        } catch (e: Exception) {
            view.showToast("Ошибка при вычислении выражения")
        }
    }

    private fun appendDigit(digit: String) {
        if (currentExpression.length + digit.length <= MAX_EXPRESSION_LENGTH) {
            currentExpression.append(digit)
            updateUI()
        } else {
            view.showToast("Превышена максимальная длина числа")
        }
    }

    private fun updateUI() {
        view.updateFormula(currentExpression.toString())
        view.updateResult(currentResult.toString())
    }
}
