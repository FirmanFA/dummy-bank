package com.dummy.banking.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatToRupiah(amount: Long): String {
        val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        return format.format(amount).replace("Rp", "IDR ")
    }
}
