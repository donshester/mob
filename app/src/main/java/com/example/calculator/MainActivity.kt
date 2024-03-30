package com.example.calculator
import android.content.Context
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.presenter.CalculatorPresenter
import com.example.calculator.view.CalculatorView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), CalculatorView {

    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var tvFormula: TextView
    private lateinit var tvResult: TextView
    private lateinit var presenter: CalculatorPresenter
    private lateinit var sensorManager: SensorManager
    private var gyroscopeSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        constraintLayout = findViewById(R.id.constraintLayout)
        tvFormula = findViewById(R.id.tvFormula)
        tvResult = findViewById(R.id.tvResult)
        presenter = CalculatorPresenter(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        setupButtons()
    }

    private fun setupButtons() {
        val clearButton: Button = findViewById(R.id.clear)
        val plusButton: Button = findViewById(R.id.plus)
        val divideButton: Button = findViewById(R.id.devide)
        val multiplyButton: Button = findViewById(R.id.multiply)
        val minusButton: Button = findViewById(R.id.mines)
        val equalButton: Button = findViewById(R.id.equal)
        val sqrtButton: Button = findViewById(R.id.sqrt)
        val powerButton: Button = findViewById(R.id.power)
        val sinButton: Button = findViewById(R.id.sin)
        val cosButton: Button = findViewById(R.id.cos)
        val lnButton: Button = findViewById(R.id.ln)
        val parenthesesButton: Button = findViewById(R.id.parentheses)
        val percentageButton: Button = findViewById(R.id.percentage)
        val dotButton: Button = findViewById(R.id.dot)

        val oneButton: Button = findViewById(R.id.one)
        val twoButton: Button = findViewById(R.id.two)
        val threeButton: Button = findViewById(R.id.three)
        val fourButton: Button = findViewById(R.id.four)
        val fiveButton: Button = findViewById(R.id.five)
        val sixButton: Button = findViewById(R.id.six)
        val sevenButton: Button = findViewById(R.id.seven)
        val eightButton: Button = findViewById(R.id.eight)
        val nineButton: Button = findViewById(R.id.nine)
        val zeroButton: Button = findViewById(R.id.zero)

        val digitButtons = arrayOf(oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton, zeroButton)

        clearButton.setOnClickListener { presenter.onClearClicked() }
        plusButton.setOnClickListener { presenter.onOperatorClicked("+") }
        divideButton.setOnClickListener { presenter.onOperatorClicked("/") }
        multiplyButton.setOnClickListener { presenter.onOperatorClicked("*") }
        minusButton.setOnClickListener { presenter.onOperatorClicked("-") }
        equalButton.setOnClickListener { presenter.onEqualClicked() }
        sqrtButton.setOnClickListener { presenter.onSqrtClicked() }
        powerButton.setOnClickListener { presenter.onPowerClicked() }
        sinButton.setOnClickListener { presenter.onSinClicked() }
        cosButton.setOnClickListener { presenter.onCosClicked() }
        lnButton.setOnClickListener { presenter.onLnClicked() }
        parenthesesButton.setOnClickListener { presenter.onParenthesesClicked() }
        percentageButton.setOnClickListener { presenter.onPercentageClicked() }
        dotButton.setOnClickListener { presenter.onDotClicked() }

        digitButtons.forEachIndexed { index, button ->
            button.setOnClickListener { presenter.onDigitClicked(index.toString()) }
        }
    }

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(gyroListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_GAME)
    }

    private val gyroListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
                val x = event.values[0]
                changeBackgroundColor(x)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun changeBackgroundColor(x: Float) {
        if (!::constraintLayout.isInitialized) {
            return
        }
        val min = -2.0f
        val max = 2.0f

        val startColor: Int
        val endColor: Int

        if (x < 0) {
            startColor = getColor(R.color.color1)
            endColor = getColor(R.color.color2)
        } else {
            startColor = getColor(R.color.color2)
            endColor = getColor(R.color.color3)
        }

        val ratio = (x - min) / (max - min)
        val newColor = interpolateColor(startColor, endColor, ratio)

        constraintLayout.setBackgroundColor(newColor)
    }

    private fun interpolateColor(startColor: Int, endColor: Int, ratio: Float): Int {
        val startA = (startColor shr 24 and 0xff) / 255.0f
        val startR = (startColor shr 16 and 0xff) / 255.0f
        val startG = (startColor shr 8 and 0xff) / 255.0f
        val startB = (startColor and 0xff) / 255.0f

        val endA = (endColor shr 24 and 0xff) / 255.0f
        val endR = (endColor shr 16 and 0xff) / 255.0f
        val endG = (endColor shr 8 and 0xff) / 255.0f
        val endB = (endColor and 0xff) / 255.0f

        val a = startA + (endA - startA) * ratio
        val r = startR + (endR - startR) * ratio
        val g = startG + (endG - startG) * ratio
        val b = startB + (endB - startB) * ratio

        return ((a * 255.0f + 0.5f).toInt() shl 24) or
                ((r * 255.0f + 0.5f).toInt() shl 16) or
                ((g * 255.0f + 0.5f).toInt() shl 8) or
                ((b * 255.0f + 0.5f).toInt())
    }

    override fun updateFormula(formula: String) {
        tvFormula.text = formula
    }

    override fun updateResult(result: String) {
        tvResult.text = result
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}