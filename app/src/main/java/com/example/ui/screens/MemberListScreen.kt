package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.auth.AppPermission
import com.example.data.model.Member
import com.example.data.model.PaymentStatusType
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.components.PermissionGate
import com.example.ui.components.QrCodeCanvas
import com.example.ui.components.StatusBadge
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import com.example.util.NumberToWords
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberListScreen(
    repository: SamityRepository,
    onRecordDepositForMember: (Member) -> Unit,
    onSendReminderForMember: (Member) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedMemberForProfile by remember { mutableStateOf<Member?>(null) }

    val members = repository.activeMembers
    val isClientPrivacyProtected = repository.currentUserRole == UserRole.SUPER_ADMIN && !repository.activeSamity.isOwnedBySuperAdmin

    val filteredMembers = members.filter { mem ->
        val matchesSearch = mem.nameBn.contains(searchQuery, ignoreCase = true) ||
                mem.nameEn.contains(searchQuery, ignoreCase = true) ||
                mem.memberCode.contains(searchQuery, ignoreCase = true) ||
                mem.phone.contains(searchQuery)

        val matchesFilter = when (selectedFilter) {
            "PAID" -> mem.currentMonthPaid >= mem.monthlyFee && mem.monthlyFee > 0
            "UNPAID" -> mem.currentMonthPaid == 0.0 && mem.monthlyFee > 0
            "PARTIAL" -> mem.currentMonthPaid > 0 && mem.currentMonthPaid < mem.monthlyFee
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        floatingActionButton = {
            PermissionGate(
                permission = AppPermission.CREATE_MEMBER,
                userSession = repository.userSession,
                targetSamityId = repository.activeSamityId
            ) {
                ExtendedFloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = SamityNavyPrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                    text = { Text(LanguageManager.btnAddMember, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("add_member_fab")
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
            // Header with Member Count & Export/Print
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "সমিতি সদস্য তালিকা" else "Samity Member Directory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Text(
                        text = "${if (isBn) "মোট সদস্য: " else "Total Members: "}${NumberToWords.formatNumber(members.size, isBn)} জন",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SamityNavyPrimary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = if (isBn) "সক্রিয়: ${NumberToWords.formatNumber(members.count { it.status == "Active" }, isBn)}" else "Active: ${members.count { it.status == "Active" }}",
                        color = SamityNavyPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_member_input"),
                placeholder = { Text(LanguageManager.searchMemberPlaceholder, fontSize = 12.5.sp) },
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

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text(if (isBn) "সকল (${NumberToWords.formatNumber(members.size, isBn)})" else "All (${members.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "PAID",
                    onClick = { selectedFilter = "PAID" },
                    label = { Text(if (isBn) "পরিশোধিত" else "Paid", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "UNPAID",
                    onClick = { selectedFilter = "UNPAID" },
                    label = { Text(if (isBn) "বকেয়া" else "Unpaid", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "PARTIAL",
                    onClick = { selectedFilter = "PARTIAL" },
                    label = { Text(if (isBn) "আংশিক" else "Partial", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Member Cards LazyColumn or Privacy Guard
            if (isClientPrivacyProtected) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth().testTag("member_privacy_guard_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFEF3C7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFD97706))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBn) "ক্লায়েন্ট সদস্য ডেটা সুরক্ষিত (Privacy Protected)" else "Client Member Data Protected",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SamityNavyDark
                                )
                                Text(
                                    text = "${if (isBn) "এই সমিতিতে মোট সদস্য: " else "Total Members: "}${members.size} জন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SamityEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isBn)
                                "🔒 আপনি সফটওয়্যার বিক্রেতা / সুপার অ্যাডমিন। এই ক্লায়েন্ট ব্যবসায়ী সমিতির মোট সদস্য সংখ্যা (${members.size} জন) দৃশ্যমান রয়েছে, কিন্তু ক্লায়েন্টের গোপনীয়তা রক্ষার্থে কোনো সদস্যের ব্যক্তিগত প্রোফাইল, ফোন নম্বর বা লেনদেন দেখার অনুমতি সংরক্ষিত নেই।\n\nযদি আপনি আপনার নিজস্ব সমিতি পরিচালনা করতে চান, তবে টপ বার হতে 'স্বপ্নছোঁয়া সমবায় সমিতি' নির্বাচন করুন।"
                            else
                                "🔒 Client Data Privacy Guard Active: As a Super Admin vendor, you can see the member count (${members.size} members), but individual profiles are hidden to preserve client trust.",
                            fontSize = 11.5.sp,
                            color = Color(0xFF475569),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (isBn) "মোট সদস্য" else "Members", fontSize = 10.sp, color = Color.Gray)
                                    Text(text = "${members.size} জন", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SamityNavyPrimary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (isBn) "অ্যাক্টিভ একাউন্ট" else "Active", fontSize = 10.sp, color = Color.Gray)
                                    Text(text = "${members.count { it.status == "Active" }} জন", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SamityEmerald)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = if (isBn) "লাইসেন্স লিমিট" else "Plan Limit", fontSize = 10.sp, color = Color.Gray)
                                    Text(text = "${repository.activeLicense.maxMembersLimit} জন", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SamityGold)
                                }
                            }
                        }
                    }
                }
            } else if (filteredMembers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBn) "কোন সদস্য খুঁজে পাওয়া যায়নি" else "No members found",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredMembers) { member ->
                        MemberItemCard(
                            member = member,
                            isBn = isBn,
                            onCardClick = { selectedMemberForProfile = member },
                            onDepositClick = { onRecordDepositForMember(member) },
                            onReminderClick = { onSendReminderForMember(member) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(72.dp))
                    }
                }
            }
        }
    }

    // Add Member Dialog
    if (showAddDialog) {
        AddMemberDialog(
            nextCode = "SM-${String.format("%04d", members.size + 1)}",
            isBn = isBn,
            onDismiss = { showAddDialog = false },
            onSave = { newMember ->
                repository.addMember(newMember.copy(samityId = repository.activeSamityId))
                showAddDialog = false
            }
        )
    }

    // Member Profile Dialog
    selectedMemberForProfile?.let { member ->
        MemberProfileDialog(
            member = member,
            samityName = if (isBn) repository.activeSamity.nameBn else repository.activeSamity.nameEn,
            isBn = isBn,
            onDismiss = { selectedMemberForProfile = null },
            onDepositClick = {
                selectedMemberForProfile = null
                onRecordDepositForMember(member)
            },
            onReminderClick = {
                selectedMemberForProfile = null
                onSendReminderForMember(member)
            }
        )
    }
}

@Composable
private fun MemberItemCard(
    member: Member,
    isBn: Boolean,
    onCardClick: () -> Unit,
    onDepositClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    val status = when {
        member.currentMonthPaid >= member.monthlyFee && member.monthlyFee > 0 -> PaymentStatusType.PAID
        member.currentMonthPaid > 0 -> PaymentStatusType.PARTIAL
        member.totalDue > 0 -> PaymentStatusType.UNPAID
        else -> PaymentStatusType.PAID
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Member Avatar with initials
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SamityNavyPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.nameBn.take(1).ifEmpty { "স" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = SamityNavyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.nameBn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SlateTextPrimary
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

            Spacer(modifier = Modifier.height(10.dp))

            // Monthly Fee & Payment Progress
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBn) "মাসিক চাঁদা" else "Monthly Fee",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = NumberToWords.formatCurrency(member.monthlyFee, isBn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isBn) "চলতি মাসে জমা" else "Paid this Month",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = NumberToWords.formatCurrency(member.currentMonthPaid, isBn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPaidGreen
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isBn) "মোট বকেয়া" else "Total Due",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = NumberToWords.formatCurrency(member.totalDue, isBn),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (member.totalDue > 0) StatusUnpaidRed else Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (member.totalDue > 0 || member.currentMonthPaid < member.monthlyFee) {
                    OutlinedButton(
                        onClick = onReminderClick,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = SamityGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "রিমাইন্ডার" else "Reminder", fontSize = 11.sp, color = SamityNavyDark)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onDepositClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBn) "জমা দিন" else "Deposit", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun AddMemberDialog(
    nextCode: String,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onSave: (Member) -> Unit
) {
    var nameBn by remember { mutableStateOf("") }
    var nameEn by remember { mutableStateOf("") }
    var fatherName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var telegram by remember { mutableStateOf("") }
    var facebook by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("B+") }
    var membershipType by remember { mutableStateOf("সাধারণ সদস্য") }
    var monthlyFeeStr by remember { mutableStateOf("5000") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (isBn) "নতুন সদস্য যোগ করুন ($nextCode)" else "Add New Member ($nextCode)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SamityNavyPrimary
                    )
                    Divider(modifier = Modifier.padding(vertical = 6.dp))
                }

                item {
                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text(if (isBn) "পূর্ণ নাম (বাংলায়) *" else "Full Name (Bangla) *") },
                        modifier = Modifier.fillMaxWidth().testTag("member_name_bn_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(if (isBn) "পূর্ণ নাম (ইংরেজিতে)" else "Full Name (English)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isBn) "মোবাইল নম্বর *" else "Mobile Number *") },
                        modifier = Modifier.fillMaxWidth().testTag("member_phone_input"),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text(if (isBn) "হোয়াটসঅ্যাপ নম্বর (যেমন 88017...)" else "WhatsApp Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = fatherName,
                        onValueChange = { fatherName = it },
                        label = { Text(if (isBn) "পিতার নাম" else "Father's Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = monthlyFeeStr,
                        onValueChange = { monthlyFeeStr = it },
                        label = { Text(if (isBn) "মাসিক চাঁদার পরিমাণ (টাকা)" else "Monthly Fee (BDT)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBn) "বর্তমান ঠিকানা" else "Present Address") },
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
                                if (nameBn.isNotBlank() && phone.isNotBlank()) {
                                    val fee = monthlyFeeStr.toDoubleOrNull() ?: 5000.0
                                    val newMem = Member(
                                        id = UUID.randomUUID().toString(),
                                        samityId = "",
                                        memberCode = nextCode,
                                        nameBn = nameBn,
                                        nameEn = nameEn.ifBlank { nameBn },
                                        fatherName = fatherName,
                                        phone = phone,
                                        whatsappNumber = whatsapp.ifBlank { phone.replace("-", "").replace(" ", "") },
                                        telegramHandle = telegram,
                                        facebookUrl = facebook,
                                        email = "",
                                        address = address,
                                        occupation = occupation,
                                        bloodGroup = bloodGroup,
                                        membershipType = membershipType,
                                        monthlyFee = fee,
                                        joinDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date()),
                                        status = "Active",
                                        totalDue = fee
                                    )
                                    onSave(newMem)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                            modifier = Modifier.testTag("save_member_btn")
                        ) {
                            Text(LanguageManager.btnSave, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberProfileDialog(
    member: Member,
    samityName: String,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onDepositClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header & Digital Member ID Card Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(samityName, fontSize = 11.sp, color = SamityNavyPrimary, fontWeight = FontWeight.Bold)
                        Text(if (isBn) "সদস্য প্রোফাইল ও পরিচিতি" else "Member Profile & Card", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Digital Card Preview Box
                Card(
                    colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = member.memberCode,
                                color = SamityGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = member.nameBn,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = member.nameEn,
                                color = Color(0xFFD0E4FF),
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${member.membershipType} • ${member.phone}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                        }

                        QrCodeCanvas(content = "MEMBER:${member.memberCode}:${member.nameEn}", size = 64.dp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Profile Fields
                ProfileInfoRow(label = if (isBn) "পিতার নাম" else "Father's Name", value = member.fatherName)
                ProfileInfoRow(label = if (isBn) "রক্তের গ্রুপ" else "Blood Group", value = member.bloodGroup)
                ProfileInfoRow(label = if (isBn) "পেশা" else "Occupation", value = member.occupation)
                ProfileInfoRow(label = if (isBn) "ঠিকানা" else "Address", value = member.address)
                ProfileInfoRow(label = if (isBn) "যোগদানের তারিখ" else "Join Date", value = member.joinDate)
                ProfileInfoRow(label = if (isBn) "মোট জমা" else "Total Paid", value = NumberToWords.formatCurrency(member.totalPaid, isBn))
                ProfileInfoRow(label = if (isBn) "মোট বকেয়া" else "Total Due", value = NumberToWords.formatCurrency(member.totalDue, isBn))

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-Channel Contact Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val cleanNumber = member.whatsappNumber.replace("+", "").replace("-", "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanNumber"))
                            try { context.startActivity(intent) } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 11.sp, color = Color.White)
                    }

                    Button(
                        onClick = onDepositClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBn) "জমা এন্ট্রি" else "Deposit", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(text = value.ifBlank { "-" }, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = SlateTextPrimary)
    }
}
