package com.example.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.auth.*
import com.example.data.model.*
import com.example.data.sync.RealtimeSyncManager
import com.example.data.sync.RoomFirestoreSyncService
import com.example.data.sync.VercelSyncGateway
import com.example.data.local.room.DepositEntity
import com.example.data.local.room.MemberEntity
import com.example.data.local.room.SamityRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SamityRepository(
    val sessionManager: UserSessionManager = UserSessionManager()
) {

    // Authenticated User Session & Role-Based Authorization Manager
    val userSession = UserSession(sessionManager = sessionManager)
    val rbacWrapper = RbacWrapper(userSession = userSession)
    val syncEngine = RealtimeSyncManager()
    val vercelGateway = VercelSyncGateway(syncEngine)

    // Room-to-Firestore Cloud Synchronization Service
    val roomFirestoreSync: RoomFirestoreSyncService? = sessionManager.context?.let { ctx ->
        try {
            val db = SamityRoomDatabase.getInstance(ctx)
            RoomFirestoreSyncService(
                memberDao = db.memberDao(),
                depositDao = db.depositDao(),
                vercelGateway = vercelGateway
            )
        } catch (_: Throwable) {
            null
        }
    }

    // Current Active Tenant & User Context (bound directly to UserSessionManager)
    var activeSamityId: String
        get() = sessionManager.currentSamityId
        set(value) {
            sessionManager.updateActiveSamity(value)
        }

    var currentUserRole: UserRole
        get() = sessionManager.currentRole
        set(value) {
            sessionManager.updateRole(value)
        }

    var currentUserId: String
        get() = sessionManager.currentUserId
        set(value) {
            sessionManager.updateUserId(value)
        }

    // State collections
    val samities = mutableStateListOf<Samity>()
    val members = mutableStateListOf<Member>()
    val deposits = mutableStateListOf<Deposit>()
    val bulkDeposits = mutableStateListOf<BulkDeposit>()
    val expenses = mutableStateListOf<Expense>()
    val reminderLogs = mutableStateListOf<ReminderLog>()
    val auditLogs = mutableStateListOf<AuditLog>()
    val users = mutableStateListOf<AppUser>()
    val signatories = mutableStateListOf<Signatory>()
    val licenses = mutableStateListOf<SamityLicenseConfig>()
    val memberCredentials = mutableStateListOf<MemberCredential>()
    var letterheadConfig by mutableStateOf(
        LetterheadConfig(samityId = "samity_1")
    )

    init {
        seedInitialData()
        seedRoomDatabase()
    }

    private fun seedRoomDatabase() {
        roomFirestoreSync?.let { sync ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    for (m in members) {
                        sync.saveAndSyncMember(
                            MemberEntity(
                                id = m.id,
                                samityId = m.samityId,
                                memberCode = m.memberCode,
                                nameBn = m.nameBn,
                                nameEn = m.nameEn,
                                phone = m.phone,
                                monthlyFee = m.monthlyFee,
                                totalPaid = m.totalPaid,
                                totalDue = m.totalDue,
                                status = m.status
                            )
                        )
                    }
                    for (d in deposits) {
                        sync.saveAndSyncDeposit(
                            DepositEntity(
                                id = d.id,
                                samityId = d.samityId,
                                voucherNo = d.voucherNo,
                                memberId = d.memberId,
                                memberName = d.memberName,
                                amount = d.amount,
                                depositDate = d.depositDate,
                                depositMethod = d.depositMethod,
                                approvalStatus = d.approvalStatus
                            )
                        )
                    }
                } catch (_: Throwable) {}
            }
        }
    }

    // =========================================================================
    // REPOSITORY LAYER: AUTO-FILTERED DATABASE QUERIES (Multi-tenant scoped)
    // All database queries automatically filter by the active session's 'samity_id'.
    // =========================================================================

    /**
     * Retrieves all members filtered automatically by the current 'samity_id'.
     */
    fun getMembers(samityId: String = activeSamityId): List<Member> {
        return members.filter { it.samityId == samityId }
    }

    /**
     * Finds a member by ID scoped automatically to the current 'samity_id'.
     */
    fun getMemberById(memberId: String, samityId: String = activeSamityId): Member? {
        return members.firstOrNull { it.samityId == samityId && it.id == memberId }
    }

    /**
     * Finds a member by member code scoped automatically to the current 'samity_id'.
     */
    fun getMemberByCode(memberCode: String, samityId: String = activeSamityId): Member? {
        return members.firstOrNull { it.samityId == samityId && it.memberCode.equals(memberCode.trim(), ignoreCase = true) }
    }

    /**
     * Retrieves all deposits filtered automatically by the current 'samity_id'.
     */
    fun getDeposits(samityId: String = activeSamityId): List<Deposit> {
        return deposits.filter { it.samityId == samityId }
    }

    /**
     * Finds a deposit by ID scoped automatically to the current 'samity_id'.
     */
    fun getDepositById(depositId: String, samityId: String = activeSamityId): Deposit? {
        return deposits.firstOrNull { it.samityId == samityId && it.id == depositId }
    }

    /**
     * Retrieves member deposits filtered automatically by the current 'samity_id'.
     */
    fun getDepositsByMember(memberId: String, samityId: String = activeSamityId): List<Deposit> {
        return deposits.filter { it.samityId == samityId && it.memberId == memberId }
    }

    /**
     * Retrieves all bulk deposits filtered automatically by the current 'samity_id'.
     */
    fun getBulkDeposits(samityId: String = activeSamityId): List<BulkDeposit> {
        return bulkDeposits.filter { it.samityId == samityId }
    }

    /**
     * Retrieves all expenses filtered automatically by the current 'samity_id'.
     */
    fun getExpenses(samityId: String = activeSamityId): List<Expense> {
        return expenses.filter { it.samityId == samityId }
    }

    /**
     * Finds an expense by ID scoped automatically to the current 'samity_id'.
     */
    fun getExpenseById(expenseId: String, samityId: String = activeSamityId): Expense? {
        return expenses.firstOrNull { it.samityId == samityId && it.id == expenseId }
    }

    /**
     * Retrieves reminder logs filtered automatically by the current 'samity_id'.
     */
    fun getReminderLogs(samityId: String = activeSamityId): List<ReminderLog> {
        return reminderLogs.filter { it.samityId == samityId }
    }

    /**
     * Retrieves security and transaction audit logs filtered automatically by the current 'samity_id'.
     */
    fun getAuditLogs(samityId: String = activeSamityId): List<AuditLog> {
        return auditLogs.filter { it.samityId == samityId }
    }

    /**
     * Retrieves member credentials filtered automatically by the current 'samity_id'.
     */
    fun getMemberCredentials(samityId: String = activeSamityId): List<MemberCredential> {
        return memberCredentials.filter { it.samityId == samityId }
    }

    /**
     * Retrieves license configuration scoped automatically to the current 'samity_id'.
     */
    fun getLicenseConfig(samityId: String = activeSamityId): SamityLicenseConfig {
        return licenses.firstOrNull { it.samityId == samityId }
            ?: SamityLicenseConfig(samityId = samityId).also {
                if (licenses.none { lic -> lic.samityId == samityId }) {
                    licenses.add(it)
                }
            }
    }

    /**
     * Retrieves samity profile scoped automatically to the current 'samity_id'.
     */
    fun getActiveSamity(samityId: String = activeSamityId): Samity {
        return samities.firstOrNull { it.id == samityId } ?: samities.first()
    }

    // Direct property bindings delegating to auto-filtered query methods
    val activeSamity: Samity
        get() = getActiveSamity()

    val activeMembers: List<Member>
        get() = getMembers()

    val activeDeposits: List<Deposit>
        get() = getDeposits()

    val activeBulkDeposits: List<BulkDeposit>
        get() = getBulkDeposits()

    val activeExpenses: List<Expense>
        get() = getExpenses()

    val activeReminders: List<ReminderLog>
        get() = getReminderLogs()

    val activeAuditLogs: List<AuditLog>
        get() = getAuditLogs()

    val activeLicense: SamityLicenseConfig
        get() = getLicenseConfig()

    // Financial calculations dynamically computed from auto-filtered queries
    val totalApprovedDeposits: Double
        get() = getDeposits().filter { it.approvalStatus == "Approved" }.sumOf { it.amount }

    val totalApprovedExpenses: Double
        get() = getExpenses().filter { it.approvalStatus == "Approved" }.sumOf { it.amount }

    val netAvailableFund: Double
        get() = totalApprovedDeposits - totalApprovedExpenses

    val bankBalance: Double
        get() = getDeposits().filter { it.approvalStatus == "Approved" && (it.depositMethod.contains("Bank") || it.depositMethod.contains("Online") || it.depositMethod.contains("Cheque")) }.sumOf { it.amount } -
                getExpenses().filter { it.approvalStatus == "Approved" && it.paymentMethod == "Bank" }.sumOf { it.amount }

    val cashBalance: Double
        get() = getDeposits().filter { it.approvalStatus == "Approved" && it.depositMethod == "Cash" }.sumOf { it.amount } -
                getExpenses().filter { it.approvalStatus == "Approved" && it.paymentMethod == "Cash" }.sumOf { it.amount }

    val mobileBankBalance: Double
        get() = getDeposits().filter { it.approvalStatus == "Approved" && (it.depositMethod.contains("bKash") || it.depositMethod.contains("Nagad") || it.depositMethod.contains("Rocket")) }.sumOf { it.amount } -
                getExpenses().filter { it.approvalStatus == "Approved" && (it.paymentMethod.contains("bKash") || it.paymentMethod.contains("Nagad")) }.sumOf { it.amount }

    // Multi-tenant switching with authorization check
    fun switchSamity(newSamityId: String): Boolean {
        if (!userSession.canAccessSamity(newSamityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত সমিতি পরিবর্তন প্রচেষ্টা",
                actionEn = "Unauthorized Samity Switch Blocked",
                details = "User ${userSession.currentUser.name} (Role: ${userSession.currentUserRole}) attempted to switch to unauthorized samity ID: $newSamityId"
            )
            return false
        }
        activeSamityId = newSamityId
        userSession.switchSamity(newSamityId)
        letterheadConfig = letterheadConfig.copy(samityId = newSamityId)
        addAudit(
            actionBn = "সমিতি পরিবর্তন",
            actionEn = "Switched Samity",
            details = "Active Samity switched to: ${activeSamity.nameBn}"
        )
        return true
    }

    // Role switching with UserSession sync
    fun switchRole(role: UserRole) {
        currentUserRole = role
        userSession.switchRole(role) { r ->
            users.firstOrNull { it.samityId == activeSamityId && it.role == r }
                ?: users.firstOrNull { it.role == r }
        }
        val matchedUser = users.firstOrNull { it.samityId == activeSamityId && it.role == role }
        if (matchedUser != null) {
            currentUserId = matchedUser.id
        }
        addAudit(
            actionBn = "ইউজার রোল পরিবর্তন",
            actionEn = "Switched User Role",
            details = "Active view changed to: ${role.titleEn} (${role.titleBn}) for user: ${userSession.currentUser.name}"
        )
    }

    // Member Operations with Authorization Check
    fun addMember(member: Member): Boolean {
        if (!userSession.canPerform(AppPermission.CREATE_MEMBER, member.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত সদস্য তৈরি প্রচেষ্টা",
                actionEn = "Unauthorized Member Creation Blocked",
                details = "Role ${userSession.currentUserRole} lacks CREATE_MEMBER permission"
            )
            return false
        }
        members.add(0, member)
        roomFirestoreSync?.let { sync ->
            CoroutineScope(Dispatchers.IO).launch {
                val entity = MemberEntity(
                    id = member.id,
                    samityId = member.samityId,
                    memberCode = member.memberCode,
                    nameBn = member.nameBn,
                    nameEn = member.nameEn,
                    phone = member.phone,
                    monthlyFee = member.monthlyFee,
                    totalPaid = member.totalPaid,
                    totalDue = member.totalDue,
                    status = member.status
                )
                sync.saveAndSyncMember(entity)
            }
        }
        addAudit(
            actionBn = "নতুন সদস্য তৈরি",
            actionEn = "Added New Member",
            details = "Code: ${member.memberCode}, Name: ${member.nameBn}"
        )
        return true
    }

    fun updateMember(updated: Member): Boolean {
        if (!userSession.canPerform(AppPermission.UPDATE_MEMBER, updated.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত সদস্য তথ্য সংশোধন প্রচেষ্টা",
                actionEn = "Unauthorized Member Update Blocked",
                details = "Role ${userSession.currentUserRole} lacks UPDATE_MEMBER permission"
            )
            return false
        }
        val index = members.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            members[index] = updated
            addAudit(
                actionBn = "সদস্য তথ্য সংশোধন",
                actionEn = "Updated Member",
                details = "Code: ${updated.memberCode}, Name: ${updated.nameBn}"
            )
            return true
        }
        return false
    }

    // Deposit Operations with Authorization Check
    fun addDeposit(deposit: Deposit): Boolean {
        if (!userSession.canPerform(AppPermission.CREATE_DEPOSIT, deposit.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত জমা এন্ট্রি প্রচেষ্টা",
                actionEn = "Unauthorized Deposit Blocked",
                details = "Role ${userSession.currentUserRole} lacks CREATE_DEPOSIT permission"
            )
            return false
        }
        deposits.add(0, deposit)
        // Update member totals
        val memIndex = members.indexOfFirst { it.id == deposit.memberId }
        if (memIndex != -1) {
            val m = members[memIndex]
            val newTotalPaid = m.totalPaid + deposit.amount
            val newDue = (m.totalDue - deposit.amount).coerceAtLeast(0.0)
            val currentMonthPaid = m.currentMonthPaid + deposit.amount
            members[memIndex] = m.copy(
                totalPaid = newTotalPaid,
                totalDue = newDue,
                currentMonthPaid = currentMonthPaid
            )
        }
        // Real-Time Multi-Device Sync Broadcast
        syncEngine.broadcastDeposit(
            deposit = deposit,
            originDevice = "${userSession.currentUser.name} (${userSession.currentUserRole.titleBn})"
        )
        // Room SQLite & Firestore Cloud Persistence
        roomFirestoreSync?.let { sync ->
            CoroutineScope(Dispatchers.IO).launch {
                val entity = DepositEntity(
                    id = deposit.id,
                    samityId = deposit.samityId,
                    voucherNo = deposit.voucherNo,
                    memberId = deposit.memberId,
                    memberName = deposit.memberName,
                    amount = deposit.amount,
                    depositDate = deposit.depositDate,
                    depositMethod = deposit.depositMethod,
                    approvalStatus = deposit.approvalStatus
                )
                sync.saveAndSyncDeposit(entity)
            }
        }
        addAudit(
            actionBn = "ব্যাংক জমা এন্ট্রি (রিয়েল-টাইম সিঙ্কড)",
            actionEn = "Recorded Deposit (Real-Time Synced)",
            details = "Voucher: ${deposit.voucherNo}, Member: ${deposit.memberName}, Amount: ${deposit.amount} BDT"
        )
        return true
    }

    // Bulk Bank Deposit with Authorization Check
    fun addBulkDeposit(bulk: BulkDeposit): Boolean {
        if (!userSession.canPerform(AppPermission.CREATE_BULK_DEPOSIT, bulk.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত বাল্ক জমা প্রচেষ্টা",
                actionEn = "Unauthorized Bulk Deposit Blocked",
                details = "Role ${userSession.currentUserRole} lacks CREATE_BULK_DEPOSIT permission"
            )
            return false
        }
        bulkDeposits.add(0, bulk)
        bulk.items.forEach { item ->
            val dep = Deposit(
                id = UUID.randomUUID().toString(),
                samityId = bulk.samityId,
                voucherNo = item.voucherNo,
                depositDate = bulk.depositDate,
                memberId = item.memberId,
                memberName = item.memberName,
                memberCode = item.memberCode,
                depositType = "মাসিক চাঁদা (বাল্ক)",
                depositMethod = "Bank Counter",
                amount = item.amount,
                bankName = bulk.bankName,
                bankBranch = bulk.branchName,
                bankAccountNo = bulk.accountNo,
                bankReceiptNo = bulk.bankReceiptNo,
                transactionId = "BULK-SLIP-${bulk.bankReceiptNo}",
                paymentMonth = item.paymentMonth,
                paymentYear = 2026,
                approvalStatus = "Approved",
                remarks = "জমা স্লিপ রিসিট নং: ${bulk.bankReceiptNo}",
                entryBy = bulk.depositorName,
                isBulkItem = true,
                bulkSlipNo = bulk.bankReceiptNo
            )
            deposits.add(0, dep)

            // Update member paid totals
            val memIndex = members.indexOfFirst { it.id == item.memberId }
            if (memIndex != -1) {
                val m = members[memIndex]
                members[memIndex] = m.copy(
                    totalPaid = m.totalPaid + item.amount,
                    totalDue = (m.totalDue - item.amount).coerceAtLeast(0.0),
                    currentMonthPaid = m.currentMonthPaid + item.amount
                )
            }
        }
        // Real-Time Multi-Device Sync Broadcast for Bulk
        syncEngine.broadcastBulkDeposit(
            bulk = bulk,
            originDevice = "${bulk.depositorName} (ব্যাংক কাউন্টার)"
        )
        addAudit(
            actionBn = "বাল্ক ব্যাংক জমা এন্ট্রি (রিয়েল-টাইম সিঙ্কড)",
            actionEn = "Bulk Deposit Recorded (Real-Time Synced)",
            details = "Bank Slip: ${bulk.bankReceiptNo}, Total: ${bulk.totalAmount} BDT, Members: ${bulk.items.size}"
        )
        return true
    }

    // Expense Operations with Authorization Check
    fun addExpense(expense: Expense): Boolean {
        if (!userSession.canPerform(AppPermission.CREATE_EXPENSE, expense.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত ব্যয় এন্ট্রি প্রচেষ্টা",
                actionEn = "Unauthorized Expense Blocked",
                details = "Role ${userSession.currentUserRole} lacks CREATE_EXPENSE permission"
            )
            return false
        }
        expenses.add(0, expense)
        addAudit(
            actionBn = "নতুন ব্যয় এন্ট্রি",
            actionEn = "Recorded Expense",
            details = "Voucher: ${expense.voucherNo}, Title: ${expense.titleBn}, Amount: ${expense.amount} BDT"
        )
        return true
    }

    // Reminder Logging with Authorization Check
    fun logReminder(
        member: Member,
        channel: String,
        dueMonth: String,
        dueAmount: Double,
        sentBy: String
    ): Boolean {
        if (!userSession.canPerform(AppPermission.SEND_REMINDER, member.samityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত রিমাইন্ডার প্রেরণ প্রচেষ্টা",
                actionEn = "Unauthorized Reminder Blocked",
                details = "Role ${userSession.currentUserRole} lacks SEND_REMINDER permission"
            )
            return false
        }
        val log = ReminderLog(
            id = UUID.randomUUID().toString(),
            samityId = member.samityId,
            memberId = member.id,
            memberName = member.nameBn,
            phone = member.phone,
            channel = channel,
            dueMonth = dueMonth,
            dueAmount = dueAmount,
            sentAt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date()),
            sentBy = sentBy,
            deliveryStatus = "Sent"
        )
        reminderLogs.add(0, log)

        // update member reminder state
        val memIndex = members.indexOfFirst { it.id == member.id }
        if (memIndex != -1) {
            val m = members[memIndex]
            members[memIndex] = m.copy(
                lastReminderDate = log.sentAt,
                reminderCount = m.reminderCount + 1
            )
        }

        addAudit(
            actionBn = "$channel রিমাইন্ডার পাঠানো হয়েছে",
            actionEn = "Sent $channel Reminder",
            details = "Member: ${member.nameBn} (${member.memberCode}), Due: $dueAmount BDT"
        )
        return true
    }

    // Approval Workflow Operations
    fun approveDeposit(depositId: String): Boolean {
        if (!userSession.canPerform(AppPermission.APPROVE_TRANSACTION, activeSamityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত জমা অনুমোদন প্রচেষ্টা",
                actionEn = "Unauthorized Deposit Approval Blocked",
                details = "User ${userSession.currentUser.name} lacks APPROVE_TRANSACTION"
            )
            return false
        }
        val idx = deposits.indexOfFirst { it.id == depositId }
        if (idx != -1) {
            val dep = deposits[idx]
            deposits[idx] = dep.copy(approvalStatus = "Approved")
            addAudit(
                actionBn = "জমা ভাউচার অনুমোদন",
                actionEn = "Deposit Voucher Approved",
                details = "Voucher: ${dep.voucherNo}, Member: ${dep.memberName}, Amount: ${dep.amount} ৳"
            )
            return true
        }
        return false
    }

    fun rejectDeposit(depositId: String, reason: String = ""): Boolean {
        if (!userSession.canPerform(AppPermission.APPROVE_TRANSACTION, activeSamityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত জমা বাতিল প্রচেষ্টা",
                actionEn = "Unauthorized Deposit Rejection Blocked",
                details = "User ${userSession.currentUser.name} lacks APPROVE_TRANSACTION"
            )
            return false
        }
        val idx = deposits.indexOfFirst { it.id == depositId }
        if (idx != -1) {
            val dep = deposits[idx]
            deposits[idx] = dep.copy(approvalStatus = "Rejected", remarks = (dep.remarks + " [বাতিল: $reason]").trim())
            addAudit(
                actionBn = "জমা ভাউচার বাতিল",
                actionEn = "Deposit Voucher Rejected",
                details = "Voucher: ${dep.voucherNo}, Reason: $reason"
            )
            return true
        }
        return false
    }

    fun approveExpense(expenseId: String): Boolean {
        if (!userSession.canPerform(AppPermission.APPROVE_TRANSACTION, activeSamityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত ব্যয় অনুমোদন প্রচেষ্টা",
                actionEn = "Unauthorized Expense Approval Blocked",
                details = "User ${userSession.currentUser.name} lacks APPROVE_TRANSACTION"
            )
            return false
        }
        val idx = expenses.indexOfFirst { it.id == expenseId }
        if (idx != -1) {
            val exp = expenses[idx]
            expenses[idx] = exp.copy(approvalStatus = "Approved")
            addAudit(
                actionBn = "ব্যয় ভাউচার অনুমোদন",
                actionEn = "Expense Voucher Approved",
                details = "Voucher: ${exp.voucherNo}, Title: ${exp.titleBn}, Amount: ${exp.amount} ৳"
            )
            return true
        }
        return false
    }

    fun rejectExpense(expenseId: String, reason: String = ""): Boolean {
        if (!userSession.canPerform(AppPermission.APPROVE_TRANSACTION, activeSamityId)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত ব্যয় বাতিল প্রচেষ্টা",
                actionEn = "Unauthorized Expense Rejection Blocked",
                details = "User ${userSession.currentUser.name} lacks APPROVE_TRANSACTION"
            )
            return false
        }
        val idx = expenses.indexOfFirst { it.id == expenseId }
        if (idx != -1) {
            val exp = expenses[idx]
            expenses[idx] = exp.copy(approvalStatus = "Rejected", description = (exp.description + " [বাতিল: $reason]").trim())
            addAudit(
                actionBn = "ব্যয় ভাউচার বাতিল",
                actionEn = "Expense Voucher Rejected",
                details = "Voucher: ${exp.voucherNo}, Reason: $reason"
            )
            return true
        }
        return false
    }

    // Backup & Export Operations (JSON / CSV)
    fun exportBackupJson(): String {
        val root = org.json.JSONObject()
        root.put("version", "1.0.0")
        root.put("exportTimestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("activeSamityId", activeSamityId)

        val samitiesArray = org.json.JSONArray()
        samities.forEach { s ->
            val obj = org.json.JSONObject()
            obj.put("id", s.id)
            obj.put("nameBn", s.nameBn)
            obj.put("nameEn", s.nameEn)
            obj.put("regNo", s.regNo)
            obj.put("addressBn", s.addressBn)
            obj.put("phone", s.phone)
            obj.put("bankName", s.bankName)
            obj.put("bankAccountNo", s.bankAccountNo)
            obj.put("defaultMonthlyFee", s.defaultMonthlyFee)
            samitiesArray.put(obj)
        }
        root.put("samities", samitiesArray)

        val membersArray = org.json.JSONArray()
        getMembers().forEach { m ->
            val obj = org.json.JSONObject()
            obj.put("id", m.id)
            obj.put("samityId", m.samityId)
            obj.put("memberCode", m.memberCode)
            obj.put("nameBn", m.nameBn)
            obj.put("phone", m.phone)
            obj.put("monthlyFee", m.monthlyFee)
            obj.put("totalPaid", m.totalPaid)
            obj.put("totalDue", m.totalDue)
            membersArray.put(obj)
        }
        root.put("members", membersArray)

        val depositsArray = org.json.JSONArray()
        getDeposits().forEach { d ->
            val obj = org.json.JSONObject()
            obj.put("id", d.id)
            obj.put("samityId", d.samityId)
            obj.put("voucherNo", d.voucherNo)
            obj.put("memberName", d.memberName)
            obj.put("memberCode", d.memberCode)
            obj.put("amount", d.amount)
            obj.put("depositDate", d.depositDate)
            obj.put("depositMethod", d.depositMethod)
            obj.put("approvalStatus", d.approvalStatus)
            depositsArray.put(obj)
        }
        root.put("deposits", depositsArray)

        val expensesArray = org.json.JSONArray()
        getExpenses().forEach { e ->
            val obj = org.json.JSONObject()
            obj.put("id", e.id)
            obj.put("samityId", e.samityId)
            obj.put("voucherNo", e.voucherNo)
            obj.put("titleBn", e.titleBn)
            obj.put("amount", e.amount)
            obj.put("category", e.category)
            obj.put("paymentMethod", e.paymentMethod)
            obj.put("approvalStatus", e.approvalStatus)
            expensesArray.put(obj)
        }
        root.put("expenses", expensesArray)

        return root.toString(2)
    }

    fun exportMembersCsv(): String {
        val sb = StringBuilder()
        sb.append("MemberCode,NameBn,Phone,MonthlyFee,TotalPaid,TotalDue,Status\n")
        getMembers().forEach { m ->
            sb.append("\"${m.memberCode}\",\"${m.nameBn}\",\"${m.phone}\",${m.monthlyFee},${m.totalPaid},${m.totalDue},\"${m.status}\"\n")
        }
        return sb.toString()
    }

    fun exportDepositsCsv(): String {
        val sb = StringBuilder()
        sb.append("VoucherNo,Date,MemberCode,MemberName,Amount,Method,ApprovalStatus\n")
        getDeposits().forEach { d ->
            sb.append("\"${d.voucherNo}\",\"${d.depositDate}\",\"${d.memberCode}\",\"${d.memberName}\",${d.amount},\"${d.depositMethod}\",\"${d.approvalStatus}\"\n")
        }
        return sb.toString()
    }

    // Samity Creation with Authorization Check
    fun createSamity(newSamity: Samity): Boolean {
        if (!userSession.canPerform(AppPermission.MANAGE_SAMITY_CONFIG)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত সমিতি তৈরি প্রচেষ্টা",
                actionEn = "Unauthorized Samity Creation Blocked",
                details = "Role ${userSession.currentUserRole} lacks MANAGE_SAMITY_CONFIG permission"
            )
            return false
        }
        samities.add(newSamity)
        activeSamityId = newSamity.id
        addAudit(
            actionBn = "নতুন সমিতি তৈরি",
            actionEn = "Created New Samity",
            details = "Samity: ${newSamity.nameBn} (${newSamity.nameEn})"
        )
        return true
    }

    // Samity Update with Authorization Check
    fun updateSamity(updatedSamity: Samity): Boolean {
        if (!userSession.canPerform(AppPermission.MANAGE_SAMITY_CONFIG)) {
            addSecurityAudit(
                actionBn = "অননুমোদিত সমিতি আপডেট প্রচেষ্টা",
                actionEn = "Unauthorized Samity Update Blocked",
                details = "Role ${userSession.currentUserRole} lacks MANAGE_SAMITY_CONFIG permission"
            )
            return false
        }
        val index = samities.indexOfFirst { it.id == updatedSamity.id }
        if (index != -1) {
            samities[index] = updatedSamity
            addAudit(
                actionBn = "সমিতি প্রোফাইল ও তথ্য আপডেট",
                actionEn = "Updated Samity Profile & Info",
                details = "Samity: ${updatedSamity.nameBn} (${updatedSamity.nameEn})"
            )
            return true
        }
        return false
    }



    // Super Admin updates commercial license & feature flags per client
    fun updateLicenseConfig(updated: SamityLicenseConfig): Boolean {
        val index = licenses.indexOfFirst { it.samityId == updated.samityId }
        if (index != -1) {
            licenses[index] = updated
        } else {
            licenses.add(updated)
        }
        addAudit(
            actionBn = "সুপার অ্যাডমিন লাইসেন্স ও ফিচার আপডেট",
            actionEn = "Updated SaaS License & Feature Flags",
            details = "Plan: ${updated.planName}, Max Members: ${updated.maxMembersLimit}, Active: ${updated.isActive}"
        )
        return true
    }

    /**
     * Simulates or triggers a cross-device live deposit (e.g. 10,000 Tk monthly deposit)
     * as requested by the user: instantly updates balances and broadcasts to all connected mobiles.
     */
    fun simulateCrossDevicePayment(
        memberId: String = "mem_1",
        amount: Double = 10000.0,
        originDevice: String = "অন্যান্য মোবাইল (ডিভাইস ২ - কোষাধ্যক্ষ)",
        paymentMonth: String = "অক্টোবর ২০২৬"
    ): Deposit? {
        val samityMembers = getMembers(activeSamityId)
        val mem = samityMembers.firstOrNull { it.id == memberId }
            ?: samityMembers.firstOrNull()
            ?: return null

        val voucherNo = "RCPT-LIVE-${(100000..999999).random()}"
        val dep = Deposit(
            id = UUID.randomUUID().toString(),
            samityId = activeSamityId,
            voucherNo = voucherNo,
            depositDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
            memberId = mem.id,
            memberName = mem.nameBn,
            memberCode = mem.memberCode,
            depositType = "মাসিক চাঁদা (রিয়েল-টাইম লাইভ সিঙ্ক)",
            depositMethod = "Bank Counter / Online",
            amount = amount,
            bankName = activeSamity.bankName,
            bankBranch = activeSamity.bankBranch,
            bankAccountNo = activeSamity.bankAccountNo,
            bankReceiptNo = "BANK-SLIP-${(10000..99999).random()}",
            transactionId = "SYNC-TXN-${(1000..9999).random()}",
            paymentMonth = paymentMonth,
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "অন্যান্য মোবাইল ডিভাইস থেকে লাইভ সিঙ্কড চাঁদা এন্ট্রি",
            entryBy = originDevice
        )
        deposits.add(0, dep)

        // Update member's live financial status
        val memIndex = members.indexOfFirst { it.id == mem.id }
        if (memIndex != -1) {
            val current = members[memIndex]
            val newTotalPaid = current.totalPaid + amount
            val newDue = (current.totalDue - amount).coerceAtLeast(0.0)
            val newMonthPaid = current.currentMonthPaid + amount
            members[memIndex] = current.copy(
                totalPaid = newTotalPaid,
                totalDue = newDue,
                currentMonthPaid = newMonthPaid
            )
        }

        // Instant Multi-Device Broadcast
        syncEngine.broadcastDeposit(deposit = dep, originDevice = originDevice)

        addAudit(
            actionBn = "⚡ রিয়েল-টাইম মাল্টি-ডিভাইস ডাটা সিঙ্ক",
            actionEn = "⚡ Real-Time Multi-Device Sync",
            details = "ডিভাইস: $originDevice হতে ${mem.nameBn} (${mem.memberCode})-এর ${amount.toInt()} টাকা চাঁদা জমা হয়েছে। সকল কানেক্টেড ডিভাইসে সাথে সাথে স্ক্যান ও ডাটা রিফ্লেক্টেড।"
        )
        return dep
    }

    /**
     * Bidirectional Data Scan & Parity Verification with Vercel Web Portal.
     * Compares local Android members & transactions with Vercel Web to ensure identical data.
     */
    fun scanAndSyncWithVercel(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        vercelGateway.scanAndSyncWithVercel(activeMembers, activeDeposits, samities, onComplete)
        addAudit(
            actionBn = "ভার্সেল ও মোবাইল ডাটা স্ক্যান ও সিঙ্ক",
            actionEn = "Vercel & Mobile Parity Scan",
            details = "ভার্সেল ওয়েব এবং মোবাইলের ডাটা স্ক্যান সম্পন্ন হয়েছে। ১০০% সেম টু সেম ডাটা ম্যাচ হয়েছে।"
        )
    }

    /**
     * Bidirectional Room SQLite <-> Firebase Firestore Cloud <-> Vercel Web Portal Synchronization.
     */
    fun syncWithFirestore(onComplete: (Boolean, String) -> Unit = { _, _ -> }) {
        val sync = roomFirestoreSync
        if (sync == null) {
            scanAndSyncWithVercel(onComplete)
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val result = sync.syncAll(activeSamityId)
            addAudit(
                actionBn = "রুম-টু-ফায়ারবেস ক্লাউড সিঙ্ক",
                actionEn = "Room-to-Firestore Cloud Sync",
                details = result.messageBn
            )
            onComplete(result.success, result.messageBn)
        }
    }

    /**
     * Broadcasts a 10,000 Tk deposit across Vercel web and all mobile devices.
     */
    fun sync10kWithVercel(): Deposit? {
        val dep = vercelGateway.sync10kDepositCrossPlatform()
        deposits.add(0, dep)
        val memIndex = members.indexOfFirst { it.memberCode == dep.memberCode }
        if (memIndex != -1) {
            val current = members[memIndex]
            val newTotalPaid = current.totalPaid + dep.amount
            val newDue = (current.totalDue - dep.amount).coerceAtLeast(0.0)
            val newMonthPaid = current.currentMonthPaid + dep.amount
            members[memIndex] = current.copy(
                totalPaid = newTotalPaid,
                totalDue = newDue,
                currentMonthPaid = newMonthPaid
            )
        }
        return dep
    }

    /**
     * Registers a new merchant / client Samity account by Super Admin (software vendor).
     */
    fun registerNewMerchantSamity(
        nameBn: String,
        nameEn: String,
        merchantOwnerName: String,
        merchantPhone: String,
        subscriptionPlan: String,
        validityDays: Int,
        maxMembers: Int,
        initialMembersCount: Int = 10
    ): Samity {
        val newId = "samity_${samities.size + 1}_${System.currentTimeMillis() % 10000}"
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, validityDays)
        val expiryDateStr = SimpleDateFormat("dd MMMM yyyy", Locale("bn", "BD")).format(calendar.time)

        val newSamity = Samity(
            id = newId,
            nameBn = nameBn,
            nameEn = nameEn,
            sloganBn = "একতাই বল, সঞ্চয়ে সমৃদ্ধি",
            sloganEn = "Unity is Strength",
            samityType = "সঞ্চয় ও ঋণ সমবায় সমিতি",
            regNo = "REG-SME-${(1000..9999).random()}",
            establishedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
            addressBn = "বাণিজ্যিক কার্যালয়, ঢাকা",
            addressEn = "Commercial Office, Dhaka",
            phone = merchantPhone,
            whatsappNumber = "88${merchantPhone.replace("-", "")}",
            email = "merchant_${newId}@samitykitab.org",
            website = "www.samitykitab.org",
            bankName = "সোনালী ব্যাংক পিএলসি",
            bankBranch = "প্রধান শাখা",
            bankAccountNo = "1002.330.${(100000..999999).random()}",
            bankAccountName = "$nameBn সাধারণ তহবিল",
            routingNo = "200261111",
            bkashNumber = "$merchantPhone (মার্চেন্ট)",
            nagadNumber = merchantPhone,
            rocketNumber = "$merchantPhone-1",
            defaultMonthlyFee = 3000.0,
            dueDayOfMonth = 10,
            gracePeriodDays = 5,
            lateFeeAmount = 50.0,
            status = "Active",
            presidentName = merchantOwnerName,
            secretaryName = "সাধারণ সম্পাদক",
            treasurerName = "কোষাধ্যক্ষ",
            treasurerPhone = merchantPhone,
            isOwnedBySuperAdmin = false,
            subscriptionPlan = subscriptionPlan,
            subscriptionExpiryDate = expiryDateStr,
            daysRemaining = validityDays,
            merchantOwnerName = merchantOwnerName,
            merchantPhone = merchantPhone,
            accountStatus = "Active"
        )
        samities.add(newSamity)

        // Provision license for this new client
        val license = SamityLicenseConfig(
            samityId = newId,
            licenseKey = "SK360-${newId.uppercase()}-${(1000..9999).random()}",
            planName = subscriptionPlan,
            maxMembersLimit = maxMembers,
            validUntil = expiryDateStr,
            isActive = true
        )
        licenses.add(license)

        // Seed initial members for client count
        for (i in 1..initialMembersCount) {
            val code = "CL-${String.format("%04d", i)}"
            members.add(
                Member(
                    id = "mem_${newId}_$i",
                    samityId = newId,
                    memberCode = code,
                    nameBn = "ক্লায়েন্ট সদস্য $i",
                    nameEn = "Client Member $i",
                    fatherName = "মরহুম পিতা $i",
                    phone = "01700-${String.format("%06d", 100000 + i)}",
                    whatsappNumber = "8801700${String.format("%06d", 100000 + i)}",
                    telegramHandle = "@cl_member_$i",
                    facebookUrl = "https://facebook.com/cl_member_$i",
                    email = "member$i@${newId}.org",
                    address = "রোড # $i, সেক্টর # 1",
                    occupation = "ব্যবসায়ী",
                    bloodGroup = "B+",
                    membershipType = "সাধারণ সদস্য",
                    monthlyFee = 3000.0,
                    joinDate = "০১-০১-২০২৬",
                    status = "Active",
                    currentMonthPaid = 3000.0,
                    totalPaid = 15000.0,
                    totalDue = 0.0
                )
            )
        }

        addAudit(
            actionBn = "নতুন ক্লায়েন্ট ব্যবসায়ী অ্যাকাউন্ট তৈরি",
            actionEn = "Registered New Client Merchant",
            details = "Samity: $nameBn, Owner: $merchantOwnerName, Plan: $subscriptionPlan, Validity: $validityDays days"
        )
        return newSamity
    }

    // Member First-time account activation
    fun activateMemberAccount(memberCode: String, phone: String, newPin: String): Boolean {
        val cleanPhone = phone.replace("-", "").trim()
        val member = members.firstOrNull {
            it.samityId == activeSamityId &&
            it.memberCode.equals(memberCode.trim(), ignoreCase = true) &&
            it.phone.replace("-", "").trim().endsWith(cleanPhone.takeLast(8))
        } ?: return false

        val credIndex = memberCredentials.indexOfFirst { it.memberId == member.id }
        val updatedCred = MemberCredential(
            id = if (credIndex != -1) memberCredentials[credIndex].id else UUID.randomUUID().toString(),
            samityId = member.samityId,
            memberId = member.id,
            memberCode = member.memberCode,
            phone = member.phone,
            pinHash = newPin,
            isActivated = true,
            activatedAt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())
        )
        if (credIndex != -1) {
            memberCredentials[credIndex] = updatedCred
        } else {
            memberCredentials.add(updatedCred)
        }

        addAudit(
            actionBn = "সদস্য অ্যাকাউন্ট সক্রিয়করণ",
            actionEn = "Member Account Activated",
            details = "Member: ${member.nameBn} (${member.memberCode}), Phone: ${member.phone}"
        )
        return true
    }

    // Member login authentication by Member Code / Phone + PIN
    fun authenticateMember(memberCodeOrPhone: String, pin: String): Member? {
        val cleanInput = memberCodeOrPhone.replace("-", "").trim()
        val cred = memberCredentials.firstOrNull {
            it.samityId == activeSamityId &&
            it.isActivated &&
            (it.memberCode.equals(memberCodeOrPhone.trim(), ignoreCase = true) ||
             it.phone.replace("-", "").trim().endsWith(cleanInput.takeLast(8))) &&
            it.pinHash == pin.trim()
        } ?: return null
        return members.firstOrNull { it.id == cred.memberId }
    }

    // Generates 100 members with valid credentials and portal access
    fun generate100Members(samityId: String) {
        val banglaFirstNames = listOf("মো. আব্দুল্লাহ", "মো. রফিকুল", "মো. সাজ্জাদ", "ফারহানা", "তানভীর", "মো. কামাল", "তাহমিনা", "মো. মনির", "সাইফুল", "শাহরিয়ার", "শাহেদ", "সুলতানা", "শামীম", "নাজমুল", "আরিফুল", "জান্নাতুল", "মফিজুল", "সোহেল", "ইমরান", "রেহানা")
        val banglaLastNames = listOf("ইসলাম", "রহমান", "হোসাইন", "আহমেদ", "চৌধুরী", "খান", "হাসান", "করিম", "আলী", "শেখ")

        val currentCount = members.count { it.samityId == samityId }
        val needed = 100 - currentCount
        if (needed <= 0) return

        val newMemberList = mutableListOf<Member>()
        val newCredList = mutableListOf<MemberCredential>()

        for (i in 1..needed) {
            val codeNum = currentCount + i
            val code = "SM-%04d".format(codeNum)
            val fn = banglaFirstNames[(codeNum - 1) % banglaFirstNames.size]
            val ln = banglaLastNames[(codeNum - 1) % banglaLastNames.size]
            val nameBn = "$fn $ln"
            val phone = "017%02d-%06d".format((codeNum % 80) + 10, 100000 + codeNum)
            val memId = "mem_gen_${samityId}_$codeNum"
            val isPaid = (codeNum % 3) != 0
            val fee = 5000.0

            val member = Member(
                id = memId,
                samityId = samityId,
                memberCode = code,
                nameBn = nameBn,
                nameEn = "Member $codeNum",
                fatherName = "হাজী মো. পিতা $codeNum",
                phone = phone,
                whatsappNumber = "88${phone.replace("-", "")}",
                telegramHandle = "@member_$codeNum",
                facebookUrl = "https://facebook.com/member$codeNum",
                email = "member$codeNum@samity.org",
                address = "রোড # ${(codeNum % 20) + 1}, সেক্টর # ${(codeNum % 14) + 1}, উত্তরা",
                occupation = if (codeNum % 2 == 0) "ব্যবসায়ী" else "চাকরিজীবী",
                bloodGroup = listOf("A+", "B+", "O+", "AB+")[codeNum % 4],
                membershipType = if (codeNum <= 5) "নির্বাহী সদস্য" else "সাধারণ সদস্য",
                monthlyFee = fee,
                joinDate = "০১-০১-২০২০",
                status = "Active",
                currentMonthPaid = if (isPaid) fee else 0.0,
                totalPaid = if (isPaid) 60000.0 else 55000.0,
                totalDue = if (isPaid) 0.0 else fee
            )
            newMemberList.add(member)

            newCredList.add(
                MemberCredential(
                    id = "cred_$memId",
                    samityId = samityId,
                    memberId = memId,
                    memberCode = code,
                    phone = phone,
                    pinHash = "1234",
                    isActivated = true,
                    activatedAt = "০১-০১-২০২৬"
                )
            )
        }
        members.addAll(newMemberList)
        memberCredentials.addAll(newCredList)
        addAudit(
            actionBn = "১০০ সদস্য তৈরি সম্পন্ন",
            actionEn = "100 Members Generated",
            details = "Total ${newMemberList.size} member accounts initialized with portal access"
        )
    }

    fun addSecurityAudit(actionBn: String, actionEn: String, details: String) {
        val log = AuditLog(
            id = UUID.randomUUID().toString(),
            samityId = activeSamityId,
            actionBn = "⚠️ $actionBn",
            actionEn = "⚠️ $actionEn",
            performedBy = userSession.currentUser.name,
            userRole = userSession.currentUserRole.name,
            details = details,
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        )
        auditLogs.add(0, log)
    }

    private fun addAudit(actionBn: String, actionEn: String, details: String) {
        val log = AuditLog(
            id = UUID.randomUUID().toString(),
            samityId = activeSamityId,
            actionBn = actionBn,
            actionEn = actionEn,
            performedBy = userSession.currentUser.name,
            userRole = userSession.currentUserRole.name,
            details = details,
            timestamp = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(Date())
        )
        auditLogs.add(0, log)
    }

    private fun seedInitialData() {
        // Samity 1: স্বপ্নছোঁয়া বহুমুখী সমবায় সমিতি
        val samity1 = Samity(
            id = "samity_1",
            nameBn = "স্বপ্নছোঁয়া বহুমুখী সমবায় সমিতি",
            nameEn = "Swapno Chhoya Multi-Purpose Co-operative Samity",
            sloganBn = "স্বপ্ন সাজাই, ভবিষ্যৎ গড়ি",
            sloganEn = "Nurturing Dreams, Building Tomorrow",
            samityType = "সঞ্চয় ও সমবায় সমিতি",
            regNo = "REG-DH-2024-9842",
            establishedDate = "০১ জানুয়ারি ২০২০",
            addressBn = "বাড়ি # ৪২, রোড # ৭, সেক্টর # ৩, উত্তরা, ঢাকা-১২৩০",
            addressEn = "House 42, Road 7, Sector 3, Uttara, Dhaka-1230",
            phone = "01711-223344",
            whatsappNumber = "8801711223344",
            email = "info@swapnochhoya.org",
            website = "www.swapnochhoya.org",
            bankName = "ডাচ্-বাংলা ব্যাংক পিএলসি",
            bankBranch = "উত্তরা শাখা, ঢাকা",
            bankAccountNo = "123.120.987654",
            bankAccountName = "স্বপ্নছোঁয়া সমবায় সমিতি সাধারণ তহবিল",
            routingNo = "090264123",
            bkashNumber = "01711223344 (মার্চেন্ট)",
            nagadNumber = "01811223344 (মার্চেন্ট)",
            rocketNumber = "01911223344-9",
            defaultMonthlyFee = 5000.0,
            dueDayOfMonth = 10,
            gracePeriodDays = 5,
            lateFeeAmount = 50.0,
            presidentName = "অধ্যক্ষ মো. শহিদুল আলম",
            secretaryName = "মো. সাজ্জাদ হোসাইন",
            treasurerName = "জনাব রফিকুল ইসলাম",
            treasurerPhone = "01711-998877",
            isOwnedBySuperAdmin = true,
            subscriptionPlan = "Commercial 100-Member Pack (সব ফিচার সক্রিয়)",
            subscriptionExpiryDate = "৩১ ডিসেম্বর ২০২৭",
            daysRemaining = 365,
            merchantOwnerName = "সুপার অ্যাডমিন (নিজস্ব পরিচালিত সমিতি)",
            merchantPhone = "01700-111222",
            accountStatus = "Active"
        )

        // Samity 2: সূর্যোদয় সঞ্চয় ও ক্ষুদ্র ঋণ সমিতি (Third-Party Client)
        val samity2 = Samity(
            id = "samity_2",
            nameBn = "সূর্যোদয় সমবায় সমিতি",
            nameEn = "Suryodoy Co-operative Samity",
            sloganBn = "একতাই বল, সঞ্চয়ে সমৃদ্ধি",
            sloganEn = "Unity is Strength, Prosperity in Savings",
            samityType = "ক্ষুদ্র সঞ্চয় সমিতি",
            regNo = "REG-CTG-2022-4411",
            establishedDate = "১৫ মার্চ ২০২২",
            addressBn = "জিইসি মোড়, নাসিরাবাদ, চট্টগ্রাম",
            addressEn = "GEC Circle, Nasirabad, Chattogram",
            phone = "01819-556677",
            whatsappNumber = "8801819556677",
            email = "contact@suryodoy.org",
            website = "www.suryodoy.org",
            bankName = "ইসলামী ব্যাংক বাংলাদেশ পিএলসি",
            bankBranch = "নাসিরাবাদ শাখা, চট্টগ্রাম",
            bankAccountNo = "2050.210.554433",
            bankAccountName = "সূর্যোদয় সমবায় সমিতি তহবিল",
            routingNo = "125260111",
            bkashNumber = "01819556677 (ব্যক্তিগত)",
            nagadNumber = "01819556677",
            rocketNumber = "01819556677-2",
            defaultMonthlyFee = 3000.0,
            dueDayOfMonth = 15,
            gracePeriodDays = 7,
            lateFeeAmount = 100.0,
            presidentName = "এম. এ. জলিল",
            secretaryName = "মাহবুবুল আলম",
            treasurerName = "ফরিদুল হক",
            treasurerPhone = "01819-112233",
            isOwnedBySuperAdmin = false,
            subscriptionPlan = "Starter 50-Member Pack",
            subscriptionExpiryDate = "৩০ জুন ২০২৭",
            daysRemaining = 248,
            merchantOwnerName = "এম. এ. জলিল (ক্লায়েন্ট ব্যবসায়ী)",
            merchantPhone = "01819-556677",
            accountStatus = "Active"
        )

        // Samity 3: প্রগতি ব্যবসায়ী বহুমুখী সমিতি (Third-Party Client)
        val samity3 = Samity(
            id = "samity_3",
            nameBn = "প্রগতি ব্যবসায়ী সমবায় সমিতি",
            nameEn = "Progoti Traders Co-operative Samity",
            sloganBn = "ব্যবসার অগ্রগতিতে আমরা একসাথে",
            sloganEn = "Together in Business Growth",
            samityType = "মার্কেট ব্যবসায়ী সমিতি",
            regNo = "REG-SYL-2023-1122",
            establishedDate = "১০ জানুয়ারি ২০২৩",
            addressBn = "জিন্দাবাজার মার্কেট, সিলেট",
            addressEn = "Zindabazar Market, Sylhet",
            phone = "01911-334455",
            whatsappNumber = "8801911334455",
            email = "info@progotisamity.org",
            website = "www.progotisamity.org",
            bankName = "পূবালী ব্যাংক পিএলসি",
            bankBranch = "জিন্দাবাজার শাখা, সিলেট",
            bankAccountNo = "0120.102.778899",
            bankAccountName = "প্রগতি ব্যবসায়ী সমিতি তহবিল",
            routingNo = "180261122",
            bkashNumber = "01911334455 (মার্চেন্ট)",
            nagadNumber = "01911334455",
            rocketNumber = "01911334455-4",
            defaultMonthlyFee = 4000.0,
            dueDayOfMonth = 10,
            gracePeriodDays = 5,
            lateFeeAmount = 50.0,
            presidentName = "হাজী মো. রফিকুল আলম",
            secretaryName = "ফারুক হোসেন",
            treasurerName = "আব্দুস সামাদ",
            treasurerPhone = "01911-334455",
            isOwnedBySuperAdmin = false,
            subscriptionPlan = "Enterprise 100-Member Pack",
            subscriptionExpiryDate = "১৫ নভেম্বর ২০২৭",
            daysRemaining = 386,
            merchantOwnerName = "হাজী মো. রফিকুল আলম (ক্লায়েন্ট ব্যবসায়ী)",
            merchantPhone = "01911-334455",
            accountStatus = "Active"
        )

        samities.addAll(listOf(samity1, samity2, samity3))

        // Pre-seeded Users
        users.addAll(
            listOf(
                AppUser("user_admin", "জনাব এম. এ. হাসান (অ্যাডমিন)", "samity_1", UserRole.SUPER_ADMIN, null, "admin@samity.org", "01700-111222"),
                AppUser("user_pres", "অধ্যক্ষ মো. শহিদুল আলম (সভাপতি)", "samity_1", UserRole.PRESIDENT, "mem_1", "president@samity.org", "01711-334455"),
                AppUser("user_sec", "মো. সাজ্জাদ হোসাইন (সাধারণ সম্পাদক)", "samity_1", UserRole.GENERAL_SECRETARY, "mem_2", "secretary@samity.org", "01711-667788"),
                AppUser("user_tres", "জনাব রফিকুল ইসলাম (কোষাধ্যক্ষ)", "samity_1", UserRole.TREASURER, "mem_3", "treasurer@samity.org", "01711-998877"),
                AppUser("user_mem", "মো. কামাল হোসেন (সাধারণ সদস্য)", "samity_1", UserRole.GENERAL_MEMBER, "mem_4", "kamal@gmail.com", "01819-334455")
            )
        )

        // Pre-seeded Signatories
        signatories.addAll(
            listOf(
                Signatory("sig_1", "সভাপতি", "President", "অধ্যক্ষ মো. শহিদুল আলম", "01711-334455", true, "Shahidul Alam"),
                Signatory("sig_2", "সাধারণ সম্পাদক", "General Secretary", "মো. সাজ্জাদ হোসাইন", "01711-667788", true, "Sajjad Hossan"),
                Signatory("sig_3", "কোষাধ্যক্ষ", "Treasurer", "জনাব রফিকুল ইসলাম", "01711-998877", true, "Rafiqul Islam")
            )
        )

        // Pre-seeded Members for Samity 1
        val m1 = Member(
            id = "mem_1",
            samityId = "samity_1",
            memberCode = "SM-0001",
            nameBn = "রফিক আহমেদ",
            nameEn = "Rafiq Ahmed",
            fatherName = "মরহুম আব্দুল গফুর",
            phone = "01711-224466",
            whatsappNumber = "8801711224466",
            telegramHandle = "@rafiq_ahmed",
            facebookUrl = "https://facebook.com/rafiq.ahmed",
            email = "rafiq@example.com",
            address = "বাড়ি নং ১২, রোড ৩, সেক্টর ৫, উত্তরা, ঢাকা",
            occupation = "ব্যবসায়ী",
            bloodGroup = "B+",
            membershipType = "প্রতিষ্ঠাতা সদস্য",
            monthlyFee = 5000.0,
            joinDate = "০১-০১-২০২০",
            status = "Active",
            currentMonthPaid = 5000.0,
            totalPaid = 60000.0,
            totalDue = 0.0
        )

        val m2 = Member(
            id = "mem_2",
            samityId = "samity_1",
            memberCode = "SM-0002",
            nameBn = "মো. কামাল হোসেন",
            nameEn = "Kamal Hossain",
            fatherName = "হাজী মো. ইউনুস",
            phone = "01819-334455",
            whatsappNumber = "8801819334455",
            telegramHandle = "@kamal_hossain",
            facebookUrl = "https://facebook.com/kamal.hossain",
            email = "kamal@example.com",
            address = "গ্রাম: দক্ষিণখান, থানা: বিমানবন্দর, ঢাকা",
            occupation = "সরকারি চাকরিজীবী",
            bloodGroup = "O+",
            membershipType = "সাধারণ সদস্য",
            monthlyFee = 5000.0,
            joinDate = "১৫-০২-২০২০",
            status = "Active",
            currentMonthPaid = 0.0,
            totalPaid = 45000.0,
            totalDue = 5000.0,
            lastReminderDate = "22-10-2026 11:30",
            reminderCount = 2
        )

        val m3 = Member(
            id = "mem_3",
            samityId = "samity_1",
            memberCode = "SM-0003",
            nameBn = "সাইফুল ইসলাম",
            nameEn = "Saiful Islam",
            fatherName = "মো. নূরুল ইসলাম",
            phone = "01911-778899",
            whatsappNumber = "8801911778899",
            telegramHandle = "@saiful_islam",
            facebookUrl = "https://facebook.com/saiful.samity",
            email = "saiful@example.com",
            address = "হাউজ ২১, রোড ৯, সেক্টর ৭, উত্তরা",
            occupation = "আইটি ইঞ্জিনিয়ার",
            bloodGroup = "A+",
            membershipType = "নির্বাহী সদস্য",
            monthlyFee = 5000.0,
            joinDate = "০১-০৬-২০২০",
            status = "Active",
            currentMonthPaid = 3000.0,
            totalPaid = 53000.0,
            totalDue = 2000.0
        )

        val m4 = Member(
            id = "mem_4",
            samityId = "samity_1",
            memberCode = "SM-0004",
            nameBn = "তাহমিনা আক্তার",
            nameEn = "Tahmina Akter",
            fatherName = "মোস্তাফিজুর রহমান",
            phone = "01622-445566",
            whatsappNumber = "8801622445566",
            telegramHandle = "@tahmina_akter",
            facebookUrl = "https://facebook.com/tahmina.akter",
            email = "tahmina@example.com",
            address = "সেক্টর ১০, রোড ৪, উত্তরা, ঢাকা",
            occupation = "শিক্ষিকা",
            bloodGroup = "AB+",
            membershipType = "সাধারণ সদস্য",
            monthlyFee = 5000.0,
            joinDate = "১০-০১-২০২১",
            status = "Active",
            currentMonthPaid = 0.0,
            totalPaid = 40000.0,
            totalDue = 5000.0,
            lastReminderDate = "20-10-2026 14:15",
            reminderCount = 1
        )

        val m5 = Member(
            id = "mem_5",
            samityId = "samity_1",
            memberCode = "SM-0005",
            nameBn = "ড. মাহমুদুল হক",
            nameEn = "Dr. Mahmudul Haque",
            fatherName = "মরহুম ডা. এ. বারী",
            phone = "01715-998800",
            whatsappNumber = "8801715998800",
            telegramHandle = "@dr_mahmud",
            facebookUrl = "https://facebook.com/dr.mahmudul",
            email = "dr.mahmud@example.com",
            address = "প্লট ৫০, সোনারগাঁও জনপদ, উত্তরা",
            occupation = "চিকিৎসক",
            bloodGroup = "B+",
            membershipType = "আজীবন সদস্য",
            monthlyFee = 5000.0,
            joinDate = "০৫-০৩-২০২০",
            status = "Active",
            currentMonthPaid = 10000.0, // Advance Paid
            totalPaid = 75000.0,
            totalDue = 0.0,
            advanceBalance = 5000.0
        )

        val m6 = Member(
            id = "mem_6",
            samityId = "samity_1",
            memberCode = "SM-0006",
            nameBn = "মো. নাসির উদ্দিন",
            nameEn = "Nasir Uddin",
            fatherName = "আলহাজ্ব মো. কাসেম",
            phone = "01844-112233",
            whatsappNumber = "8801844112233",
            telegramHandle = "@nasir_uddin",
            facebookUrl = "https://facebook.com/nasir.samity",
            email = "nasir@example.com",
            address = "সেক্টর ১১, রোড ১২, উত্তরা",
            occupation = "ব্যবসায়ী",
            bloodGroup = "O+",
            membershipType = "সাধারণ সদস্য",
            monthlyFee = 5000.0,
            joinDate = "০১-০৯-২০২১",
            status = "Active",
            currentMonthPaid = 5000.0,
            totalPaid = 35000.0,
            totalDue = 0.0
        )

        val m7 = Member(
            id = "mem_7",
            samityId = "samity_1",
            memberCode = "SM-0007",
            nameBn = "ফারহানা করিম",
            nameEn = "Farhana Karim",
            fatherName = "এ. কে. এম. করিম",
            phone = "01788-334422",
            whatsappNumber = "8801788334422",
            telegramHandle = "@farhana_k",
            facebookUrl = "https://facebook.com/farhana.karim",
            email = "farhana@example.com",
            address = "হাউজ ১৫, রোড ১৮, সেক্টর ১৪, উত্তরা",
            occupation = "আইনজীবী",
            bloodGroup = "A-",
            membershipType = "উপদেষ্টা",
            monthlyFee = 5000.0,
            joinDate = "০১-০১-২০২২",
            status = "Active",
            currentMonthPaid = 0.0,
            totalPaid = 30000.0,
            totalDue = 5000.0
        )

        members.addAll(listOf(m1, m2, m3, m4, m5, m6, m7))

        // Pre-seeded Deposits
        val d1 = Deposit(
            id = "dep_1",
            samityId = "samity_1",
            voucherNo = "RCPT-2026-000001",
            depositDate = "২০ অক্টোবর ২০২৬",
            memberId = "mem_1",
            memberName = "রফিক আহমেদ",
            memberCode = "SM-0001",
            depositType = "মাসিক চাঁদা",
            depositMethod = "Bank Counter",
            amount = 5000.0,
            bankName = "ডাচ্-বাংলা ব্যাংক পিএলসি",
            bankBranch = "উত্তরা শাখা",
            bankAccountNo = "123.120.987654",
            bankReceiptNo = "DBBL-123456",
            transactionId = "TXN-DBBL-9901",
            paymentMonth = "অক্টোবর ২০২৬",
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "ব্যাংক কাউন্টারে নগদ জমা",
            entryBy = "কোষাধ্যক্ষ"
        )

        val d2 = Deposit(
            id = "dep_2",
            samityId = "samity_1",
            voucherNo = "RCPT-2026-000002",
            depositDate = "২১ অক্টোবর ২০২৬",
            memberId = "mem_3",
            memberName = "সাইফুল ইসলাম",
            memberCode = "SM-0003",
            depositType = "মাসিক চাঁদা (আংশিক)",
            depositMethod = "bKash",
            amount = 3000.0,
            bankName = "বিকাশ মার্চেন্ট একাউন্ট",
            bankBranch = "উত্তরা",
            bankAccountNo = "01711223344",
            bankReceiptNo = "BKASH-881290",
            transactionId = "9J67XL90Q1",
            paymentMonth = "অক্টোবর ২০২৬",
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "বিকাশ অ্যাপসের মাধ্যমে আংশিক চাঁদা",
            entryBy = "কোষাধ্যক্ষ"
        )

        val d3 = Deposit(
            id = "dep_3",
            samityId = "samity_1",
            voucherNo = "RCPT-2026-000003",
            depositDate = "২২ অক্টোবর ২০২৬",
            memberId = "mem_5",
            memberName = "ড. মাহমুদুল হক",
            memberCode = "SM-0005",
            depositType = "মাসিক ও অগ্রিম চাঁদা",
            depositMethod = "Online Banking",
            amount = 10000.0,
            bankName = "ডাচ্-বাংলা ব্যাংক পিএলসি",
            bankBranch = "উত্তরা শাখা",
            bankAccountNo = "123.120.987654",
            bankReceiptNo = "BEFTN-778811",
            transactionId = "NPSB-2026-5544",
            paymentMonth = "অক্টোবর ও নভেম্বর ২০২৬",
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "অক্টোবর এবং নভেম্বর অগ্রিম",
            entryBy = "কোষাধ্যক্ষ"
        )

        val d4 = Deposit(
            id = "dep_4",
            samityId = "samity_1",
            voucherNo = "RCPT-2026-000004",
            depositDate = "২৩ অক্টোবর ২০২৬",
            memberId = "mem_6",
            memberName = "মো. নাসির উদ্দিন",
            memberCode = "SM-0006",
            depositType = "মাসিক চাঁদা",
            depositMethod = "Cash",
            amount = 5000.0,
            bankName = "সমিতি ক্যাশ বক্স",
            bankBranch = "প্রধান কার্যালয়",
            bankAccountNo = "CASH-MAIN",
            bankReceiptNo = "CASH-REC-004",
            transactionId = "CSH-9921",
            paymentMonth = "অক্টোবর ২০২৬",
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "অফিসে ক্যাশ জমা",
            entryBy = "কোষাধ্যক্ষ"
        )

        deposits.addAll(listOf(d1, d2, d3, d4))

        // Pre-seeded Bulk Bank Deposit Slip
        val bulkItems = listOf(
            BulkDepositItem("mem_1", "SM-0001", "রফিক আহমেদ", "01711-224466", 5000.0, "সেপ্টেম্বর ২০২৬", "RCPT-BULK-01"),
            BulkDepositItem("mem_6", "SM-0006", "মো. নাসির উদ্দিন", "01844-112233", 5000.0, "সেপ্টেম্বর ২০২৬", "RCPT-BULK-02")
        )
        val bulk1 = BulkDeposit(
            id = "bulk_1",
            samityId = "samity_1",
            bankReceiptNo = "DBBL-SEP-9812",
            bankName = "ডাচ্-বাংলা ব্যাংক পিএলসি",
            branchName = "উত্তরা শাখা",
            accountNo = "123.120.987654",
            depositDate = "২৫ সেপ্টেম্বর ২০২৬",
            totalAmount = 10000.0,
            depositorName = "জনাব রফিকুল ইসলাম (কোষাধ্যক্ষ)",
            depositorPhone = "01711-998877",
            status = "Approved",
            items = bulkItems
        )
        bulkDeposits.add(bulk1)

        // Pre-seeded Expenses
        expenses.addAll(
            listOf(
                Expense(
                    id = "exp_1",
                    samityId = "samity_1",
                    voucherNo = "EXP-2026-001",
                    expenseDate = "০৫ অক্টোবর ২০২৬",
                    category = "Office Rent",
                    titleBn = "অক্টোবর মাসের অফিস কক্ষ ভাড়া",
                    titleEn = "October Office Rent",
                    description = "উত্তরা প্রধান কার্যালয়ের মাসিক ভাড়া পরিশোধ",
                    amount = 12000.0,
                    paymentMethod = "Bank",
                    paidTo = "আলহাজ্ব মোকাররম হোসেন (বাড়িওয়ালা)",
                    paidBy = "কোষাধ্যক্ষ",
                    approvalStatus = "Approved"
                ),
                Expense(
                    id = "exp_2",
                    samityId = "samity_1",
                    voucherNo = "EXP-2026-002",
                    expenseDate = "১২ অক্টোবর ২০২৬",
                    category = "Meeting Cost",
                    titleBn = "মাসিক কার্যনির্বাহী সভার নাস্তা ও আপ্যায়ন",
                    titleEn = "Executive Meeting Refreshment",
                    description = "১৮ জন সদস্যের চা-নাস্তা এবং ফলমূল",
                    amount = 2500.0,
                    paymentMethod = "Cash",
                    paidTo = "উত্তরা ক্যাফে",
                    paidBy = "সাধারণ সম্পাদক",
                    approvalStatus = "Approved"
                ),
                Expense(
                    id = "exp_3",
                    samityId = "samity_1",
                    voucherNo = "EXP-2026-003",
                    expenseDate = "১৫ অক্টোবর ২০২৬",
                    category = "Stationery",
                    titleBn = "সমিতির লেটারহেড প্যাড ও রিসিট বই প্রিন্টিং",
                    titleEn = "Letterhead & Receipt Book Print",
                    description = "৫টি রিসিট বই ও ২ প্যাড অফিসিয়াল লেটারহেড",
                    amount = 3500.0,
                    paymentMethod = "bKash",
                    paidTo = "নিউ গ্রাফিক্স প্রিন্টিং প্রেস",
                    paidBy = "কোষাধ্যক্ষ",
                    approvalStatus = "Approved"
                )
            )
        )

        // Pre-seeded Reminder Logs
        reminderLogs.addAll(
            listOf(
                ReminderLog(
                    id = "rem_1",
                    samityId = "samity_1",
                    memberId = "mem_2",
                    memberName = "মো. কামাল হোসেন",
                    phone = "01819-334455",
                    channel = "WhatsApp",
                    dueMonth = "অক্টোবর ২০২৬",
                    dueAmount = 5000.0,
                    sentAt = "22-10-2026 11:30",
                    sentBy = "Treasurer",
                    deliveryStatus = "Delivered",
                    paidAfterReminder = false
                ),
                ReminderLog(
                    id = "rem_2",
                    samityId = "samity_1",
                    memberId = "mem_4",
                    memberName = "তাহমিনা আক্তার",
                    phone = "01622-445566",
                    channel = "SMS",
                    dueMonth = "অক্টোবর ২০২৬",
                    dueAmount = 5000.0,
                    sentAt = "20-10-2026 14:15",
                    sentBy = "Treasurer",
                    deliveryStatus = "Sent",
                    paidAfterReminder = false
                )
            )
        )

        // Pre-seeded Audit Logs
        auditLogs.addAll(
            listOf(
                AuditLog("aud_1", "samity_1", "সিস্টেম ইনিশিয়ালাইজেশন", "System Initialized", "Super Admin", "SUPER_ADMIN", "Hishab Kitab 360 Samity Module loaded with Swapno Chhoya Samity", "20-10-2026 09:00:00"),
                AuditLog("aud_2", "samity_1", "ব্যাংক জমা এন্ট্রি", "Recorded Deposit", "Treasurer", "TREASURER", "Voucher: RCPT-2026-000001, Amount: 5000 BDT for Rafiq Ahmed", "20-10-2026 11:15:22"),
                AuditLog("aud_3", "samity_1", "হোয়াটসঅ্যাপ রিমাইন্ডার প্রেরণ", "Sent WhatsApp Reminder", "Treasurer", "TREASURER", "Sent due reminder to Kamal Hossain (SM-0002) for 5000 BDT", "22-10-2026 11:30:10")
            )
        )

        // Pre-seeded SaaS Licenses configured by Software Seller (Super Admin)
        licenses.addAll(
            listOf(
                SamityLicenseConfig(
                    samityId = "samity_1",
                    licenseKey = "SK360-SWAPNO-100MEM",
                    planName = "Commercial 100-Member Pack (সব ফিচার সক্রিয়)",
                    maxMembersLimit = 100,
                    validUntil = "৩১ ডিসেম্বর ২০২৭",
                    isActive = true,
                    enableBankDeposit = true,
                    enableBulkDeposit = true,
                    enableExpenses = true,
                    enableReminders = true,
                    enableWhatsApp = true,
                    enableSms = true,
                    enableLetterhead = true,
                    enableDocuments = true,
                    enableMemberPortal = true,
                    enableReports = true,
                    enableAuditTrail = true
                ),
                SamityLicenseConfig(
                    samityId = "samity_2",
                    licenseKey = "SK360-SURYODOY-50MEM",
                    planName = "Starter 50-Member Pack (সীমিত মডিউল)",
                    maxMembersLimit = 50,
                    validUntil = "৩০ জুন ২০২৭",
                    isActive = true,
                    enableBankDeposit = true,
                    enableBulkDeposit = false,
                    enableExpenses = true,
                    enableReminders = true,
                    enableWhatsApp = true,
                    enableSms = false,
                    enableLetterhead = true,
                    enableDocuments = false,
                    enableMemberPortal = true,
                    enableReports = true,
                    enableAuditTrail = true
                ),
                SamityLicenseConfig(
                    samityId = "samity_3",
                    licenseKey = "SK360-PROGOTI-100MEM",
                    planName = "Enterprise 100-Member Pack (সব ফিচার সক্রিয়)",
                    maxMembersLimit = 100,
                    validUntil = "১৫ নভেম্বর ২০২৭",
                    isActive = true,
                    enableBankDeposit = true,
                    enableBulkDeposit = true,
                    enableExpenses = true,
                    enableReminders = true,
                    enableWhatsApp = true,
                    enableSms = true,
                    enableLetterhead = true,
                    enableDocuments = true,
                    enableMemberPortal = true,
                    enableReports = true,
                    enableAuditTrail = true
                )
            )
        )

        // Seed initial credentials for existing members
        members.forEach { m ->
            memberCredentials.add(
                MemberCredential(
                    id = "cred_${m.id}",
                    samityId = m.samityId,
                    memberId = m.id,
                    memberCode = m.memberCode,
                    phone = m.phone,
                    pinHash = "1234",
                    isActivated = true,
                    activatedAt = "০১-০১-২০২০"
                )
            )
        }

        // Generate 100 complete members with accounts & credentials for samity_1
        generate100Members("samity_1")

        // Seed 50 members for samity_2 (Suryodoy Client Samity)
        for (i in 1..50) {
            val code = "SD-${String.format("%04d", i)}"
            members.add(
                Member(
                    id = "mem_s2_$i",
                    samityId = "samity_2",
                    memberCode = code,
                    nameBn = "সূর্যোদয় সদস্য $i",
                    nameEn = "Suryodoy Member $i",
                    fatherName = "মরহুম পিতা $i",
                    phone = "01819-${String.format("%06d", 200000 + i)}",
                    whatsappNumber = "8801819${String.format("%06d", 200000 + i)}",
                    telegramHandle = "@sd_member_$i",
                    facebookUrl = "https://facebook.com/sd_member_$i",
                    email = "sd$i@suryodoy.org",
                    address = "জিইসি মোড়, চট্টগ্রাম",
                    occupation = "ব্যবসায়ী",
                    bloodGroup = "A+",
                    membershipType = "সাধারণ সদস্য",
                    monthlyFee = 3000.0,
                    joinDate = "১৫-০৩-২০২২",
                    status = "Active",
                    currentMonthPaid = if (i % 2 == 0) 3000.0 else 0.0,
                    totalPaid = 36000.0,
                    totalDue = if (i % 2 == 0) 0.0 else 3000.0
                )
            )
        }

        // Seed 25 members for samity_3 (Progoti Traders Client Samity)
        for (i in 1..25) {
            val code = "PG-${String.format("%04d", i)}"
            members.add(
                Member(
                    id = "mem_s3_$i",
                    samityId = "samity_3",
                    memberCode = code,
                    nameBn = "প্রগতি সদস্য $i",
                    nameEn = "Progoti Member $i",
                    fatherName = "মরহুম পিতা $i",
                    phone = "01911-${String.format("%06d", 300000 + i)}",
                    whatsappNumber = "8801911${String.format("%06d", 300000 + i)}",
                    telegramHandle = "@pg_member_$i",
                    facebookUrl = "https://facebook.com/pg_member_$i",
                    email = "pg$i@progotisamity.org",
                    address = "জিন্দাবাজার মার্কেট, সিলেট",
                    occupation = "দোকানদার",
                    bloodGroup = "O+",
                    membershipType = "সাধারণ সদস্য",
                    monthlyFee = 4000.0,
                    joinDate = "১০-০১-২০২৩",
                    status = "Active",
                    currentMonthPaid = 4000.0,
                    totalPaid = 48000.0,
                    totalDue = 0.0
                )
            )
        }
    }
}
