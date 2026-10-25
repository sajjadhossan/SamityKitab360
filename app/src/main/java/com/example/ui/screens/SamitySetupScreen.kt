package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import com.example.data.model.Samity
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamitySetupScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val currentSamity = repository.activeSamity

    var isCreatingNew by remember { mutableStateOf(false) }

    var nameBn by remember { mutableStateOf(currentSamity.nameBn) }
    var nameEn by remember { mutableStateOf(currentSamity.nameEn) }
    var sloganBn by remember { mutableStateOf(currentSamity.sloganBn) }
    var sloganEn by remember { mutableStateOf(currentSamity.sloganEn) }
    var samityType by remember { mutableStateOf(currentSamity.samityType) }
    var regNo by remember { mutableStateOf(currentSamity.regNo) }
    var establishedDate by remember { mutableStateOf(currentSamity.establishedDate) }
    var addressBn by remember { mutableStateOf(currentSamity.addressBn) }
    var phone by remember { mutableStateOf(currentSamity.phone) }
    var whatsapp by remember { mutableStateOf(currentSamity.whatsappNumber) }
    var email by remember { mutableStateOf(currentSamity.email) }
    var bankName by remember { mutableStateOf(currentSamity.bankName) }
    var bankBranch by remember { mutableStateOf(currentSamity.bankBranch) }
    var bankAccountNo by remember { mutableStateOf(currentSamity.bankAccountNo) }
    var bkash by remember { mutableStateOf(currentSamity.bkashNumber) }
    var nagad by remember { mutableStateOf(currentSamity.nagadNumber) }
    var defaultFeeStr by remember { mutableStateOf(currentSamity.defaultMonthlyFee.toInt().toString()) }
    var dueDayStr by remember { mutableStateOf(currentSamity.dueDayOfMonth.toString()) }
    var gracePeriodStr by remember { mutableStateOf(currentSamity.gracePeriodDays.toString()) }
    var lateFeeStr by remember { mutableStateOf(currentSamity.lateFeeAmount.toInt().toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "সমিতি সেটআপ ও মাল্টি-টেন্যান্ট ব্যবস্থাপনা" else "Samity Setup & Multi-Tenant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamityNavyPrimary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Toggle Bar (Edit Current / Create New Samity)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isCreatingNew = false
                        nameBn = currentSamity.nameBn
                        nameEn = currentSamity.nameEn
                        sloganBn = currentSamity.sloganBn
                        bankName = currentSamity.bankName
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isCreatingNew) SamityNavyPrimary.copy(alpha = 0.12f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBn) "চলতি সমিতি এডিট" else "Edit Active Samity", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        isCreatingNew = true
                        nameBn = ""
                        nameEn = ""
                        sloganBn = ""
                        sloganEn = ""
                        regNo = "REG-NEW-${(1000..9999).random()}"
                        bankName = "ডাচ-বাংলা ব্যাংক"
                        bankAccountNo = ""
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCreatingNew) SamityGold else Color(0xFFE2E8F0),
                        contentColor = if (isCreatingNew) SamityNavyDark else Color(0xFF475569)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("create_new_samity_tab_btn")
                ) {
                    Text(if (isBn) "+ নতুন সমিতি তৈরি" else "+ Create New Samity", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Samity General Information Card
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
                        text = if (isBn) "১. সাধারণ পরিচিতি (Basic Information)" else "1. Basic Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyPrimary
                    )

                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text(if (isBn) "সমিতির নাম (বাংলায়) *" else "Samity Name (Bangla) *") },
                        modifier = Modifier.fillMaxWidth().testTag("samity_name_bn_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(if (isBn) "সমিতির নাম (ইংরেজিতে)" else "Samity Name (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = sloganBn,
                        onValueChange = { sloganBn = it },
                        label = { Text(if (isBn) "স্লোগান (বাংলায়)" else "Slogan (Bangla)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regNo,
                            onValueChange = { regNo = it },
                            label = { Text(if (isBn) "রেজিস্ট্রেশন নং" else "Reg Number") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = establishedDate,
                            onValueChange = { establishedDate = it },
                            label = { Text(if (isBn) "প্রতিষ্ঠার তারিখ" else "Est. Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = addressBn,
                        onValueChange = { addressBn = it },
                        label = { Text(if (isBn) "পূর্ণাঙ্গ ঠিকানা" else "Full Address") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBn) "অফিস ফোন" else "Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text(if (isBn) "অফিসিয়াল হোয়াটসঅ্যাপ" else "WhatsApp") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Bank & Mobile Banking Info Card
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
                        text = if (isBn) "২. ব্যাংক ও মোবাইল ব্যাংকিং হিসাব বিবরণ" else "2. Bank & Mobile Banking Accounts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyPrimary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text(if (isBn) "ব্যাংকের নাম" else "Bank Name") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankBranch,
                            onValueChange = { bankBranch = it },
                            label = { Text(if (isBn) "শাখা" else "Branch") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = bankAccountNo,
                        onValueChange = { bankAccountNo = it },
                        label = { Text(if (isBn) "ব্যাংক একাউন্ট নম্বর" else "Bank Account Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bkash,
                            onValueChange = { bkash = it },
                            label = { Text(if (isBn) "বিকাশ নম্বর" else "bKash") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = nagad,
                            onValueChange = { nagad = it },
                            label = { Text(if (isBn) "নগদ নম্বর" else "Nagad") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Subscription & Due Rules Card
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
                        text = if (isBn) "৩. চাঁদা ও জরিমানা সেটিংস (Subscription & Late Fee)" else "3. Subscription & Late Fee Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyPrimary
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = defaultFeeStr,
                            onValueChange = { defaultFeeStr = it },
                            label = { Text(if (isBn) "ডিফল্ট মাসিক চাঁদা (টাকা)" else "Default Fee (BDT)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = dueDayStr,
                            onValueChange = { dueDayStr = it },
                            label = { Text(if (isBn) "জমার শেষ দিন (তারিখ)" else "Due Day") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gracePeriodStr,
                            onValueChange = { gracePeriodStr = it },
                            label = { Text(if (isBn) "গ্রেস পিরিয়ড (দিন)" else "Grace Period (Days)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lateFeeStr,
                            onValueChange = { lateFeeStr = it },
                            label = { Text(if (isBn) "জরিমানা (বিলম্ব ফি)" else "Late Fee (BDT)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Save / Update Button
            Button(
                onClick = {
                    if (nameBn.isNotBlank()) {
                        val fee = defaultFeeStr.toDoubleOrNull() ?: 5000.0
                        val dueDay = dueDayStr.toIntOrNull() ?: 10
                        val grace = gracePeriodStr.toIntOrNull() ?: 5
                        val late = lateFeeStr.toDoubleOrNull() ?: 50.0

                        if (isCreatingNew) {
                            val newSamity = Samity(
                                id = "samity_${UUID.randomUUID()}",
                                nameBn = nameBn,
                                nameEn = nameEn.ifBlank { nameBn },
                                sloganBn = sloganBn,
                                sloganEn = sloganEn,
                                samityType = samityType,
                                regNo = regNo,
                                establishedDate = establishedDate,
                                addressBn = addressBn,
                                addressEn = addressBn,
                                phone = phone,
                                whatsappNumber = whatsapp,
                                email = email,
                                website = "",
                                bankName = bankName,
                                bankBranch = bankBranch,
                                bankAccountNo = bankAccountNo,
                                bankAccountName = nameBn,
                                routingNo = "",
                                bkashNumber = bkash,
                                nagadNumber = nagad,
                                rocketNumber = "",
                                defaultMonthlyFee = fee,
                                dueDayOfMonth = dueDay,
                                gracePeriodDays = grace,
                                lateFeeAmount = late,
                                presidentName = "সভাপতি",
                                secretaryName = "সাধারণ সম্পাদক",
                                treasurerName = "কোষাধ্যক্ষ",
                                treasurerPhone = phone
                            )
                            repository.createSamity(newSamity)
                            Toast.makeText(context, if (isBn) "নতুন সমিতি সফলভাবে তৈরি ও সক্রিয় করা হয়েছে!" else "New Samity created & switched!", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            val updated = currentSamity.copy(
                                nameBn = nameBn,
                                nameEn = nameEn,
                                sloganBn = sloganBn,
                                sloganEn = sloganEn,
                                regNo = regNo,
                                addressBn = addressBn,
                                phone = phone,
                                whatsappNumber = whatsapp,
                                bankName = bankName,
                                bankBranch = bankBranch,
                                bankAccountNo = bankAccountNo,
                                bkashNumber = bkash,
                                nagadNumber = nagad,
                                defaultMonthlyFee = fee,
                                dueDayOfMonth = dueDay,
                                gracePeriodDays = grace,
                                lateFeeAmount = late
                            )
                            val idx = repository.samities.indexOfFirst { it.id == currentSamity.id }
                            if (idx != -1) {
                                repository.samities[idx] = updated
                            }
                            Toast.makeText(context, if (isBn) "সমিতি তথ্য সফলভাবে আপডেট হয়েছে!" else "Samity info updated!", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_samity_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCreatingNew)
                        (if (isBn) "নতুন সমিতি তৈরি ও এক্টিভ করুন" else "Create & Activate Samity")
                    else
                        (if (isBn) "সমিতির তথ্য সংরক্ষণ করুন" else "Save Samity Settings"),
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
