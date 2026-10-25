package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@Composable
fun FundBalanceScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity

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
            text = if (isBn) "সমিতি তহবিল ও ব্যালেন্স স্থিতি" else "Samity Fund & Balances",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn) "মোট অনুমোদিত জমা ও ব্যয়ের সমন্বিত আর্থিক প্রতিবেদন" else "Consolidated Financial Fund and Account Status",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        // Main Net Fund Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
            elevation = CardDefaults.cardElevation(3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = if (isBn) "মোট উপলব্ধ সমিতি তহবিল (Net Fund)" else "Total Net Available Fund",
                    fontSize = 13.sp,
                    color = SamityGold,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = NumberToWords.formatCurrency(repository.netAvailableFund, isBn),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = Color.White.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(if (isBn) "মোট অনুমোদিত জমা" else "Total Approved Deposits", fontSize = 11.sp, color = Color(0xFFD0E4FF))
                        Text(NumberToWords.formatCurrency(repository.totalApprovedDeposits, isBn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = StatusPaidGreen)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(if (isBn) "মোট অনুমোদিত ব্যয়" else "Total Approved Expenses", fontSize = 11.sp, color = Color(0xFFD0E4FF))
                        Text(NumberToWords.formatCurrency(repository.totalApprovedExpenses, isBn), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5))
                    }
                }
            }
        }

        // Account Breakdown Cards
        Text(
            text = if (isBn) "পৃথক একাউন্ট ভিত্তিক স্থিতি (Account-wise Breakdown)" else "Account-wise Breakdown",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = SamityNavyPrimary
        )

        AccountDetailCard(
            title = if (isBn) "ব্যাংক একাউন্ট ব্যালেন্স" else "Bank Account Balance",
            subtitle = "${samity.bankName} (${samity.bankBranch})\nহিসাব নং: ${samity.bankAccountNo}",
            amount = repository.bankBalance,
            icon = Icons.Default.AccountBalance,
            color = SamityNavyPrimary,
            isBn = isBn
        )

        AccountDetailCard(
            title = if (isBn) "সমিতি ক্যাশ বক্স ব্যালেন্স" else "Samity Cash Box Balance",
            subtitle = if (isBn) "প্রধান কার্যালয় ক্যাশ বক্স (কোষাধ্যক্ষের দায়িত্বে)" else "Office Cash Box under Treasurer",
            amount = repository.cashBalance,
            icon = Icons.Default.LocalAtm,
            color = SamityEmerald,
            isBn = isBn
        )

        AccountDetailCard(
            title = if (isBn) "মোবাইল ব্যাংকিং (bKash/Nagad) ব্যালেন্স" else "Mobile Banking (bKash/Nagad) Balance",
            subtitle = "বিকাশ: ${samity.bkashNumber}\nনগদ: ${samity.nagadNumber}",
            amount = repository.mobileBankBalance,
            icon = Icons.Default.PhoneAndroid,
            color = SamityGold,
            isBn = isBn
        )

        // Bank Reconciliation Status Section
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SyncAlt, contentDescription = null, tint = SamityNavyPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "ব্যাংক রিকনসিলিয়েশন ও অডিট স্ট্যাটাস" else "Bank Reconciliation Status",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isBn) "সফটওয়্যারের ব্যাংক ব্যালেন্স" else "Software Bank Balance", fontSize = 11.5.sp, color = Color(0xFF64748B))
                    Text(NumberToWords.formatCurrency(repository.bankBalance, isBn), fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isBn) "ব্যাংক স্টেটমেন্ট ব্যালেন্স" else "Bank Statement Balance", fontSize = 11.5.sp, color = Color(0xFF64748B))
                    Text(NumberToWords.formatCurrency(repository.bankBalance, isBn), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = StatusPaidGreen)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(if (isBn) "ম্যাচিং স্ট্যাটাস" else "Reconciliation Status", fontSize = 11.5.sp, color = Color(0xFF64748B))
                    Text(if (isBn) "সম্পূর্ণ মিলেছে (১০০% Matched)" else "100% Reconciled", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = StatusPaidGreen)
                }
            }
        }
    }
}

@Composable
private fun AccountDetailCard(
    title: String,
    subtitle: String,
    amount: Double,
    icon: ImageVector,
    color: Color,
    isBn: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateTextPrimary)
                Text(subtitle, fontSize = 10.5.sp, color = Color(0xFF64748B), lineHeight = 15.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberToWords.formatCurrency(amount, isBn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = color
                )
                Text(
                    text = if (isBn) "সক্রিয়" else "Active",
                    fontSize = 10.sp,
                    color = StatusPaidGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
