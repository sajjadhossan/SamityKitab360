package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
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
import com.example.ui.theme.SamityNavyPrimary

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

    // Navigation back stack handler
    BackHandler(enabled = currentScreen !is Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    // Role-dependent interface routing
    val isGeneralMember = repository.currentUserRole == UserRole.GENERAL_MEMBER

    Scaffold(
        topBar = {
            SamityTopAppBar(
                repository = repository,
                onOpenSamitySetup = { currentScreen = Screen.SamitySetup },
                onOpenLogin = { currentScreen = Screen.LoginAndActivation },
                onOpenSaaSControl = { currentScreen = Screen.SaaSAdminControl },
                onOpenRolePermissions = { currentScreen = Screen.RolePermissionManager },
                onOpenVercelSync = { currentScreen = Screen.VercelCloudSync },
                onOpenSettings = { currentScreen = Screen.Settings }
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
