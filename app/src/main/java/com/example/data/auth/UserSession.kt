package com.example.data.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.model.AppUser
import com.example.data.model.UserRole
import java.util.UUID

/**
 * Granular permissions for the Samity Kitab 360 ERP system.
 */
enum class AppPermission(val titleBn: String, val titleEn: String) {
    // Screen Access Permissions
    VIEW_DASHBOARD("ড্যাশবোর্ড দর্শন", "View Dashboard"),
    VIEW_MEMBERS("সদস্য তালিকা দর্শন", "View Member Directory"),
    VIEW_DEPOSITS("ব্যাংক জমা দর্শন", "View Deposits"),
    VIEW_BULK_DEPOSIT("বাল্ক জমা দর্শন", "View Bulk Deposit"),
    VIEW_PAYMENT_STATUS("পরিশোধ স্থিতি দর্শন", "View Payment Status"),
    VIEW_REMINDERS("রিমাইন্ডার সেন্টার দর্শন", "View Reminder Center"),
    VIEW_EXPENSES("ব্যয় হিসাব দর্শন", "View Expenses"),
    VIEW_FUND_BALANCE("তহবিল স্থিতি দর্শন", "View Fund & Balance"),
    VIEW_REPORTS("রিপোর্ট দর্শন", "View Reports"),
    VIEW_LETTERHEAD("লেটারহেড সেটিংস দর্শন", "View Letterhead Settings"),
    VIEW_DOCUMENTS("নথি জেনারেটর দর্শন", "View Document Generator"),
    VIEW_AUDIT_TRAIL("অডিট লগ দর্শন", "View Audit Trail"),
    VIEW_SAMITY_SETUP("সমিতি সেটআপ দর্শন", "View Samity Setup"),
    VIEW_MEMBER_PORTAL("সদস্য পোর্টাল দর্শন", "View Member Portal"),
    VIEW_RECEIPT("রিসিট দর্শন", "View Paid Receipt"),
    VIEW_DUE_NOTICE("বকেয়া নোটিশ দর্শন", "View Due Notice"),

    // CRUD & Administrative Permissions
    CREATE_MEMBER("নতুন সদস্য তৈরি", "Create Member"),
    UPDATE_MEMBER("সদস্য তথ্য সংশোধন", "Update Member"),
    DELETE_MEMBER("সদস্য অপসারণ", "Delete Member"),
    CREATE_DEPOSIT("নতুন জমা এন্ট্রি", "Create Deposit"),
    UPDATE_DEPOSIT("জমা সংশোধন", "Update Deposit"),
    DELETE_DEPOSIT("জমা বাতিল", "Delete Deposit"),
    CREATE_BULK_DEPOSIT("বাল্ক স্লিপ এন্ট্রি", "Create Bulk Deposit"),
    CREATE_EXPENSE("ব্যয় ভাউচার তৈরি", "Create Expense"),
    UPDATE_EXPENSE("ব্যয় সংশোধন", "Update Expense"),
    DELETE_EXPENSE("ব্যয় বাতিল", "Delete Expense"),
    APPROVE_TRANSACTION("লেনদেন অনুমোদন", "Approve Transaction"),
    SEND_REMINDER("রিমাইন্ডার প্রেরণ", "Send Reminder"),
    MANAGE_SAMITY_CONFIG("সমিতি প্রোফাইল সম্পাদনা", "Manage Samity Config"),
    MANAGE_LETTERHEAD("লেটারহেড ও স্বাক্ষর সম্পাদনা", "Manage Letterhead & Signatures"),
    VIEW_AUDIT_LOGS("অডিট ট্রেইল যাচাই", "View Security Audit Logs")
}

/**
 * UserSession Manager
 * Tracks the authenticated user's role, their assigned 'samity_id', session token,
 * and manages role-based access control (RBAC).
 */
