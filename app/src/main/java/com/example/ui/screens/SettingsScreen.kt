package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    onNavigateToLetterhead: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToVercelSync: () -> Unit,
    onNavigateToRoles: () -> Unit,
    onNavigateToApprovals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isBn by remember { mutableStateOf(LanguageManager.isBangla) }
    val samity = repository.activeSamity

    var defaultFeeStr by remember { mutableStateOf(samity.defaultMonthlyFee.toInt().toString()) }
    var dueDayStr by remember { mutableStateOf(samity.dueDayOfMonth.toString()) }
    var lateFeeStr by remember { mutableStateOf(samity.lateFeeAmount.toInt().toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "সিস্টেম সেটিংস ও ভাষা (Settings)" else "System Settings & Language",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "ভাষা, চাঁদা কনফিগারেশন, এপিআই ও ব্যাকআপ" else "Language, Subscription Config & API",
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
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Language Module Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "🌐 ভাষা পরিবর্তন (Language Selection)" else "🌐 Language Selection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SamityNavyDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = isBn,
                            onClick = {
                                isBn = true
                                LanguageManager.isBangla = true
                                Toast.makeText(context, "ভাষা বাংলা নির্ধারণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("🇧🇩 বাংলা (Bengali)", fontWeight = if (isBn) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = !isBn,
                            onClick = {
                                isBn = false
                                LanguageManager.isBangla = false
                                Toast.makeText(context, "Language set to English", Toast.LENGTH_SHORT).show()
                            },
                            label = { Text("🇺🇸 English", fontWeight = if (!isBn) FontWeight.Bold else FontWeight.Normal) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Subscription & Fee Configuration Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "⚙️ চাঁদা ও ফি কনফিগারেশন (Subscription Settings)" else "⚙️ Subscription Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SamityNavyDark
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = defaultFeeStr,
                        onValueChange = { defaultFeeStr = it },
                        label = { Text(if (isBn) "ডিফল্ট মাসিক চাঁদা (টাকা)" else "Default Monthly Fee (BDT)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = dueDayStr,
                            onValueChange = { dueDayStr = it },
                            label = { Text(if (isBn) "মাসের শেষ তারিখ (দিন)" else "Due Day of Month") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lateFeeStr,
                            onValueChange = { lateFeeStr = it },
                            label = { Text(if (isBn) "বিলম্ব ফি (টাকা)" else "Late Fee (BDT)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val fee = defaultFeeStr.toDoubleOrNull() ?: samity.defaultMonthlyFee
                            val day = dueDayStr.toIntOrNull() ?: samity.dueDayOfMonth
                            val late = lateFeeStr.toDoubleOrNull() ?: samity.lateFeeAmount
                            val updated = samity.copy(
                                defaultMonthlyFee = fee,
                                dueDayOfMonth = day,
                                lateFeeAmount = late
                            )
                            val idx = repository.samities.indexOfFirst { it.id == samity.id }
                            if (idx != -1) {
                                repository.samities[idx] = updated
                            }
                            Toast.makeText(context, if (isBn) "চাঁদা সেটিংস সফলভাবে সংরক্ষিত হয়েছে" else "Subscription settings updated", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("save_settings_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "সেটিংস সংরক্ষণ করুন" else "Save Settings")
                    }
                }
            }

            // Quick Nav Links to Core Modules
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isBn) "প্রধান মডিউল সেটিংস নেভিগেশন" else "Core Module Configurations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SamityNavyDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SettingNavItem(
                        title = if (isBn) "লেটারহেড, লোগো ও ডিজিটাল সিল-স্বাক্ষর" else "Letterhead, Signatures & Seals",
                        subtitle = if (isBn) "প্যাড ডিজাইন, স্লোগান ও ৩ কর্মকর্তার স্বাক্ষর" else "Custom pad, slogan & 3 signatories",
                        icon = Icons.Default.BorderColor,
                        onClick = onNavigateToLetterhead
                    )
                    Divider()
                    SettingNavItem(
                        title = if (isBn) "অনুমোদন ওয়ার্কফ্লো ও হিস্ট্রি" else "Approval Workflow & Reviews",
                        subtitle = if (isBn) "জমা ও খরচের প্রশাসনিক অনুমোদন" else "Pending transaction reviews",
                        icon = Icons.Default.CheckCircleOutline,
                        onClick = onNavigateToApprovals
                    )
                    Divider()
                    SettingNavItem(
                        title = if (isBn) "রোল, ব্যবহারকারী ও পারমিশন কন্ট্রোল" else "Roles, Users & Permissions",
                        subtitle = if (isBn) "সভাপতি, সাধারণ সম্পাদক ও কোষাধ্যক্ষের ক্ষমতা" else "Staff role privileges",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = onNavigateToRoles
                    )
                    Divider()
                    SettingNavItem(
                        title = if (isBn) "ডাটাবেজ ব্যাকআপ ও CSV এক্সপোর্ট" else "Database Backup & CSV Export",
                        subtitle = if (isBn) "সম্পূর্ণ ডাটা JSON ও CSV তে রূপান্তর" else "Full export & disaster recovery",
                        icon = Icons.Default.Backup,
                        onClick = onNavigateToBackup
                    )
                    Divider()
                    SettingNavItem(
                        title = if (isBn) "ভার্সেল ক্লাউড ও ওয়েব API গেটওয়ে" else "Vercel Cloud & Web API Gateway",
                        subtitle = if (isBn) "রিয়েল-টাইম বাইডাইরেকশনাল ডাটা সিঙ্ক" else "Bidirectional 100% cloud parity",
                        icon = Icons.Default.CloudSync,
                        onClick = onNavigateToVercelSync
                    )
                }
            }

            // Software About Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary.copy(alpha = 0.05f)),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "সমিতি কিতাব ৩৬০ ডিগ্রি ইআরপি (Samity Kitab 360)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = SamityNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn)
                            "ভার্সন ১.০.০ • সম্পূর্ণ কমার্শিয়াল মাল্টি-টেন্যান্ট ইআরপি • অ্যান্ড্রয়েড মোবাইল অ্যাপ + ভার্সেল ক্লাউড ওয়েব পোর্টাল • ১০০% অফলাইন ও অনলাইন রিয়েল-টাইম ডাটা সিঙ্ক।"
                        else
                            "Version 1.0.0 • Commercial Multi-Tenant ERP • Android Mobile + Vercel Cloud Web • 100% Offline & Online Real-Time Parity.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingNavItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SamityNavyDark)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
    }
}
