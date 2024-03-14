package com.example.calculator

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {
    private lateinit var tvFormula: TextView
    private lateinit var tvResult: TextView
    private var currentExpression = StringBuilder()
    private var currentResult = 0.0
    private var lastOperator = ""
    private val MAX_RESULT_LENGTH = 12;
    private val MAX_EXPRESSION_LENGTH = 20;
    private val OPERATORS = "+-*/^"
    private val PARENTHESES = "()"
    private val MAX_INPUT_VALUE: Double = 1e32


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvFormula = findViewById(R.id.tvFormula)
        tvResult = findViewById(R.id.tvResult)
        val clearButton: MaterialButton = findViewById(R.id.clear)
        val plusButton: MaterialButton = findViewById(R.id.plus)
        val divideButton: MaterialButton = findViewById(R.id.devide)
        val multiplyButton: MaterialButton = findViewById(R.id.multiply)
        val minusButton: MaterialButton = findViewById(R.id.mines)
        val equalButton: MaterialButton = findViewById(R.id.equal)
        val sqrtButton: MaterialButton = findViewById(R.id.sqrt)
        val powerButton: MaterialButton = findViewById(R.id.power)
        val sinButton: MaterialButton = findViewById(R.id.sin)
        val cosButton: MaterialButton = findViewById(R.id.cos)
        val lnButton: MaterialButton = findViewById(R.id.ln)
        val parenthesesButton: MaterialButton = findViewById(R.id.parentheses)
        val percentageButton: MaterialButton = findViewById(R.id.percentage)
        val dotButton: MaterialButton = findViewById(R.id.dot)


        val oneButton: MaterialButton = findViewById(R.id.one)
        val twoButton: MaterialButton = findViewById(R.id.two)
        val threeButton: MaterialButton = findViewById(R.id.three)
        val fourButton: MaterialButton = findViewById(R.id.four)
        val fiveButton: MaterialButton = findViewById(R.id.five)
        val sixButton: MaterialButton = findViewById(R.id.six)
        val sevenButton: MaterialButton = findViewById(R.id.seven)
        val eightButton: MaterialButton = findViewById(R.id.eight)
        val nineButton: MaterialButton = findViewById(R.id.nine)
        val zeroButton: MaterialButton = findViewById(R.id.zero)

        clearButton.setOnClickListener(buttonClickListener)
        plusButton.setOnClickListener(buttonClickListener)
        divideButton.setOnClickListener(buttonClickListener)
        sqrtButton.setOnClickListener(buttonClickListener)
        multiplyButton.setOnClickListener(buttonClickListener)
        minusButton.setOnClickListener(buttonClickListener)
        equalButton.setOnClickListener(buttonClickListener)
        powerButton.setOnClickListener(buttonClickListener)
        sinButton.setOnClickListener(buttonClickListener)
        cosButton.setOnClickListener(buttonClickListener)
        lnButton.setOnClickListener(buttonClickListener)
        parenthesesButton.setOnClickListener(buttonClickListener)
        percentageButton.setOnClickListener(buttonClickListener)
        dotButton.setOnClickListener(buttonClickListener)

        val digitButtons = arrayOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton)
        digitButtons.forEach { button ->
            button.setOnClickListener(buttonClickListener)
        }
    }
    val buttonClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.clear -> {
                clearExpression()
            }
            R.id.dot ->{
                appendDot()
                updateUI()
            }
            R.id.plus, R.id.mines, R.id.multiply, R.id.devide -> {
                val lastChar = if (currentExpression.isNotEmpty()) currentExpression.last() else ' '

                val isLastCharOperator = lastChar in listOf('+', '-', '*', '/')

                if (!isLastCharOperator) {
                    handleOperator((view as MaterialButton).text.toString())
                }
            }
            R.id.equal -> {
                calculateResult()
            }
            R.id.sqrt -> {
                currentExpression.append("sqrt(")
                updateUI()
            }

            R.id.power -> {
                currentExpression.append("^2")
                updateUI()
            }

            R.id.sin -> {
                currentExpression.append("sin(")
                updateUI()
            }

            R.id.cos -> {
                currentExpression.append("cos(")
                updateUI()
            }

            R.id.ln -> {
                currentExpression.append("ln(")
                updateUI()
            }


            R.id.parentheses -> {
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

            R.id.percentage -> {
                onPercentageButtonClick()
            }

            else -> {
                val digit = (view as MaterialButton).text.toString()
                appendDigit(digit)
            }
        }
    }

    private fun appendDot() {
        val lastChar = currentExpression.lastOrNull()

        if (currentExpression.isEmpty() || lastChar!! in OPERATORS.toCharArray()) {
            currentExpression.append("0.")
        } else if (lastChar != '.') {
            currentExpression.append(".")
        }

        updateUI()
    }

    private fun handleOperator(operator: String) {
        val lastChar = if (currentExpression.isNotEmpty()) currentExpression.last() else ' '

        val isLastCharOperator = lastChar in listOf('+', '-', '*', '/')

        if (!isLastCharOperator) {
            currentExpression.append(" $operator ")
            lastOperator = operator
            updateUI()
        } else {
            currentExpression.setLength(currentExpression.length - 1)
            currentExpression.append(" $operator ")
            lastOperator = operator
            updateUI()
        }
    }

    private fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }

    private fun clearExpression() {
        currentExpression.clear()
        currentResult = 0.0
        tvFormula.text = ""
        tvResult.text = "0"
    }

    private fun calculateResult() {
        val expression = currentExpression.toString()

        val expressionWithPercentConverted = expression.replace("%", "* 0.01")

        try {
            val exp4jExpression = ExpressionBuilder(expressionWithPercentConverted).build()
            val result = exp4jExpression.evaluate()
            if(result>=MAX_INPUT_VALUE){
                clearExpression()
                updateUI()
                showToast("Слишком большое число")
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
            showToast("Ошибка при вычислении выражения")
        }
    }



    private fun appendDigit(digit: String) {
        if (currentExpression.length + digit.length <= MAX_EXPRESSION_LENGTH) {
            currentExpression.append(digit)
            updateUI()
        } else {
            showToast("Превышена максимальная длина числа")
        }
        Log.d("AppendDigit", "Длина текущего выражения: ${currentExpression.length}")
    }

    private fun updateUI() {
        tvFormula.text = currentExpression.toString()
        tvResult.text = currentResult.toString()
    }
    private fun onPercentageButtonClick() {
        currentExpression.append("%")
        updateUI()
    }
}

