package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.example.data.auth.AppPermission
import com.example.data.model.Deposit
import com.example.data.model.Member
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.components.AuthorizedScreen
import com.example.ui.components.RealtimeSyncBanner
import com.example.ui.components.SamityTopAppBar
import com.example.ui.language.LanguageManager
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SamityGold
import com.example.ui.theme.SamityNavyDark
import com.example.ui.theme.SamityNavyPrimary
import kotlinx.coroutines.launch

sealed class Screen {
    object Dashboard : Screen()
    object Members : Screen()
    object Deposits : Screen()
    object BulkDeposit : Screen()
    object PaymentStatus : Screen()
    object Reminders : Screen()
    object Expenses : Screen()
    object FundBalance : Screen()
    object Reports : Screen()
    object Letterhead : Screen()
    object DocumentGenerator : Screen()
    object AuditTrail : Screen()
    object SamitySetup : Screen()
    object MemberPortal : Screen()
    object LoginAndActivation : Screen()
    object SaaSAdminControl : Screen()
    object RolePermissionManager : Screen()
    object VercelCloudSync : Screen()
    object ApprovalWorkflow : Screen()
    object BackupExport : Screen()
    object Settings : Screen()
    data class ReceiptView(val deposit: Deposit) : Screen()
    data class DueNoticeView(val member: Member) : Screen()
}

class MainActivity : ComponentActivity() {

