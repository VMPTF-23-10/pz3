package com.nure.vmptf.pz3

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.Locale

class RomanConverterActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_roman_converter)

        val romanInput = findViewById<EditText>(R.id.romanInput)
        val resultText = findViewById<TextView>(R.id.romanResultText)

        findViewById<Button>(R.id.convertRomanButton).setOnClickListener {
            val input = romanInput.text.toString()
            val result = romanToInt(input)

            if (result == null) {
                romanInput.error = getString(R.string.roman_invalid)
                resultText.text = getString(R.string.roman_invalid)
            } else {
                resultText.text = getString(R.string.roman_result, result)
            }
        }
    }

    companion object {
        private val strictRomanPattern =
            Regex("^M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})$")
        private val romanValues = mapOf(
            'I' to 1,
            'V' to 5,
            'X' to 10,
            'L' to 50,
            'C' to 100,
            'D' to 500,
            'M' to 1000
        )

        fun romanToInt(input: String): Int? {
            val roman = input.trim().uppercase(Locale.US)
            if (roman.isEmpty() || !strictRomanPattern.matches(roman)) {
                return null
            }

            var result = 0
            var index = 0
            while (index < roman.length) {
                val current = romanValues[roman[index]] ?: return null
                val next = roman.getOrNull(index + 1)?.let { romanValues[it] }

                if (next != null && current < next) {
                    result += next - current
                    index += 2
                } else {
                    result += current
                    index += 1
                }
            }

            return result.takeIf { it in 1..3999 }
        }
    }
}