class UserSession(
    val sessionManager: UserSessionManager = UserSessionManager(),
    initialUser: AppUser = AppUser(
        id = sessionManager.currentUserId,
        name = sessionManager.currentUserName,
        samityId = sessionManager.currentSamityId,
        role = sessionManager.currentRole,
        memberId = sessionManager.memberId,
        email = sessionManager.email ?: "admin@samity.org",
        phone = sessionManager.phone ?: "01700-111222"
    )
) {
    var currentUser by mutableStateOf(initialUser)
        private set

    var sessionToken by mutableStateOf(sessionManager.sessionToken)
        private set

    var loginTimestamp by mutableStateOf(sessionManager.loginTimestamp)
        private set

    var isAuthenticated by mutableStateOf(sessionManager.isLoggedIn)
        private set

    val currentUserRole: UserRole
        get() = sessionManager.currentRole

    val assignedSamityId: String
        get() = sessionManager.currentSamityId

    /**
     * Map defining permissions granted to each UserRole.
     * Can be dynamically customized by the Samity Admin for internal role management.
     */
    var rolePermissionsMap by mutableStateOf<Map<UserRole, Set<AppPermission>>>(
        mapOf(
            UserRole.SUPER_ADMIN to AppPermission.values().toSet(),

            UserRole.PRESIDENT to setOf(
                // Screens
                AppPermission.VIEW_DASHBOARD,
                AppPermission.VIEW_MEMBERS,
                AppPermission.VIEW_DEPOSITS,
                AppPermission.VIEW_BULK_DEPOSIT,
                AppPermission.VIEW_PAYMENT_STATUS,
                AppPermission.VIEW_REMINDERS,
                AppPermission.VIEW_EXPENSES,
                AppPermission.VIEW_FUND_BALANCE,
                AppPermission.VIEW_REPORTS,
                AppPermission.VIEW_LETTERHEAD,
                AppPermission.VIEW_DOCUMENTS,
                AppPermission.VIEW_AUDIT_TRAIL,
                AppPermission.VIEW_MEMBER_PORTAL,
                AppPermission.VIEW_RECEIPT,
                AppPermission.VIEW_DUE_NOTICE,
                // Actions
                AppPermission.CREATE_MEMBER,
                AppPermission.UPDATE_MEMBER,
                AppPermission.CREATE_DEPOSIT,
                AppPermission.CREATE_BULK_DEPOSIT,
                AppPermission.CREATE_EXPENSE,
                AppPermission.APPROVE_TRANSACTION,
                AppPermission.SEND_REMINDER,
                AppPermission.MANAGE_SAMITY_CONFIG,
                AppPermission.MANAGE_LETTERHEAD,
                AppPermission.VIEW_AUDIT_LOGS
            ),

            UserRole.GENERAL_SECRETARY to setOf(
                // Screens
                AppPermission.VIEW_DASHBOARD,
                AppPermission.VIEW_MEMBERS,
                AppPermission.VIEW_DEPOSITS,
                AppPermission.VIEW_PAYMENT_STATUS,
                AppPermission.VIEW_REMINDERS,
                AppPermission.VIEW_EXPENSES,
                AppPermission.VIEW_FUND_BALANCE,
                AppPermission.VIEW_REPORTS,
                AppPermission.VIEW_DOCUMENTS,
                AppPermission.VIEW_MEMBER_PORTAL,
                AppPermission.VIEW_RECEIPT,
                AppPermission.VIEW_DUE_NOTICE,
                // Actions
                AppPermission.CREATE_MEMBER,
                AppPermission.UPDATE_MEMBER,
                AppPermission.SEND_REMINDER,
                AppPermission.CREATE_EXPENSE
            ),

            UserRole.TREASURER to setOf(
                // Screens
                AppPermission.VIEW_DASHBOARD,
                AppPermission.VIEW_MEMBERS,
                AppPermission.VIEW_DEPOSITS,
                AppPermission.VIEW_BULK_DEPOSIT,
                AppPermission.VIEW_PAYMENT_STATUS,
                AppPermission.VIEW_REMINDERS,
                AppPermission.VIEW_EXPENSES,
                AppPermission.VIEW_FUND_BALANCE,
                AppPermission.VIEW_REPORTS,
                AppPermission.VIEW_RECEIPT,
                AppPermission.VIEW_DUE_NOTICE,
                // Actions
                AppPermission.CREATE_DEPOSIT,
                AppPermission.UPDATE_DEPOSIT,
                AppPermission.CREATE_BULK_DEPOSIT,
                AppPermission.CREATE_EXPENSE,
                AppPermission.UPDATE_EXPENSE,
                AppPermission.SEND_REMINDER
            ),

            UserRole.GENERAL_MEMBER to setOf(
                // Screens
                AppPermission.VIEW_MEMBER_PORTAL,
                AppPermission.VIEW_FUND_BALANCE,
                AppPermission.VIEW_RECEIPT
                // No administrative or CRUD permissions
            )
        )
    )
        private set

    fun getPermissionsForRole(role: UserRole): Set<AppPermission> {
        return rolePermissionsMap[role] ?: emptySet()
    }

    /**
     * Enables Samity Admin to configure and customize which permissions each role has.
     */
    fun updateRolePermissions(role: UserRole, permissions: Set<AppPermission>) {
        val updated = rolePermissionsMap.toMutableMap()
        updated[role] = permissions
        rolePermissionsMap = updated
    }

    /**
     * Authenticates a user into the session and persists via UserSessionManager.
     */
    fun login(user: AppUser) {
        currentUser = user
        sessionToken = "session_${UUID.randomUUID()}"
        loginTimestamp = System.currentTimeMillis()
        isAuthenticated = true
        sessionManager.saveSession(
            userId = user.id,
            role = user.role,
            samityId = user.samityId,
            userName = user.name,
            memberId = user.memberId,
            phone = user.phone,
            email = user.email,
            token = sessionToken
        )
    }

    /**
     * Member individual portal login using their Member record.
     */
    fun loginAsMember(member: com.example.data.model.Member) {
        val user = AppUser(
            id = "user_${member.id}",
            name = "${member.nameBn} (${member.memberCode})",
            samityId = member.samityId,
            role = UserRole.GENERAL_MEMBER,
            memberId = member.id,
            email = member.email,
            phone = member.phone
        )
        login(user)
    }

    /**
     * Terminates the current session and clears persisted session.
     */
    fun logout() {
        isAuthenticated = false
        sessionManager.clearSession()
    }

    /**
     * Switches the current role and active user, updating persisted session.
     */
    fun switchRole(newRole: UserRole, userLookup: (UserRole) -> AppUser?) {
        val user = userLookup(newRole) ?: currentUser.copy(role = newRole)
        currentUser = user
        loginTimestamp = System.currentTimeMillis()
        sessionManager.saveSession(
            userId = user.id,
            role = newRole,
            samityId = user.samityId,
            userName = user.name,
            memberId = user.memberId,
            phone = user.phone,
            email = user.email
        )
    }

    /**
     * Changes the user's assigned Samity (allowed for Super Admin or when multi-tenant switching).
     */
    fun switchSamity(newSamityId: String) {
        currentUser = currentUser.copy(samityId = newSamityId)
        sessionManager.updateActiveSamity(newSamityId)
    }

    /**
     * Checks if the user's active role has the requested permission.
     */
    fun hasPermission(permission: AppPermission): Boolean {
        if (!isAuthenticated) return false
        val rolePerms = rolePermissionsMap[currentUserRole] ?: emptySet()
        return rolePerms.contains(permission)
    }

    /**
     * Multi-tenant scope check:
     * Verifies if the user is authorized to view or mutate data for a specific Samity ID.
     * SUPER_ADMIN can access any Samity; all other roles can only access their assigned 'samity_id'.
     */
    fun canAccessSamity(targetSamityId: String): Boolean {
        if (!isAuthenticated) return false
        if (currentUserRole == UserRole.SUPER_ADMIN) return true
        return assignedSamityId == targetSamityId
    }

    /**
     * Comprehensive check verifying both the permission and the target samity tenant scope.
     */
    fun canPerform(permission: AppPermission, targetSamityId: String? = null): Boolean {
        if (!hasPermission(permission)) return false
        if (targetSamityId != null && !canAccessSamity(targetSamityId)) return false
        return true
    }

    /**
     * Returns true if the user's role allows accessing the given Screen route.
     */
    fun canAccessScreen(screenClass: String): Boolean {
        val requiredPerm = getRequiredPermissionForScreen(screenClass) ?: return true
        return hasPermission(requiredPerm)
    }

    companion object {
        fun getRequiredPermissionForScreen(screenClass: String): AppPermission? {
            return when {
                screenClass.contains("Dashboard") -> AppPermission.VIEW_DASHBOARD
                screenClass.contains("Members") -> AppPermission.VIEW_MEMBERS
                screenClass.contains("Deposits") -> AppPermission.VIEW_DEPOSITS
                screenClass.contains("BulkDeposit") -> AppPermission.VIEW_BULK_DEPOSIT
                screenClass.contains("PaymentStatus") -> AppPermission.VIEW_PAYMENT_STATUS
                screenClass.contains("Reminders") -> AppPermission.VIEW_REMINDERS
                screenClass.contains("Expenses") -> AppPermission.VIEW_EXPENSES
                screenClass.contains("FundBalance") -> AppPermission.VIEW_FUND_BALANCE
                screenClass.contains("Reports") -> AppPermission.VIEW_REPORTS
                screenClass.contains("Letterhead") -> AppPermission.VIEW_LETTERHEAD
                screenClass.contains("DocumentGenerator") -> AppPermission.VIEW_DOCUMENTS
                screenClass.contains("AuditTrail") -> AppPermission.VIEW_AUDIT_TRAIL
                screenClass.contains("SamitySetup") -> AppPermission.VIEW_SAMITY_SETUP
                screenClass.contains("MemberPortal") -> AppPermission.VIEW_MEMBER_PORTAL
                screenClass.contains("ReceiptView") -> AppPermission.VIEW_RECEIPT
                screenClass.contains("DueNoticeView") -> AppPermission.VIEW_DUE_NOTICE
                else -> null
            }
        }
    }
}
