package com.nure.vmptf.pz3

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.DecimalFormat

class SumActivity : Activity() {
    private val numberFormat = DecimalFormat("#.##########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sum)

        val firstNumberInput = findViewById<EditText>(R.id.firstNumberInput)
        val secondNumberInput = findViewById<EditText>(R.id.secondNumberInput)
        val resultText = findViewById<TextView>(R.id.sumResultText)

        findViewById<Button>(R.id.sumButton).setOnClickListener {
            val first = readNumber(firstNumberInput)
            val second = readNumber(secondNumberInput)

            if (first == null || second == null) {
                resultText.text = getString(R.string.sum_invalid_input)
                return@setOnClickListener
            }

            val result = first + second
            resultText.text = getString(R.string.sum_result, numberFormat.format(result))
        }
    }

    private fun readNumber(input: EditText): Double? {
        val value = input.text.toString().trim().replace(',', '.')
        val number = value.toDoubleOrNull()
        if (number == null) {
            input.error = getString(R.string.enter_valid_number)
        }
        return number
    }
}
