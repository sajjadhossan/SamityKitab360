package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.SamityGold
import com.example.ui.theme.SamityNavyDark
import com.example.ui.theme.SamityNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamityTopAppBar(
    repository: SamityRepository,
    onOpenSamitySetup: () -> Unit,
    onOpenLogin: () -> Unit,
    onOpenSaaSControl: () -> Unit,
    onOpenRolePermissions: () -> Unit,
    onOpenVercelSync: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showSamityMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }
    var showLiveSyncDialog by remember { mutableStateOf(false) }
    val isBn = LanguageManager.isBangla
    val activeSamity = repository.activeSamity

    TopAppBar(
        modifier = modifier.testTag("samity_top_app_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SamityNavyPrimary,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showSamityMenu = true }
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SamityNavyDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Samity Logo",
                        tint = SamityGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = LanguageManager.appTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SamityGold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isBn) "ERP" else "PRO",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 1.dp)
                    ) {
                        Text(
                            text = if (isBn) activeSamity.nameBn else activeSamity.nameEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Switch Samity",
                            tint = SamityGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Dropdown for multi-tenant Samities
                DropdownMenu(
                    expanded = showSamityMenu,
                    onDismissRequest = { showSamityMenu = false }
                ) {
                    Text(
                        text = if (isBn) "সমিতি নির্বাচন করুন (মাল্টি-টেন্যান্ট)" else "Select Samity (Multi-Tenant)",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Divider()

                    repository.samities.forEach { s ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = if (isBn) s.nameBn else s.nameEn,
                                        fontWeight = if (s.id == repository.activeSamityId) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = "${s.regNo} | ${s.samityType}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                repository.switchSamity(s.id)
                                showSamityMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (s.id == repository.activeSamityId) Icons.Default.CheckCircle else Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = if (s.id == repository.activeSamityId) SamityGold else Color.Gray
                                )
                            }
                        )
                    }

                    Divider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = if (isBn) "+ নতুন সমিতি তৈরি করুন" else "+ Create New Samity",
                                color = SamityNavyPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            showSamityMenu = false
                            onOpenSamitySetup()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AddBusiness, contentDescription = null, tint = SamityNavyPrimary)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = if (isBn) "সুপার অ্যাডমিন কমার্শিয়াল কন্ট্রোল" else "SaaS Feature Control",
                                    color = SamityNavyPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isBn) "গ্রাহকের ফিচার অন/অফ ও লাইসেন্স" else "Provision client features",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            showSamityMenu = false
                            onOpenSaaSControl()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = SamityGold)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = if (isBn) "রোল ও পারমিশন কন্ট্রোল" else "Role Permissions",
                                    color = SamityNavyPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isBn) "কর্মকর্তাদের অনুমতি নির্ধারণ" else "Configure staff permissions",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            showSamityMenu = false
                            onOpenRolePermissions()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SamityNavyPrimary)
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = if (isBn) "ভার্সেল ক্লাউড ও ওয়েব সিঙ্ক হাব" else "Vercel & Web Parity Hub",
                                    color = SamityNavyPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isBn) "ওয়েবসাইট ও মোবাইলের ডাটা একসাথে স্ক্যান" else "Bidirectional web parity",
                                    fontSize = 10.sp,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            showSamityMenu = false
                            onOpenVercelSync()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF10B981))
                        }
                    )
                }
            }
        },
        actions = {
            // Real-Time Multi-Device Sync Indicator
            IconButton(
                onClick = { showLiveSyncDialog = true },
                modifier = Modifier.testTag("open_live_sync_modal_btn")
            ) {
                BadgedBox(
                    badge = {
                        Badge(containerColor = Color(0xFF10B981)) {
                            Text(
                                text = "${repository.syncEngine.connectedDevices.size}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Real-Time Multi-Device Sync",
                        tint = SamityGold,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Login & Account Switcher Icon
            IconButton(
                onClick = onOpenLogin,
                modifier = Modifier.testTag("open_login_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Login & Activation",
                    tint = Color.White
                )
            }
            // User Role Switcher chip
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SamityGold,
                    modifier = Modifier
                        .clickable { showRoleMenu = true }
                        .padding(end = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "User Role",
                            tint = SamityNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBn) repository.currentUserRole.titleBn else repository.currentUserRole.titleEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SamityNavyDark
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = SamityNavyDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Role Dropdown
                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    Text(
                        text = if (isBn) "ইউজার রোল পরিবর্তন করুন" else "Switch User Role",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SamityNavyPrimary
                    )
                    Divider()

                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (isBn) role.titleBn else role.titleEn,
                                    fontWeight = if (role == repository.currentUserRole) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                repository.switchRole(role)
                                showRoleMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (role) {
                                        UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.PRESIDENT -> Icons.Default.Stars
                                        UserRole.GENERAL_SECRETARY -> Icons.Default.EditNote
                                        UserRole.TREASURER -> Icons.Default.AccountBalanceWallet
                                        UserRole.GENERAL_MEMBER -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = if (role == repository.currentUserRole) SamityGold else Color.Gray
                                )
                            }
                        )
                    }
                }
            }

            // Language Switcher Toggle
            IconButton(
                onClick = { LanguageManager.toggle() },
                modifier = Modifier.testTag("language_toggle_btn")
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.18f),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = if (isBn) "EN" else "বাং",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }
    )

    if (showLiveSyncDialog) {
        LiveSyncMonitorDialog(
            repository = repository,
            onDismiss = { showLiveSyncDialog = false }
        )
    }
}
