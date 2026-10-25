package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AppPermission
import com.example.data.auth.UserSession
import com.example.data.model.UserRole
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

/**
 * Screen-level Authorization Wrapper.
 * If the user's role grants [requiredPermission] and matches the tenant scope,
 * [content] is rendered. Otherwise, a clear, beautiful Material 3 "Access Denied" view is shown.
 */
@Composable
fun AuthorizedScreen(
    requiredPermission: AppPermission,
    userSession: UserSession,
    targetSamityId: String? = null,
    onNavigateBack: () -> Unit,
    onSwitchRole: ((UserRole) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isBn = LanguageManager.isBangla
    val hasPerm = userSession.hasPermission(requiredPermission)
    val hasTenantAccess = targetSamityId == null || userSession.canAccessSamity(targetSamityId)

    if (hasPerm && hasTenantAccess) {
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

/**
 * In-line Permission Gate for CRUD buttons, tabs, and actions.
 */
@Composable
fun PermissionGate(
    permission: AppPermission,
    userSession: UserSession,
    targetSamityId: String? = null,
    fallback: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val isAuthorized = userSession.canPerform(permission, targetSamityId)
    if (isAuthorized) {
        content()
    } else {
        fallback?.invoke()
    }
}

/**
 * RBAC Component Wrapper that checks user permissions before executing UI components.
 */
@Composable
fun RbacGuard(
    permission: AppPermission,
    rbacWrapper: com.example.data.auth.RbacWrapper,
    targetSamityId: String? = null,
    fallback: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    rbacWrapper.Guard(
        permission = permission,
        targetSamityId = targetSamityId,
        fallback = fallback,
        content = content
    )
}

/**
 * UI View rendered when a user attempts to access an unauthorized screen or tenant.
 */
@Composable
fun AccessDeniedView(
    requiredPermission: AppPermission,
    userSession: UserSession,
    targetSamityId: String? = null,
    onNavigateBack: () -> Unit,
    onSwitchRole: ((UserRole) -> Unit)? = null
) {
    val isBn = LanguageManager.isBangla
    var showRolePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(24.dp)
            .testTag("access_denied_view"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Shield / Lock Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(SamityRed.copy(alpha = 0.12f))
                        .border(2.dp, SamityRed.copy(alpha = 0.35f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LockPerson,
                        contentDescription = "Access Restricted",
                        tint = SamityRed,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = if (isBn) "প্রবেশাধিকার সংরক্ষিত / অনুমতি সীমাবদ্ধ" else "Access Restricted",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (isBn)
                        "আপনার বর্তমান ইউজার রোল বা পদবীর জন্য এই মডিউল বা অ্যাকশনটির অনুমতি নেই।"
                    else
                        "Your current user role does not have permission to view or manage this module.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Permission info card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF8FAFC),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = SamityNavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${if (isBn) "বর্তমান ব্যবহারকারী: " else "Current User: "}${userSession.currentUser.name}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SamityNavyDark
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = SamityGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${if (isBn) "বর্তমান রোল: " else "Active Role: "}${if (isBn) userSession.currentUserRole.titleBn else userSession.currentUserRole.titleEn}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = SamityRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${if (isBn) "প্রয়োজনীয় অনুমতি: " else "Required Permission: "}${if (isBn) requiredPermission.titleBn else requiredPermission.titleEn}",
                                fontSize = 12.sp,
                                color = SamityRed,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (targetSamityId != null && !userSession.canAccessSamity(targetSamityId)) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DomainVerification,
                                    contentDescription = null,
                                    tint = SamityRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBn)
                                        "টেন্যান্ট সীমা: আপনি কেবল '${userSession.assignedSamityId}' সমিতিতে সীমাবদ্ধ।"
                                    else
                                        "Tenant Scope: Restricted to assigned samity '${userSession.assignedSamityId}'.",
                                    fontSize = 11.sp,
                                    color = SamityRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBn) "হোমে ফিরুন" else "Go Back",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (onSwitchRole != null) {
                        Button(
                            onClick = { showRolePicker = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwitchAccount,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBn) "রোল পরিবর্তন" else "Switch Role",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Role switcher dialog
                if (showRolePicker && onSwitchRole != null) {
                    AlertDialog(
                        onDismissRequest = { showRolePicker = false },
                        title = {
                            Text(
                                text = if (isBn) "সিস্টেম রোল নির্বাচন করুন" else "Switch System Role",
                                fontWeight = FontWeight.Bold,
                                color = SamityNavyPrimary
                            )
                        },
                        text = {
                            Column {
                                UserRole.values().forEach { role ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSwitchRole(role)
                                                showRolePicker = false
                                            }
                                            .padding(vertical = 10.dp, horizontal = 4.dp)
                                    ) {
                                        RadioButton(
                                            selected = role == userSession.currentUserRole,
                                            onClick = {
                                                onSwitchRole(role)
                                                showRolePicker = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isBn) role.titleBn else role.titleEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (role == UserRole.GENERAL_MEMBER)
                                                    (if (isBn) "শুধুমাত্র নিজের প্রোফাইল ও রিসিট দর্শন" else "View own profile and receipts only")
                                                else
                                                    (if (isBn) "প্রশাসনিক ও আর্থিক সুবিধা অন্তর্ভুক্ত" else "Includes admin & financial features"),
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showRolePicker = false }) {
                                Text(if (isBn) "বন্ধ করুন" else "Close")
                            }
                        }
                    )
                }
            }
        }
    }
}
