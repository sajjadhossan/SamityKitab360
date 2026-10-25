package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.SamityEmerald
import com.example.ui.theme.SamityGold
import com.example.ui.theme.SamityNavyDark
import com.example.ui.theme.SamityNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamityTopAppBar(
    repository: SamityRepository,
    onOpenSamitySetup: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenSaaSControl: () -> Unit,
    onOpenRolePermissions: () -> Unit,
    onOpenVercelSync: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onToggleDrawer: () -> Unit = {},
    onNavigateToMembers: () -> Unit = {},
    onNavigateToDeposits: () -> Unit = {},
    onNavigateToBulkDeposit: () -> Unit = {},
    onNavigateToExpenses: () -> Unit = {},
    onNavigateToReminders: () -> Unit = {},
    onNavigateToPaymentStatus: () -> Unit = {},
    onNavigateToFundBalance: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onOpenLetterhead: () -> Unit = {},
    onOpenDocumentGenerator: () -> Unit = {},
    onOpenAuditTrail: () -> Unit = {},
    onOpenApprovalWorkflow: () -> Unit = {},
    onOpenBackupExport: () -> Unit = {},
    canNavigateBack: Boolean = false,
    onNavigateBack: () -> Unit = {},
    screenTitle: String? = null,
    modifier: Modifier = Modifier
) {
    var showSamityModal by remember { mutableStateOf(false) }
    var showThreeDotMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }
    var showLiveSyncDialog by remember { mutableStateOf(false) }
    val isBn = LanguageManager.isBangla
    val activeSamity = repository.activeSamity

    TopAppBar(
        modifier = modifier.testTag("samity_top_app_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SamityNavyPrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (canNavigateBack) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("topbar_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
                IconButton(
                    onClick = onToggleDrawer,
                    modifier = Modifier.testTag("topbar_hamburger_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Hamburger Navigation Menu",
                        tint = Color.White
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showSamityModal = true }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                    .testTag("topbar_samity_brand_btn")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SamityNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Samity Logo",
                        tint = SamityGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = screenTitle ?: LanguageManager.appTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SamityGold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = if (isBn) "ERP" else "PRO",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        Text(
                            text = if (isBn) activeSamity.nameBn else activeSamity.nameEn,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 140.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Open Samity Hub",
                            tint = SamityGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        },
        actions = {
            // Real-Time Multi-Device Sync Indicator
            IconButton(
                onClick = { showLiveSyncDialog = true },
                modifier = Modifier.testTag("open_live_sync_modal_btn")
            ) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = Color(0xFF10B981)) {
                            Text(
                                text = "${repository.syncEngine.connectedDevices.size}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Real-Time Multi-Device Sync",
                        tint = SamityGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // User Role Switcher Chip
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SamityGold,
                    modifier = Modifier
                        .clickable { showRoleMenu = true }
                        .padding(end = 2.dp)
                        .testTag("topbar_role_switcher_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "User Role",
                            tint = SamityNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isBn) repository.currentUserRole.titleBn else repository.currentUserRole.titleEn,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityNavyDark
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = SamityNavyDark,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Role Dropdown
                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    Text(
                        text = if (isBn) "ইউজার রোল নির্বাচন করুন" else "Switch User Role",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Divider()

                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isBn) role.titleBn else role.titleEn,
                                    fontWeight = if (role == repository.currentUserRole) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                repository.switchRole(role)
                                showRoleMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.PRESIDENT -> Icons.Default.Stars
                                        UserRole.GENERAL_SECRETARY -> Icons.Default.EditNote
                                        UserRole.TREASURER -> Icons.Default.AccountBalanceWallet
                                        UserRole.GENERAL_MEMBER -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = if (role == repository.currentUserRole) SamityGold else Color.Gray
                                )
                            }
                        )
                    }
                }
            }

            // Language Switcher Toggle
            IconButton(
                onClick = { LanguageManager.toggle() },
                modifier = Modifier.testTag("language_toggle_btn")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = if (isBn) "EN" else "বাং",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.5.dp)
                    )
                }
            }

            // Three-Dot Overflow Menu Button
            Box {
                IconButton(
                    onClick = { showThreeDotMenu = true },
                    modifier = Modifier.testTag("topbar_three_dot_menu_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options Overflow Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showThreeDotMenu,
                    onDismissRequest = { showThreeDotMenu = false }
                ) {
                    Text(
                        text = if (isBn) "প্রধান সেবা ও মডিউল মেনু" else "Quick ERP Menu & Actions",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Divider()

                    DropdownMenuItem(
                        text = { Text(if (isBn) "সমিতি সেটআপ ও তথ্য" else "Samity Setup & Details") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenSamitySetup()
                        },
                        leadingIcon = { Icon(Icons.Default.Apartment, contentDescription = null, tint = SamityNavyPrimary) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "+ নতুন সমিতি তৈরি" else "+ Create New Samity") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenSamitySetup()
                        },
                        leadingIcon = { Icon(Icons.Default.AddBusiness, contentDescription = null, tint = SamityEmerald) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "সদস্য তালিকা ও তথ্য" else "Members Directory") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToMembers()
                        },
                        leadingIcon = { Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF0284C7)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "চাঁদা জমা ও ব্যাংক এন্ট্রি" else "Deposits & Banking") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToDeposits()
                        },
                        leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF059669)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "বাল্ক জমা স্লিপ" else "Bulk Deposit Slip") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToBulkDeposit()
                        },
                        leadingIcon = { Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = SamityGold) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "আদায় ও বকেয়া ট্র্যাকিং" else "Payment & Due Status") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToPaymentStatus()
                        },
                        leadingIcon = { Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color(0xFFDC2626)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "বকেয়া তাগিদ ও রিমাইন্ডার" else "Reminders & Notices") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToReminders()
                        },
                        leadingIcon = { Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFEA580C)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "ব্যয় ভাউচার ও তহবিল" else "Expenses & Funds") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToFundBalance()
                        },
                        leadingIcon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF7C3AED)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "আর্থিক ও অডিট রিপোর্টস" else "Financial & Audit Reports") },
                        onClick = {
                            showThreeDotMenu = false
                            onNavigateToReports()
                        },
                        leadingIcon = { Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF2563EB)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "লেটারহেড ও প্যাড সেটিংস" else "Letterhead Setup") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenLetterhead()
                        },
                        leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFF4F46E5)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "প্রত্যয়ন ও অফিস নথি" else "Document Generator") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenDocumentGenerator()
                        },
                        leadingIcon = { Icon(Icons.Default.Print, contentDescription = null, tint = Color(0xFF0891B2)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "ট্রানজেকশন অনুমোদন" else "Approval Workflow") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenApprovalWorkflow()
                        },
                        leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "অডিট ট্রেইল ও নিরাপত্তা" else "Audit Trail & Logs") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenAuditTrail()
                        },
                        leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SamityNavyDark) }
                    )

                    Divider()

                    DropdownMenuItem(
                        text = { Text(if (isBn) "ভার্সেল ক্লাউড ও ওয়েব সিঙ্ক" else "Vercel Cloud Sync") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenVercelSync()
                        },
                        leadingIcon = { Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFF10B981)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "ডাটাবেজ ব্যাকআপ ও এক্সপোর্ট" else "Database Backup & Export") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenBackupExport()
                        },
                        leadingIcon = { Icon(Icons.Default.Backup, contentDescription = null, tint = Color(0xFF6366F1)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "রোল ও পারমিশন কন্ট্রোল" else "Role Permissions") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenRolePermissions()
                        },
                        leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SamityNavyPrimary) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "সাশ কমার্শিয়াল কন্ট্রোল" else "SaaS Admin Control") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenSaaSControl()
                        },
                        leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null, tint = SamityGold) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "সিস্টেম সেটিংস ও ভাষা" else "System Settings") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenSettings()
                        },
                        leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF64748B)) }
                    )

                    DropdownMenuItem(
                        text = { Text(if (isBn) "লগইন ও এক্টিভেশন" else "Login & Activation") },
                        onClick = {
                            showThreeDotMenu = false
                            onOpenLogin()
                        },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SamityNavyPrimary) }
                    )
                }
            }
        }
    )

    // Modal when tapping Samity Brand: presents Switch Samity + 4-Corner Quick Options Grid!
    if (showSamityModal) {
        SamityQuickAccessModal(
            repository = repository,
            isBn = isBn,
            onDismiss = { showSamityModal = false },
            onOpenSamitySetup = {
                showSamityModal = false
                onOpenSamitySetup()
            },
            onNavigateToMembers = {
                showSamityModal = false
                onNavigateToMembers()
            },
            onNavigateToDeposits = {
                showSamityModal = false
                onNavigateToDeposits()
            },
            onNavigateToBulkDeposit = {
                showSamityModal = false
                onNavigateToBulkDeposit()
            },
            onNavigateToPaymentStatus = {
                showSamityModal = false
                onNavigateToPaymentStatus()
            },
            onNavigateToReminders = {
                showSamityModal = false
                onNavigateToReminders()
            },
            onNavigateToFundBalance = {
                showSamityModal = false
                onNavigateToFundBalance()
            },
            onNavigateToReports = {
                showSamityModal = false
                onNavigateToReports()
            },
            onOpenLetterhead = {
                showSamityModal = false
                onOpenLetterhead()
            },
            onOpenDocumentGenerator = {
                showSamityModal = false
                onOpenDocumentGenerator()
            },
            onOpenApprovalWorkflow = {
                showSamityModal = false
                onOpenApprovalWorkflow()
            },
            onOpenAuditTrail = {
                showSamityModal = false
                onOpenAuditTrail()
            },
            onOpenVercelSync = {
                showSamityModal = false
                onOpenVercelSync()
            },
            onOpenBackupExport = {
                showSamityModal = false
                onOpenBackupExport()
            },
            onOpenRolePermissions = {
                showSamityModal = false
                onOpenRolePermissions()
            },
            onOpenSaaSControl = {
                showSamityModal = false
                onOpenSaaSControl()
            },
            onOpenSettings = {
                showSamityModal = false
                onOpenSettings()
            }
        )
    }

    if (showLiveSyncDialog) {
        LiveSyncMonitorDialog(
            repository = repository,
            onDismiss = { showLiveSyncDialog = false }
        )
    }
}

