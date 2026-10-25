package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Member
import com.example.data.model.UserRole
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*

/**
 * Commercial Software Login & Activation Center.
 * Handles:
 * 1. Individual Member Login for all 100+ members (Member Code / Phone + PIN).
 * 2. Samity Staff & Super Admin Login (Role + Password).
 * 3. Member First-Time Account Activation (Set PIN).
 * 4. Commercial License Key Activation for purchasing Samities.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginAndActivationScreen(
    repository: SamityRepository,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBn = LanguageManager.isBangla
    var selectedTab by remember { mutableStateOf(0) } // 0: Member Login, 1: Staff Login, 2: Member Activation, 3: License Key
    val activeSamity = repository.activeSamity
    val license = repository.activeLicense

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App & Samity Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SamityNavyPrimary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SamityGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = SamityNavyDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = LanguageManager.appTitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) activeSamity.nameBn else activeSamity.nameEn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFD0E4FF)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // License info pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = SamityGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${license.planName} (${repository.activeMembers.size}/${license.maxMembersLimit} সদস্য)",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Navigation Tabs for Login & Activation
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = SamityNavyPrimary,
            edgePadding = 8.dp,
            modifier = Modifier
                .widthIn(max = 600.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isBn) "সদস্য লগইন" else "Member Login", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (isBn) "কর্মকর্তা লগইন" else "Staff Login", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (isBn) "সদস্য সক্রিয়করণ" else "Activate Member", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text(if (isBn) "লাইসেন্স কি" else "License Key", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            when (selectedTab) {
                0 -> MemberLoginTab(repository = repository, onLoginSuccess = onLoginSuccess)
                1 -> StaffLoginTab(repository = repository, onLoginSuccess = onLoginSuccess)
                2 -> MemberActivationTab(repository = repository) { selectedTab = 0 }
                3 -> LicenseActivationTab(repository = repository)
            }
        }
    }
}

/**
 * Tab 1: Member Login (Member Code / Phone + PIN)
 * Allows any of the 100 members to securely log in.
 */
@Composable
fun MemberLoginTab(
    repository: SamityRepository,
    onLoginSuccess: () -> Unit
) {
    val isBn = LanguageManager.isBangla
    var memberCodeOrPhone by remember { mutableStateOf("SM-0001") }
    var pin by remember { mutableStateOf("1234") }
    var showPin by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val members = repository.activeMembers

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = if (isBn) "সদস্য নিজস্ব পোর্টাল লগইন" else "Member Portal Login",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn)
                "সমিতির সকল ১০০ সদস্য তাদের সদস্য কোড (SM-XXXX) অথবা মোবাইল নম্বর এবং ৪-সংখ্যার পিন দিয়ে নিজ প্রোফাইলে ঢুকতে পারবেন।"
            else
                "All members can log in using their Member Code (SM-XXXX) or Phone + 4-digit PIN.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        OutlinedTextField(
            value = memberCodeOrPhone,
            onValueChange = {
                memberCodeOrPhone = it
                errorMessage = null
            },
            label = { Text(if (isBn) "সদস্য কোড অথবা মোবাইল নম্বর" else "Member Code or Phone") },
            placeholder = { Text("SM-0001 / 01711-224466") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SamityNavyPrimary) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("member_login_id_field")
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = {
                pin = it
                errorMessage = null
            },
            label = { Text(if (isBn) "গোপন পিন কোড (ডিফল্ট: 1234)" else "Secret PIN (Default: 1234)") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SamityNavyPrimary) },
            trailingIcon = {
                IconButton(onClick = { showPin = !showPin }) {
                    Icon(
                        imageVector = if (showPin) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            visualTransformation = if (showPin) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("member_login_pin_field")
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = SamityRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                val member = repository.authenticateMember(memberCodeOrPhone, pin)
                if (member != null) {
                    repository.userSession.loginAsMember(member)
                    repository.currentUserRole = UserRole.GENERAL_MEMBER
                    onLoginSuccess()
                } else {
                    errorMessage = if (isBn)
                        "ভুল সদস্য কোড বা পিন! অনুগ্রহ করে সঠিক তথ্য দিন বা 'সদস্য সক্রিয়করণ' ট্যাব থেকে পিন সেট করুন।"
                    else
                        "Invalid Member Code or PIN! Please verify or activate your account."
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("member_login_submit_btn")
        ) {
            Icon(Icons.Default.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isBn) "সদস্য পোর্টালে প্রবেশ করুন" else "Log In to Member Portal", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Divider()
        Spacer(modifier = Modifier.height(12.dp))

        // 1-Click Fast Member Access Switcher
        Text(
            text = if (isBn) "সরাসরি সদস্য অ্যাকাউন্ট নির্বাচন (প্রবেশ করুন):" else "Quick Member Account Access:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(members.take(15)) { m ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    border = CardDefaults.outlinedCardBorder(),
                    modifier = Modifier.clickable {
                        memberCodeOrPhone = m.memberCode
                        pin = "1234"
                    }
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Text(text = m.memberCode, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SamityNavyPrimary)
                        Text(text = m.nameBn, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
                    }
                }
            }
        }
    }
}

