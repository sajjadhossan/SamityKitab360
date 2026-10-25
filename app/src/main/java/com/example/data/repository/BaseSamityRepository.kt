package com.example.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.data.auth.AppPermission
import com.example.data.auth.RbacWrapper
import com.example.data.auth.UserSessionManager
import com.example.data.local.room.DepositDao
import com.example.data.local.room.DepositEntity
import com.example.data.local.room.MemberDao
import com.example.data.local.room.MemberEntity
import com.example.data.local.room.SamityScopedEntity
import kotlinx.coroutines.flow.Flow

/**
 * Base Repository class that automatically appends a WHERE clause for 'samity_id'
 * to every database query.
 *
 * This enforces strict data isolation between different samities within the Room database.
 */
abstract class BaseSamityRepository<T : SamityScopedEntity>(
    val sessionManager: UserSessionManager,
    val rbacWrapper: RbacWrapper? = null
) {
    abstract val tableName: String

    val currentSamityId: String
        get() = sessionManager.currentSamityId

    /**
     * Automatically appends 'WHERE samity_id = ?' (or 'AND (samity_id = ?)') to any raw SQL query string.
     *
     * Guarantees:
     * 1. Queries without a WHERE clause get " WHERE samity_id = ?"
     * 2. Queries with an existing WHERE clause get " WHERE (samity_id = ?) AND (...)"
     * 3. Suffix clauses (ORDER BY, GROUP BY, LIMIT) are preserved at the end.
     * 4. The current active 'samity_id' is automatically injected as the binding parameter.
     */
    fun appendSamityScope(
        sql: String,
        existingArgs: Array<Any> = emptyArray(),
        targetSamityId: String = currentSamityId
    ): SupportSQLiteQuery {
        val trimmed = sql.trim().removeSuffix(";")
        val whereIndex = trimmed.indexOf("WHERE", ignoreCase = true)

        val scopedSql: String
        val combinedArgs: Array<Any>

        if (whereIndex == -1) {
            val orderIndex = trimmed.indexOf("ORDER BY", ignoreCase = true)
            val groupIndex = trimmed.indexOf("GROUP BY", ignoreCase = true)
            val limitIndex = trimmed.indexOf("LIMIT", ignoreCase = true)
            val suffixIndices = listOf(orderIndex, groupIndex, limitIndex).filter { it != -1 }
            val firstSuffixIndex = suffixIndices.minOrNull()

            if (firstSuffixIndex != null) {
                val prefix = trimmed.substring(0, firstSuffixIndex).trim()
                val suffix = trimmed.substring(firstSuffixIndex).trim()
                scopedSql = "$prefix WHERE samity_id = ? $suffix"
            } else {
                scopedSql = "$trimmed WHERE samity_id = ?"
            }
            combinedArgs = arrayOf(targetSamityId, *existingArgs)
        } else {
            val beforeWhere = trimmed.substring(0, whereIndex + 5)
            val afterWhere = trimmed.substring(whereIndex + 5).trim()
            scopedSql = "$beforeWhere (samity_id = ?) AND ($afterWhere)"
            combinedArgs = arrayOf(targetSamityId, *existingArgs)
        }

        return SimpleSQLiteQuery(scopedSql, combinedArgs)
    }

    /**
     * Builds a complete SELECT query strictly scoped to the active 'samity_id'.
     */
    fun buildScopedSelect(
        selection: String? = null,
        selectionArgs: Array<Any> = emptyArray(),
        orderBy: String? = null,
        limit: Int? = null,
        targetSamityId: String = currentSamityId
    ): SupportSQLiteQuery {
        val sb = StringBuilder("SELECT * FROM ").append(tableName)
        val argsList = ArrayList<Any>()

        sb.append(" WHERE samity_id = ?")
        argsList.add(targetSamityId)

        if (!selection.isNullOrBlank()) {
            sb.append(" AND (").append(selection).append(")")
            argsList.addAll(selectionArgs)
        }

        if (!orderBy.isNullOrBlank()) {
            sb.append(" ORDER BY ").append(orderBy)
        }

        if (limit != null && limit > 0) {
            sb.append(" LIMIT ").append(limit)
        }

        return SimpleSQLiteQuery(sb.toString(), argsList.toArray())
    }

    /**
     * Enforces tenant boundary on write operations.
     */
    protected fun validateTenantScope(entity: T) {
        if (entity.samityId != currentSamityId) {
            throw SecurityException(
                "Multi-Tenant Isolation Violation: Attempted to save record for '${entity.samityId}' while session is scoped to '$currentSamityId'."
            )
        }
    }

    /**
     * Checks RBAC permission before executing data repository mutations.
     */
    protected fun checkPermission(permission: AppPermission, targetSamityId: String? = currentSamityId) {
        rbacWrapper?.assertPermission(permission, targetSamityId)
    }
}

