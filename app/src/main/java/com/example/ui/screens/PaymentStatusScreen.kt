package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Deposit
import com.example.data.model.Member
import com.example.data.model.PaymentStatusType
import com.example.data.repository.SamityRepository
import com.example.ui.components.StatusBadge
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@Composable
fun PaymentStatusScreen(
    repository: SamityRepository,
    onRecordDeposit: (Member) -> Unit,
    onSendReminder: (Member) -> Unit,
    onViewDueNotice: (Member) -> Unit,
    onViewReceipt: (Deposit) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val members = repository.activeMembers
    val deposits = repository.activeDeposits

    var statusFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = members.filter { mem ->
        val status = when {
            mem.currentMonthPaid >= mem.monthlyFee && mem.monthlyFee > 0 -> PaymentStatusType.PAID
            mem.currentMonthPaid > 0 -> PaymentStatusType.PARTIAL
            mem.totalDue > 0 -> PaymentStatusType.UNPAID
            else -> PaymentStatusType.PAID
        }

        val matchesFilter = when (statusFilter) {
            "PAID" -> status == PaymentStatusType.PAID
            "UNPAID" -> status == PaymentStatusType.UNPAID
            "PARTIAL" -> status == PaymentStatusType.PARTIAL
            "ADVANCE" -> mem.advanceBalance > 0
            else -> true
        }

        val matchesSearch = mem.nameBn.contains(searchQuery, ignoreCase = true) ||
                mem.memberCode.contains(searchQuery, ignoreCase = true) ||
                mem.phone.contains(searchQuery)

        matchesFilter && matchesSearch
    }

    val totalExpected = members.sumOf { it.monthlyFee }
    val totalCollected = members.sumOf { it.currentMonthPaid }
    val totalDue = members.sumOf { it.totalDue }
    val collectionPercent = if (totalExpected > 0) ((totalCollected / totalExpected) * 100).toInt() else 0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isBn) "সদস্য চাঁদা ও বকেয়া স্থিতি ড্যাশবোর্ড" else "Subscription & Due Status",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary
                )
                Text(
                    text = if (isBn) "মাস: অক্টোবর ২০২৬ • জমার শেষ তারিখ: ১০ অক্টোবর" else "Month: October 2026 • Deadline: 10th",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SamityNavyPrimary
            ) {
                Text(
                    text = "${if (isBn) "আদায়: " else "Col: "}${NumberToWords.formatNumber(collectionPercent, isBn)}%",
                    color = SamityGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Summary Bar (Expected, Collected, Due)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusStatItem(
                label = if (isBn) "প্রত্যাশিত" else "Expected",
                value = NumberToWords.formatCurrency(totalExpected, isBn),
                color = SamityNavyPrimary,
                modifier = Modifier.weight(1f)
            )
            StatusStatItem(
                label = if (isBn) "মোট আদায়" else "Collected",
                value = NumberToWords.formatCurrency(totalCollected, isBn),
                color = StatusPaidGreen,
                modifier = Modifier.weight(1f)
            )
            StatusStatItem(
                label = if (isBn) "মোট বকেয়া" else "Total Due",
                value = NumberToWords.formatCurrency(totalDue, isBn),
                color = StatusUnpaidRed,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().testTag("payment_status_search"),
            placeholder = { Text(if (isBn) "সদস্যের নাম বা আইডি খুঁজুন..." else "Search member name or ID...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SamityNavyPrimary) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text(if (isBn) "সকল (${NumberToWords.formatNumber(members.size, isBn)})" else "All (${members.size})", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "PAID",
                onClick = { statusFilter = "PAID" },
                label = { Text(if (isBn) "পরিশোধিত" else "Paid", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "UNPAID",
                onClick = { statusFilter = "UNPAID" },
                label = { Text(if (isBn) "বকেয়া" else "Unpaid", fontSize = 11.sp) }
            )
            FilterChip(
                selected = statusFilter == "PARTIAL",
                onClick = { statusFilter = "PARTIAL" },
                label = { Text(if (isBn) "আংশিক" else "Partial", fontSize = 11.sp) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Member Status List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredList) { member ->
                val status = when {
                    member.currentMonthPaid >= member.monthlyFee && member.monthlyFee > 0 -> PaymentStatusType.PAID
                    member.currentMonthPaid > 0 -> PaymentStatusType.PARTIAL
                    member.totalDue > 0 -> PaymentStatusType.UNPAID
                    else -> PaymentStatusType.PAID
                }

                val linkedDeposit = deposits.firstOrNull { it.memberId == member.id }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = member.nameBn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFFF1F5F9)
                                    ) {
                                        Text(
                                            text = member.memberCode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SamityNavyPrimary,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${member.phone} • ${member.membershipType}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            StatusBadge(status = status)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Amount Breakdown Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${if (isBn) "ধার্য চাঁদা: " else "Required: "}${NumberToWords.formatCurrency(member.monthlyFee, isBn)}",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "${if (isBn) "জমা: " else "Paid: "}${NumberToWords.formatCurrency(member.currentMonthPaid, isBn)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusPaidGreen
                            )
                            Text(
                                text = "${if (isBn) "বকেয়া: " else "Due: "}${NumberToWords.formatCurrency(member.totalDue, isBn)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (member.totalDue > 0) StatusUnpaidRed else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Action Buttons Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (status == PaymentStatusType.UNPAID || status == PaymentStatusType.PARTIAL) {
                                OutlinedButton(
                                    onClick = { onViewDueNotice(member) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Article, contentDescription = null, tint = StatusUnpaidRed, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "বকেয়া নোটিশ" else "Due Notice", fontSize = 10.sp, color = StatusUnpaidRed)
                                }
                                Spacer(modifier = Modifier.width(6.dp))

                                Button(
                                    onClick = { onSendReminder(member) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SamityGold),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = null, tint = SamityNavyDark, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "রিমাইন্ডার" else "Reminder", fontSize = 10.sp, color = SamityNavyDark, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            if (linkedDeposit != null) {
                                OutlinedButton(
                                    onClick = { onViewReceipt(linkedDeposit) },
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Print, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "রিসিট প্রিন্ট" else "Print Receipt", fontSize = 10.sp, color = SamityNavyPrimary)
                                }
                            } else {
                                Button(
                                    onClick = { onRecordDeposit(member) },
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isBn) "জমা দিন" else "Pay", fontSize = 10.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusStatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 10.sp, color = Color(0xFF64748B))
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