/**
 * Tab 2: Staff / Admin Login
 */
@Composable
fun StaffLoginTab(
    repository: SamityRepository,
    onLoginSuccess: () -> Unit
) {
    val isBn = LanguageManager.isBangla
    var selectedRole by remember { mutableStateOf(UserRole.SUPER_ADMIN) }
    var password by remember { mutableStateOf("admin123") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = if (isBn) "সমিতি কর্মকর্তা ও অ্যাডমিন লগইন" else "Staff & Admin Login",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn)
                "সমিতির সভাপতি, সাধারণ সম্পাদক, কোষাধ্যক্ষ অথবা সফটওয়্যার সুপার অ্যাডমিন হিসেবে প্রবেশ করুন।"
            else
                "Log in as President, General Secretary, Treasurer, or Super Admin.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Text(
            text = if (isBn) "পদবী / রোল নির্বাচন করুন:" else "Select Role:",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SamityNavyDark
        )

        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            UserRole.SUPER_ADMIN,
            UserRole.PRESIDENT,
            UserRole.GENERAL_SECRETARY,
            UserRole.TREASURER
        ).forEach { role ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedRole = role }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedRole == role,
                    onClick = { selectedRole = role }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isBn) role.titleBn else role.titleEn,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SamityNavyPrimary
                    )
                    Text(
                        text = when (role) {
                            UserRole.SUPER_ADMIN -> if (isBn) "সফটওয়্যারের সকল নিয়ন্ত্রণ ও লাইসেন্সিং" else "Complete system control & client licensing"
                            UserRole.PRESIDENT -> if (isBn) "সমিতির সার্বিক অনুমোদন ও সিদ্ধান্ত" else "Overall organizational approval"
                            UserRole.GENERAL_SECRETARY -> if (isBn) "সদস্য ব্যবস্থাপনা, নোটিশ ও সাধারণ প্রশাসন" else "Member management & communication"
                            UserRole.TREASURER -> if (isBn) "ব্যাংক জমা, চাঁদা আদায় ও তহবিল হিসাব" else "Bank deposits & treasury management"
                            else -> ""
                        },
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (isBn) "অ্যাডমিন পাসওয়ার্ড" else "Admin Password") },
            leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = SamityNavyPrimary) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_login_password_field")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                repository.switchRole(selectedRole)
                onLoginSuccess()
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_login_submit_btn")
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${if (isBn) "লগইন করুন: " else "Log In as: "}${if (isBn) selectedRole.titleBn else selectedRole.titleEn}",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Tab 3: Member First-Time Account Activation
 */
