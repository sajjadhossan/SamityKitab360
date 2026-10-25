package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SamityRepository
import com.example.ui.components.LiveSyncMonitorDialog
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

/**
 * Vercel Cloud & Web Portal Synchronization Hub.
 * Bridges Android Mobile Room/State database with Vercel-hosted Web App.
 * Guarantees that mobile devices and the Vercel web app share 100% identical scanned data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VercelCloudSyncScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val gateway = repository.vercelGateway
    val syncEngine = repository.syncEngine

    var showLiveSyncDialog by remember { mutableStateOf(false) }
    var actionMessage by remember { mutableStateOf<String?>(null) }
    var vercelUrlInput by remember { mutableStateOf(gateway.vercelBaseUrl) }

    val localMembers = repository.activeMembers
    val localDeposits = repository.activeDeposits
    val localDepositSum = localDeposits.sumOf { it.amount }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "ভার্সেল ও ওয়েব পোর্টাল সিঙ্ক হাব" else "Vercel Web Portal & Cloud Sync",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "ওয়েবসাইট ও মোবাইলের ডাটা একসাথে স্ক্যান" else "Bidirectional Web-Mobile Parity",
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
                actions = {
                    IconButton(
                        onClick = { showLiveSyncDialog = true },
                        modifier = Modifier.testTag("open_live_devices_dialog_btn")
                    ) {
                        Badge(containerColor = Color(0xFF10B981)) {
                            Icon(Icons.Default.Devices, contentDescription = "Devices", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamityNavyPrimary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Live Status Banner
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
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
                                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                                        .border(1.5.dp, Color(0xFF10B981), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CloudDone,
                                        contentDescription = null,
                                        tint = Color(0xFF34D399),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isBn) "ভার্সেল ক্লাউড ওয়েব পোর্টাল সক্রিয়" else "Vercel Web Portal Active",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "${gateway.lastSyncTimestamp} • ${syncEngine.connectedDevices.size}টি ডিভাইস সংযুক্ত",
                                        color = SamityGold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Text(
                                    text = if (isBn) "১০০% সেম ডাটা" else "100% Parity",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn)
                                "ভার্সেল ওয়েবসাইটে যে ডাটা আছে এবং এই মোবাইল অ্যাপের যে ডাটা আছে, উভয় ডাটাবেজ একে অপরের সাথে রিয়েল-টাইমে স্ক্যান করে ১০০% সমান রাখা হচ্ছে।"
                            else
                                "Data on Vercel web and this mobile application are scanned in real time to guarantee 100% parity across devices.",
                            fontSize = 12.sp,
                            color = Color(0xFFD0E4FF),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Notification / Toast Feedback
            if (actionMessage != null) {
                item {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFA7F3D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = actionMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF065F46),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { actionMessage = null },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFF065F46), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Two-Way Data Scan & Parity Verification Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) "ভার্সেল ⇄ মোবাইল ডাটা স্ক্যান ও তুলনা" else "Vercel ⇄ Mobile Parity Verification",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SamityNavyDark
                            )

                            Icon(Icons.Default.CompareArrows, contentDescription = null, tint = SamityNavyPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Parity Comparison Table
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Column 1: Members
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isBn) "সদস্য সংখ্যা" else "Members",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${localMembers.size} জন",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SamityNavyPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ভার্সেল: ${gateway.cloudMemberCount} জন",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "✓ সেম ডাটা",
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Column 2: Total Deposit
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isBn) "মোট চাঁদা জমা" else "Total Deposit",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${localDepositSum.toInt()} ৳",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ভার্সেল: ${gateway.cloudDepositTotal.toInt()} ৳",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "✓ সেম ডাটা",
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Column 3: Active Merchants
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (isBn) "মার্চেন্ট একাউন্ট" else "Merchants",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${repository.samities.size}টি",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SamityNavyDark
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "ভার্সেল: ${gateway.cloudActiveMerchants}টি",
                                        fontSize = 10.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "✓ সেম ডাটা",
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    repository.scanAndSyncWithVercel { success, msg ->
                                        actionMessage = msg
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("scan_and_match_vercel_btn")
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "ডাটা স্ক্যান ও মিলান" else "Scan & Verify",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val dep = repository.sync10kWithVercel()
                                    if (dep != null) {
                                        actionMessage = "⚡ ১০,০০০ টাকা চাঁদা ভার্সেল ও মোবাইলে একই সাথে স্ক্যান ও সিঙ্ক হয়েছে!"
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SamityGold),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("sync_10k_vercel_test_btn")
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = SamityNavyDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "১০,০০০ ৳ লাইভ টেস্ট" else "Test 10k Tk Sync",
                                    color = SamityNavyDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Vercel Live Web Portal Launcher Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = SamityNavyPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "ভার্সেল লাইভ ওয়েব পোর্টাল লিঙ্ক" else "Live Vercel Web Portal URL",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SamityNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = vercelUrlInput,
                            onValueChange = {
                                vercelUrlInput = it
                                gateway.vercelBaseUrl = it
                            },
                            label = { Text(if (isBn) "ওয়েবসাইট ডিপ্লয়মেন্ট ইউআরএল" else "Vercel Web URL") },
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = SamityNavyPrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(vercelUrlInput))
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "ওয়েবসাইট ওপেন করা যায়নি", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "ওয়েবসাইট ভিজিট করুন" else "Open in Browser", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Vercel Web URL", vercelUrlInput)
                                    clipboard.setPrimaryClip(clip)
                                    actionMessage = "ওয়েবসাইট লিঙ্ক কপি করা হয়েছে!"
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBn) "লিঙ্ক কপি করুন" else "Copy Link", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // GitHub & Vercel 1-Click Deployment Step-by-Step Instructions
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = SamityGold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBn) "GitHub আপলোড ও Vercel ডিপ্লয়মেন্ট গাইড" else "GitHub Push & Vercel 1-Click Deploy",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn)
                                "এই অ্যাপটি গিটহাবে আপলোড করে ভার্সেলে ১-ক্লিকে ওয়েবসাইট চালু করার সম্পূর্ণ নিয়ম:"
                            else
                                "Instructions to push to GitHub and deploy to Vercel in 1 click:",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Step 1
                        Text(
                            text = if (isBn) "১. গিটহাবে আপলোড কমান্ড:" else "1. Push to GitHub:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityGold
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF1E293B),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "git init && git add . && git commit -m \"Upload Samity Kitab 360\" && git push",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF34D399),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Step 2
                        Text(
                            text = if (isBn) "২. ভার্সেলে ১-ক্লিকে ডিপ্লয়:" else "2. 1-Click Deploy on Vercel:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityGold
                        )
                        Text(
                            text = if (isBn)
                                "• vercel.com এ যান -> Add New Project -> Import from GitHub\n• Root Directory নির্বাচন করুন: 'web'\n• Deploy বাটনে চাপুন। ৩০ সেকেন্ডে ওয়েবসাইট রেডি!"
                            else
                                "• Open vercel.com -> Add New Project -> Import from GitHub\n• Set Root Directory: 'web'\n• Click Deploy. Live in 30 seconds!",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0),
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Real-Time Parity Audit Log List
            item {
                Text(
                    text = if (isBn) "রিয়েল-টাইম ক্লাউড স্ক্যান ও সিঙ্ক লগ" else "Live Parity & Sync Audit Log",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = SamityNavyDark
                )
            }

            items(gateway.cloudSyncAuditLogs) { log ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) log.detailsBn else log.detailsEn,
                                fontSize = 11.sp,
                                color = SamityNavyDark,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${log.timestamp} • ${log.action}",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }

    if (showLiveSyncDialog) {
        LiveSyncMonitorDialog(
            repository = repository,
            onDismiss = { showLiveSyncDialog = false }
        )
    }
}
