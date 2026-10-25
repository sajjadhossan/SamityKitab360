package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentStatusType
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@Composable
fun StatusBadge(
    status: PaymentStatusType,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        PaymentStatusType.PAID -> Triple(
            StatusPaidBg,
            StatusPaidGreen,
            if (LanguageManager.isBangla) "পরিশোধিত" else "Paid"
        )
        PaymentStatusType.UNPAID -> Triple(
            StatusUnpaidBg,
            StatusUnpaidRed,
            if (LanguageManager.isBangla) "বকেয়া" else "Unpaid"
        )
        PaymentStatusType.PARTIAL -> Triple(
            StatusPartialBg,
            StatusPartialAmber,
            if (LanguageManager.isBangla) "আংশিক" else "Partial"
        )
        PaymentStatusType.ADVANCE -> Triple(
            StatusAdvanceBg,
            StatusAdvanceBlue,
            if (LanguageManager.isBangla) "অগ্রিম" else "Advance"
        )
        PaymentStatusType.WAIVED -> Triple(
            StatusWaivedBg,
            StatusWaivedSlate,
            if (LanguageManager.isBangla) "মওকুফ" else "Waived"
        )
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun QrCodeCanvas(
    content: String,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    // Elegant simulated QR code drawing with real corner finder patterns and pseudo data matrix
    Box(
        modifier = modifier
            .size(size)
            .background(Color.White, RoundedCornerShape(4.dp))
            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            val step = w / 9f

            // Top-left finder pattern
            drawRect(Color.Black, Offset(0f, 0f), Size(step * 3, step * 3))
            drawRect(Color.White, Offset(step * 0.5f, step * 0.5f), Size(step * 2, step * 2))
            drawRect(Color.Black, Offset(step, step), Size(step, step))

            // Top-right finder pattern
            drawRect(Color.Black, Offset(w - step * 3, 0f), Size(step * 3, step * 3))
            drawRect(Color.White, Offset(w - step * 2.5f, step * 0.5f), Size(step * 2, step * 2))
            drawRect(Color.Black, Offset(w - step * 2, step), Size(step, step))

            // Bottom-left finder pattern
            drawRect(Color.Black, Offset(0f, h - step * 3), Size(step * 3, step * 3))
            drawRect(Color.White, Offset(step * 0.5f, h - step * 2.5f), Size(step * 2, step * 2))
            drawRect(Color.Black, Offset(step, h - step * 2), Size(step, step))

            // Inner data dots derived deterministically from content hash
            val hash = content.hashCode()
            for (i in 0..8) {
                for (j in 0..8) {
                    val inFinderTL = i < 3 && j < 3
                    val inFinderTR = i >= 6 && j < 3
                    val inFinderBL = i < 3 && j >= 6
                    if (!inFinderTL && !inFinderTR && !inFinderBL) {
                        val bit = ((hash xor (i * 17 + j * 31)) and 1) == 0
                        if (bit) {
                            drawRect(
                                Color.Black,
                                Offset(i * step + step * 0.15f, j * step + step * 0.15f),
                                Size(step * 0.7f, step * 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}
