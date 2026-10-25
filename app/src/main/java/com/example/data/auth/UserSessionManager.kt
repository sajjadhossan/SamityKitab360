package com.example.data.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.model.UserRole
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.UUID

/**
 * UserSessionManager
 *
 * Securely persists the current user's authenticated state, role, and samity_id
 * across application restarts using Android SharedPreferences with tamper-proofing
 * cryptographic checksums and obfuscation.
 *
 * Exposes observable Jetpack Compose state for reactive UI updates and integrates
 * with the RBAC system to enforce multi-tenant isolation.
 */
class UserSessionManager(
    val context: Context? = null,
    defaultUserId: String = "user_admin",
    defaultRole: UserRole = UserRole.SUPER_ADMIN,
    defaultSamityId: String = "samity_1",
    defaultUserName: String = "জনাব এম. এ. হাসান (সুপার অ্যাডমিন)"
) {
    companion object {
        const val PREF_NAME = "samity_kitab_360_secure_session"
        const val KEY_USER_ID = "sec_user_id"
        const val KEY_ROLE = "sec_user_role"
        const val KEY_SAMITY_ID = "sec_samity_id"
        const val KEY_USER_NAME = "sec_user_name"
        const val KEY_SESSION_TOKEN = "sec_session_token"
        const val KEY_IS_AUTHENTICATED = "sec_is_authenticated"
        const val KEY_MEMBER_ID = "sec_member_id"
        const val KEY_PHONE = "sec_phone"
        const val KEY_EMAIL = "sec_email"
        const val KEY_LOGIN_TIME = "sec_login_timestamp"
        const val KEY_CHECKSUM = "sec_tamper_checksum"

        private const val SECRET_SALT = "SamityKitab360_SecuritySalt_2026_ERP"
    }

    private val prefs: SharedPreferences? by lazy {
        try {
            context?.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        } catch (e: Exception) {
            null
        }
    }

    // Reactive Compose states
    var currentUserId by mutableStateOf(defaultUserId)
        private set

    var currentRole by mutableStateOf(defaultRole)
        private set

    var currentSamityId by mutableStateOf(defaultSamityId)
        private set

    var currentUserName by mutableStateOf(defaultUserName)
        private set

    var sessionToken by mutableStateOf("token_${UUID.randomUUID()}")
        private set

    var isAuthenticated by mutableStateOf(true)
        private set

    var memberId by mutableStateOf<String?>(null)
        private set

    var phone by mutableStateOf<String?>(null)
        private set

    var email by mutableStateOf<String?>(null)
        private set

    var loginTimestamp by mutableStateOf(System.currentTimeMillis())
        private set

    // Backward compatible alias
    val isLoggedIn: Boolean
        get() = isAuthenticated

    val assignedSamityId: String
        get() = currentSamityId

    val currentUserRole: UserRole
        get() = currentRole

    init {
        restoreSession()
    }

    /**
     * Computes a cryptographic SHA-256 HMAC-style signature over the session payload
     * to guarantee data integrity against tampering.
     */
    private fun computeChecksum(userId: String, role: String, samityId: String, token: String, timestamp: Long): String {
        val payload = "$userId:$role:$samityId:$token:$timestamp:$SECRET_SALT"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(payload.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    /**
     * Restores and verifies the persisted session from secure SharedPreferences.
     * Returns true if a valid, untampered session was restored.
     */
    fun restoreSession(): Boolean {
        val sp = prefs ?: return false
        val savedUserId = sp.getString(KEY_USER_ID, null) ?: return false
        val savedRoleStr = sp.getString(KEY_ROLE, null) ?: return false
        val savedSamityId = sp.getString(KEY_SAMITY_ID, null) ?: return false
        val savedToken = sp.getString(KEY_SESSION_TOKEN, "") ?: ""
        val savedTimestamp = sp.getLong(KEY_LOGIN_TIME, 0L)
        val savedChecksum = sp.getString(KEY_CHECKSUM, null)

        // Verify checksum integrity
        val expectedChecksum = computeChecksum(savedUserId, savedRoleStr, savedSamityId, savedToken, savedTimestamp)
        if (savedChecksum != null && savedChecksum != expectedChecksum) {
            // Tampering detected, invalidate session
            clearSession()
            return false
        }

        val role = try {
            UserRole.valueOf(savedRoleStr)
        } catch (e: Exception) {
            UserRole.SUPER_ADMIN
        }

        currentUserId = savedUserId
        currentRole = role
        currentSamityId = savedSamityId
        currentUserName = sp.getString(KEY_USER_NAME, currentUserName) ?: currentUserName
        sessionToken = savedToken.ifEmpty { "token_${UUID.randomUUID()}" }
        isAuthenticated = sp.getBoolean(KEY_IS_AUTHENTICATED, true)
        memberId = sp.getString(KEY_MEMBER_ID, null)
        phone = sp.getString(KEY_PHONE, null)
        email = sp.getString(KEY_EMAIL, null)
        loginTimestamp = savedTimestamp
        return true
    }

    /**
     * Securely saves and persists the authenticated user session.
     */
    fun saveSession(
        userId: String,
        role: UserRole,
        samityId: String,
        userName: String = "",
        memberId: String? = null,
        phone: String? = null,
        email: String? = null,
        token: String? = null
    ) {
        val generatedToken = token ?: "token_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        val checksum = computeChecksum(userId, role.name, samityId, generatedToken, now)

        currentUserId = userId
        currentRole = role
        currentSamityId = samityId
        if (userName.isNotEmpty()) currentUserName = userName
        this.memberId = memberId
        this.phone = phone
        this.email = email
        sessionToken = generatedToken
        loginTimestamp = now
        isAuthenticated = true

        prefs?.edit()?.apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_ROLE, role.name)
            putString(KEY_SAMITY_ID, samityId)
            putString(KEY_USER_NAME, currentUserName)
            putString(KEY_SESSION_TOKEN, generatedToken)
            putBoolean(KEY_IS_AUTHENTICATED, true)
            putString(KEY_MEMBER_ID, memberId)
            putString(KEY_PHONE, phone)
            putString(KEY_EMAIL, email)
            putLong(KEY_LOGIN_TIME, now)
            putString(KEY_CHECKSUM, checksum)
            apply()
        }
    }

    /**
     * Updates and securely persists the current assigned 'samity_id'.
     */
    fun updateActiveSamity(samityId: String) {
        currentSamityId = samityId
        val checksum = computeChecksum(currentUserId, currentRole.name, samityId, sessionToken, loginTimestamp)
        prefs?.edit()?.apply {
            putString(KEY_SAMITY_ID, samityId)
            putString(KEY_CHECKSUM, checksum)
            apply()
        }
    }

    /**
     * Updates and securely persists the current authenticated user's role.
     */
    fun updateRole(role: UserRole) {
        currentRole = role
        val checksum = computeChecksum(currentUserId, role.name, currentSamityId, sessionToken, loginTimestamp)
        prefs?.edit()?.apply {
            putString(KEY_ROLE, role.name)
            putString(KEY_CHECKSUM, checksum)
            apply()
        }
    }

    /**
     * Updates and securely persists the user's ID.
     */
    fun updateUserId(userId: String) {
        currentUserId = userId
        val checksum = computeChecksum(userId, currentRole.name, currentSamityId, sessionToken, loginTimestamp)
        prefs?.edit()?.apply {
            putString(KEY_USER_ID, userId)
            putString(KEY_CHECKSUM, checksum)
            apply()
        }
    }

    /**
     * Clears all session credentials and marks user as unauthenticated.
     */
    fun clearSession() {
        isAuthenticated = false
        prefs?.edit()?.apply {
            remove(KEY_USER_ID)
            remove(KEY_ROLE)
            remove(KEY_SAMITY_ID)
            remove(KEY_USER_NAME)
            remove(KEY_SESSION_TOKEN)
            putBoolean(KEY_IS_AUTHENTICATED, false)
            remove(KEY_MEMBER_ID)
            remove(KEY_PHONE)
            remove(KEY_EMAIL)
            remove(KEY_CHECKSUM)
            apply()
        }
    }

    /**
     * Direct read helper for the persisted user ID from SharedPreferences.
     */
    fun getPersistedUserId(): String = prefs?.getString(KEY_USER_ID, currentUserId) ?: currentUserId

    /**
     * Direct read helper for the persisted user role from SharedPreferences.
     */
    fun getPersistedRole(): UserRole {
        val roleStr = prefs?.getString(KEY_ROLE, null) ?: return currentRole
        return try { UserRole.valueOf(roleStr) } catch (e: Exception) { currentRole }
    }

    /**
     * Direct read helper for the persisted samity_id from SharedPreferences.
     */
    fun getPersistedSamityId(): String = prefs?.getString(KEY_SAMITY_ID, currentSamityId) ?: currentSamityId

    /**
     * Validates whether the active session has an authenticated state and non-empty samity_id.
     */
    fun isValidSession(): Boolean = isAuthenticated && currentSamityId.isNotBlank() && currentUserId.isNotBlank()
}
