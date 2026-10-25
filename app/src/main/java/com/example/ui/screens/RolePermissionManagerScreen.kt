package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AppPermission
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

/**
 * Samity Admin Role & Permission Management Screen.
 * Enables the Samity Admin to configure granular permissions for each internal role
 * (President, General Secretary, Treasurer, General Member).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RolePermissionManagerScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    val session = repository.userSession
    var selectedRole by remember { mutableStateOf(UserRole.TREASURER) }
    var currentRolePerms by remember(selectedRole) {
        mutableStateOf(session.getPermissionsForRole(selectedRole).toMutableSet())
    }
    var successToast by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "রোল ও পারমিশন কন্ট্রোল প্যানেল" else "Role & Permission Control",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "সমিতি অ্যাডমিন কর্তৃক কর্মকর্তাদের অনুমতি নির্ধারণ" else "Configure permissions per internal role",
                            fontSize = 11.sp,
                            color = SamityGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamityNavyPrimary)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Role Selector Card
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBn) "অনুমতি নির্ধারণের জন্য রোল নির্বাচন করুন:" else "Select Role to Configure Permissions:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SamityNavyPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        ScrollableTabRow(
                            selectedTabIndex = listOf(
                                UserRole.PRESIDENT,
                                UserRole.GENERAL_SECRETARY,
                                UserRole.TREASURER,
                                UserRole.GENERAL_MEMBER
                            ).indexOf(selectedRole).coerceAtLeast(0),
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = SamityNavyPrimary,
                            edgePadding = 6.dp,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                        ) {
                            listOf(
                                UserRole.PRESIDENT,
                                UserRole.GENERAL_SECRETARY,
                                UserRole.TREASURER,
                                UserRole.GENERAL_MEMBER
                            ).forEach { role ->
                                Tab(
                                    selected = selectedRole == role,
                                    onClick = {
                                        selectedRole = role
                                        currentRolePerms = session.getPermissionsForRole(role).toMutableSet()
                                        successToast = null
                                    },
                                    text = {
                                        Text(
                                            text = if (isBn) role.titleBn else role.titleEn,
                                            fontWeight = if (selectedRole == role) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Permissions Checklist
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBn) selectedRole.titleBn else selectedRole.titleEn} - এর জন্য অনুমতিসমূহ:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = SamityNavyPrimary
                            )
                            Text(
                                text = "${currentRolePerms.size} / ${AppPermission.values().size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SamityGold
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Category: Financial & CRUD Operations
                        Text(
                            text = if (isBn) "আর্থিক ও ক্রুড (CRUD) অপারেশনের অনুমতি:" else "CRUD & Financial Operations:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityNavyDark,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        listOf(
                            AppPermission.CREATE_DEPOSIT,
                            AppPermission.UPDATE_DEPOSIT,
                            AppPermission.CREATE_BULK_DEPOSIT,
                            AppPermission.CREATE_EXPENSE,
                            AppPermission.UPDATE_EXPENSE,
                            AppPermission.APPROVE_TRANSACTION,
                            AppPermission.CREATE_MEMBER,
                            AppPermission.UPDATE_MEMBER,
                            AppPermission.SEND_REMINDER,
                            AppPermission.MANAGE_SAMITY_CONFIG,
                            AppPermission.MANAGE_LETTERHEAD
                        ).forEach { perm ->
                            PermissionCheckboxItem(
                                permission = perm,
                                isChecked = currentRolePerms.contains(perm),
                                isBn = isBn,
                                onToggle = {
                                    if (currentRolePerms.contains(perm)) {
                                        currentRolePerms.remove(perm)
                                    } else {
                                        currentRolePerms.add(perm)
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Category: Screen Access
                        Text(
                            text = if (isBn) "স্ক্রিন ও মডিউল দর্শনের অনুমতি:" else "Screen & Module View Access:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityNavyDark,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        listOf(
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
                            AppPermission.VIEW_MEMBER_PORTAL
                        ).forEach { perm ->
                            PermissionCheckboxItem(
                                permission = perm,
                                isChecked = currentRolePerms.contains(perm),
                                isBn = isBn,
                                onToggle = {
                                    if (currentRolePerms.contains(perm)) {
                                        currentRolePerms.remove(perm)
                                    } else {
                                        currentRolePerms.add(perm)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (successToast != null) {
                item {
                    Text(
                        text = successToast!!,
                        color = SamityEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        session.updateRolePermissions(selectedRole, currentRolePerms.toSet())
                        successToast = if (isBn)
                            "সফল! '${selectedRole.titleBn}' রোলের অনুমতি সফলভাবে সংরক্ষণ করা হয়েছে।"
                        else
                            "Success! Permissions updated for ${selectedRole.titleEn}."
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_role_permissions_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBn) "অনুমতি সংরক্ষণ করুন" else "Save Role Permissions",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionCheckboxItem(
    permission: AppPermission,
    isChecked: Boolean,
    isBn: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = SamityNavyPrimary)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = if (isBn) permission.titleBn else permission.titleEn,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isChecked) SamityNavyDark else Color.Gray
            )
            Text(
                text = permission.name,
                fontSize = 10.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }
}
