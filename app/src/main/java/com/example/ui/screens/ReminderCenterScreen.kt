package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.ReminderLog
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCenterScreen(
    repository: SamityRepository,
    targetMember: Member? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers
    val unpaidMembers = members.filter { it.totalDue > 0 || it.currentMonthPaid < it.monthlyFee }

    var selectedTab by remember { mutableStateOf(0) } // 0: Dispatcher, 1: History Logs
    var selectedChannel by remember { mutableStateOf("WhatsApp") } // WhatsApp, SMS, Telegram, Messenger
    var currentMemberToRemind by remember { mutableStateOf(targetMember ?: unpaidMembers.firstOrNull()) }

    val dueMonth = "অক্টোবর ২০২৬"
    val dueAmount = currentMemberToRemind?.let { if (it.totalDue > 0) it.totalDue else it.monthlyFee } ?: 5000.0

    // Customizable Template Message
    val sampleMessage = """
        প্রিয় ${currentMemberToRemind?.nameBn ?: "সদস্য"},

        আপনার $dueMonth মাসের ${samity.nameBn}-এর মাসিক চাঁদা ${dueAmount.toInt()} টাকা এখনো জমা হয়নি।

        জমার শেষ তারিখ: ${samity.dueDayOfMonth} অক্টোবর ২০২৬
        জমার মাধ্যম: ব্যাংক / বিকাশ / নগদ

        ব্যাংক: ${samity.bankName}
        একাউন্ট নং: ${samity.bankAccountNo}
        বিকাশ: ${samity.bkashNumber}

        জমা দেওয়ার পর ব্যাংক রিসিটের ছবি পাঠাবেন।

        ধন্যবাদান্তে,
        ${samity.nameBn}
        ${samity.treasurerName} (${samity.treasurerPhone})
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // Screen Header
        Text(
            text = if (isBn) "মাল্টি-চ্যানেল রিমাইন্ডার সেন্টার" else "Multi-Channel Reminder Center",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn) "WhatsApp, SMS, Telegram ও Messenger-এর মাধ্যমে চাঁদা আদায় রিমাইন্ডার পাঠান" else "Send payment reminders via WhatsApp, SMS, Telegram & Messenger",
            fontSize = 11.5.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs: 1. Send Reminders, 2. Reminder Logs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = SamityNavyPrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isBn) "রিমাইন্ডার পাঠান" else "Dispatch Reminder", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        "${if (isBn) "রিমাইন্ডার লগ হিস্টোরি" else "Reminder Logs"} (${NumberToWords.formatNumber(repository.activeReminders.size, isBn)})",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Channel Selection Cards (WhatsApp, SMS, Telegram, Messenger)
                item {
                    Text(
                        text = if (isBn) "১. রিমাইন্ডার চ্যানেল নির্বাচন করুন" else "1. Select Delivery Channel",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChannelSelectCard(
                            title = "WhatsApp",
                            subtitle = if (isBn) "wa.me সরাসরি চ্যাট" else "Direct wa.me",
                            icon = Icons.Default.Chat,
                            brandColor = Color(0xFF25D366),
                            isSelected = selectedChannel == "WhatsApp",
                            modifier = Modifier.weight(1f).testTag("select_whatsapp_channel"),
                            onClick = { selectedChannel = "WhatsApp" }
                        )
                        ChannelSelectCard(
                            title = "SMS",
                            subtitle = if (isBn) "বাংলা ইউনিকোড" else "Unicode SMS",
                            icon = Icons.Default.Sms,
                            brandColor = Color(0xFF0284C7),
                            isSelected = selectedChannel == "SMS",
                            modifier = Modifier.weight(1f).testTag("select_sms_channel"),
                            onClick = { selectedChannel = "SMS" }
                        )
                        ChannelSelectCard(
                            title = "Telegram",
                            subtitle = if (isBn) "টেলিগ্রাম বট" else "Bot/User",
                            icon = Icons.Default.Send,
                            brandColor = Color(0xFF0088CC),
                            isSelected = selectedChannel == "Telegram",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedChannel = "Telegram" }
                        )
                        ChannelSelectCard(
                            title = "Messenger",
                            subtitle = "Facebook",
                            icon = Icons.Default.Facebook,
                            brandColor = Color(0xFF0084FF),
                            isSelected = selectedChannel == "Messenger",
                            modifier = Modifier.weight(1f),
                            onClick = { selectedChannel = "Messenger" }
                        )
                    }
                }

                // Member Selector for reminder
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isBn) "২. বকেয়া সদস্য নির্বাচন করুন" else "2. Select Unpaid Member",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SamityNavyPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            var memDropdown by remember { mutableStateOf(false) }
                            Box {
                                OutlinedCard(
                                    onClick = { memDropdown = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = currentMemberToRemind?.nameBn ?: "কোন সদস্য নেই",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${currentMemberToRemind?.memberCode} • বকেয়া: ৳ ${dueAmount.toInt()}",
                                                fontSize = 11.sp,
                                                color = StatusUnpaidRed
                                            )
                                        }
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                }

                                DropdownMenu(
                                    expanded = memDropdown,
                                    onDismissRequest = { memDropdown = false }
                                ) {
                                    unpaidMembers.forEach { m ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text("${m.nameBn} (${m.memberCode})")
                                                    Text("বকেয়া: ৳ ${m.totalDue.toInt()} • ফোন: ${m.phone}", fontSize = 10.sp, color = Color.Gray)
                                                }
                                            },
                                            onClick = {
                                                currentMemberToRemind = m
                                                memDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Message Preview Card
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "৩. রিমাইন্ডার মেসেজ প্রিভিউ" else "3. Message Preview",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SamityNavyPrimary
                                )

                                TextButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Reminder Message", sampleMessage))
                                        Toast.makeText(context, if (isBn) "মেসেজ কপি করা হয়েছে" else "Message copied!", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "কপি করুন" else "Copy", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = sampleMessage,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = Color(0xFF1E293B),
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // Send Action Button
                item {
                    Button(
                        onClick = {
                            val mem = currentMemberToRemind
                            if (mem != null) {
                                when (selectedChannel) {
                                    "WhatsApp" -> {
                                        val cleanNumber = mem.whatsappNumber.replace("+", "").replace("-", "").replace(" ", "")
                                        val url = "https://wa.me/$cleanNumber?text=${URLEncoder.encode(sampleMessage, "UTF-8")}"
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                            repository.logReminder(mem, "WhatsApp", dueMonth, dueAmount, repository.currentUserRole.titleEn)
                                            Toast.makeText(context, if (isBn) "হোয়াটসঅ্যাপ ওপেন হচ্ছে..." else "Opening WhatsApp...", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    "SMS" -> {
                                        val cleanNumber = mem.phone.replace("+", "").replace("-", "")
                                        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$cleanNumber")).apply {
                                            putExtra("sms_body", sampleMessage)
                                        }
                                        try {
                                            context.startActivity(smsIntent)
                                            repository.logReminder(mem, "SMS", dueMonth, dueAmount, repository.currentUserRole.titleEn)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "SMS app not found", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    "Telegram" -> {
                                        val url = if (mem.telegramHandle.isNotBlank()) "https://t.me/${mem.telegramHandle.replace("@", "")}" else "https://t.me"
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                            repository.logReminder(mem, "Telegram", dueMonth, dueAmount, repository.currentUserRole.titleEn)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Telegram not installed", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    "Messenger" -> {
                                        val url = if (mem.facebookUrl.isNotBlank()) mem.facebookUrl else "https://m.me"
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                            repository.logReminder(mem, "Facebook Messenger", dueMonth, dueAmount, repository.currentUserRole.titleEn)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Messenger not available", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("send_reminder_now_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (selectedChannel) {
                                "WhatsApp" -> Color(0xFF25D366)
                                "SMS" -> Color(0xFF0284C7)
                                "Telegram" -> Color(0xFF0088CC)
                                else -> Color(0xFF0084FF)
                            }
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBn) "$selectedChannel-এ রিমাইন্ডার পাঠান" else "Send via $selectedChannel Now",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        } else {
            // Tab 1: Reminder Logs History
            val logs = repository.activeReminders
            if (logs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (isBn) "কোন রিমাইন্ডার লগ নেই" else "No reminder logs yet", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(logs) { log ->
                        ReminderLogItemCard(log = log, isBn = isBn)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChannelSelectCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    brandColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) brandColor.copy(alpha = 0.15f) else Color.White,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) brandColor else Color(0xFFCBD5E1)),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(brandColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) brandColor else Color(0xFF1E293B))
            Text(subtitle, fontSize = 8.5.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ReminderLogItemCard(
    log: ReminderLog,
    isBn: Boolean
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SamityNavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SamityGold, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(log.memberName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(log.channel, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SamityNavyPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text("${log.dueMonth} • বকেয়া: ৳ ${log.dueAmount.toInt()} • সময়: ${log.sentAt}", fontSize = 10.5.sp, color = Color(0xFF64748B))
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = StatusPaidBg
            ) {
                Text(
                    text = if (isBn) "প্রেরিত" else "Sent",
                    color = StatusPaidGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}
