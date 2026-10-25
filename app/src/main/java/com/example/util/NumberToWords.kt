package com.example.util

object NumberToWords {

    private val banglaUnits = arrayOf(
        "", "এক", "দুই", "তিন", "চার", "পাঁচ", "ছয়", "সাত", "আট", "নয়", "দশ",
        "এগারো", "বারো", "তেরো", "চৌদ্দ", "পনেরো", "ষোল", "সতেরো", "আঠারো", "উনিশ"
    )

    private val banglaTens = arrayOf(
        "", "", "বিশ", "ত্রিশ", "চল্লিশ", "পঞ্চাশ", "ষাট", "সত্তর", "আশি", "নব্বই"
    )

    private val englishUnits = arrayOf(
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
        "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    )

    private val englishTens = arrayOf(
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    )

    fun convertBangla(amount: Double): String {
        val n = amount.toLong()
        if (n == 0L) return "শূন্য টাকা মাত্র"
        return convertBanglaInternal(n).trim() + " টাকা মাত্র"
    }

    private fun convertBanglaInternal(n: Long): String {
        return when {
            n < 0 -> "ঋণাত্মক " + convertBanglaInternal(-n)
            n == 0L -> ""
            n < 20 -> banglaUnits[n.toInt()] + " "
            n < 100 -> banglaTens[(n / 10).toInt()] + " " + banglaUnits[(n % 10).toInt()] + " "
            n < 1000 -> banglaUnits[(n / 100).toInt()] + " শত " + convertBanglaInternal(n % 100)
            n < 100000 -> convertBanglaInternal(n / 1000) + " হাজার " + convertBanglaInternal(n % 1000)
            n < 10000000 -> convertBanglaInternal(n / 100000) + " লাখ " + convertBanglaInternal(n % 100000)
            else -> convertBanglaInternal(n / 10000000) + " কোটি " + convertBanglaInternal(n % 10000000)
        }
    }

    fun convertEnglish(amount: Double): String {
        val n = amount.toLong()
        if (n == 0L) return "Zero Taka Only"
        return convertEnglishInternal(n).trim() + " Taka Only"
    }

    private fun convertEnglishInternal(n: Long): String {
        return when {
            n < 0 -> "Minus " + convertEnglishInternal(-n)
            n == 0L -> ""
            n < 20 -> englishUnits[n.toInt()] + " "
            n < 100 -> englishTens[(n / 10).toInt()] + " " + englishUnits[(n % 10).toInt()] + " "
            n < 1000 -> englishUnits[(n / 100).toInt()] + " Hundred " + convertEnglishInternal(n % 100)
            n < 1000000 -> convertEnglishInternal(n / 1000) + " Thousand " + convertEnglishInternal(n % 1000)
            n < 1000000000 -> convertEnglishInternal(n / 1000000) + " Million " + convertEnglishInternal(n % 1000000)
            else -> convertEnglishInternal(n / 1000000000) + " Billion " + convertEnglishInternal(n % 1000000000)
        }
    }

    fun formatCurrency(amount: Double, isBangla: Boolean): String {
        val formattedNumber = String.format("%,.0f", amount)
        return if (isBangla) {
            val bnDigits = formattedNumber
                .replace('0', '০')
                .replace('1', '১')
                .replace('2', '২')
                .replace('3', '৩')
                .replace('4', '৪')
                .replace('5', '৫')
                .replace('6', '৬')
                .replace('7', '৭')
                .replace('8', '৮')
                .replace('9', '৯')
            "৳ $bnDigits"
        } else {
            "BDT $formattedNumber"
        }
    }

    fun formatNumber(num: Int, isBangla: Boolean): String {
        val str = num.toString()
        if (!isBangla) return str
        return str
            .replace('0', '০')
            .replace('1', '১')
            .replace('2', '২')
            .replace('3', '৩')
            .replace('4', '৪')
            .replace('5', '৫')
            .replace('6', '৬')
            .replace('7', '৭')
            .replace('8', '৮')
            .replace('9', '৯')
    }
}
