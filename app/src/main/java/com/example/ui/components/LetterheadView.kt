package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Samity
import com.example.data.model.Signatory
import com.example.ui.language.LanguageManager
import com.example.ui.theme.SamityGold
import com.example.ui.theme.SamityNavyPrimary

@Composable
fun LetterheadHeader(
    samity: Samity,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFAFCFF))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Embellished Logo Emblem
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SamityNavyPrimary)
                    .border(2.dp, SamityGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = "Samity Logo",
                    tint = SamityGold,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isBn) samity.nameBn else samity.nameEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SamityNavyPrimary,
                    lineHeight = 20.sp
                )
                Text(
                    text = "${if (isBn) "স্লোগান: " else "Slogan: "}${if (isBn) samity.sloganBn else samity.sloganEn}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "${if (isBn) "রেজিস্ট্রেশন নং: " else "Reg No: "}${samity.regNo} | ${if (isBn) "প্রতিষ্ঠিত: " else "Est: "}${samity.establishedDate}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Address & Contact line
        Text(
            text = "${if (isBn) samity.addressBn else samity.addressEn} | ${if (isBn) "ফোন: " else "Phone: "}${samity.phone} | ${samity.email}",
            fontSize = 9.5.sp,
            color = Color(0xFF334155),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Decorative double line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(SamityNavyPrimary)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SamityGold)
        )
    }
}

@Composable
fun LetterheadSignatureBar(
    signatories: List<Signatory>,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        signatories.filter { it.isVisible }.forEach { sig ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Digital stylized signature rendering
                Text(
                    text = sig.signatureText,
                    fontFamily = FontFamily.Cursive,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = SamityNavyPrimary
                )
                Box(
                    modifier = Modifier
                        .width(90.dp)
                        .height(1.dp)
                        .background(Color(0xFF64748B))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBn) sig.designationBn else sig.designationEn,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = sig.name,
                    fontSize = 10.sp,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "${if (isBn) "তারিখ: " else "Date: "}...........",
                    fontSize = 9.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}
