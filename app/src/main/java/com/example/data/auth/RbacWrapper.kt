package com.example.data.auth

import androidx.compose.runtime.Composable
import com.example.data.model.UserRole
import com.example.ui.components.AccessDeniedView

/**
 * Role-Based Access Control (RBAC) Wrapper.
 *
 * Checks user permissions and tenant scope ('samity_id') before executing
 * UI components or data repository actions.
 */
class RbacWrapper(
    val userSession: UserSession
) {
    /**
     * Checks if the authenticated user has the requested permission for the specified samity_id.
     */
    fun hasPermission(permission: AppPermission, targetSamityId: String? = null): Boolean {
        return userSession.canPerform(permission, targetSamityId)
    }

    /**
     * Asserts that the authenticated user possesses the required permission.
     * Throws [SecurityException] if permission or tenant check fails.
     */
    fun assertPermission(permission: AppPermission, targetSamityId: String? = null) {
        if (!userSession.isAuthenticated) {
            throw SecurityException("RBAC Unauthorized: No active authenticated session.")
        }

        if (!userSession.hasPermission(permission)) {
            val roleName = userSession.currentUserRole.name
            val userName = userSession.currentUser.name
            throw SecurityException(
                "RBAC Access Denied: User '$userName' ($roleName) lacks required permission '${permission.name}' (${permission.titleEn})."
            )
        }

        if (targetSamityId != null && !userSession.canAccessSamity(targetSamityId)) {
            val assignedTenant = userSession.assignedSamityId
            throw SecurityException(
                "RBAC Multi-Tenant Breach: User is assigned to samity '$assignedTenant' and cannot access '$targetSamityId'."
            )
        }
    }

    /**
     * Executes a data repository action only if the authenticated user has the required permission.
     * If unauthorized, invokes [onUnauthorized] and returns its result without executing [action].
     */
    inline fun <T> executeAction(
        permission: AppPermission,
        targetSamityId: String? = null,
        onUnauthorized: (errorMessage: String) -> T? = { null },
        action: () -> T
    ): T? {
        return try {
            assertPermission(permission, targetSamityId)
            action()
        } catch (e: SecurityException) {
            onUnauthorized(e.message ?: "Access Denied")
        }
    }

    /**
     * Executes a data repository action or throws [SecurityException] if unauthorized.
     */
    inline fun <T> executeOrThrow(
        permission: AppPermission,
        targetSamityId: String? = null,
        action: () -> T
    ): T {
        assertPermission(permission, targetSamityId)
        return action()
    }

    /**
     * UI Component Guard:
     * Renders [content] only when the active user role possesses [permission] for [targetSamityId].
     * Otherwise renders [fallback] or leaves empty space.
     */
    @Composable
    fun Guard(
        permission: AppPermission,
        targetSamityId: String? = null,
        fallback: (@Composable () -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        if (hasPermission(permission, targetSamityId)) {
            content()
        } else {
            fallback?.invoke()
        }
    }

    /**
     * Full-screen RBAC Guard:
     * Validates permission before rendering a screen, displaying [AccessDeniedView] if unauthorized.
     */
    @Composable
    fun GuardScreen(
        requiredPermission: AppPermission,
        targetSamityId: String? = null,
        onNavigateBack: () -> Unit,
        onSwitchRole: ((UserRole) -> Unit)? = null,
        content: @Composable () -> Unit
    ) {
        if (hasPermission(requiredPermission, targetSamityId)) {
            content()
        } else {
            AccessDeniedView(
                requiredPermission = requiredPermission,
                userSession = userSession,
                targetSamityId = targetSamityId,
                onNavigateBack = onNavigateBack,
                onSwitchRole = onSwitchRole
            )
        }
    }
}
