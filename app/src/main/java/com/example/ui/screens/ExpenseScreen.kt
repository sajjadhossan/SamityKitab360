package com.example.ui.screens

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
import com.example.data.auth.AppPermission
import com.example.data.model.Expense
import com.example.data.repository.SamityRepository
import com.example.ui.components.PermissionGate
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    repository: SamityRepository,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val expenses = repository.activeExpenses
    val filteredExpenses = expenses.filter {
        it.titleBn.contains(searchQuery, ignoreCase = true) ||
                it.titleEn.contains(searchQuery, ignoreCase = true) ||
                it.voucherNo.contains(searchQuery, ignoreCase = true) ||
                it.category.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        floatingActionButton = {
            PermissionGate(
                permission = AppPermission.CREATE_EXPENSE,
                userSession = repository.userSession,
                targetSamityId = repository.activeSamityId
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showAddExpenseDialog = true },
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text(LanguageManager.btnNewExpense, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("add_expense_fab")
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
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
                        text = if (isBn) "সমিতি ব্যয় ও খরচ রেজিস্টার" else "Samity Expense Register",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Text(
                        text = "${if (isBn) "মোট ব্যয় ভাউচার: " else "Total Vouchers: "}${NumberToWords.formatNumber(expenses.size, isBn)} টি",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusUnpaidBg
                ) {
                    Text(
                        text = "${if (isBn) "মোট: " else "Total: "}${NumberToWords.formatCurrency(repository.totalApprovedExpenses, isBn)}",
                        color = StatusUnpaidRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isBn) "ভাউচার নং বা ব্যয়ের বিবরণ খুঁজুন..." else "Search voucher or title...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SamityNavyPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Expenses List
            if (filteredExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (isBn) "কোন ব্যয়ের রেকর্ড পাওয়া যায়নি" else "No expense records found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExpenses) { exp ->
                        ExpenseItemCard(expense = exp, isBn = isBn)
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            nextVoucher = "EXP-2026-${String.format("%03d", expenses.size + 1)}",
            isBn = isBn,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { newExp ->
                repository.addExpense(newExp.copy(samityId = repository.activeSamityId))
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
private fun ExpenseItemCard(
    expense: Expense,
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
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MoneyOff, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(expense.titleBn, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Text(expense.voucherNo, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SamityNavyPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                    }
                }
                Text(
                    text = "${expense.category} • মাধ্যম: ${expense.paymentMethod} • তারিখ: ${expense.expenseDate}",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "প্রাপক: ${expense.paidTo} • প্রদানকারী: ${expense.paidBy}",
                    fontSize = 10.sp,
                    color = Color(0xFF475569)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = NumberToWords.formatCurrency(expense.amount, isBn),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFFDC2626)
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = StatusPaidBg
                ) {
                    Text(
                        text = if (isBn) "অনুমোদিত" else "Approved",
                        fontSize = 9.sp,
                        color = StatusPaidGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddExpenseDialog(
    nextVoucher: String,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSave: (Expense) -> Unit
) {
    var titleBn by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Office Rent") }
    var amountStr by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Bank") }
    var paidTo by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf("কোষাধ্যক্ষ") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("Office Rent", "Meeting Cost", "Charity & Relief", "Stationery", "Electricity Bill", "Travel", "Bank Charge", "Other")

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
                        text = if (isBn) "নতুন সমিতি ব্যয় ভাউচার ($nextVoucher)" else "New Expense Voucher ($nextVoucher)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = SamityNavyPrimary
                    )
                    Divider(modifier = Modifier.padding(vertical = 4.dp))
                }

                item {
                    OutlinedTextField(
                        value = titleBn,
                        onValueChange = { titleBn = it },
                        label = { Text(if (isBn) "ব্যয়ের শিরোনাম / বিবরণ *" else "Expense Title *") },
                        modifier = Modifier.fillMaxWidth().testTag("expense_title_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text(if (isBn) "টাকার পরিমাণ *" else "Amount (BDT) *") },
                        modifier = Modifier.fillMaxWidth().testTag("expense_amount_input"),
                        singleLine = true
                    )
                }

                item {
                    Text(if (isBn) "ব্যয়ের ক্যাটাগরি" else "Expense Category", fontSize = 11.sp, color = Color.Gray)
                    var catExpanded by remember { mutableStateOf(false) }
                    Box {
                        OutlinedCard(
                            onClick = { catExpanded = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(category, fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                        DropdownMenu(
                            expanded = catExpanded,
                            onDismissRequest = { catExpanded = false }
                        ) {
                            categories.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        category = c
                                        catExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Text(if (isBn) "পরিশোধের মাধ্যম" else "Payment Method", fontSize = 11.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Bank", "Cash", "bKash", "Cheque").forEach { method ->
                            FilterChip(
                                selected = paymentMethod == method,
                                onClick = { paymentMethod = method },
                                label = { Text(method, fontSize = 10.sp) }
                            )
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = paidTo,
                        onValueChange = { paidTo = it },
                        label = { Text(if (isBn) "যাকে প্রদান করা হয়েছে (প্রাপক)" else "Paid To") },
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
                                val amt = amountStr.toDoubleOrNull() ?: 0.0
                                if (titleBn.isNotBlank() && amt > 0) {
                                    val exp = Expense(
                                        id = UUID.randomUUID().toString(),
                                        samityId = "",
                                        voucherNo = nextVoucher,
                                        expenseDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
                                        category = category,
                                        titleBn = titleBn,
                                        titleEn = titleBn,
                                        description = description,
                                        amount = amt,
                                        paymentMethod = paymentMethod,
                                        paidTo = paidTo,
                                        paidBy = paidBy,
                                        approvalStatus = "Approved"
                                    )
                                    onSave(exp)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                            modifier = Modifier.testTag("save_expense_btn")
                        ) {
                            Text(LanguageManager.btnSave, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
