package com.example.calculator.model

import net.objecthunter.exp4j.ExpressionBuilder

class CalculatorModel {

    private val MAX_INPUT_VALUE: Double = 1e32

    fun isValidInput(value: Double): Boolean {
        return value <= MAX_INPUT_VALUE
    }

    fun calculateResult(expression: String): Double {
        val expressionWithPercentConverted = expression.replace("%", "* 0.01")
        val exp4jExpression = ExpressionBuilder(expressionWithPercentConverted).build()
        return exp4jExpression.evaluate()
    }
}
