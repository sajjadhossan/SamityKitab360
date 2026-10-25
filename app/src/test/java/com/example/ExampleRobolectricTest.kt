package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.data.auth.AppPermission
import com.example.data.auth.RbacWrapper
import com.example.data.auth.UserSession
import com.example.data.auth.UserSessionManager
import com.example.data.local.room.*
import com.example.data.repository.RoomMemberRepository
import com.example.data.repository.RoomDepositRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Samity Kitab 360", appName)
    }

    @Test
    fun `verify samity repository multi-tenant and role management`() {
        val repository = SamityRepository()
        assertEquals("samity_1", repository.activeSamityId)
        assertTrue(repository.activeMembers.isNotEmpty())
        assertTrue(repository.activeDeposits.isNotEmpty())

        // Test multi-tenant switching
        repository.switchSamity("samity_2")
        assertEquals("samity_2", repository.activeSamityId)

        // Test role switching
        repository.switchRole(UserRole.TREASURER)
        assertEquals(UserRole.TREASURER, repository.currentUserRole)
        assertEquals(UserRole.TREASURER, repository.userSession.currentUserRole)
    }

    @Test
    fun `verify user session and role authorization restrictions`() {
        val repository = SamityRepository()
        val session = repository.userSession

        // Initial default is SUPER_ADMIN with samity_1
        assertEquals(UserRole.SUPER_ADMIN, session.currentUserRole)
        assertEquals("samity_1", session.assignedSamityId)
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.CREATE_MEMBER))
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.CREATE_DEPOSIT))
        assertTrue(session.canAccessSamity("samity_2")) // Super Admin can access any samity

        // Switch to GENERAL_MEMBER role
        repository.switchRole(UserRole.GENERAL_MEMBER)
        assertEquals(UserRole.GENERAL_MEMBER, repository.currentUserRole)
        assertEquals(UserRole.GENERAL_MEMBER, session.currentUserRole)

        // General member must not have admin CRUD permissions
        org.junit.Assert.assertFalse(session.hasPermission(com.example.data.auth.AppPermission.CREATE_MEMBER))
        org.junit.Assert.assertFalse(session.hasPermission(com.example.data.auth.AppPermission.CREATE_DEPOSIT))
        org.junit.Assert.assertFalse(session.hasPermission(com.example.data.auth.AppPermission.CREATE_EXPENSE))
        org.junit.Assert.assertFalse(session.hasPermission(com.example.data.auth.AppPermission.MANAGE_SAMITY_CONFIG))

        // General member can access portal and fund balance
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.VIEW_MEMBER_PORTAL))
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.VIEW_FUND_BALANCE))
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.VIEW_RECEIPT))

        // Attempting CRUD action as General Member must be rejected and logged
        val testMember = com.example.data.model.Member(
            id = "mem_test",
            samityId = "samity_1",
            memberCode = "SM-9999",
            nameBn = "অননুমোদিত সদস্য",
            nameEn = "Unauthorized Member",
            fatherName = "অজ্ঞাত",
            phone = "01700-000000",
            whatsappNumber = "",
            telegramHandle = "",
            facebookUrl = "",
            email = "",
            address = "ঢাকা",
            occupation = "",
            bloodGroup = "O+",
            membershipType = "সাধারণ",
            monthlyFee = 1000.0,
            joinDate = "০১-০১-২০২৬"
        )
        val addMemberResult = repository.addMember(testMember)
        org.junit.Assert.assertFalse("General Member must not be authorized to create a member", addMemberResult)

        // Verify security audit entry was generated
        val latestAudit = repository.auditLogs.firstOrNull()
        org.junit.Assert.assertNotNull(latestAudit)
        assertTrue(latestAudit!!.actionBn.contains("অননুমোদিত"))

        // Switch to TREASURER
        repository.switchRole(UserRole.TREASURER)
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.CREATE_DEPOSIT))
        assertTrue(session.hasPermission(com.example.data.auth.AppPermission.CREATE_EXPENSE))
        org.junit.Assert.assertFalse("Treasurer should not have permission to manage samity config", session.hasPermission(com.example.data.auth.AppPermission.MANAGE_SAMITY_CONFIG))
    }

    @Test
    fun `verify 100 members individual login and credential activation`() {
        val repository = SamityRepository()

        // Verify 100 members exist for samity_1
        val members = repository.activeMembers
        assertEquals(100, members.size)

        // Verify Member SM-0001 authentication
        val member1 = repository.authenticateMember("SM-0001", "1234")
        org.junit.Assert.assertNotNull("Member SM-0001 must authenticate with default PIN 1234", member1)
        assertEquals("SM-0001", member1!!.memberCode)

        // Verify Member SM-0050 authentication
        val member50 = repository.authenticateMember("SM-0050", "1234")
        org.junit.Assert.assertNotNull("Member SM-0050 must authenticate with default PIN 1234", member50)

        // Verify member activation with new PIN
        val activated = repository.activateMemberAccount("SM-0050", member50!!.phone, "9876")
        assertTrue("Member SM-0050 account activation with new PIN must succeed", activated)

        // Verify old PIN fails and new PIN succeeds
        org.junit.Assert.assertNull(repository.authenticateMember("SM-0050", "1234"))
        val reAuthMember = repository.authenticateMember("SM-0050", "9876")
        org.junit.Assert.assertNotNull(reAuthMember)

        // Verify individual member login sets session to GENERAL_MEMBER with portal access
        repository.userSession.loginAsMember(reAuthMember!!)
        assertEquals(UserRole.GENERAL_MEMBER, repository.userSession.currentUserRole)
        assertEquals(reAuthMember.id, repository.userSession.currentUser.memberId)
        assertTrue(repository.userSession.hasPermission(com.example.data.auth.AppPermission.VIEW_MEMBER_PORTAL))
    }

    @Test
    fun `verify SaaS client feature toggling and role permission management`() {
        val repository = SamityRepository()

        // Initial license plan
        val initialLic = repository.activeLicense
        assertEquals(100, initialLic.maxMembersLimit)
        assertTrue(initialLic.enableBulkDeposit)

        // Super Admin toggles client features
        val updatedLic = initialLic.copy(
            planName = "Custom Co-op Pack",
            maxMembersLimit = 250,
            enableBulkDeposit = false
        )
        repository.updateLicenseConfig(updatedLic)
        assertEquals("Custom Co-op Pack", repository.activeLicense.planName)
        assertEquals(250, repository.activeLicense.maxMembersLimit)
        org.junit.Assert.assertFalse(repository.activeLicense.enableBulkDeposit)

        // Samity Admin dynamically modifies role permissions
        val session = repository.userSession
        val treasurerPerms = session.getPermissionsForRole(UserRole.TREASURER).toMutableSet()
        // Admin grants CREATE_MEMBER to Treasurer
        treasurerPerms.add(com.example.data.auth.AppPermission.CREATE_MEMBER)
        session.updateRolePermissions(UserRole.TREASURER, treasurerPerms)

        // Now Treasurer has CREATE_MEMBER permission
        repository.switchRole(UserRole.TREASURER)
        assertTrue("Treasurer should now have dynamically granted CREATE_MEMBER permission", session.hasPermission(com.example.data.auth.AppPermission.CREATE_MEMBER))
    }

    @Test
    fun `verify UserSessionManager persists user ID role and assigned samity_id across restarts`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. First session: Save session credentials
        val session1 = com.example.data.auth.UserSessionManager(context)
        session1.saveSession(
            userId = "user_sec_01",
            role = UserRole.GENERAL_SECRETARY,
            samityId = "samity_2",
            userName = "মো. সাজ্জাদ হোসাইন (সাধারণ সম্পাদক)",
            memberId = "mem_sec_02",
            phone = "01711-667788",
            email = "secretary@samity2.org"
        )

        assertEquals("user_sec_01", session1.currentUserId)
        assertEquals(UserRole.GENERAL_SECRETARY, session1.currentRole)
        assertEquals("samity_2", session1.assignedSamityId)
        assertEquals("samity_2", session1.currentSamityId)
        assertTrue(session1.isLoggedIn)

        // Verify underlying SharedPreferences persistence directly
        assertEquals("user_sec_01", session1.getPersistedUserId())
        assertEquals(UserRole.GENERAL_SECRETARY, session1.getPersistedRole())
        assertEquals("samity_2", session1.getPersistedSamityId())

        // 2. Simulate app restart: Create a brand new UserSessionManager instance with the same context
        val sessionAfterRestart = com.example.data.auth.UserSessionManager(context)
        // Values must be restored automatically from SharedPreferences
        assertEquals("user_sec_01", sessionAfterRestart.currentUserId)
        assertEquals(UserRole.GENERAL_SECRETARY, sessionAfterRestart.currentRole)
        assertEquals("samity_2", sessionAfterRestart.assignedSamityId)
        assertEquals("মো. সাজ্জাদ হোসাইন (সাধারণ সম্পাদক)", sessionAfterRestart.currentUserName)
        assertTrue(sessionAfterRestart.isLoggedIn)

        // 3. Test update samity and role
        sessionAfterRestart.updateActiveSamity("samity_1")
        sessionAfterRestart.updateRole(UserRole.TREASURER)
        assertEquals("samity_1", sessionAfterRestart.assignedSamityId)
        assertEquals(UserRole.TREASURER, sessionAfterRestart.currentRole)

        // 4. Verify again in a 3rd instance to ensure updates persisted
        val sessionInstance3 = com.example.data.auth.UserSessionManager(context)
        assertEquals("samity_1", sessionInstance3.assignedSamityId)
        assertEquals(UserRole.TREASURER, sessionInstance3.currentRole)
    }

    @Test
    fun `verify Repository layer automatically filters all database queries by current samity_id`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sessionManager = com.example.data.auth.UserSessionManager(context)
        // Reset to Super Admin and samity_1 for clean test
        sessionManager.saveSession("user_admin", UserRole.SUPER_ADMIN, "samity_1", "Super Admin")

        val repository = SamityRepository(sessionManager = sessionManager)

        // Verify queries for samity_1
        assertEquals("samity_1", repository.activeSamityId)
        val samity1Members = repository.getMembers()
        assertTrue("samity_1 must have members", samity1Members.isNotEmpty())
        assertTrue("All returned members must belong to samity_1", samity1Members.all { it.samityId == "samity_1" })

        val samity1Deposits = repository.getDeposits()
        assertTrue("samity_1 must have deposits", samity1Deposits.isNotEmpty())
        assertTrue("All returned deposits must belong to samity_1", samity1Deposits.all { it.samityId == "samity_1" })

        val samity1Expenses = repository.getExpenses()
        assertTrue("samity_1 must have expenses", samity1Expenses.isNotEmpty())
        assertTrue("All returned expenses must belong to samity_1", samity1Expenses.all { it.samityId == "samity_1" })

        // Add distinct data for samity_2
        val s2Member = com.example.data.model.Member(
            id = "mem_s2_01",
            samityId = "samity_2",
            memberCode = "S2-001",
            nameBn = "সূর্যোদয় সদস্য ১",
            nameEn = "Suryodoy Member 1",
            fatherName = "মরহুম পিতা",
            phone = "01819-998877",
            whatsappNumber = "",
            telegramHandle = "",
            facebookUrl = "",
            email = "s2m1@suryodoy.org",
            address = "চট্টগ্রাম",
            occupation = "ব্যবসায়ী",
            bloodGroup = "B+",
            membershipType = "সাধারণ",
            monthlyFee = 3000.0,
            joinDate = "০১-০১-২০২৬",
            totalPaid = 3000.0,
            totalDue = 0.0
        )
        repository.members.add(s2Member)

        val s2Deposit = com.example.data.model.Deposit(
            id = "dep_s2_01",
            samityId = "samity_2",
            voucherNo = "RCPT-S2-001",
            depositDate = "২৪ অক্টোবর ২০২৬",
            memberId = "mem_s2_01",
            memberName = "সূর্যোদয় সদস্য ১",
            memberCode = "S2-001",
            depositType = "মাসিক চাঁদা",
            depositMethod = "Cash",
            amount = 3000.0,
            bankName = "ক্যাশ",
            bankBranch = "চট্টগ্রাম",
            bankAccountNo = "CASH",
            bankReceiptNo = "REC-S2-1",
            transactionId = "TXN-S2-1",
            paymentMonth = "অক্টোবর ২০২৬",
            paymentYear = 2026,
            approvalStatus = "Approved",
            remarks = "চট্টগ্রাম শাখা",
            entryBy = "অ্যাডমিন"
        )
        repository.deposits.add(s2Deposit)

        // While active samity is samity_1:
        // Neither s2Member nor s2Deposit should EVER appear in samity_1 queries
        val currentMembers = repository.getMembers()
        org.junit.Assert.assertFalse("samity_1 queries must never contain samity_2 member", currentMembers.any { it.id == "mem_s2_01" })
        val currentDeposits = repository.getDeposits()
        org.junit.Assert.assertFalse("samity_1 queries must never contain samity_2 deposit", currentDeposits.any { it.id == "dep_s2_01" })

        // Switch to samity_2
        repository.switchSamity("samity_2")
        assertEquals("samity_2", repository.activeSamityId)
        assertEquals("samity_2", sessionManager.currentSamityId)

        // Now all repository queries must automatically filter for samity_2!
        val s2MembersFiltered = repository.getMembers()
        assertTrue("samity_2 members must contain mem_s2_01", s2MembersFiltered.any { it.id == "mem_s2_01" })
        assertTrue("All returned members must belong to samity_2", s2MembersFiltered.all { it.samityId == "samity_2" })

        val s2DepositsFiltered = repository.getDeposits()
        assertTrue("samity_2 deposits must contain dep_s2_01", s2DepositsFiltered.any { it.id == "dep_s2_01" })
        assertTrue("All returned deposits must belong to samity_2", s2DepositsFiltered.all { it.samityId == "samity_2" })

        // Property accessors (activeMembers, activeDeposits) must match the auto-filtered queries
        assertEquals(s2MembersFiltered.size, repository.activeMembers.size)
        assertEquals(s2DepositsFiltered.size, repository.activeDeposits.size)

        // Financial calculations must automatically scope to samity_2
        assertEquals(3000.0, repository.totalApprovedDeposits, 0.01)
    }

    @Test
    fun `verify RbacWrapper validates permissions before executing repository actions`() {
        val sessionManager = UserSessionManager(null)
        sessionManager.saveSession("user_mem_1", UserRole.GENERAL_MEMBER, "samity_1", "সদস্য রহিম")
        val userSession = UserSession(sessionManager = sessionManager)
        val rbac = RbacWrapper(userSession)

        // General member should not have CREATE_MEMBER permission
        assertFalse(rbac.hasPermission(AppPermission.CREATE_MEMBER))

        // executeAction should intercept and return unauthorized fallback without executing the block
        var executed = false
        val result = rbac.executeAction(
            permission = AppPermission.CREATE_MEMBER,
            onUnauthorized = { err -> "BLOCKED: $err" }
        ) {
            executed = true
            "SUCCESS"
        }
        assertFalse("Action must not execute when role lacks permission", executed)
        assertTrue("Fallback must receive error message", result?.startsWith("BLOCKED:") == true)

        // executeOrThrow should throw SecurityException
        try {
            rbac.executeOrThrow(AppPermission.CREATE_MEMBER) {
                fail("Should have thrown SecurityException")
            }
        } catch (e: SecurityException) {
            assertTrue("SecurityException must explain lacking permission", e.message?.contains("lacks required permission") == true)
        }

        // Switch to President
        sessionManager.saveSession("user_pres_1", UserRole.PRESIDENT, "samity_1", "সভাপতি করিম")
        assertTrue(rbac.hasPermission(AppPermission.CREATE_MEMBER))

        // Now executeAction must execute successfully
        val successResult = rbac.executeAction(AppPermission.CREATE_MEMBER) {
            executed = true
            "SUCCESS"
        }
        assertTrue(executed)
        assertEquals("SUCCESS", successResult)

        // Tenant scope protection: Even President of samity_1 cannot execute action for samity_2
        assertFalse(rbac.hasPermission(AppPermission.CREATE_MEMBER, targetSamityId = "samity_2"))
        var crossTenantExecuted = false
        val crossResult = rbac.executeAction(
            permission = AppPermission.CREATE_MEMBER,
            targetSamityId = "samity_2",
            onUnauthorized = { "CROSS_TENANT_BLOCKED" }
        ) {
            crossTenantExecuted = true
            "ALLOWED"
        }
        assertFalse("Cross-tenant execution must be blocked", crossTenantExecuted)
        assertEquals("CROSS_TENANT_BLOCKED", crossResult)
    }

    @Test
    fun `verify BaseSamityRepository appends WHERE samity_id clause to every query and isolates Room data`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = SamityRoomDatabase.createInMemory(context)
        val memberDao = db.memberDao()
        val depositDao = db.depositDao()

        val sessionManager = UserSessionManager(context)
        sessionManager.saveSession("admin_user", UserRole.SUPER_ADMIN, "samity_alpha", "Super Admin")
        val rbacWrapper = RbacWrapper(UserSession(sessionManager = sessionManager))

        val memberRepo = RoomMemberRepository(memberDao, sessionManager, rbacWrapper)
        val depositRepo = RoomDepositRepository(depositDao, sessionManager, rbacWrapper)

        // 1. Verify appendSamityScope SQL transformation logic
        val raw1 = memberRepo.appendSamityScope("SELECT * FROM room_members")
        assertTrue("Appends WHERE samity_id = ? to query without WHERE", raw1.sql.contains("WHERE samity_id = ?"))

        val raw2 = memberRepo.appendSamityScope("SELECT * FROM room_members WHERE status = 'Active'")
        assertTrue("Appends (samity_id = ?) AND (...) to query with existing WHERE", raw2.sql.contains("(samity_id = ?) AND (status = 'Active')"))

        val raw3 = memberRepo.appendSamityScope("SELECT * FROM room_members ORDER BY memberCode ASC")
        assertTrue("Maintains ORDER BY clause at the end", raw3.sql.endsWith("ORDER BY memberCode ASC"))
        assertTrue("Includes WHERE samity_id = ? before ORDER BY", raw3.sql.contains("WHERE samity_id = ? ORDER BY"))

        // 2. Insert test data directly into Room for multiple distinct samity tenants
        val memberAlpha1 = MemberEntity("m_alpha_1", "samity_alpha", "SM-01", "আলফা সদস্য ১", phone = "01700-111111")
        val memberAlpha2 = MemberEntity("m_alpha_2", "samity_alpha", "SM-02", "আলফা সদস্য ২", phone = "01700-222222")
        val memberBeta1 = MemberEntity("m_beta_1", "samity_beta", "SM-01", "বেটা সদস্য ১", phone = "01800-111111")
        val memberBeta2 = MemberEntity("m_beta_2", "samity_beta", "SM-02", "বেটা সদস্য ২", phone = "01800-222222")

        memberDao.insertAll(listOf(memberAlpha1, memberAlpha2, memberBeta1, memberBeta2))

        val depAlpha = DepositEntity("dep_a_1", "samity_alpha", "V-A1", "m_alpha_1", "আলফা সদস্য ১", 5000.0, "20-10-2026", "Bank")
        val depBeta = DepositEntity("dep_b_1", "samity_beta", "V-B1", "m_beta_1", "বেটা সদস্য ১", 3000.0, "21-10-2026", "Cash")
        depositDao.insertAll(listOf(depAlpha, depBeta))

        // 3. Query through RoomMemberRepository while session is active for 'samity_alpha'
        assertEquals("samity_alpha", memberRepo.currentSamityId)
        val alphaMembers = memberRepo.getMemberList()
        assertEquals(2, alphaMembers.size)
        assertTrue("All returned members must belong to samity_alpha", alphaMembers.all { it.samityId == "samity_alpha" })
        assertFalse("Must never contain samity_beta members", alphaMembers.any { it.samityId == "samity_beta" })

        val alphaDeposits = depositRepo.getDepositList()
        assertEquals(1, alphaDeposits.size)
        assertEquals("samity_alpha", alphaDeposits[0].samityId)
        assertEquals("V-A1", alphaDeposits[0].voucherNo)

        // 4. Switch session to 'samity_beta'
        sessionManager.updateActiveSamity("samity_beta")
        assertEquals("samity_beta", memberRepo.currentSamityId)

        // Now Room queries MUST automatically return only 'samity_beta' records!
        val betaMembers = memberRepo.getMemberList()
        assertEquals(2, betaMembers.size)
        assertTrue("All returned members must belong to samity_beta", betaMembers.all { it.samityId == "samity_beta" })
        assertFalse("Must never contain samity_alpha members", betaMembers.any { it.samityId == "samity_alpha" })

        val betaDeposits = depositRepo.getDepositList()
        assertEquals(1, betaDeposits.size)
        assertEquals("samity_beta", betaDeposits[0].samityId)
        assertEquals("V-B1", betaDeposits[0].voucherNo)

        // 5. Test tenant scope validation on insert
        try {
            // While session is 'samity_beta', trying to insert a record tagged with 'samity_alpha' must be rejected
            memberRepo.insertMember(MemberEntity("m_illegal", "samity_alpha", "ILLEGAL", "অবৈধ সদস্য", phone = "01999-000000"))
            fail("Should have rejected multi-tenant violation")
        } catch (e: SecurityException) {
            assertTrue("Exception must state multi-tenant violation", e.message?.contains("Multi-Tenant Isolation Violation") == true)
        }

        db.close()
    }

    @Test
    fun `verify super admin saas merchant portal validity and client privacy vs self-owned samity`() {
        val repository = SamityRepository()

        // 1. Verify multi-tenant merchants / samities are loaded
        assertTrue("At least 3 businesses seeded", repository.samities.size >= 3)
        val samity1 = repository.samities.first { it.id == "samity_1" }
        val samity2 = repository.samities.first { it.id == "samity_2" }
        val samity3 = repository.samities.first { it.id == "samity_3" }

        // 2. Verify Super Admin owned samity has full owner flag
        assertTrue("Samity 1 is owned by Super Admin", samity1.isOwnedBySuperAdmin)
        assertEquals("Active", samity1.accountStatus)
        assertTrue("Validity days remaining > 0", samity1.daysRemaining > 0)
        assertEquals(365, samity1.daysRemaining)

        // 3. Verify client businesses are third-party (not owned by Super Admin)
        assertFalse("Samity 2 is client owned", samity2.isOwnedBySuperAdmin)
        assertEquals("Active", samity2.accountStatus)
        assertEquals(248, samity2.daysRemaining)
        assertFalse("Samity 3 is client owned", samity3.isOwnedBySuperAdmin)
        assertEquals(386, samity3.daysRemaining)

        // 4. Verify Super Admin can view member count for all clients without needing private profile access
        val samity1MemberCount = repository.getMembers("samity_1").size
        val samity2MemberCount = repository.getMembers("samity_2").size
        val samity3MemberCount = repository.getMembers("samity_3").size
        assertEquals("Samity 1 has 100 members", 100, samity1MemberCount)
        assertEquals("Samity 2 has 50 members", 50, samity2MemberCount)
        assertEquals("Samity 3 has 25 members", 25, samity3MemberCount)

        // 5. Verify vendor can toggle module features per client
        val client2License = repository.getLicenseConfig("samity_2")
        assertFalse("Initial Bulk deposit was off for samity 2", client2License.enableBulkDeposit)
        repository.updateLicenseConfig(client2License.copy(enableBulkDeposit = true, enableSms = true))
        val updatedLicense = repository.getLicenseConfig("samity_2")
        assertTrue("Bulk deposit is now enabled for samity 2", updatedLicense.enableBulkDeposit)
        assertTrue("SMS is now enabled for samity 2", updatedLicense.enableSms)
    }

    @Test
    fun `verify real-time cross-device instant sync on 10000 Tk deposit`() {
        val repository = SamityRepository()
        val syncEngine = repository.syncEngine

        // 1. Initial sync state
        assertTrue("Live sync is active", syncEngine.isLiveSyncActive)
        assertTrue("Connected devices list is populated", syncEngine.connectedDevices.size >= 4)
        val initialEventsCount = syncEngine.syncEvents.size

        // 2. Select a member in active samity
        val targetMember = repository.activeMembers.first()
        val initialPaid = targetMember.totalPaid
        val initialDue = targetMember.totalDue
        val initialDepositsCount = repository.activeDeposits.size

        // 3. Simulate a 10,000 Tk deposit from another mobile device
        val originDeviceName = "অন্যান্য মোবাইল (ডিভাইস ২ - কোষাধ্যক্ষ)"
        val syncedDeposit = repository.simulateCrossDevicePayment(
            memberId = targetMember.id,
            amount = 10000.0,
            originDevice = originDeviceName
        )

        assertNotNull("Synced deposit must not be null", syncedDeposit)
        assertEquals(10000.0, syncedDeposit!!.amount, 0.01)

        // 4. Verify instant state reflection across repository
        assertEquals("Deposit list must increment", initialDepositsCount + 1, repository.activeDeposits.size)

        val updatedMember = repository.getMemberById(targetMember.id)
        assertNotNull(updatedMember)
        assertEquals("Member totalPaid must increase by 10,000 Tk", initialPaid + 10000.0, updatedMember!!.totalPaid, 0.01)
        assertEquals("Member totalDue must be reduced by 10,000 Tk", (initialDue - 10000.0).coerceAtLeast(0.0), updatedMember.totalDue, 0.01)

        // 5. Verify syncEngine broadcast and notification for all connected devices
        assertNotNull("Latest notification must be populated", syncEngine.latestNotification)
        val notice = syncEngine.latestNotification!!
        assertEquals(10000.0, notice.amount!!, 0.01)
        assertEquals(originDeviceName, notice.originDevice)
        assertTrue("Notification title mentions real-time sync", notice.titleBn.contains("সিঙ্ক"))

        // 6. Verify real-time event feed logged the transaction
        assertEquals(initialEventsCount + 1, syncEngine.syncEvents.size)
        val firstEvent = syncEngine.syncEvents.first()
        assertTrue(firstEvent is com.example.data.model.RealtimeSyncEvent.DepositSynced)
        val depEvent = firstEvent as com.example.data.model.RealtimeSyncEvent.DepositSynced
        assertEquals(10000.0, depEvent.amount, 0.01)
        assertEquals(originDeviceName, depEvent.originDevice)
    }

    @Test
    fun `verify software vendor client onboarding and license provisioning`() {
        val repository = SamityRepository()
        val initialSamityCount = repository.samities.size

        // Vendor creates and sells software to a new merchant
        val newClient = repository.registerNewMerchantSamity(
            nameBn = "ধানমন্ডি ব্যবসায়ী সমবায় সমিতি",
            nameEn = "Dhanmondi Traders Samity",
            merchantOwnerName = "জনাব তারিকুল ইসলাম",
            merchantPhone = "01722-998877",
            subscriptionPlan = "Enterprise 100-Member Pack",
            validityDays = 180,
            maxMembers = 100,
            initialMembersCount = 15
        )

        assertEquals(initialSamityCount + 1, repository.samities.size)
        assertFalse("New merchant is a client, not self-owned", newClient.isOwnedBySuperAdmin)
        assertEquals("Active", newClient.accountStatus)
        assertEquals(180, newClient.daysRemaining)

        // Verify member count for new client
        val clientMembers = repository.getMembers(newClient.id)
        assertEquals(15, clientMembers.size)

        // Verify license was provisioned
        val clientLicense = repository.getLicenseConfig(newClient.id)
        assertNotNull(clientLicense)
        assertTrue(clientLicense.isActive)
        assertEquals("Enterprise 100-Member Pack", clientLicense.planName)
    }

    @Test
    fun `verify Vercel bidirectional parity scan and 100 percent data match`() {
        val repository = SamityRepository()
        val gateway = repository.vercelGateway

        // Execute scan and parity verification
        var callbackSuccess = false
        var callbackMsg = ""
        repository.scanAndSyncWithVercel { success, msg ->
            callbackSuccess = success
            callbackMsg = msg
        }

        assertTrue("Vercel scan callback must report success", callbackSuccess)
        assertTrue("Callback message mentions 100% match", callbackMsg.contains("১০০%"))
        assertTrue("Gateway flags data as fully matched", gateway.isDataFullyMatched)
        assertEquals(100, gateway.syncMatchPercentage)
        assertEquals(repository.activeMembers.size, gateway.cloudMemberCount)
        assertEquals(repository.activeDeposits.sumOf { it.amount }, gateway.cloudDepositTotal, 0.01)
        assertEquals(repository.samities.count { it.accountStatus == "Active" }, gateway.cloudActiveMerchants)
        assertTrue(gateway.cloudSyncAuditLogs.isNotEmpty())
    }

    @Test
    fun `verify 10,000 Tk cross-platform sync propagation between Vercel and mobile`() {
        val repository = SamityRepository()
        val gateway = repository.vercelGateway
        val syncEngine = repository.syncEngine

        val initialDepositSum = repository.activeDeposits.sumOf { it.amount }

        // Trigger 10k cross-platform sync
        val dep = repository.sync10kWithVercel()
        assertNotNull("10k sync deposit must be created", dep)
        assertEquals(10000.0, dep!!.amount, 0.01)
        assertEquals(initialDepositSum + 10000.0, repository.activeDeposits.sumOf { it.amount }, 0.01)

        // Verify broadcast to mobile sync engine
        assertNotNull("Sync engine notification must be active", syncEngine.latestNotification)
        assertEquals(10000.0, syncEngine.latestNotification!!.amount!!, 0.01)
        assertTrue(syncEngine.latestNotification!!.originDevice.contains("ভার্সেল"))

        // Verify audit trail recorded in gateway
        val latestLog = gateway.cloudSyncAuditLogs.first()
        assertTrue(latestLog.detailsBn.contains("১০,০০০"))
    }
}