@Composable
fun SamityQuickAccessModal(
    repository: SamityRepository,
    isBn: Boolean,
    onDismiss: () -> Unit,
    onOpenSamitySetup: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToDeposits: () -> Unit,
    onNavigateToBulkDeposit: () -> Unit,
    onNavigateToPaymentStatus: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onNavigateToFundBalance: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenLetterhead: () -> Unit,
    onOpenDocumentGenerator: () -> Unit,
    onOpenApprovalWorkflow: () -> Unit,
    onOpenAuditTrail: () -> Unit,
    onOpenVercelSync: () -> Unit,
    onOpenBackupExport: () -> Unit,
    onOpenRolePermissions: () -> Unit,
    onOpenSaaSControl: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val active = repository.activeSamity

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFF8FAFC),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 16.dp)
                .testTag("samity_quick_access_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Active Samity Info
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(SamityGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Apartment,
                                contentDescription = null,
                                tint = SamityNavyDark,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) active.nameBn else active.nameEn,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${active.regNo} | ${active.samityType}",
                                fontSize = 11.sp,
                                color = Color(0xFFD0E4FF)
                            )
                            Text(
                                text = "${active.district}, ${active.division}",
                                fontSize = 10.sp,
                                color = SamityGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-Tenant Switcher Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "সমিতি নির্বাচন (মাল্টি-টেন্যান্ট)" else "Switch Samity (Multi-Tenant)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyDark
                    )

                    TextButton(onClick = onOpenSamitySetup) {
                        Text(
                            text = if (isBn) "+ নতুন সমিতি" else "+ New Samity",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityEmerald
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    repository.samities.forEach { s ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (s.id == repository.activeSamityId) Color(0xFFEFF6FF) else Color.White,
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.switchSamity(s.id)
                                    onDismiss()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (s.id == repository.activeSamityId) Icons.Default.CheckCircle else Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = if (s.id == repository.activeSamityId) SamityGold else Color.Gray,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isBn) s.nameBn else s.nameEn,
                                        fontSize = 12.sp,
                                        fontWeight = if (s.id == repository.activeSamityId) FontWeight.Bold else FontWeight.Normal,
                                        color = SamityNavyDark
                                    )
                                    Text(
                                        text = "${s.regNo} | ${s.samityType}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4-CORNER COMPACT TILES GRID
                Text(
                    text = if (isBn) "৪-কর্নার দ্রুত সেবা ও কন্ট্রোল" else "4-Corner Quick Action Hub",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SamityNavyDark
                )
                Text(
                    text = if (isBn) "যে মডিউলে যেতে চান সরাসরি ট্যাপ করুন" else "Tap any card to directly open the module",
                    fontSize = 10.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Grid Rows of 4-Corner Modern Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "সমিতি সেটআপ" else "Samity Setup",
                        subtitle = if (isBn) "তথ্য ও কনফিগারেশন" else "Info & Config",
                        icon = Icons.Default.Apartment,
                        gradient = listOf(Color(0xFF0D2C54), Color(0xFF1E40AF)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenSamitySetup
                    )
                    FourCornerTile(
                        title = if (isBn) "সদস্য তালিকা" else "Members",
                        subtitle = if (isBn) "সদস্য ও মোবাইল" else "Directory & Phone",
                        icon = Icons.Default.People,
                        gradient = listOf(Color(0xFF047857), Color(0xFF10B981)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToMembers
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "চাঁদা ও জমা" else "Deposits",
                        subtitle = if (isBn) "ব্যাংক, ক্যাশ, বিকাশ" else "Bank & MFS Entry",
                        icon = Icons.Default.AccountBalance,
                        gradient = listOf(Color(0xFF0E7490), Color(0xFF06B6D4)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToDeposits
                    )
                    FourCornerTile(
                        title = if (isBn) "বাল্ক স্লিপ" else "Bulk Slip",
                        subtitle = if (isBn) "একসাথে জমা এন্ট্রি" else "Batch Collection",
                        icon = Icons.Default.ReceiptLong,
                        gradient = listOf(Color(0xFFD97706), Color(0xFFF59E0B)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBulkDeposit
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "আদায় ও বকেয়া" else "Due Tracking",
                        subtitle = if (isBn) "পরিশোধিত ও বাকি" else "Paid & Unpaid",
                        icon = Icons.Default.FactCheck,
                        gradient = listOf(Color(0xFFBE123C), Color(0xFFF43F5E)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPaymentStatus
                    )
                    FourCornerTile(
                        title = if (isBn) "তাগিদ নোটিশ" else "Reminders",
                        subtitle = if (isBn) "WhatsApp ও SMS" else "SMS & WhatsApp",
                        icon = Icons.Default.NotificationsActive,
                        gradient = listOf(Color(0xFFC2410C), Color(0xFFFB923C)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReminders
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "খরচ ও তহবিল" else "Expenses & Fund",
                        subtitle = if (isBn) "ভাউচার ও ব্যালেন্স" else "Voucher & Balance",
                        icon = Icons.Default.AccountBalanceWallet,
                        gradient = listOf(Color(0xFF6D28D9), Color(0xFF8B5CF6)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToFundBalance
                    )
                    FourCornerTile(
                        title = if (isBn) "আর্থিক রিপোর্ট" else "Reports & Audit",
                        subtitle = if (isBn) "মাসিক ও অডিট স্টেটমেন্ট" else "Statements & Audit",
                        icon = Icons.Default.Assessment,
                        gradient = listOf(Color(0xFF1D4ED8), Color(0xFF3B82F6)),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReports
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "লেটারহেড ও প্যাড" else "Letterhead",
                        subtitle = if (isBn) "প্রত্যয়ন ও অফিস নথি" else "Official Documents",
                        icon = Icons.Default.Description,
                        gradient = listOf(Color(0xFF4338CA), Color(0xFF6366F1)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenLetterhead
                    )
                    FourCornerTile(
                        title = if (isBn) "ক্লাউড সিঙ্ক হাব" else "Cloud Sync Hub",
                        subtitle = if (isBn) "ওয়েবসাইট ও ডাটা সিঙ্ক" else "Vercel Web Parity",
                        icon = Icons.Default.CloudSync,
                        gradient = listOf(Color(0xFF0F766E), Color(0xFF14B8A6)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenVercelSync
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FourCornerTile(
                        title = if (isBn) "রোল ও পারমিশন" else "Role Permissions",
                        subtitle = if (isBn) "কর্মকর্তাদের ক্ষমতা" else "Staff Permissions",
                        icon = Icons.Default.AdminPanelSettings,
                        gradient = listOf(Color(0xFF334155), Color(0xFF475569)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenRolePermissions
                    )
                    FourCornerTile(
                        title = if (isBn) "সিস্টেম সেটিংস" else "Settings",
                        subtitle = if (isBn) "ভাষা ও কনফিগারেশন" else "Preferences & Config",
                        icon = Icons.Default.Settings,
                        gradient = listOf(Color(0xFF1E293B), Color(0xFF334155)),
                        modifier = Modifier.weight(1f),
                        onClick = onOpenSettings
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary)
                ) {
                    Text(
                        text = if (isBn) "বন্ধ করুন" else "Close",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun FourCornerTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    badgeText: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(12.dp)
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (badgeText != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.28f)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = Color.White.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
