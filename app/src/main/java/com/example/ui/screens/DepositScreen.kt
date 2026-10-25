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
import androidx.compose.ui.window.Dialog
import com.example.data.auth.AppPermission
import com.example.data.model.Deposit
import com.example.data.model.Member
import com.example.data.repository.SamityRepository
import com.example.ui.components.PermissionGate
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositScreen(
    repository: SamityRepository,
    initialMember: Member? = null,
    onViewReceipt: (Deposit) -> Unit,
    onNavigateToBulkDeposit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    var showNewDepositDialog by remember { mutableStateOf(initialMember != null) }
    var selectedMemberForDeposit by remember { mutableStateOf(initialMember) }
    var searchQuery by remember { mutableStateOf("") }

    val deposits = repository.activeDeposits
    val filteredDeposits = deposits.filter {
        it.memberName.contains(searchQuery, ignoreCase = true) ||
                it.voucherNo.contains(searchQuery, ignoreCase = true) ||
                it.bankReceiptNo.contains(searchQuery, ignoreCase = true) ||
                it.memberCode.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            PermissionGate(
                permission = AppPermission.CREATE_DEPOSIT,
                userSession = repository.userSession,
                targetSamityId = repository.activeSamityId
            ) {
                ExtendedFloatingActionButton(
                    onClick = {
                        selectedMemberForDeposit = null
                        showNewDepositDialog = true
                    },
                    containerColor = SamityNavyPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(LanguageManager.btnNewDeposit, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("add_deposit_fab")
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Header with Bulk Deposit Navigation Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "ব্যাংক ও চাঁদা জমা রেজিস্টার" else "Bank & Subscription Deposits",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Text(
                        text = "${if (isBn) "মোট জমা রেকর্ড: " else "Total Records: "}${NumberToWords.formatNumber(deposits.size, isBn)} টি",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                PermissionGate(
                    permission = AppPermission.CREATE_BULK_DEPOSIT,
                    userSession = repository.userSession,
                    targetSamityId = repository.activeSamityId
                ) {
                    FilledTonalButton(
                        onClick = onNavigateToBulkDeposit,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SamityEmerald.copy(alpha = 0.15f),
                            contentColor = SamityEmerald
                        ),
                        modifier = Modifier.testTag("nav_bulk_deposit_btn")
                    ) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "বাল্ক ব্যাংক স্লিপ" else "Bulk Bank Slip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isBn) "ভাউচার নং, রিসিট নং বা সদস্য নাম খুঁজুন..." else "Search voucher, receipt or member...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SamityNavyPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SamityNavyPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Total Approved Summary Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isBn) "মোট অনুমোদিত ব্যাংক জমা" else "Total Approved Deposits",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = NumberToWords.formatCurrency(repository.totalApprovedDeposits, isBn),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusPaidGreen
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = StatusPaidBg
                    ) {
                        Text(
                            text = if (isBn) "অনুমোদিত" else "Approved",
                            color = StatusPaidGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deposits List
            if (filteredDeposits.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (isBn) "কোন জমার তথ্য পাওয়া যায়নি" else "No deposit records found",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredDeposits) { deposit ->
                        DepositCardItem(
                            deposit = deposit,
                            isBn = isBn,
                            onViewReceipt = { onViewReceipt(deposit) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // New Deposit Dialog
    if (showNewDepositDialog) {
        NewDepositDialog(
            repository = repository,
            preSelectedMember = selectedMemberForDeposit,
            isBn = isBn,
            onDismiss = { showNewDepositDialog = false },
            onSave = { newDep ->
                repository.addDeposit(newDep)
                showNewDepositDialog = false
                onViewReceipt(newDep)
            }
        )
    }
}

@Composable
private fun DepositCardItem(
    deposit: Deposit,
    isBn: Boolean,
    onViewReceipt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SamityNavyPrimary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = deposit.voucherNo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityNavyPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (deposit.isBulkItem) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SamityEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isBn) "বাল্ক স্লিপ" else "Bulk Slip",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityEmerald,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = deposit.depositDate,
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deposit.memberName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "${deposit.memberCode} • ${deposit.depositType} (${deposit.paymentMonth})",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "${deposit.bankName} • ${deposit.depositMethod}${if (deposit.bankReceiptNo.isNotBlank()) " • রিসিট নং: ${deposit.bankReceiptNo}" else ""}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = NumberToWords.formatCurrency(deposit.amount, isBn),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = StatusPaidGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = onViewReceipt,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp).testTag("print_receipt_btn_${deposit.voucherNo}")
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "রিসিট" else "Receipt", fontSize = 10.sp, color = SamityNavyPrimary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewDepositDialog(
    repository: SamityRepository,
    preSelectedMember: Member?,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSave: (Deposit) -> Unit
) {
    val members = repository.activeMembers
    val activeSamity = repository.activeSamity

    var selectedMember by remember { mutableStateOf(preSelectedMember ?: members.firstOrNull()) }
    var amountStr by remember { mutableStateOf(selectedMember?.monthlyFee?.toInt()?.toString() ?: "5000") }
    var depositType by remember { mutableStateOf("মাসিক চাঁদা") }
    var depositMethod by remember { mutableStateOf("Bank Counter") }
    var bankName by remember { mutableStateOf(activeSamity.bankName) }
    var bankBranch by remember { mutableStateOf(activeSamity.bankBranch) }
    var bankReceiptNo by remember { mutableStateOf("DBBL-${(100000..999999).random()}") }
    var transactionId by remember { mutableStateOf("TXN-${(1000..9999).random()}") }
    var paymentMonth by remember { mutableStateOf("অক্টোবর ২০২৬") }
    var remarks by remember { mutableStateOf("") }

    val nextVoucher = "RCPT-2026-${String.format("%06d", repository.activeDeposits.size + 1)}"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (isBn) "নতুন ব্যাংক জমা এন্ট্রি ($nextVoucher)" else "New Bank Deposit Entry ($nextVoucher)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SamityNavyPrimary
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }

                // Member Selector
                item {
                    Text(
                        text = if (isBn) "সদস্য নির্বাচন করুন *" else "Select Member *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    var memberDropdownExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedCard(
                            onClick = { memberDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth().testTag("select_member_dropdown")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedMember?.let { "${it.nameBn} (${it.memberCode})" } ?: if (isBn) "সদস্য নির্বাচন করুন" else "Select Member",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = memberDropdownExpanded,
                            onDismissRequest = { memberDropdownExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text("${m.nameBn} (${m.memberCode}) - ${m.phone}") },
                                    onClick = {
                                        selectedMember = m
                                        amountStr = m.monthlyFee.toInt().toString()
                                        memberDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Amount
                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text(if (isBn) "জমার পরিমাণ (টাকা) *" else "Deposit Amount (BDT) *") },
                        modifier = Modifier.fillMaxWidth().testTag("deposit_amount_input"),
                        singleLine = true
                    )
                }

                // Deposit Type
                item {
                    OutlinedTextField(
                        value = depositType,
                        onValueChange = { depositType = it },
                        label = { Text(if (isBn) "জমার ধরন" else "Deposit Type") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Deposit Method
                item {
                    Text(if (isBn) "জমার মাধ্যম" else "Payment Method", fontSize = 11.sp, color = Color(0xFF475569))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Bank Counter", "bKash", "Nagad", "Cash").forEach { method ->
                            FilterChip(
                                selected = depositMethod == method,
                                onClick = { depositMethod = method },
                                label = { Text(method, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                // Bank Receipt No
                item {
                    OutlinedTextField(
                        value = bankReceiptNo,
                        onValueChange = { bankReceiptNo = it },
                        label = { Text(if (isBn) "ব্যাংক রিসিট / স্লিপ নম্বর" else "Bank Receipt / Slip No") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Month
                item {
                    OutlinedTextField(
                        value = paymentMonth,
                        onValueChange = { paymentMonth = it },
                        label = { Text(if (isBn) "চাঁদার মাস ও বছর" else "Payment Month & Year") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Remarks
                item {
                    OutlinedTextField(
                        value = remarks,
                        onValueChange = { remarks = it },
                        label = { Text(if (isBn) "মন্তব্য (ঐচ্ছিক)" else "Remarks (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(LanguageManager.btnCancel, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val mem = selectedMember
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (mem != null && amt > 0) {
                                    val newDeposit = Deposit(
                                        id = UUID.randomUUID().toString(),
                                        samityId = activeSamity.id,
                                        voucherNo = nextVoucher,
                                        depositDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
                                        memberId = mem.id,
                                        memberName = mem.nameBn,
                                        memberCode = mem.memberCode,
                                        depositType = depositType,
                                        depositMethod = depositMethod,
                                        amount = amt,
                                        bankName = if (depositMethod == "bKash") "bKash Merchant" else if (depositMethod == "Cash") "অফিস ক্যাশ" else bankName,
                                        bankBranch = bankBranch,
                                        bankAccountNo = activeSamity.bankAccountNo,
                                        bankReceiptNo = bankReceiptNo,
                                        transactionId = transactionId,
                                        paymentMonth = paymentMonth,
                                        paymentYear = 2026,
                                        approvalStatus = "Approved",
                                        remarks = remarks,
                                        entryBy = repository.currentUserRole.titleBn
                                    )
                                    onSave(newDeposit)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                            modifier = Modifier.testTag("save_deposit_btn")
                        ) {
                            Text(if (isBn) "সংরক্ষণ ও রিসিট প্রিন্ট" else "Save & Print Receipt", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
