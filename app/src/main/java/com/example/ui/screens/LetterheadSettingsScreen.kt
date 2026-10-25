package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Signatory
import com.example.data.repository.SamityRepository
import com.example.ui.components.LetterheadHeader
import com.example.ui.components.LetterheadSignatureBar
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterheadSettingsScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    var letterheadConfig by remember { mutableStateOf(repository.letterheadConfig) }

    // Signatory states
    var presName by remember { mutableStateOf(samity.presidentName) }
    var secName by remember { mutableStateOf(samity.secretaryName) }
    var tresName by remember { mutableStateOf(samity.treasurerName) }
    var watermarkText by remember { mutableStateOf(letterheadConfig.watermarkText) }
    var footerBn by remember { mutableStateOf(letterheadConfig.footerTextBn) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        Text(
            text = if (isBn) "লেটারহেড ও ডিজিটাল স্বাক্ষর ব্যবস্থাপনা" else "Letterhead & Signatures Management",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn) "প্রিন্ট প্যাড, লোগো, স্লোগান ও অনুমোদিত পদবির স্বাক্ষর কনফিগার করুন" else "Customize official letterhead pad, slogan & authorized signatures",
            fontSize = 11.5.sp,
            color = Color(0xFF64748B)
        )

        // Live Preview of Current Letterhead
        Text(
            text = if (isBn) "লাইভ লেটারহেড প্যাড প্রিভিউ (Live Preview)" else "Live Letterhead Preview",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SamityNavyPrimary
        )

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LetterheadHeader(samity = samity)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color(0xFFFAFAFA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "— অফিসিয়াল নথির মূল লেখার অংশ / Document Content Area —",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }

                LetterheadSignatureBar(signatories = repository.signatories)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SamityNavyPrimary)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = footerBn,
                        fontSize = 9.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Configuration Form
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (isBn) "১. অনুমোদিত স্বাক্ষরকারীগণ (Authorized Signatories)" else "1. Authorized Signatories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SamityNavyPrimary
                )

                OutlinedTextField(
                    value = presName,
                    onValueChange = { presName = it },
                    label = { Text(if (isBn) "সভাপতির নাম ও পদবি" else "President Name") },
                    modifier = Modifier.fillMaxWidth().testTag("pres_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = secName,
                    onValueChange = { secName = it },
                    label = { Text(if (isBn) "সাধারণ সম্পাদকের নাম ও পদবি" else "General Secretary Name") },
                    modifier = Modifier.fillMaxWidth().testTag("sec_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = tresName,
                    onValueChange = { tresName = it },
                    label = { Text(if (isBn) "কোষাধ্যক্ষের নাম ও পদবি" else "Treasurer Name") },
                    modifier = Modifier.fillMaxWidth().testTag("tres_name_input"),
                    singleLine = true
                )

                Divider()

                Text(
                    text = if (isBn) "২. ওয়াটারমার্ক ও ফুটার বার্তা" else "2. Watermark & Footer Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SamityNavyPrimary
                )

                OutlinedTextField(
                    value = watermarkText,
                    onValueChange = { watermarkText = it },
                    label = { Text(if (isBn) "ওয়াটারমার্ক টেক্সট" else "Watermark Text") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = footerBn,
                    onValueChange = { footerBn = it },
                    label = { Text(if (isBn) "লেটারহেড ফুটার লেখা (বাংলা)" else "Footer Note") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        val updatedSignatories = listOf(
                            Signatory("sig_1", "সভাপতি", "President", presName, samity.phone, true, presName),
                            Signatory("sig_2", "সাধারণ সম্পাদক", "General Secretary", secName, samity.phone, true, secName),
                            Signatory("sig_3", "কোষাধ্যক্ষ", "Treasurer", tresName, samity.treasurerPhone, true, tresName)
                        )
                        repository.signatories.clear()
                        repository.signatories.addAll(updatedSignatories)
                        repository.letterheadConfig = letterheadConfig.copy(
                            watermarkText = watermarkText,
                            footerTextBn = footerBn
                        )
                        Toast.makeText(context, if (isBn) "লেটারহেড ও স্বাক্ষর সংরক্ষিত হয়েছে!" else "Letterhead & Signatures updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_letterhead_btn"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBn) "সেটিংস সংরক্ষণ করুন" else "Save Letterhead Settings", color = Color.White)
                }
            }
        }
    }
}
