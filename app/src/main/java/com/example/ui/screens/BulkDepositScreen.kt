package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BulkDeposit
import com.example.data.model.BulkDepositItem
import com.example.data.model.Member
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkDepositScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    onViewReceipts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val samity = repository.activeSamity
    val members = repository.activeMembers

    var bankReceiptNo by remember { mutableStateOf("DBBL-${(100000..999999).random()}") }
    var bankName by remember { mutableStateOf(samity.bankName) }
    var branchName by remember { mutableStateOf(samity.bankBranch) }
    var accountNo by remember { mutableStateOf(samity.bankAccountNo) }
    var depositDate by remember { mutableStateOf(SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())) }
    var depositorName by remember { mutableStateOf(samity.treasurerName) }
    var depositorPhone by remember { mutableStateOf(samity.treasurerPhone) }
    // List of member items inside this single bank slip
    val itemsList = remember {
        mutableStateListOf<BulkDepositItem>().apply {
            members.take(3).forEachIndexed { idx, m ->
                add(
                    BulkDepositItem(
                        memberId = m.id,
                        memberCode = m.memberCode,
                        memberName = m.nameBn,
                        phone = m.phone,
                        amount = m.monthlyFee,
                        paymentMonth = "অক্টোবর ২০২৬",
                        voucherNo = "RCPT-BULK-${String.format("%03d", idx + 1)}"
                    )
                )
            }
        }
    }
    var totalBankAmountStr by remember { mutableStateOf(itemsList.sumOf { it.amount }.toInt().toString()) }

    val totalEnteredInSlip = totalBankAmountStr.toDoubleOrNull() ?: 0.0
    val totalSumOfMembers = itemsList.sumOf { it.amount }
    val isAmountMatched = totalEnteredInSlip > 0 && totalSumOfMembers == totalEnteredInSlip

    var showSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isBn) "বাল্ক ব্যাংক জমা স্লিপ এন্ট্রি" else "Bulk Bank Deposit Slip Entry",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Rule / Guideline Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isBn)
                                "একটি ব্যাংক জমার রিসিট স্লিপের অধীনে একাধিক সদস্যের চাঁদার টাকা এন্ট্রি করুন। সদস্যদের মোট টাকা ও মূল ব্যাংক স্লিপের টাকা সমান হতে হবে।"
                            else
                                "Record multiple members' payments under a single bank deposit slip. Total member allocation must match the total bank receipt amount.",
                            fontSize = 11.5.sp,
                            color = SamityNavyPrimary
                        )
                    }
                }
            }

            // Bank Slip Details Form
            item {
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
                            text = if (isBn) "১. ব্যাংক স্লিপ তথ্য (Bank Slip Header)" else "1. Bank Slip Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = SamityNavyPrimary
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = bankReceiptNo,
                                onValueChange = { bankReceiptNo = it },
                                label = { Text(if (isBn) "ব্যাংক রিসিট নং *" else "Bank Receipt No *") },
                                modifier = Modifier.weight(1f).testTag("bulk_receipt_no_input"),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = totalBankAmountStr,
                                onValueChange = { totalBankAmountStr = it },
                                label = { Text(if (isBn) "মোট স্লিপ টাকা *" else "Total Slip Amount *") },
                                modifier = Modifier.weight(1f).testTag("bulk_total_amount_input"),
                                singleLine = true
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = bankName,
                                onValueChange = { bankName = it },
                                label = { Text(if (isBn) "ব্যাংকের নাম" else "Bank Name") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = branchName,
                                onValueChange = { branchName = it },
                                label = { Text(if (isBn) "শাখা" else "Branch") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = depositorName,
                                onValueChange = { depositorName = it },
                                label = { Text(if (isBn) "জমাদানকারীর নাম" else "Depositor Name") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = depositDate,
                                onValueChange = { depositDate = it },
                                label = { Text(if (isBn) "জমার তারিখ" else "Deposit Date") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // Member-wise Allocations Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "২. সদস্য অনুযায়ী চাঁদা বণ্টন (${NumberToWords.formatNumber(itemsList.size, isBn)} জন)" else "2. Member-wise Split (${itemsList.size} members)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )

                    FilledTonalButton(
                        onClick = {
                            val availableMember = members.firstOrNull { m -> itemsList.none { it.memberId == m.id } } ?: members.first()
                            itemsList.add(
                                BulkDepositItem(
                                    memberId = availableMember.id,
                                    memberCode = availableMember.memberCode,
                                    memberName = availableMember.nameBn,
                                    phone = availableMember.phone,
                                    amount = 5000.0,
                                    paymentMonth = "অক্টোবর ২০২৬",
                                    voucherNo = "RCPT-BULK-${String.format("%03d", itemsList.size + 1)}"
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_bulk_row_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "+ সদস্য যোগ করুন" else "+ Add Member", fontSize = 11.sp)
                    }
                }
            }

            // Items List
            itemsIndexed(itemsList) { index, item ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SamityNavyPrimary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${item.memberName} (${item.memberCode})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${item.phone} • ${item.paymentMonth}",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        OutlinedTextField(
                            value = item.amount.toInt().toString(),
                            onValueChange = { newVal ->
                                val amt = newVal.toDoubleOrNull() ?: 0.0
                                itemsList[index] = item.copy(amount = amt)
                            },
                            label = { Text(if (isBn) "টাকা" else "Amount", fontSize = 10.sp) },
                            modifier = Modifier.width(90.dp),
                            singleLine = true
                        )

                        IconButton(
                            onClick = {
                                if (itemsList.size > 1) {
                                    itemsList.removeAt(index)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = StatusUnpaidRed)
                        }
                    }
                }
            }

            // Validation Status Box
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isAmountMatched) StatusPaidBg else StatusUnpaidBg,
                    border = BorderStroke(1.dp, if (isAmountMatched) StatusPaidGreen else StatusUnpaidRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBn) "ব্যাংক স্লিপের মূল টাকা:" else "Total Slip Amount:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = NumberToWords.formatCurrency(totalEnteredInSlip, isBn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBn) "সদস্যদের বণ্টনের যোগফল:" else "Sum of Member Splits:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = NumberToWords.formatCurrency(totalSumOfMembers, isBn),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAmountMatched) StatusPaidGreen else StatusUnpaidRed
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (isAmountMatched) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusPaidGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "সঠিক হিসাব: ব্যাংকের স্লিপের টাকার সাথে সদস্যদের টাকা মিলেছে।" else "Match Validated: Bank slip total matches member splits perfectly.",
                                    fontSize = 11.sp,
                                    color = StatusPaidGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            val diff = (totalEnteredInSlip - totalSumOfMembers)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = StatusUnpaidRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isBn) "পার্থক্য: ${NumberToWords.formatCurrency(diff, isBn)} টাকা অমিল রয়েছে!" else "Mismatch: ${NumberToWords.formatCurrency(diff, isBn)} difference!",
                                    fontSize = 11.sp,
                                    color = StatusUnpaidRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (bankReceiptNo.isNotBlank() && totalEnteredInSlip > 0 && itemsList.isNotEmpty()) {
                            val bulk = BulkDeposit(
                                id = UUID.randomUUID().toString(),
                                samityId = samity.id,
                                bankReceiptNo = bankReceiptNo,
                                bankName = bankName,
                                branchName = branchName,
                                accountNo = accountNo,
                                depositDate = depositDate,
                                totalAmount = totalSumOfMembers,
                                depositorName = depositorName,
                                depositorPhone = depositorPhone,
                                status = "Approved",
                                items = itemsList.toList()
                            )
                            repository.addBulkDeposit(bulk)
                            showSuccessDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_bulk_deposit_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isAmountMatched) SamityNavyPrimary else SamityGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "বাল্ক ব্যাংক জমা স্লিপ সংরক্ষণ করুন" else "Save Bulk Bank Slip & Generate Receipts",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text(if (isBn) "বাল্ক ব্যাংক জমা সফল হয়েছে!" else "Bulk Deposit Recorded Successfully!") },
            text = {
                Text(
                    if (isBn)
                        "ব্যাংক স্লিপ নং: $bankReceiptNo এর অধীনে ${itemsList.size} জন সদস্যের জমা অনুমোদিত হয়েছে এবং পৃথক রিসিট তৈরি হয়েছে।"
                    else
                        "Bank slip $bankReceiptNo saved with ${itemsList.size} member receipts generated."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onViewReceipts()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary)
                ) {
                    Text(if (isBn) "রিসিট তালিকা দেখুন" else "View Receipts")
                }
            }
        )
    }
}