/**
 * Concrete Room Repository for Members, inheriting BaseSamityRepository
 * with automatic samity_id query filtering and RBAC checks.
 */
class RoomMemberRepository(
    private val memberDao: MemberDao,
    sessionManager: UserSessionManager,
    rbacWrapper: RbacWrapper? = null
) : BaseSamityRepository<MemberEntity>(sessionManager, rbacWrapper) {

    override val tableName: String = "room_members"

    /**
     * Observes members for the active samity tenant via Flow.
     */
    fun getAllMembersFlow(): Flow<List<MemberEntity>> {
        val query = buildScopedSelect(orderBy = "memberCode ASC")
        return memberDao.queryMembersFlow(query)
    }

    /**
     * Returns a snapshot of members for the active samity tenant.
     */
    fun getMemberList(): List<MemberEntity> {
        val query = buildScopedSelect(orderBy = "memberCode ASC")
        return memberDao.queryMemberList(query)
    }

    /**
     * Fetches a member by ID scoped automatically to the active samity.
     */
    fun getMemberById(id: String): MemberEntity? {
        val query = buildScopedSelect(selection = "id = ?", selectionArgs = arrayOf(id))
        return memberDao.querySingleMember(query)
    }

    /**
     * Inserts a member after verifying CREATE_MEMBER permission and tenant scope.
     */
    suspend fun insertMember(member: MemberEntity) {
        checkPermission(AppPermission.CREATE_MEMBER, member.samityId)
        validateTenantScope(member)
        memberDao.insertMember(member)
    }

    /**
     * Updates a member after verifying UPDATE_MEMBER permission and tenant scope.
     */
    suspend fun updateMember(member: MemberEntity) {
        checkPermission(AppPermission.UPDATE_MEMBER, member.samityId)
        validateTenantScope(member)
        memberDao.updateMember(member)
    }

    /**
     * Deletes a member after verifying DELETE_MEMBER permission and tenant scope.
     */
    suspend fun deleteMember(member: MemberEntity) {
        checkPermission(AppPermission.DELETE_MEMBER, member.samityId)
        validateTenantScope(member)
        memberDao.deleteMember(member)
    }
}

/**
 * Concrete Room Repository for Deposits, inheriting BaseSamityRepository
 * with automatic samity_id query filtering and RBAC checks.
 */
class RoomDepositRepository(
    private val depositDao: DepositDao,
    sessionManager: UserSessionManager,
    rbacWrapper: RbacWrapper? = null
) : BaseSamityRepository<DepositEntity>(sessionManager, rbacWrapper) {

    override val tableName: String = "room_deposits"

    /**
     * Observes deposits for the active samity tenant via Flow.
     */
    fun getAllDepositsFlow(): Flow<List<DepositEntity>> {
        val query = buildScopedSelect(orderBy = "depositDate DESC")
        return depositDao.queryDepositsFlow(query)
    }

    /**
     * Returns a snapshot of deposits for the active samity tenant.
     */
    fun getDepositList(): List<DepositEntity> {
        val query = buildScopedSelect(orderBy = "depositDate DESC")
        return depositDao.queryDepositList(query)
    }

    /**
     * Inserts a deposit after verifying CREATE_DEPOSIT permission and tenant scope.
     */
    suspend fun insertDeposit(deposit: DepositEntity) {
        checkPermission(AppPermission.CREATE_DEPOSIT, deposit.samityId)
        validateTenantScope(deposit)
        depositDao.insertDeposit(deposit)
    }
}
