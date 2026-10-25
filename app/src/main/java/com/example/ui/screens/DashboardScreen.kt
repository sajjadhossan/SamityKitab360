package com.example.ui.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    onNavigateToPaymentStatus: () -> Unit = {},
    onNavigateToFundBalance: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToLetterhead: () -> Unit = {},
    onNavigateToDocumentGenerator: () -> Unit = {},
    onNavigateToAuditTrail: () -> Unit = {},
    onNavigateToSamitySetup: () -> Unit = {},
    onNavigateToApprovalWorkflow: () -> Unit = {},
    onNavigateToBackupExport: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToSubscriptionConfig: () -> Unit = {},
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

        // 2x2 Grid Layout (Four Corner Layout): Members, Deposits, Reports, Settings
        item {
            Column(modifier = Modifier.fillMaxWidth().testTag("four_corner_2x2_hero_grid")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SamityNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridView,
                                contentDescription = null,
                                tint = SamityGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBn) "প্রধান ৪-কর্নার ড্যাশবোর্ড হাব" else "Core 2x2 Feature Quadrants",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = if (isBn) "সরাসরি ৪টি প্রধান সেবায় প্রবেশ করুন (Members, Deposits, Reports, Settings)"
                                else "Direct access: Members, Deposits, Reports & Settings",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = if (isBn) "২x২ গ্রিড" else "2x2 Grid",
                            color = SamityNavyPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ROW 1: Quadrant 1 (Top-Left: Members) & Quadrant 2 (Top-Right: Deposits)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuadrantFeatureCard(
                        quadrantLabel = "Quadrant 1 • Top-Left",
                        title = if (isBn) "সদস্য ব্যবস্থাপনা" else "Members",
                        subtitle = if (isBn) "সদস্য তালিকা, প্রোফাইল ও ডিরেক্টরি" else "Member directory, dues & profile",
                        footerMetric = "${totalMembersCount} জন সদস্য • ${paidCount} পেইড",
                        badgeText = "${members.size} Members",
                        actionCue = if (isBn) "প্রবেশ করুন →" else "Open →",
                        icon = Icons.Default.People,
                        gradient = listOf(Color(0xFF047857), Color(0xFF10B981)),
                        testTag = "quadrant_card_members",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMembers
                    )

                    QuadrantFeatureCard(
                        quadrantLabel = "Quadrant 2 • Top-Right",
                        title = if (isBn) "চাঁদা ও জমা সংগ্রহ" else "Deposits",
                        subtitle = if (isBn) "ব্যাংক, ক্যাশ, বিকাশ ও বাল্ক স্লিপ" else "Bank, cash, bKash & collection",
                        footerMetric = NumberToWords.formatCurrency(actualCollection, isBn),
                        badgeText = if (isBn) "কালেকশন" else "Deposits",
                        actionCue = if (isBn) "জমা দিন →" else "Collect →",
                        icon = Icons.Default.AccountBalance,
                        gradient = listOf(Color(0xFF0F3B66), Color(0xFF1D4ED8)),
                        testTag = "quadrant_card_deposits",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDeposits
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ROW 2: Quadrant 3 (Bottom-Left: Reports) & Quadrant 4 (Bottom-Right: Settings)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuadrantFeatureCard(
                        quadrantLabel = "Quadrant 3 • Bottom-Left",
                        title = if (isBn) "আর্থিক ও অডিট রিপোর্ট" else "Reports",
                        subtitle = if (isBn) "মাসিক, বার্ষিক ও অডিট বিবরণী" else "Financial statements & audit logs",
                        footerMetric = "নিট ফান্ড: ${NumberToWords.formatCurrency(repository.netAvailableFund, isBn)}",
                        badgeText = if (isBn) "অডিট বিবরণী" else "Reports",
                        actionCue = if (isBn) "রিপোর্ট →" else "Reports →",
                        icon = Icons.Default.Assessment,
                        gradient = listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6)),
                        testTag = "quadrant_card_reports",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )

                    QuadrantFeatureCard(
                        quadrantLabel = "Quadrant 4 • Bottom-Right",
                        title = if (isBn) "সমিতি সেটিংস ও কনফিগ" else "Settings",
                        subtitle = if (isBn) "সমিতি সেটআপ, ব্যাকআপ, ভাষা ও রুলস" else "Samity config, rules & backup",
                        footerMetric = "${samity.samityType} • ফি: ${samity.defaultMonthlyFee.toInt()}৳",
                        badgeText = if (isBn) "কনফিগ" else "Settings",
                        actionCue = if (isBn) "কনফিগ →" else "Config →",
                        icon = Icons.Default.Settings,
                        gradient = listOf(Color(0xFFB45309), Color(0xFFF59E0B)),
                        testTag = "quadrant_card_settings",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSettings
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

        // 4-Corner Modern Squircle Module Hub ("ফোর কর্নার ছোট ছোট করে ফোর কর্নার ফোর কর্নার কইরা এই অপশনগুলা অ্যাড করবা")
        item {
            Column(modifier = Modifier.fillMaxWidth().testTag("four_corner_quick_hub_section")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(SamityNavyPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GridView, contentDescription = null, tint = SamityGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isBn) "৪-কর্নার দ্রুত সেবা ও মডিউল হাব" else "4-Corner Quick Action Hub",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = if (isBn) "যেকোনো মডিউলে সরাসরি দ্রুত প্রবেশ করুন" else "Direct 1-tap quick module navigation",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEFF6FF)
                    ) {
                        Text(
                            text = if (isBn) "১২টি দ্রুত হাব" else "12 Tiles",
                            color = SamityNavyPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2-Column 4-Corner Squircle Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "সমিতি সেটআপ" else "Samity Setup",
                        subtitle = if (isBn) "নাম, ফি, ব্যাংক ও কনফিগ" else "Name, fee & bank setup",
                        badge = if (isBn) "প্রশাসন" else "Admin",
                        icon = Icons.Default.Apartment,
                        gradient = listOf(Color(0xFF0D2C54), Color(0xFF1E40AF)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSamitySetup
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "সদস্য তালিকা" else "Members",
                        subtitle = if (isBn) "সদস্য, মোবাইল ও বকেয়া" else "Directory & balances",
                        badge = "${members.size} জন",
                        icon = Icons.Default.People,
                        gradient = listOf(Color(0xFF047857), Color(0xFF10B981)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMembers
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "চাঁদা ও জমা" else "Deposits",
                        subtitle = if (isBn) "ব্যাংক, ক্যাশ, বিকাশ ও রসিদ" else "Bank, cash & bKash",
                        badge = "কালেকশন",
                        icon = Icons.Default.AccountBalance,
                        gradient = listOf(Color(0xFF0E7490), Color(0xFF06B6D4)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDeposits
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "বাল্ক জমা স্লিপ" else "Bulk Slip",
                        subtitle = if (isBn) "একসাথে অনেকের চাঁদা এন্ট্রি" else "Batch receipt entry",
                        badge = "মাল্টি স্লিপ",
                        icon = Icons.Default.ReceiptLong,
                        gradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBulkDeposit
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "আদায় ও বকেয়া" else "Due Tracking",
                        subtitle = if (isBn) "কার পরিশোধ আর কার বাকি" else "Paid & unpaid tracking",
                        badge = "${totalDue.toInt()} ৳",
                        icon = Icons.Default.FactCheck,
                        gradient = listOf(Color(0xFFBE123C), Color(0xFFF43F5E)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPaymentStatus
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "তাগিদ নোটিশ" else "Reminders",
                        subtitle = if (isBn) "WhatsApp, SMS ও মেসেঞ্জার" else "SMS & WhatsApp alerts",
                        badge = "নোটিশ",
                        icon = Icons.Default.NotificationsActive,
                        gradient = listOf(Color(0xFFC2410C), Color(0xFFFB923C)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReminders
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "ব্যয় ও তহবিল" else "Expenses & Fund",
                        subtitle = if (isBn) "ভাউচার অনুমোদন ও নিট ফান্ড" else "Voucher & available fund",
                        badge = "ব্যালেন্স",
                        icon = Icons.Default.AccountBalanceWallet,
                        gradient = listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFundBalance
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "আর্থিক রিপোর্ট" else "Reports & Audit",
                        subtitle = if (isBn) "দৈনিক, মাসিক ও অডিট হিসাব" else "Daily & audit reports",
                        badge = "অডিট",
                        icon = Icons.Default.Assessment,
                        gradient = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "লেটারহেড ও প্যাড" else "Letterhead",
                        subtitle = if (isBn) "অফিস প্যাড, প্রত্যয়ন ও সনদ" else "Official pads & notices",
                        badge = "ডকুমেন্টস",
                        icon = Icons.Default.Description,
                        gradient = listOf(Color(0xFF4338CA), Color(0xFF6366F1)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDocumentGenerator
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "অনুমোদন ও অডিট" else "Approvals",
                        subtitle = if (isBn) "লেনদেন অনুমোদন ও লগ" else "Approvals & audit log",
                        badge = "নিরাপত্তা",
                        icon = Icons.Default.VerifiedUser,
                        gradient = listOf(Color(0xFF065F46), Color(0xFF059669)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToApprovalWorkflow
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardFourCornerCard(
                        title = if (isBn) "ভার্সেল সিঙ্ক হাব" else "Cloud Sync Hub",
                        subtitle = if (isBn) "ওয়েবসাইট ও ক্লাউড সিঙ্ক" else "Vercel real-time sync",
                        badge = "Live Sync",
                        icon = Icons.Default.CloudSync,
                        gradient = listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToVercelSync
                    )
                    DashboardFourCornerCard(
                        title = if (isBn) "সেটিংস ও ব্যাকআপ" else "Settings & Backup",
                        subtitle = if (isBn) "রোল, ভাষা ও ডাটাবেজ ব্যাকআপ" else "Roles, language & backup",
                        badge = "সিস্টেম",
                        icon = Icons.Default.Settings,
                        gradient = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSettings
                    )
                }
            }
        }

        // 11 Core ERP Modules Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth().testTag("erp_modules_hub_card")
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
                                Icon(
                                    Icons.Default.Widgets,
                                    contentDescription = null,
                                    tint = SamityGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBn) "২৮টি কোর মডিউল ও ইআরপি সেবা" else "28 Core ERP Modules & Services",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SamityNavyDark
                                )
                                Text(
                                    text = if (isBn) "সমিতি পরিচালনার সকল ফিচার ও সেবা" else "Complete ERP System Features",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = if (isBn) "২৮টি মডিউল সক্রিয়" else "28 Modules Active",
                                color = SamityNavyPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ErpModuleItem(
                            title = if (isBn) "১. সমিতি ও মাল্টি-টেন্যান্ট ব্যবস্থাপনা" else "1. Samity & Multi-Tenant Setup",
                            subtitle = if (isBn) "১ বা একাধিক সমিতি তৈরি ও পূর্ণ পরিচালনা" else "Create and manage multiple Samities",
                            badge = if (isBn) "সমিতি" else "Samity",
                            icon = Icons.Default.Apartment,
                            iconColor = SamityNavyPrimary,
                            onClick = onNavigateToSamitySetup
                        )
                        ErpModuleItem(
                            title = if (isBn) "২. সদস্য তালিকা ও পূর্ণাঙ্গ যোগাযোগ" else "2. Members & Complete Contact Info",
                            subtitle = if (isBn) "সদস্য যোগ, মোবাইল, হোয়াটসঅ্যাপ, ফেসবুক ও ঠিকানা" else "Add and manage members with all contacts",
                            badge = if (isBn) "সদস্য" else "Members",
                            icon = Icons.Default.People,
                            iconColor = Color(0xFF0284C7),
                            onClick = onNavigateToMembers
                        )
                        ErpModuleItem(
                            title = if (isBn) "৩. মাসিক চাঁদা ও সাবস্ক্রিপশন নির্ধারণ" else "3. Monthly Subscription / চাঁদা Config",
                            subtitle = if (isBn) "প্রত্যেক সদস্যের চাঁদা ও ডিফল্ট রেট সেট করুন" else "Set monthly fee amount for each member",
                            badge = if (isBn) "চাঁদা রেট" else "Fee",
                            icon = Icons.Default.PriceCheck,
                            iconColor = Color(0xFFD97706),
                            onClick = onNavigateToSamitySetup
                        )
                        ErpModuleItem(
                            title = if (isBn) "৪. ব্যাংক, ক্যাশ ও এমএফএস জমা" else "4. Bank, Cash & MFS Deposits",
                            subtitle = if (isBn) "ব্যাংক কাউন্টার, অনলাইন, বিকাশ, নগদ ও বাল্ক স্লিপ" else "Record bank, cash and mobile deposits",
                            badge = if (isBn) "জমা ও বাল্ক" else "Deposits",
                            icon = Icons.Default.AccountBalance,
                            iconColor = SamityEmerald,
                            onClick = onNavigateToDeposits
                        )
                        ErpModuleItem(
                            title = if (isBn) "৫. পরিশোধ ও বকেয়া ট্র্যাকিং" else "5. Payment & Due Tracking",
                            subtitle = if (isBn) "কার পরিশোধিত, কার বকেয়া, কার আংশিক ট্র্যাক" else "Track paid, unpaid & partially paid members",
                            badge = if (isBn) "পরিশোধ স্থিতি" else "Due Track",
                            icon = Icons.Default.FactCheck,
                            iconColor = Color(0xFFDC2626),
                            onClick = onNavigateToPaymentStatus
                        )
                        ErpModuleItem(
                            title = if (isBn) "৬. পেইড মানিসিপ্ট ও প্রিন্ট রসিদ" else "6. Professional Paid Receipts & Print",
                            subtitle = if (isBn) "লোগো, স্লোগান, কিউআর ও অনুমোদিত স্বাক্ষরসহ রসিদ" else "Print receipts with logo, slogan & signatures",
                            badge = if (isBn) "মানিসিপ্ট" else "Receipts",
                            icon = Icons.Default.Receipt,
                            iconColor = Color(0xFF059669),
                            onClick = onNavigateToDeposits
                        )
                        ErpModuleItem(
                            title = if (isBn) "৭. মাল্টি-চ্যানেল বকেয়া রিমাইন্ডার" else "7. Multi-Channel Reminders",
                            subtitle = if (isBn) "WhatsApp, SMS, Telegram ও Messenger-এ নোটিশ" else "Send reminders via WhatsApp, SMS, Telegram, FB",
                            badge = if (isBn) "তাগিদ" else "Remind",
                            icon = Icons.Default.NotificationsActive,
                            iconColor = Color(0xFFEA580C),
                            onClick = onNavigateToReminders
                        )
                        ErpModuleItem(
                            title = if (isBn) "৮. ব্যয়, উপলব্ধ ফান্ড ও ব্যাংক ব্যালেন্স" else "8. Expenses, Net Fund & Bank Balances",
                            subtitle = if (isBn) "খরচ অনুমোদন, নগদ ও ব্যাংকের সমন্বিত ব্যালেন্স" else "Manage expenses, available fund & balances",
                            badge = if (isBn) "ব্যালেন্স" else "Funds",
                            icon = Icons.Default.AccountBalanceWallet,
                            iconColor = Color(0xFF7C3AED),
                            onClick = onNavigateToFundBalance
                        )
                        ErpModuleItem(
                            title = if (isBn) "৯. আর্থিক ও অডিট রিপোর্টস" else "9. Financial & Audit Reports",
                            subtitle = if (isBn) "দৈনিক, মাসিক, বার্ষিক, সদস্য ও ব্যাংক রিপোর্ট" else "Daily, monthly, yearly, member & bank reports",
                            badge = if (isBn) "রিপোর্টস" else "Reports",
                            icon = Icons.Default.Assessment,
                            iconColor = Color(0xFF2563EB),
                            onClick = onNavigateToReports
                        )
                        ErpModuleItem(
                            title = if (isBn) "১০. লেটারহেড ও অফিস নথি জেনারেটর" else "10. Customizable Letterhead & Documents",
                            subtitle = if (isBn) "প্যাড কনফিগারেশন, সদস্যতা সনদ ও সভার নোটিশ প্রিন্ট" else "Create documents with custom letterhead & QR",
                            badge = if (isBn) "লেটারহেড" else "Letterhead",
                            icon = Icons.Default.Description,
                            iconColor = Color(0xFF4F46E5),
                            onClick = onNavigateToDocumentGenerator
                        )
                        ErpModuleItem(
                            title = if (isBn) "১১. অডিট ট্রেইল ও নিরাপত্তা অনুমোদন" else "11. Audit Trail & Role Permissions",
                            subtitle = if (isBn) "অনুমোদন ওয়ার্কফ্লো, রোল ভিত্তিক নিরাপত্তা ও হিস্ট্রি" else "Approval workflow, audit logs & permissions",
                            badge = if (isBn) "অডিট ও সিকিউরিটি" else "Audit",
                            icon = Icons.Default.Security,
                            iconColor = SamityNavyDark,
                            onClick = onNavigateToAuditTrail
                        )
                        ErpModuleItem(
                            title = if (isBn) "১২. লেনদেন অনুমোদন ওয়ার্কফ্লো" else "12. Approval Workflow Hub",
                            subtitle = if (isBn) "জমা ও ব্যয়ের প্রশাসনিক অনুমোদন ও পর্যালোচনা" else "Approve or reject deposits & expenses",
                            badge = if (isBn) "অনুমোদন" else "Approvals",
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFF10B981),
                            onClick = onNavigateToApprovalWorkflow
                        )
                        ErpModuleItem(
                            title = if (isBn) "১৩. ডাটাবেজ ব্যাকআপ, এক্সপোর্ট ও ইমপোর্ট" else "13. Database Backup & Export",
                            subtitle = if (isBn) "সম্পূর্ণ ডাটা JSON ও CSV এক্সপোর্ট এবং ব্যাকআপ" else "Full database JSON & CSV exports",
                            badge = if (isBn) "ব্যাকআপ" else "Backup",
                            icon = Icons.Default.Backup,
                            iconColor = Color(0xFF6366F1),
                            onClick = onNavigateToBackupExport
                        )
                        ErpModuleItem(
                            title = if (isBn) "১৪. সিস্টেম সেটিংস ও বহুভাষিক কনফিগারেশন" else "14. System Settings & Languages",
                            subtitle = if (isBn) "বাংলা/ইংরেজি ভাষা, চাঁদা হার ও সিস্টেম সেটিংস" else "Language, fee config & preferences",
                            badge = if (isBn) "সেটিংস" else "Settings",
                            icon = Icons.Default.Settings,
                            iconColor = Color(0xFF64748B),
                            onClick = onNavigateToSettings
                        )
                        ErpModuleItem(
                            title = if (isBn) "১৫. ভার্সেল ক্লাউড ও ওয়েব API গেটওয়ে" else "15. Vercel Cloud & Web API Gateway",
                            subtitle = if (isBn) "ওয়েবসাইট ও মোবাইলের ডাটা একসাথে স্ক্যান ও সিঙ্ক" else "Bidirectional real-time web parity",
                            badge = if (isBn) "ক্লাউড API" else "Cloud Sync",
                            icon = Icons.Default.CloudSync,
                            iconColor = SamityGold,
                            onClick = onNavigateToVercelSync
                        )
                    }
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

@Composable
private fun ErpModuleItem(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyDark
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = iconColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = iconColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun DashboardFourCornerCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.88f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun QuadrantFeatureCard(
    quadrantLabel: String,
    title: String,
    subtitle: String,
    footerMetric: String,
    badgeText: String,
    actionCue: String,
    icon: ImageVector,
    gradient: List<Color>,
    testTag: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp, pressedElevation = 8.dp),
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .background(Brush.linearGradient(gradient))
                .padding(14.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Custom Icon Box + Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.28f)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Middle: Quadrant Label + Bold Title + Subtitle
                Column {
                    Text(
                        text = quadrantLabel.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = title,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 10.5.sp,
                        color = Color.White.copy(alpha = 0.88f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bottom: Metric Footer + Action Cue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = footerMetric,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = actionCue,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
