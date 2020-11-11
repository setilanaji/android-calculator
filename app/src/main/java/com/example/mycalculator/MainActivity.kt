package com.example.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.mycalculator.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isFutureOperationButtonClicked: Boolean = false
    private var isInstantOperationButtonClicked: Boolean = false
    private var isEqualButtonClicked: Boolean = false

    private var currentNumber: Double = 0.0
    private var currentResult: Double = 0.0
//    private var memory: Double = 0.0

    private var historyText = ""
    private var historyInstantOperationText = ""
    private var historyActionList: ArrayList<String> = ArrayList()

    //    enum class Operation { INIT, ADD, SUB, DIV, MUL, EQUAL }
    private val mapOperation = mapOf<String, String>(
        "INIT" to "",
        "ADD" to "+",
        "SUB" to "-",
        "DIV" to "÷",
        "MUL" to "×",
        "EQUAL" to "="
    )

    private var currentOperation = mapOperation["INIT"]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            textViewResult.text = (0.0).roundToInt().toString()
        }


    }

    fun onNumberButtonClick(view: View?) {
        val button = view as Button
        val number = button.text.toString()

        var currentValue: String = binding.textViewResult.text.toString()
        currentValue =
            if (currentValue == "0" || isFutureOperationButtonClicked || isInstantOperationButtonClicked || isEqualButtonClicked) number else StringBuilder().append(
                currentValue
            ).append(number).toString()

        try {
            currentNumber = formatStringToDouble(currentValue)
        } catch (e: ParseException) {
            throw IllegalArgumentException("String must be number.")
        }

        binding.textViewResult.text = currentValue

        if (isEqualButtonClicked) {
            currentOperation = mapOperation.get("INIT")
            historyText = ""
        }

        if (isInstantOperationButtonClicked) {
            historyInstantOperationText = ""
            binding.textViewFullResult.text =
                StringBuilder().append(historyText).append(currentOperation).toString()
            isInstantOperationButtonClicked = false
        }

        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false

    }

    fun onClearClick(view: View?) {

        historyInstantOperationText = ""

        if (isEqualButtonClicked) {
            currentOperation = mapOperation["INIT"]
            historyText = ""
        }

        if (isInstantOperationButtonClicked) binding.textViewFullResult.text =
            StringBuilder().append(historyText).append(currentOperation).toString()

        isInstantOperationButtonClicked = false
        isFutureOperationButtonClicked = false
        isEqualButtonClicked = false

        currentNumber = 0.0
        binding.textViewResult.text = formatDoubleToString(0.0)
    }

    fun onEqualButtonClick(view: View?) {

        if (isFutureOperationButtonClicked) {
            currentNumber = currentResult
        }

        val historyAllText = calculateResult()

        Toast.makeText(applicationContext, historyAllText, Toast.LENGTH_LONG).show()

        historyActionList.add(historyAllText)

        historyText = StringBuilder().append(formatDoubleToString(currentResult)).toString()

        binding.textViewFullResult.text = ""

        isFutureOperationButtonClicked = false
        isEqualButtonClicked = true
    }


    private fun formatDoubleToString(number: Double): String {
        return useNumberFormat().format(number)
    }

    private fun formatStringToDouble(number: String): Double {
        return useNumberFormat().format(number).toDouble()
    }

    private fun useNumberFormat(): DecimalFormat {

        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = ','

        val format = DecimalFormat("#.##############")
        format.decimalFormatSymbols = symbols

        return format
    }


    private fun calculateResult(): String {

        when (currentOperation) {
            mapOperation["INIT"] -> {
                currentResult = currentNumber
                historyText =
                    StringBuilder().append(binding.textViewFullResult.text.toString()).toString()
            }
            mapOperation["ADD"] -> currentResult += currentNumber
            mapOperation["SUB"] -> currentResult -= currentNumber
            mapOperation["MUL"] -> currentResult *= currentNumber
            mapOperation["DIV"] -> currentResult /= currentNumber
        }

        binding.textViewResult.text = formatDoubleToString(currentResult)

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = binding.textViewFullResult.text.toString()
            if (isEqualButtonClicked) historyText =
                StringBuilder().append(historyText).append(currentOperation)
                    .append(formatDoubleToString(currentNumber)).toString()
        } else {
            historyText = StringBuilder().append(historyText).append(currentOperation)
                .append(formatDoubleToString(currentNumber)).toString()
        }

        return StringBuilder().append(historyText).append("=")
            .append(formatDoubleToString(currentResult)).toString()
    }

    fun onOperationButtonClick(view: View?) {
        val button = view as Button

        if (!isFutureOperationButtonClicked && !isEqualButtonClicked) {
            calculateResult()
        }

        currentOperation = when (button.text.toString()) {
            "+" -> mapOperation["ADD"]
            "-" -> mapOperation["SUB"]
            "×" -> mapOperation["MUL"]
            "÷" -> mapOperation["DIV"]
            else -> mapOperation["INIT"]
        }

        if (isInstantOperationButtonClicked) {
            isInstantOperationButtonClicked = false
            historyText = binding.textViewFullResult.text.toString()
        }
        binding.textViewFullResult.text =
            StringBuilder().append(historyText).append(button.text.toString()).toString()

        isFutureOperationButtonClicked = true
        isEqualButtonClicked = false
    }


}