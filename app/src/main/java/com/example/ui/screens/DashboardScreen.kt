package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deposit
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@Composable
fun DashboardScreen(
    repository: SamityRepository,
    onNavigateToMembers: () -> Unit,
    onNavigateToDeposits: () -> Unit,
    onNavigateToBulkDeposit: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onViewReceipt: (Deposit) -> Unit,
    onNavigateToVercelSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers
    val deposits = repository.activeDeposits

    val totalMembersCount = members.size
    val paidCount = members.count { it.currentMonthPaid >= it.monthlyFee && it.monthlyFee > 0 }
    val unpaidCount = members.count { it.currentMonthPaid == 0.0 && it.monthlyFee > 0 }
    val partialCount = members.count { it.currentMonthPaid > 0 && it.currentMonthPaid < it.monthlyFee }

    val expectedCollection = members.sumOf { it.monthlyFee }
    val actualCollection = members.sumOf { it.currentMonthPaid }
    val totalDue = members.sumOf { it.totalDue }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner & Role Badge
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "স্বাগতম, ${repository.currentUserRole.titleBn}" else "Welcome, ${repository.currentUserRole.titleEn}",
                                color = SamityGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBn) samity.nameBn else samity.nameEn,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SamityNavyDark
                        ) {
                            Text(
                                text = if (isBn) "চলতি মাস: অক্টোবর ২০২৬" else "Month: Oct 2026",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress indicator for monthly collection
                    val progress = if (expectedCollection > 0) (actualCollection / expectedCollection).toFloat().coerceIn(0f, 1f) else 0f
                    val percent = (progress * 100).toInt()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isBn) "মাসিক চাঁদা আদায়ের অগ্রগতি" else "Monthly Collection Progress",
                            color = Color(0xFFD0E4FF),
                            fontSize = 12.sp
                        )
                        Text(
                            text = "${NumberToWords.formatNumber(percent, isBn)}%",
                            color = SamityGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SamityGold,
                        trackColor = Color(0xFF1E3A5F)
                    )
                }
            }
        }

        // Real-Time Multi-Device Sync & Scan Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(SamityNavyPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = SamityGold, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isBn) "রিয়েল-টাইম ডাটা সিঙ্ক ও স্ক্যান" else "Real-Time Multi-Device Sync",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SamityNavyDark
                                )
                                Text(
                                    text = "${if (isBn) "সংযুক্ত ডিভাইস: " else "Connected: "}${repository.syncEngine.connectedDevices.size}টি মোবাইল ও POS",
                                    fontSize = 10.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFDCFCE7)
                        ) {
                            Text(
                                text = if (isBn) "সব ডিভাইসে সেম ডাটা" else "100% Synced",
                                color = Color(0xFF15803D),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isBn)
                            "যেকোনো ডিভাইস থেকে চাঁদা বা ট্রানজেকশন যুক্ত করলে সাথে সাথে সকল ডিভাইসে ডাটা স্ক্যান ও আপডেট হয়ে যায়।"
                        else
                            "Transactions logged on any mobile are instantly scanned and reflected on all connected devices.",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                repository.simulateCrossDevicePayment(
                                    amount = 10000.0,
                                    originDevice = "অন্যান্য মোবাইল (ডিভাইস ২ - কোষাধ্যক্ষ)"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SamityGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(42.dp)
                                .testTag("dashboard_sync_10000_btn")
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = SamityNavyDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) "⚡ ১০,০০০ ৳ সিঙ্ক টেস্ট" else "⚡ Test 10k Tk Sync",
                                color = SamityNavyDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onNavigateToVercelSync,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                                .testTag("dashboard_vercel_hub_btn")
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBn) "ভার্সেল হাব" else "Vercel Hub",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SamityNavyPrimary
                            )
                        }
                    }
                }
            }
        }

        // Quick Action Bar
        item {
            Text(
                text = if (isBn) "দ্রুত কার্যক্রম (Quick Actions)" else "Quick Actions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SlateTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddCircle,
                    label = if (isBn) "জমা এন্ট্রি" else "New Deposit",
                    color = SamityNavyPrimary,
                    modifier = Modifier.weight(1f).testTag("quick_action_deposit"),
                    onClick = onNavigateToDeposits
                )
                QuickActionButton(
                    icon = Icons.Default.ReceiptLong,
                    label = if (isBn) "বাল্ক জমা" else "Bulk Slip",
                    color = SamityEmerald,
                    modifier = Modifier.weight(1f).testTag("quick_action_bulk"),
                    onClick = onNavigateToBulkDeposit
                )
                QuickActionButton(
                    icon = Icons.Default.NotificationsActive,
                    label = if (isBn) "রিমাইন্ডার" else "Reminder",
                    color = SamityGold,
                    modifier = Modifier.weight(1f).testTag("quick_action_reminder"),
                    onClick = onNavigateToReminders
                )
                QuickActionButton(
                    icon = Icons.Default.MoneyOff,
                    label = if (isBn) "ব্যয় ভাউচার" else "Expense",
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f).testTag("quick_action_expense"),
                    onClick = onNavigateToExpenses
                )
            }
        }

        // Primary KPI Financial Cards (Grid of 2 columns)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = LanguageManager.totalCollected,
                    value = NumberToWords.formatCurrency(repository.totalApprovedDeposits, isBn),
                    icon = Icons.Default.Savings,
                    color = StatusPaidGreen,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = LanguageManager.totalDue,
                    value = NumberToWords.formatCurrency(totalDue, isBn),
                    icon = Icons.Default.HourglassTop,
                    color = StatusUnpaidRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                KpiMetricCard(
                    title = LanguageManager.availableFund,
                    value = NumberToWords.formatCurrency(repository.netAvailableFund, isBn),
                    icon = Icons.Default.AccountBalanceWallet,
                    color = SamityNavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = LanguageManager.totalExpense,
                    value = NumberToWords.formatCurrency(repository.totalApprovedExpenses, isBn),
                    icon = Icons.Default.Payments,
                    color = StatusPartialAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Bank, Cash, Mobile Banking Balances
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isBn) "তহবিল বিবরণ ও একাউন্ট স্থিতি" else "Fund Breakdown & Balances",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BalanceMiniCard(
                            label = if (isBn) "ব্যাংক স্থিতি" else "Bank Balance",
                            amount = repository.bankBalance,
                            bankName = samity.bankName,
                            icon = Icons.Default.AccountBalance,
                            color = SamityNavyPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BalanceMiniCard(
                            label = if (isBn) "নগদ স্থিতি" else "Cash Balance",
                            amount = repository.cashBalance,
                            bankName = if (isBn) "অফিস ক্যাশ" else "Cash Box",
                            icon = Icons.Default.LocalAtm,
                            color = SamityEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BalanceMiniCard(
                            label = if (isBn) "মোবাইল ব্যাংকিং" else "MFS Balance",
                            amount = repository.mobileBankBalance,
                            bankName = "bKash/Nagad",
                            icon = Icons.Default.PhoneAndroid,
                            color = SamityGold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Member Payment Status Summary Counters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MemberCountItem(
                        count = totalMembersCount,
                        label = LanguageManager.totalMembers,
                        color = SamityNavyPrimary,
                        isBn = isBn
                    )
                    Divider(modifier = Modifier.height(40.dp).width(1.dp))
                    MemberCountItem(
                        count = paidCount,
                        label = LanguageManager.paidMembers,
                        color = StatusPaidGreen,
                        isBn = isBn
                    )
                    Divider(modifier = Modifier.height(40.dp).width(1.dp))
                    MemberCountItem(
                        count = unpaidCount,
                        label = LanguageManager.unpaidMembers,
                        color = StatusUnpaidRed,
                        isBn = isBn
                    )
                    Divider(modifier = Modifier.height(40.dp).width(1.dp))
                    MemberCountItem(
                        count = partialCount,
                        label = if (isBn) "আংশিক" else "Partial",
                        color = StatusPartialAmber,
                        isBn = isBn
                    )
                }
            }
        }

        // Recent Deposits & Receipts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBn) "সাম্প্রতিক ব্যাংক ও চাঁদা জমা রিসিট" else "Recent Deposits & Receipts",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateTextPrimary
                )
                TextButton(onClick = onNavigateToDeposits) {
                    Text(
                        text = if (isBn) "সব দেখুন" else "View All",
                        fontSize = 12.sp,
                        color = SamityNavyPrimary
                    )
                }
            }
        }

        if (deposits.isEmpty()) {
            item {
                Text(
                    text = if (isBn) "কোন জমা পাওয়া যায়নি" else "No deposits found",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        } else {
            items(deposits.take(5)) { dep ->
                DepositRowCard(
                    deposit = dep,
                    isBn = isBn,
                    onViewReceipt = { onViewReceipt(dep) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun BalanceMiniCard(
    label: String,
    amount: Double,
    bankName: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.08f),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, fontSize = 10.sp, color = Color(0xFF475569), maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = NumberToWords.formatCurrency(amount, LanguageManager.isBangla),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
            Text(bankName, fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1)
        }
    }
}

@Composable
private fun MemberCountItem(
    count: Int,
    label: String,
    color: Color,
    isBn: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = NumberToWords.formatNumber(count, isBn),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun DepositRowCard(
    deposit: Deposit,
    isBn: Boolean,
    onViewReceipt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewReceipt)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SamityNavyPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = SamityNavyPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = deposit.memberName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SlateTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(
                            text = deposit.memberCode,
                            fontSize = 9.sp,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Text(
                    text = "${deposit.voucherNo} • ${deposit.depositDate} • ${deposit.depositMethod}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberToWords.formatCurrency(deposit.amount, isBn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = StatusPaidGreen
                )
                Text(
                    text = if (isBn) "রিসিট দেখুন >" else "Receipt >",
                    fontSize = 10.sp,
                    color = SamityNavyPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
