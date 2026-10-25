package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionConfig
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionConfigScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val currentConfig = repository.activeSubscriptionConfig

    BackHandler { onBack() }

    // State for all fields
    var monthlyDueStr by remember { mutableStateOf(currentConfig.monthlyDueAmount.toInt().toString()) }
    var dueDayOfMonthStr by remember { mutableStateOf(currentConfig.dueDayOfMonth.toString()) }
    var gracePeriodDaysStr by remember { mutableStateOf(currentConfig.gracePeriodDays.toString()) }
    var lateFeeType by remember { mutableStateOf(currentConfig.lateFeeType) } // "Fixed Daily", "One-Time Flat", "Percentage"
    var lateFeeAmountStr by remember { mutableStateOf(currentConfig.lateFeeAmount.toInt().toString()) }
    var lateFeePercentageStr by remember { mutableStateOf(currentConfig.lateFeePercentage.toString()) }
    var autoSendSms by remember { mutableStateOf(currentConfig.autoSendSmsReminder) }
    var reminderDaysPriorStr by remember { mutableStateOf(currentConfig.reminderDaysBeforeDue.toString()) }
    var applyToExistingMembers by remember { mutableStateOf(currentConfig.applyToExistingMembers) }

    // Live Calculation Simulation
    val parsedMonthly = monthlyDueStr.toDoubleOrNull() ?: 5000.0
    val parsedDueDay = dueDayOfMonthStr.toIntOrNull() ?: 10
    val parsedGrace = gracePeriodDaysStr.toIntOrNull() ?: 5
    val parsedLateFee = lateFeeAmountStr.toDoubleOrNull() ?: 50.0
    val parsedPercent = lateFeePercentageStr.toDoubleOrNull() ?: 2.0

    // Simulate 10 days late payment
    val simulatedLateDays = 10
    val simulatedPenalty = when (lateFeeType) {
        "Fixed Daily" -> simulatedLateDays * parsedLateFee
        "One-Time Flat" -> parsedLateFee
        "Percentage" -> parsedMonthly * (parsedPercent / 100.0)
        else -> simulatedLateDays * parsedLateFee
    }
    val simulatedTotalDue = parsedMonthly + simulatedPenalty

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "সাবস্ক্রিপশন ও চাঁদা কনফিগারেশন" else "Subscription & Due Rules",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "মাসিক চাঁদা, শেষ দিন ও বিলম্ব জরিমানা নিয়মাবলী" else "Monthly Dues, Due Dates & Late Fee Rules",
                            fontSize = 11.sp,
                            color = SamityGold
                        )
                    }
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
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Status Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SamityNavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(SamityGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventRepeat,
                            contentDescription = null,
                            tint = SamityGold,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = repository.activeSamity.nameBn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "বর্তমান নির্ধারিত মাসিক চাঁদা: ${currentConfig.monthlyDueAmount.toInt()} ৳ • প্রতি মাসের ${currentConfig.dueDayOfMonth} তারিখ"
                            else "Current Due: ${currentConfig.monthlyDueAmount.toInt()} BDT • Due on ${currentConfig.dueDayOfMonth}th",
                            fontSize = 11.sp,
                            color = Color(0xFFD0E4FF)
                        )
                    }
                }
            }

            // 1. Monthly Due Amount Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "১. নির্ধারিত মাসিক চাঁদা ও কিস্তির পরিমাণ" else "1. Monthly Due & Installment Amount",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SamityNavyDark
                        )
                    }
                    Text(
                        text = if (isBn) "সমিতির সদস্যদের মাসিক বাধ্যতামূলক সঞ্চয় বা কিস্তির হার" else "Standard monthly subscription fee per member",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = monthlyDueStr,
                        onValueChange = { monthlyDueStr = it },
                        label = { Text(if (isBn) "মাসিক চাঁদার পরিমাণ (টাকা)" else "Monthly Due (BDT)") },
                        leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SamityNavyPrimary, modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("monthly_due_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Quick Chips
                    Text(
                        text = if (isBn) "দ্রুত পরিমাণ নির্বাচন:" else "Quick Presets:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1000", "2000", "3000", "5000", "10000").forEach { preset ->
                            SuggestionChip(
                                onClick = { monthlyDueStr = preset },
                                label = { Text("${preset}৳", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = if (monthlyDueStr == preset) Color(0xFFE0F2FE) else Color(0xFFF1F5F9),
                                    labelColor = if (monthlyDueStr == preset) SamityNavyPrimary else Color(0xFF475569)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Apply to existing members checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { applyToExistingMembers = !applyToExistingMembers }
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = applyToExistingMembers,
                            onCheckedChange = { applyToExistingMembers = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isBn) "সকল বিদ্যমান সদস্যদের চাঁদা এই হারে আপডেট করুন" else "Apply new fee to all existing members",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = SamityNavyDark
                            )
                            Text(
                                text = if (isBn) "সক্রিয় ${repository.activeMembers.size} জন সদস্যের ডিফল্ট চাঁদা পরিবর্তিত হবে" else "Will update ${repository.activeMembers.size} active member records",
                                fontSize = 10.5.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // 2. Due Date & Grace Period Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "২. পরিশোধের শেষ তারিখ ও গ্রেস পিরিয়ড" else "2. Due Date & Grace Period",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SamityNavyDark
                        )
                    }
                    Text(
                        text = if (isBn) "প্রতি মাসের কোন তারিখের মধ্যে চাঁদা পরিশোধ আবশ্যক এবং বিলম্বের ছাড়" else "Day of month by which members must pay before penalty",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = dueDayOfMonthStr,
                            onValueChange = { dueDayOfMonthStr = it },
                            label = { Text(if (isBn) "শেষ তারিখ (দিন)" else "Due Day (1-31)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("due_day_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = gracePeriodDaysStr,
                            onValueChange = { gracePeriodDaysStr = it },
                            label = { Text(if (isBn) "গ্রেস পিরিয়ড (দিন)" else "Grace Period (Days)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("grace_period_input"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFEFF6FF),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "প্রতি মাসের $parsedDueDay তারিখ শেষ দিন। পরবর্তী $parsedGrace দিন কোনো জরিমানা ধার্য হবে না।"
                                else "Due on $parsedDueDay of each month. Late fees waived for next $parsedGrace days.",
                                fontSize = 11.sp,
                                color = Color(0xFF1E40AF)
                            )
                        }
                    }
                }
            }

            // 3. Late Fee Rules & Penalties Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "৩. বিলম্ব জরিমানা ও পেনাল্টি রুলস" else "3. Late Fee Rules & Penalty Calculation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SamityNavyDark
                        )
                    }
                    Text(
                        text = if (isBn) "গ্রেস পিরিয়ড পার হওয়ার পর জরিমানা কীভাবে হিসাব হবে" else "Penalty calculation method after grace period ends",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // Rule Type Filter Chips
                    Text(
                        text = if (isBn) "জরিমানার ধরন নির্বাচন করুন:" else "Select Penalty Rule Type:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = lateFeeType == "Fixed Daily",
                            onClick = { lateFeeType = "Fixed Daily" },
                            label = { Text(if (isBn) "দৈনিক ফি" else "Daily Fixed", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = lateFeeType == "One-Time Flat",
                            onClick = { lateFeeType = "One-Time Flat" },
                            label = { Text(if (isBn) "এককালীন ফি" else "Flat Fee", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = lateFeeType == "Percentage",
                            onClick = { lateFeeType = "Percentage" },
                            label = { Text(if (isBn) "শতকরা হার" else "Percentage", fontSize = 11.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (lateFeeType == "Percentage") {
                        OutlinedTextField(
                            value = lateFeePercentageStr,
                            onValueChange = { lateFeePercentageStr = it },
                            label = { Text(if (isBn) "বিলম্ব জরিমানা শতকরা হার (%)" else "Late Fee Percentage (%)") },
                            trailingIcon = { Text("%", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(end = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = lateFeeAmountStr,
                            onValueChange = { lateFeeAmountStr = it },
                            label = {
                                Text(
                                    if (lateFeeType == "Fixed Daily")
                                        (if (isBn) "প্রতিদিনের বিলম্ব জরিমানা (টাকা)" else "Daily Late Fee (BDT/Day)")
                                    else
                                        (if (isBn) "এককালীন বিলম্ব জরিমানা (টাকা)" else "One-Time Flat Penalty (BDT)")
                                )
                            },
                            leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SamityNavyPrimary, modifier = Modifier.padding(start = 12.dp)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // 4. Automated Reminders Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFF7C3AED), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "৪. স্বয়ংক্রিয় নোটিফিকেশন ও এসএমএস অ্যালার্ট" else "4. Automated Reminders & Alerts",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SamityNavyDark
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "শেষ তারিখের পূর্বে এসএমএস তাগাদা" else "SMS Reminder Before Due Date",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = SamityNavyDark
                            )
                            Text(
                                text = if (isBn) "নির্ধারিত তারিখের ৩ দিন আগে সদস্যদের স্বয়ংক্রিয় এসএমএস" else "Sends automated SMS 3 days before due date",
                                fontSize = 10.5.sp,
                                color = Color.Gray
                            )
                        }
                        Switch(
                            checked = autoSendSms,
                            onCheckedChange = { autoSendSms = it }
                        )
                    }
                }
            }

            // 5. Live Simulation / Invoice Preview Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                border = BorderStroke(1.dp, Color(0xFFFDE68A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "💡 লাইভ ক্যালকুলেশন প্রিভিউ (উদাহরণ)" else "💡 Live Calculation Preview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF92400E)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBn) "ধরা যাক, একজন সদস্য শেষ তারিখের পর $simulatedLateDays দিন বিলম্বে চাঁদা পরিশোধ করছেন:"
                        else "Simulating a member paying $simulatedLateDays days after grace period ends:",
                        fontSize = 11.sp,
                        color = Color(0xFF78350F)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBn) "মূল মাসিক চাঁদা:" else "Base Monthly Fee:", fontSize = 11.5.sp, color = Color(0xFF78350F))
                        Text("${parsedMonthly.toInt()} ৳", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF78350F))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            text = if (isBn) "বিলম্ব জরিমানা ($lateFeeType):" else "Late Fee Penalty:",
                            fontSize = 11.5.sp,
                            color = Color(0xFFB45309)
                        )
                        Text("+ ${simulatedPenalty.toInt()} ৳", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFB45309))
                    }

                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFFCD34D))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (isBn) "মোট প্রদেয় বিল:" else "Total Payable:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF78350F))
                        Text("${simulatedTotalDue.toInt()} ৳", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF92400E))
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    val updatedConfig = SubscriptionConfig(
                        samityId = repository.activeSamityId,
                        monthlyDueAmount = parsedMonthly,
                        dueDayOfMonth = parsedDueDay,
                        gracePeriodDays = parsedGrace,
                        lateFeeType = lateFeeType,
                        lateFeeAmount = parsedLateFee,
                        lateFeePercentage = parsedPercent,
                        autoSendSmsReminder = autoSendSms,
                        reminderDaysBeforeDue = reminderDaysPriorStr.toIntOrNull() ?: 3,
                        applyToExistingMembers = applyToExistingMembers,
                        updatedBy = repository.userSession.currentUser.name,
                        updatedAt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    )

                    val success = repository.updateSubscriptionConfig(updatedConfig, applyToExisting = applyToExistingMembers)
                    if (success) {
                        Toast.makeText(
                            context,
                            if (isBn) "✅ সাবস্ক্রিপশন ও চাঁদা কনফিগারেশন সফলভাবে সংরক্ষিত হয়েছে!"
                            else "Subscription configuration updated and synced!",
                            Toast.LENGTH_LONG
                        ).show()
                        onBack()
                    } else {
                        Toast.makeText(
                            context,
                            if (isBn) "❌ সাবস্ক্রিপশন আপডেট করার অনুমতি নেই"
                            else "Permission denied to update subscription rules",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_subscription_config_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBn) "নিয়ম সংরক্ষণ ও ক্লাউড সিঙ্ক করুন" else "Save & Sync Rules",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
