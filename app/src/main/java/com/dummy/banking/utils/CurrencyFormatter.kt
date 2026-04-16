package com.dummy.banking.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatToRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount).replace("Rp", "IDR ")
    }

    val IndonesianCurrencyTransformation = VisualTransformation { text ->
        val originalText = text.text
        if (originalText.isEmpty()) {
            return@VisualTransformation TransformedText(text, OffsetMapping.Identity)
        }

        val number = originalText.toLongOrNull() ?: 0L
        val formattedText = NumberFormat.getNumberInstance(Locale("in", "ID")).format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                var transformedOffset = 0
                var originalCount = 0
                for (char in formattedText) {
                    if (char.isDigit()) originalCount++
                    transformedOffset++
                    if (originalCount == offset) break
                }
                return transformedOffset
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val safeOffset = offset.coerceAtMost(formattedText.length)
                var originalOffset = 0
                for (i in 0 until safeOffset) {
                    if (formattedText[i].isDigit()) originalOffset++
                }
                return originalOffset
            }
        }

        TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}
