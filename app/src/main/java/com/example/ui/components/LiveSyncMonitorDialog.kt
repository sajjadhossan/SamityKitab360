package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RealtimeSyncEvent
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveSyncMonitorDialog(
    repository: SamityRepository,
    onDismiss: () -> Unit
) {
    val isBn = LanguageManager.isBangla
    val syncEngine = repository.syncEngine
    val devices = syncEngine.connectedDevices
    val events = syncEngine.syncEvents

    var testMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SamityNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = SamityGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBn) "রিয়েল-টাইম ডাটা সিঙ্ক ও স্ক্যান" else "Real-Time Multi-Device Sync",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = SamityNavyPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBn) "${devices.size}টি ডিভাইস লাইভ সংযুক্ত • সিঙ্ক সক্রিয়" else "${devices.size} Devices Live Connected",
                                    fontSize = 11.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Simulation Trigger Card (User's Exact Scenario: 10,000 Tk Deposit Sync)
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SamityGold.copy(alpha = 0.12f)),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = SamityNavyPrimary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBn) "তাৎক্ষণিক সিঙ্ক টেস্ট (১০,০০০ টাকা চাঁদা)" else "Test Instant 10,000 Tk Sync",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = SamityNavyPrimary
                                    )
                                }

                                Text(
                                    text = if (isBn)
                                        "অন্য কোনো কানেক্টেড মোবাইল থেকে ১০,০০০ টাকা চাঁদা জমা এন্ট্রি দিন। সাথে সাথে এই মোবাইলসহ সকল কানেক্টেড ডিভাইসে ডাটা স্ক্যান ও ব্যালেন্স আপডেট হবে।"
                                    else
                                        "Record a 10,000 BDT deposit from another mobile device and observe real-time reflection across all screens.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )

                                Button(
                                    onClick = {
                                        val dep = repository.simulateCrossDevicePayment(
                                            amount = 10000.0,
                                            originDevice = "অন্যান্য মোবাইল (ডিভাইস ২ - কোষাধ্যক্ষ)"
                                        )
                                        if (dep != null) {
                                            testMessage = if (isBn)
                                                "সফল! ${dep.memberName}-এর ১০,০০০ টাকা চাঁদা সিঙ্ক হয়েছে। সকল ডিভাইসে ডাটা স্ক্যান সম্পন্ন।"
                                            else
                                                "Success! 10,000 BDT synced to all connected devices."
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("simulate_10000_sync_btn")
                                ) {
                                    Icon(Icons.Default.SendToMobile, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBn) "⚡ এখনই ১০,০০০ টাকা চাঁদার লাইভ সিঙ্ক চালান" else "⚡ Broadcast 10,000 Tk Sync Now",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (testMessage != null) {
                                    Text(
                                        text = testMessage!!,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF059669),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Connected Devices List
                    item {
                        Text(
                            text = if (isBn) "সংযুক্ত ডিভাইসসমূহ (${devices.size}টি অনলাইন)" else "Connected Devices (${devices.size} Online)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SamityNavyPrimary
                        )
                    }

                    items(devices) { dev ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when (dev.deviceType) {
                                            "Tablet" -> Icons.Default.TabletAndroid
                                            "POS Terminal" -> Icons.Default.PointOfSale
                                            else -> Icons.Default.Smartphone
                                        },
                                        contentDescription = null,
                                        tint = SamityNavyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dev.deviceName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = SamityNavyDark
                                    )
                                    Text(
                                        text = "${dev.userRole} • ${dev.userName}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        text = "${dev.ipOrLocation} • শেষ পিং: ${dev.lastPing}",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (isBn) "অনলাইন" else "Online",
                                        color = Color(0xFF059669),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Live Event Feed
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBn) "সর্বশেষ সিঙ্কড লেনদেন ও কার্যক্রম" else "Recent Synced Transactions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SamityNavyPrimary
                        )
                    }

                    if (events.isEmpty()) {
                        item {
                            Text(
                                text = if (isBn) "এখনো নতুন কোনো সিঙ্ক ইভেন্ট নেই।" else "No sync events recorded yet.",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(events) { ev ->
                            when (ev) {
                                is RealtimeSyncEvent.DepositSynced -> {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SamityEmerald, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "${ev.memberName} (${ev.memberCode}) - ${ev.amount.toInt()} টাকা",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = SamityNavyDark
                                                )
                                                Text(
                                                    text = "ভাউচার: ${ev.voucherNo} • উৎস: ${ev.originDevice}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(text = ev.timestamp, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        }
                                    }
                                }
                                is RealtimeSyncEvent.BulkSlipSynced -> {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SamityGold, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = "বাল্ক ব্যাংক স্লিপ: ${ev.bankReceiptNo} (${ev.totalAmount.toInt()} টাকা)",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = SamityNavyDark
                                                )
                                                Text(
                                                    text = "${ev.memberCount} জন সদস্যের হিসাবে সিঙ্কড • ${ev.originDevice}",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                            Text(text = ev.timestamp, fontSize = 10.sp, color = Color(0xFF94A3B8))
                                        }
                                    }
                                }
                                is RealtimeSyncEvent.MemberDueUpdated -> {
                                    // other events
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