@Composable
fun MemberActivationTab(
    repository: SamityRepository,
    onActivationSuccess: () -> Unit
) {
    val isBn = LanguageManager.isBangla
    var memberCode by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = if (isBn) "নতুন সদস্য অ্যাকাউন্ট সক্রিয়করণ" else "First-Time Member Activation",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )
        Text(
            text = if (isBn)
                "সমিতিতে নতুন সদস্য অন্তর্ভুক্তির পর সদস্য কোড ও ফোন নম্বর দিয়ে নিজের ৪-সংখ্যার গোপন পিন সেট করুন।"
            else
                "Activate your account with your Member Code and registered Phone, then set your 4-digit PIN.",
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
        )

        OutlinedTextField(
            value = memberCode,
            onValueChange = { memberCode = it },
            label = { Text(if (isBn) "সদস্য কোড (যেমন: SM-0025)" else "Member Code (e.g. SM-0025)") },
            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(if (isBn) "নিবন্ধিত মোবাইল নম্বর" else "Registered Phone Number") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = newPin,
            onValueChange = { newPin = it },
            label = { Text(if (isBn) "নতুন ৪-সংখ্যার পিন" else "New 4-digit PIN") },
            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPin,
            onValueChange = { confirmPin = it },
            label = { Text(if (isBn) "পুনরায় পিন নিশ্চিত করুন" else "Confirm PIN") },
            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (message != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message!!,
                color = if (isSuccess) SamityEmerald else SamityRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (newPin.length < 4 || newPin != confirmPin) {
                    message = if (isBn) "পিন অবশ্যই ৪ সংখ্যার হতে হবে এবং উভয় পিন মিলতে হবে!" else "PIN must be at least 4 digits and match!"
                    isSuccess = false
                    return@Button
                }
                val activated = repository.activateMemberAccount(memberCode, phone, newPin)
                if (activated) {
                    message = if (isBn) "অভিনন্দন! আপনার অ্যাকাউন্ট সফলভাবে সক্রিয় হয়েছে। এখন লগইন করুন।" else "Success! Your account is activated. You can now log in."
                    isSuccess = true
                } else {
                    message = if (isBn) "সদস্য কোড অথবা মোবাইল নম্বর মেলেনি! সঠিক তথ্য দিন।" else "Member Code or Phone does not match samity directory."
                    isSuccess = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamityEmerald),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isBn) "অ্যাকাউন্ট সক্রিয় করুন" else "Activate Account", fontWeight = FontWeight.Bold)
        }
    }
}

/**
 * Tab 4: Commercial License Activation for Client Samity
 */
@Composable
fun LicenseActivationTab(
    repository: SamityRepository
) {
    val isBn = LanguageManager.isBangla
    val license = repository.activeLicense
    var enteredKey by remember { mutableStateOf(license.licenseKey) }
    var activationStatus by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = if (isBn) "সফটওয়্যার বাণিজ্যিক লাইসেন্স কি" else "Software License & Commercial Pack",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SamityNavyPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8FAFC),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "${if (isBn) "বর্তমান প্যাকেজ: " else "Active Plan: "}${license.planName}",
                    fontWeight = FontWeight.Bold,
                    color = SamityNavyPrimary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (isBn) "অনুমোদিত সর্বোচ্চ সদস্য: " else "Max Member Limit: "}${license.maxMembersLimit} জন",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (isBn) "মেয়াদ উত্তীর্ণের তারিখ: " else "Valid Until: "}${license.validUntil}",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = enteredKey,
            onValueChange = { enteredKey = it },
            label = { Text(if (isBn) "বাণিজ্যিক লাইসেন্স কোড লিখুন" else "Enter License Key") },
            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = SamityGold) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (activationStatus != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = activationStatus!!, color = SamityEmerald, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                val updated = license.copy(
                    licenseKey = enteredKey,
                    isActive = true,
                    planName = "Commercial 100-Member Unlimited Pack",
                    maxMembersLimit = 100
                )
                repository.updateLicenseConfig(updated)
                activationStatus = if (isBn) "লাইসেন্স সফলভাবে সক্রিয় হয়েছে!" else "License successfully verified and activated!"
            },
            colors = ButtonDefaults.buttonColors(containerColor = SamityGold),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = SamityNavyDark)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isBn) "লাইসেন্স যাচাই ও সক্রিয় করুন" else "Verify & Activate License", color = SamityNavyDark, fontWeight = FontWeight.Bold)
        }
    }
}