    private val repository by lazy {
        val sessionManager = com.example.data.auth.UserSessionManager(applicationContext)
        SamityRepository(sessionManager = sessionManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(repository: SamityRepository) {
    val isBn = LanguageManager.isBangla
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Navigation back stack handler
    BackHandler(enabled = drawerState.isOpen) {
        coroutineScope.launch { drawerState.close() }
    }
    BackHandler(enabled = !drawerState.isOpen && currentScreen !is Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    // Role-dependent interface routing
    val isGeneralMember = repository.currentUserRole == UserRole.GENERAL_MEMBER
    val activeSamity = repository.activeSamity

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF8FAFC),
                drawerTonalElevation = 6.dp,
                modifier = Modifier
                    .widthIn(max = 330.dp)
                    .fillMaxHeight()
                    .testTag("app_navigation_drawer_sheet")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Drawer Header with Samity Logo & Active Info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(SamityNavyDark, SamityNavyPrimary)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
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
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = LanguageManager.appTitle,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 15.sp,
                                            color = SamityGold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "PRO",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = if (isBn) activeSamity.nameBn else activeSamity.nameEn,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${activeSamity.regNo} | ${activeSamity.samityType}",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFD0E4FF)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = SamityGold.copy(alpha = 0.25f),
                                border = BorderStroke(1.dp, SamityGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = SamityGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${if (isBn) "রোল: " else "Role: "}${if (isBn) repository.currentUserRole.titleBn else repository.currentUserRole.titleEn}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Group 1: মূল কার্যক্রম ও ড্যাশবোর্ড
                    DrawerSectionHeader(title = if (isBn) "মূল কার্যক্রম ও ড্যাশবোর্ড" else "Core Operations")

                    DrawerItem(
                        title = if (isBn) "ড্যাশবোর্ড" else "Dashboard",
                        icon = Icons.Default.Dashboard,
                        selected = currentScreen is Screen.Dashboard,
                        badgeText = if (isBn) "প্রধান" else "Home",
                        iconColor = SamityNavyPrimary,
                        onClick = {
                            currentScreen = Screen.Dashboard
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "সদস্য তালিকা ও তথ্য" else "Members Directory",
                        icon = Icons.Default.People,
                        selected = currentScreen is Screen.Members,
                        badgeText = "${repository.activeMembers.size} জন",
                        iconColor = Color(0xFF0284C7),
                        onClick = {
                            currentScreen = Screen.Members
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "চাঁদা জমা ও ব্যাংক এন্ট্রি" else "Deposits & Banking",
                        icon = Icons.Default.AccountBalance,
                        selected = currentScreen is Screen.Deposits,
                        badgeText = "কালেকশন",
                        iconColor = Color(0xFF059669),
                        onClick = {
                            currentScreen = Screen.Deposits
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "বাল্ক জমা স্লিপ" else "Bulk Deposit Slips",
                        icon = Icons.Default.ReceiptLong,
                        selected = currentScreen is Screen.BulkDeposit,
                        badgeText = "একসাথে",
                        iconColor = SamityGold,
                        onClick = {
                            currentScreen = Screen.BulkDeposit
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "আদায় ও বকেয়া ট্র্যাকিং" else "Payment & Due Tracking",
                        icon = Icons.Default.FactCheck,
                        selected = currentScreen is Screen.PaymentStatus,
                        badgeText = "বকেয়া",
                        iconColor = Color(0xFFDC2626),
                        onClick = {
                            currentScreen = Screen.PaymentStatus
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "বকেয়া নোটিশ ও তাগিদ" else "Reminders & Notices",
                        icon = Icons.Default.NotificationsActive,
                        selected = currentScreen is Screen.Reminders,
                        badgeText = "SMS/WA",
                        iconColor = Color(0xFFEA580C),
                        onClick = {
                            currentScreen = Screen.Reminders
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

                    // Group 2: আর্থিক হিসাব ও রিপোর্ট
                    DrawerSectionHeader(title = if (isBn) "আর্থিক হিসাব ও রিপোর্ট" else "Finance & Reports")

                    DrawerItem(
                        title = if (isBn) "সমিতি ব্যয় ও ভাউচার" else "Expenses & Vouchers",
                        icon = Icons.Default.MoneyOff,
                        selected = currentScreen is Screen.Expenses,
                        iconColor = Color(0xFFDC2626),
                        onClick = {
                            currentScreen = Screen.Expenses
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "তহবিল বিবরণ ও ব্যালেন্স" else "Fund Breakdown",
                        icon = Icons.Default.AccountBalanceWallet,
                        selected = currentScreen is Screen.FundBalance,
                        badgeText = "ব্যালেন্স",
                        iconColor = Color(0xFF7C3AED),
                        onClick = {
                            currentScreen = Screen.FundBalance
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "আর্থিক ও অডিট রিপোর্টস" else "Financial & Audit Reports",
                        icon = Icons.Default.Assessment,
                        selected = currentScreen is Screen.Reports,
                        badgeText = "রিপোর্ট",
                        iconColor = Color(0xFF2563EB),
                        onClick = {
                            currentScreen = Screen.Reports
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

                    // Group 3: প্রশাসনিক ও অফিসিয়াল
                    DrawerSectionHeader(title = if (isBn) "প্রশাসনিক ও অফিসিয়াল" else "Admin & Documents")

                    DrawerItem(
                        title = if (isBn) "সমিতি সেটআপ ও তথ্য" else "Samity Setup & Details",
                        icon = Icons.Default.Apartment,
                        selected = currentScreen is Screen.SamitySetup,
                        badgeText = "সেটআপ",
                        iconColor = SamityNavyPrimary,
                        onClick = {
                            currentScreen = Screen.SamitySetup
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "লেটারহেড ও প্যাড সেটিংস" else "Letterhead Settings",
                        icon = Icons.Default.Description,
                        selected = currentScreen is Screen.Letterhead,
                        iconColor = Color(0xFF4F46E5),
                        onClick = {
                            currentScreen = Screen.Letterhead
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "প্রত্যয়ন ও অফিস নথি" else "Document Generator",
                        icon = Icons.Default.Print,
                        selected = currentScreen is Screen.DocumentGenerator,
                        iconColor = Color(0xFF0891B2),
                        onClick = {
                            currentScreen = Screen.DocumentGenerator
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "লেনদেন অনুমোদন ওয়ার্কফ্লো" else "Approval Workflow",
                        icon = Icons.Default.CheckCircle,
                        selected = currentScreen is Screen.ApprovalWorkflow,
                        iconColor = Color(0xFF10B981),
                        onClick = {
                            currentScreen = Screen.ApprovalWorkflow
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "অডিট ট্রেইল ও নিরাপত্তা" else "Audit Trail & Logs",
                        icon = Icons.Default.Security,
                        selected = currentScreen is Screen.AuditTrail,
                        iconColor = SamityNavyDark,
                        onClick = {
                            currentScreen = Screen.AuditTrail
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))

                    // Group 4: সিস্টেম ও ক্লাউড
                    DrawerSectionHeader(title = if (isBn) "ক্লাউড ও সিস্টেম কন্ট্রোল" else "System & Cloud Hub")

                    DrawerItem(
                        title = if (isBn) "ভার্সেল ক্লাউড ও ওয়েব সিঙ্ক" else "Vercel Cloud Sync",
                        icon = Icons.Default.CloudSync,
                        selected = currentScreen is Screen.VercelCloudSync,
                        badgeText = "Live",
                        iconColor = Color(0xFF10B981),
                        onClick = {
                            currentScreen = Screen.VercelCloudSync
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "ডাটাবেজ ব্যাকআপ ও এক্সপোর্ট" else "Database Backup & Export",
                        icon = Icons.Default.Backup,
                        selected = currentScreen is Screen.BackupExport,
                        iconColor = Color(0xFF6366F1),
                        onClick = {
                            currentScreen = Screen.BackupExport
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "রোল ও পারমিশন কন্ট্রোল" else "Role Permissions",
                        icon = Icons.Default.AdminPanelSettings,
                        selected = currentScreen is Screen.RolePermissionManager,
                        iconColor = SamityNavyPrimary,
                        onClick = {
                            currentScreen = Screen.RolePermissionManager
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "সাশ কমার্শিয়াল কন্ট্রোল" else "SaaS Admin Control",
                        icon = Icons.Default.Tune,
                        selected = currentScreen is Screen.SaaSAdminControl,
                        iconColor = SamityGold,
                        onClick = {
                            currentScreen = Screen.SaaSAdminControl
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "সিস্টেম সেটিংস ও ভাষা" else "System Settings",
                        icon = Icons.Default.Settings,
                        selected = currentScreen is Screen.Settings,
                        iconColor = Color(0xFF64748B),
                        onClick = {
                            currentScreen = Screen.Settings
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    DrawerItem(
                        title = if (isBn) "লগইন ও এক্টিভেশন" else "Login & Activation",
                        icon = Icons.Default.Lock,
                        selected = currentScreen is Screen.LoginAndActivation,
                        iconColor = SamityNavyPrimary,
                        onClick = {
                            currentScreen = Screen.LoginAndActivation
                            coroutineScope.launch { drawerState.close() }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Drawer Footer
                    Surface(
                        color = Color(0xFFE2E8F0).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Samity Kitab 360 ERP Pro",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyDark
                            )
                            Text(
                                text = if (isBn) "প্লে স্টোর রেডি v1.0.0 | সম্পূর্ণ অফলাইন ও ক্লাউড সিঙ্ক" else "Play Store Ready v1.0.0 | Offline + Cloud",
                                fontSize = 9.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                SamityTopAppBar(
                    repository = repository,
                    onOpenSamitySetup = { currentScreen = Screen.SamitySetup },
                    onOpenLogin = { currentScreen = Screen.LoginAndActivation },
                    onOpenSaaSControl = { currentScreen = Screen.SaaSAdminControl },
                    onOpenRolePermissions = { currentScreen = Screen.RolePermissionManager },
                    onOpenVercelSync = { currentScreen = Screen.VercelCloudSync },
                    onOpenSettings = { currentScreen = Screen.Settings },
                    onToggleDrawer = {
                        coroutineScope.launch {
                            if (drawerState.isOpen) drawerState.close() else drawerState.open()
                        }
                    },
                    onNavigateToMembers = { currentScreen = Screen.Members },
                    onNavigateToDeposits = { currentScreen = Screen.Deposits },
                    onNavigateToBulkDeposit = { currentScreen = Screen.BulkDeposit },
                    onNavigateToExpenses = { currentScreen = Screen.Expenses },
                    onNavigateToReminders = { currentScreen = Screen.Reminders },
                    onNavigateToPaymentStatus = { currentScreen = Screen.PaymentStatus },
                    onNavigateToFundBalance = { currentScreen = Screen.FundBalance },
                    onNavigateToReports = { currentScreen = Screen.Reports },
                    onOpenLetterhead = { currentScreen = Screen.Letterhead },
                    onOpenDocumentGenerator = { currentScreen = Screen.DocumentGenerator },
                    onOpenAuditTrail = { currentScreen = Screen.AuditTrail },
                    onOpenApprovalWorkflow = { currentScreen = Screen.ApprovalWorkflow },
                    onOpenBackupExport = { currentScreen = Screen.BackupExport },
                    canNavigateBack = (currentScreen !is Screen.Dashboard),
                    onNavigateBack = { currentScreen = Screen.Dashboard },
                    screenTitle = when (currentScreen) {
                        is Screen.Members -> if (isBn) "সদস্য ব্যবস্থাপনা" else "Members"
                        is Screen.Deposits -> if (isBn) "চাঁদা ও জমা" else "Deposits"
                        is Screen.BulkDeposit -> if (isBn) "বাল্ক জমা স্লিপ" else "Bulk Slip"
                        is Screen.PaymentStatus -> if (isBn) "আদায় ও বকেয়া" else "Payment Status"
                        is Screen.Reminders -> if (isBn) "বকেয়া তাগিদ" else "Reminders"
                        is Screen.Expenses -> if (isBn) "সমিতি ব্যয়" else "Expenses"
                        is Screen.FundBalance -> if (isBn) "তহবিল ব্যালেন্স" else "Fund Balance"
                        is Screen.Reports -> if (isBn) "আর্থিক রিপোর্ট" else "Reports"
                        is Screen.Letterhead -> if (isBn) "লেটারহেড প্যাড" else "Letterhead"
                        is Screen.DocumentGenerator -> if (isBn) "নথি জেনারেটর" else "Documents"
                        is Screen.AuditTrail -> if (isBn) "অডিট ট্রেইল" else "Audit Trail"
                        is Screen.SamitySetup -> if (isBn) "সমিতি সেটআপ" else "Samity Setup"
                        is Screen.MemberPortal -> if (isBn) "সদস্য পোর্টাল" else "Member Portal"
                        is Screen.LoginAndActivation -> if (isBn) "লগইন ও এক্টিভেশন" else "Login"
                        is Screen.SaaSAdminControl -> if (isBn) "সাশ কন্ট্রোল" else "SaaS Control"
                        is Screen.RolePermissionManager -> if (isBn) "রোল পারমিশন" else "Role Permissions"
                        is Screen.VercelCloudSync -> if (isBn) "ভার্সেল সিঙ্ক" else "Cloud Sync"
                        is Screen.ApprovalWorkflow -> if (isBn) "অনুমোদন ওয়ার্কফ্লো" else "Approvals"
                        is Screen.BackupExport -> if (isBn) "ব্যাকআপ ও এক্সপোর্ট" else "Backup"
                        is Screen.Settings -> if (isBn) "সিস্টেম সেটিংস" else "Settings"
                        is Screen.ReceiptView -> if (isBn) "জমা মানিসিপ্ট" else "Receipt"
                        is Screen.DueNoticeView -> if (isBn) "বকেয়া নোটিশ" else "Due Notice"
                        else -> null
                    }
                )
            },
        bottomBar = {
            if (currentScreen !is Screen.ReceiptView &&
                currentScreen !is Screen.DueNoticeView &&
                currentScreen !is Screen.BulkDeposit &&
                currentScreen !is Screen.SamitySetup &&
                currentScreen !is Screen.LoginAndActivation &&
                currentScreen !is Screen.SaaSAdminControl &&
                currentScreen !is Screen.RolePermissionManager &&
                currentScreen !is Screen.VercelCloudSync &&
                currentScreen !is Screen.ApprovalWorkflow &&
                currentScreen !is Screen.BackupExport &&
                currentScreen !is Screen.Settings
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    if (isGeneralMember) {
                        NavigationBarItem(
                            selected = currentScreen is Screen.MemberPortal,
                            onClick = { currentScreen = Screen.MemberPortal },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Portal") },
                            label = { Text(if (isBn) "আমার প্রোফাইল" else "My Portal", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )
                        NavigationBarItem(
                            selected = currentScreen is Screen.FundBalance,
                            onClick = { currentScreen = Screen.FundBalance },
                            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Fund") },
                            label = { Text(LanguageManager.navFundBalance, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )
                    } else {
                        // Admin / Executive / Treasurer Navigation Items
                        NavigationBarItem(
                            selected = currentScreen is Screen.Dashboard,
                            onClick = { currentScreen = Screen.Dashboard },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text(LanguageManager.navDashboard, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.Members,
                            onClick = { currentScreen = Screen.Members },
                            icon = { Icon(Icons.Default.Group, contentDescription = "Members") },
                            label = { Text(LanguageManager.navMembers, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.Deposits,
                            onClick = { currentScreen = Screen.Deposits },
                            icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Deposits") },
                            label = { Text(LanguageManager.navDeposits, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.PaymentStatus,
                            onClick = { currentScreen = Screen.PaymentStatus },
                            icon = { Icon(Icons.Default.Payments, contentDescription = "Status") },
                            label = { Text(LanguageManager.navPaymentStatus, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.Reminders,
                            onClick = { currentScreen = Screen.Reminders },
                            icon = { Icon(Icons.Default.NotificationsActive, contentDescription = "Reminders") },
                            label = { Text(LanguageManager.navReminders, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SamityNavyPrimary,
                                selectedTextColor = SamityNavyPrimary,
                                indicatorColor = SamityNavyPrimary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            RealtimeSyncBanner(
                notification = repository.syncEngine.latestNotification,
                isBn = isBn,
                onDismiss = { repository.syncEngine.dismissNotification() }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val screen = currentScreen) {
                is Screen.Dashboard -> {
                    if (isGeneralMember) {
                        MemberPortalScreen(
                            repository = repository,
                            onViewReceipt = { dep -> currentScreen = Screen.ReceiptView(dep) }
                        )
                    } else {
                        AuthorizedScreen(
                            requiredPermission = AppPermission.VIEW_DASHBOARD,
                            userSession = repository.userSession,
                            targetSamityId = repository.activeSamityId,
                            onNavigateBack = { currentScreen = Screen.MemberPortal },
                            onSwitchRole = { repository.switchRole(it) }
                        ) {
                            DashboardScreen(
                                repository = repository,
                                onNavigateToMembers = { currentScreen = Screen.Members },
                                onNavigateToDeposits = { currentScreen = Screen.Deposits },
                                onNavigateToBulkDeposit = { currentScreen = Screen.BulkDeposit },
                                onNavigateToExpenses = { currentScreen = Screen.Expenses },
                                onNavigateToReminders = { currentScreen = Screen.Reminders },
                                onNavigateToPaymentStatus = { currentScreen = Screen.PaymentStatus },
                                onNavigateToFundBalance = { currentScreen = Screen.FundBalance },
                                onNavigateToReports = { currentScreen = Screen.Reports },
                                onNavigateToLetterhead = { currentScreen = Screen.Letterhead },
                                onNavigateToDocumentGenerator = { currentScreen = Screen.DocumentGenerator },
                                onNavigateToAuditTrail = { currentScreen = Screen.AuditTrail },
                                onNavigateToSamitySetup = { currentScreen = Screen.SamitySetup },
                                onNavigateToApprovalWorkflow = { currentScreen = Screen.ApprovalWorkflow },
                                onNavigateToBackupExport = { currentScreen = Screen.BackupExport },
                                onNavigateToSettings = { currentScreen = Screen.Settings },
                                onViewReceipt = { dep -> currentScreen = Screen.ReceiptView(dep) },
                                onNavigateToVercelSync = { currentScreen = Screen.VercelCloudSync }
                            )
                        }
                    }
                }

                is Screen.Members -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_MEMBERS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        MemberListScreen(
                            repository = repository,
                            onRecordDepositForMember = { mem ->
                                currentScreen = Screen.Deposits
                            },
                            onSendReminderForMember = { mem ->
                                currentScreen = Screen.DueNoticeView(mem)
                            }
                        )
                    }
                }

                is Screen.Deposits -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_DEPOSITS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        DepositScreen(
                            repository = repository,
                            onViewReceipt = { dep -> currentScreen = Screen.ReceiptView(dep) },
                            onNavigateToBulkDeposit = { currentScreen = Screen.BulkDeposit }
                        )
                    }
                }

                is Screen.BulkDeposit -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_BULK_DEPOSIT,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Deposits },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        BulkDepositScreen(
                            repository = repository,
                            onBack = { currentScreen = Screen.Deposits },
                            onViewReceipts = { currentScreen = Screen.Deposits }
                        )
                    }
                }

                is Screen.PaymentStatus -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_PAYMENT_STATUS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        PaymentStatusScreen(
                            repository = repository,
                            onRecordDeposit = { mem -> currentScreen = Screen.Deposits },
                            onSendReminder = { mem -> currentScreen = Screen.Reminders },
                            onViewDueNotice = { mem -> currentScreen = Screen.DueNoticeView(mem) },
                            onViewReceipt = { dep -> currentScreen = Screen.ReceiptView(dep) }
                        )
                    }
                }

                is Screen.Reminders -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_REMINDERS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        ReminderCenterScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.Expenses -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_EXPENSES,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        ExpenseScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.FundBalance -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_FUND_BALANCE,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        FundBalanceScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.Reports -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_REPORTS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        ReportsScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.Letterhead -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_LETTERHEAD,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        LetterheadSettingsScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.DocumentGenerator -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_DOCUMENTS,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        DocumentGeneratorScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.AuditTrail -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_AUDIT_TRAIL,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        AuditTrailScreen(
                            repository = repository
                        )
                    }
                }

                is Screen.SamitySetup -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_SAMITY_SETUP,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        SamitySetupScreen(
                            repository = repository,
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }
                }

                is Screen.MemberPortal -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_MEMBER_PORTAL,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        MemberPortalScreen(
                            repository = repository,
                            onViewReceipt = { dep -> currentScreen = Screen.ReceiptView(dep) }
                        )
                    }
                }

                is Screen.ReceiptView -> {
                    PaidReceiptScreen(
                        repository = repository,
                        deposit = screen.deposit,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.DueNoticeView -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.VIEW_DUE_NOTICE,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.PaymentStatus },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        DueNoticeScreen(
                            repository = repository,
                            member = screen.member,
                            onBack = { currentScreen = Screen.PaymentStatus }
                        )
                    }
                }

                is Screen.LoginAndActivation -> {
                    LoginAndActivationScreen(
                        repository = repository,
                        onLoginSuccess = {
                            currentScreen = if (repository.currentUserRole == UserRole.GENERAL_MEMBER)
                                Screen.MemberPortal
                            else
                                Screen.Dashboard
                        }
                    )
                }

                is Screen.SaaSAdminControl -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.MANAGE_SAMITY_CONFIG,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        SaaSAdminControlScreen(
                            repository = repository,
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }
                }

                is Screen.RolePermissionManager -> {
                    AuthorizedScreen(
                        requiredPermission = AppPermission.MANAGE_SAMITY_CONFIG,
                        userSession = repository.userSession,
                        targetSamityId = repository.activeSamityId,
                        onNavigateBack = { currentScreen = Screen.Dashboard },
                        onSwitchRole = { repository.switchRole(it) }
                    ) {
                        RolePermissionManagerScreen(
                            repository = repository,
                            onBack = { currentScreen = Screen.Dashboard }
                        )
                    }
                }

                is Screen.VercelCloudSync -> {
                    VercelCloudSyncScreen(
                        repository = repository,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.ApprovalWorkflow -> {
                    ApprovalWorkflowScreen(
                        repository = repository,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.BackupExport -> {
                    BackupExportScreen(
                        repository = repository,
                        onBack = { currentScreen = Screen.Dashboard }
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        repository = repository,
                        onBack = { currentScreen = Screen.Dashboard },
                        onNavigateToLetterhead = { currentScreen = Screen.Letterhead },
                        onNavigateToBackup = { currentScreen = Screen.BackupExport },
                        onNavigateToVercelSync = { currentScreen = Screen.VercelCloudSync },
                        onNavigateToRoles = { currentScreen = Screen.RolePermissionManager },
                        onNavigateToApprovals = { currentScreen = Screen.ApprovalWorkflow }
                    )
                }
        }
    }
}
}
}
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    badgeText: String? = null,
    iconColor: Color = SamityNavyPrimary,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 12.5.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                )
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (selected) SamityNavyPrimary else Color(0xFFE2E8F0)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selected) Color.White else Color(0xFF475569),
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        },
        selected = selected,
        onClick = onClick,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) SamityNavyPrimary else iconColor,
                modifier = Modifier.size(19.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = SamityNavyPrimary.copy(alpha = 0.12f),
            selectedTextColor = SamityNavyPrimary,
            unselectedTextColor = Color(0xFF1E293B)
        ),
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
    )
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B),
        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
    )
}
