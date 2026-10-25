package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.auth.AppPermission
import com.example.data.model.Deposit
import com.example.data.model.Expense
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovalWorkflowScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    var selectedTab by remember { mutableStateOf("DEPOSITS") } // DEPOSITS, EXPENSES
    var statusFilter by remember { mutableStateOf("ALL") } // ALL, APPROVED, PENDING, REJECTED

    val deposits = repository.activeDeposits
    val expenses = repository.activeExpenses

    val filteredDeposits = remember(deposits, statusFilter) {
        when (statusFilter) {
            "APPROVED" -> deposits.filter { it.approvalStatus == "Approved" }
            "PENDING" -> deposits.filter { it.approvalStatus == "Pending" }
            "REJECTED" -> deposits.filter { it.approvalStatus == "Rejected" }
            else -> deposits
        }
    }

    val filteredExpenses = remember(expenses, statusFilter) {
        when (statusFilter) {
            "APPROVED" -> expenses.filter { it.approvalStatus == "Approved" }
            "PENDING" -> expenses.filter { it.approvalStatus == "Pending" }
            "REJECTED" -> expenses.filter { it.approvalStatus == "Rejected" }
            else -> expenses
        }
    }

    val canApprove = repository.userSession.canPerform(AppPermission.APPROVE_TRANSACTION, repository.activeSamityId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "অনুমোদন ওয়ার্কফ্লো (Approval Hub)" else "Approval Workflow Hub",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "জমা ও ব্যয়ের প্রশাসনিক অনুমোদন ও স্বচ্ছতা" else "Approve/Reject Deposits & Expenses",
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
                .padding(16.dp)
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(if (isBn) "অনুমোদিত জমা" else "Approved Deposits", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            NumberToWords.formatCurrency(repository.totalApprovedDeposits, isBn),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusPaidGreen
                        )
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(if (isBn) "অনুমোদিত ব্যয়" else "Approved Expenses", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            NumberToWords.formatCurrency(repository.totalApprovedExpenses, isBn),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = StatusPartialAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Category Tabs (Deposits vs Expenses)
            TabRow(
                selectedTabIndex = if (selectedTab == "DEPOSITS") 0 else 1,
                containerColor = Color.White,
                contentColor = SamityNavyPrimary,
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == "DEPOSITS",
                    onClick = { selectedTab = "DEPOSITS" },
                    text = {
                        Text(
                            text = "${if (isBn) "জমা অনুমোদন" else "Deposits"} (${deposits.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
                Tab(
                    selected = selectedTab == "EXPENSES",
                    onClick = { selectedTab = "EXPENSES" },
                    text = {
                        Text(
                            text = "${if (isBn) "ব্যয় অনুমোদন" else "Expenses"} (${expenses.size})",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = statusFilter == "ALL",
                    onClick = { statusFilter = "ALL" },
                    label = { Text(if (isBn) "সকল" else "All", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == "APPROVED",
                    onClick = { statusFilter = "APPROVED" },
                    label = { Text(if (isBn) "অনুমোদিত" else "Approved", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == "PENDING",
                    onClick = { statusFilter = "PENDING" },
                    label = { Text(if (isBn) "অপেক্ষমাণ" else "Pending", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = statusFilter == "REJECTED",
                    onClick = { statusFilter = "REJECTED" },
                    label = { Text(if (isBn) "বাতিলকৃত" else "Rejected", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Items List
            if (selectedTab == "DEPOSITS") {
                if (filteredDeposits.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (isBn) "কোন জমা পাওয়া যায়নি" else "No deposits found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredDeposits) { dep ->
                            DepositApprovalCard(
                                deposit = dep,
                                isBn = isBn,
                                canApprove = canApprove,
                                onApprove = {
                                    val ok = repository.approveDeposit(dep.id)
                                    if (ok) Toast.makeText(context, if (isBn) "অনুমোদন সম্পন্ন" else "Approved", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    val ok = repository.rejectDeposit(dep.id, "অসঙ্গতিপূর্ণ তথ্য")
                                    if (ok) Toast.makeText(context, if (isBn) "বাতিল করা হয়েছে" else "Rejected", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            } else {
                if (filteredExpenses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(if (isBn) "কোন ব্যয় পাওয়া যায়নি" else "No expenses found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredExpenses) { exp ->
                            ExpenseApprovalCard(
                                expense = exp,
                                isBn = isBn,
                                canApprove = canApprove,
                                onApprove = {
                                    val ok = repository.approveExpense(exp.id)
                                    if (ok) Toast.makeText(context, if (isBn) "অনুমোদন সম্পন্ন" else "Approved", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    val ok = repository.rejectExpense(exp.id, "অতিরিক্ত খরচ")
                                    if (ok) Toast.makeText(context, if (isBn) "বাতিল করা হয়েছে" else "Rejected", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositApprovalCard(
    deposit: Deposit,
    isBn: Boolean,
    canApprove: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                Column {
                    Text(deposit.memberName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${deposit.memberCode} • ${deposit.voucherNo}", fontSize = 11.sp, color = Color.Gray)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (deposit.approvalStatus) {
                        "Approved" -> Color(0xFFDCFCE7)
                        "Rejected" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = when (deposit.approvalStatus) {
                            "Approved" -> if (isBn) "অনুমোদিত" else "Approved"
                            "Rejected" -> if (isBn) "বাতিল" else "Rejected"
                            else -> if (isBn) "অপেক্ষমাণ" else "Pending"
                        },
                        color = when (deposit.approvalStatus) {
                            "Approved" -> StatusPaidGreen
                            "Rejected" -> StatusUnpaidRed
                            else -> Color(0xFFD97706)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${deposit.depositMethod} • ${deposit.depositDate}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = NumberToWords.formatCurrency(deposit.amount, isBn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary
                )
            }

            if (canApprove && deposit.approvalStatus != "Approved") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusUnpaidRed),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "বাতিল" else "Reject", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusPaidGreen),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "অনুমোদন" else "Approve", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseApprovalCard(
    expense: Expense,
    isBn: Boolean,
    canApprove: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
                Column {
                    Text(expense.titleBn, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("${expense.category} • ${expense.voucherNo}", fontSize = 11.sp, color = Color.Gray)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (expense.approvalStatus) {
                        "Approved" -> Color(0xFFDCFCE7)
                        "Rejected" -> Color(0xFFFEE2E2)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = when (expense.approvalStatus) {
                            "Approved" -> if (isBn) "অনুমোদিত" else "Approved"
                            "Rejected" -> if (isBn) "বাতিল" else "Rejected"
                            else -> if (isBn) "অপেক্ষমাণ" else "Pending"
                        },
                        color = when (expense.approvalStatus) {
                            "Approved" -> StatusPaidGreen
                            "Rejected" -> StatusUnpaidRed
                            else -> Color(0xFFD97706)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${expense.paymentMethod} • প্রাপক: ${expense.paidTo}",
                    fontSize = 11.sp,
                    color = Color(0xFF475569)
                )
                Text(
                    text = NumberToWords.formatCurrency(expense.amount, isBn),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = StatusPartialAmber
                )
            }

            if (canApprove && expense.approvalStatus != "Approved") {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusUnpaidRed),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "বাতিল" else "Reject", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onApprove,
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusPaidGreen),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "অনুমোদন" else "Approve", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
