package com.example.calculator.view

interface CalculatorView {
    fun updateFormula(formula: String)
    fun updateResult(result: String)
    fun showToast(message: String)

}
