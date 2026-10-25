package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Samity
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamitySetupScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isBn = LanguageManager.isBangla
    val currentSamity = repository.activeSamity

    var isCreatingNew by remember { mutableStateOf(false) }

    // Core Identity Fields
    var samityId by remember { mutableStateOf(currentSamity.id) }
    var nameBn by remember { mutableStateOf(currentSamity.nameBn) }
    var nameEn by remember { mutableStateOf(currentSamity.nameEn) }
    var samityType by remember { mutableStateOf(currentSamity.samityType) }
    var regNo by remember { mutableStateOf(currentSamity.regNo) }
    var establishedDate by remember { mutableStateOf(currentSamity.establishedDate) }
    var sloganBn by remember { mutableStateOf(currentSamity.sloganBn) }
    var sloganEn by remember { mutableStateOf(currentSamity.sloganEn) }
    var logoUri by remember { mutableStateOf(currentSamity.logoUri) }

    // Address & Geographic Location
    var addressBn by remember { mutableStateOf(currentSamity.addressBn) }
    var addressEn by remember { mutableStateOf(currentSamity.addressEn) }
    var division by remember { mutableStateOf(currentSamity.division) }
    var district by remember { mutableStateOf(currentSamity.district) }
    var upazila by remember { mutableStateOf(currentSamity.upazila) }
    var postOffice by remember { mutableStateOf(currentSamity.postOffice) }
    var villageOrArea by remember { mutableStateOf(currentSamity.villageOrArea) }

    // Contact Information
    var phone by remember { mutableStateOf(currentSamity.phone) }
    var mobileNumber by remember { mutableStateOf(currentSamity.mobileNumber) }
    var whatsapp by remember { mutableStateOf(currentSamity.whatsappNumber) }
    var email by remember { mutableStateOf(currentSamity.email) }
    var website by remember { mutableStateOf(currentSamity.website) }
    var facebookPageUrl by remember { mutableStateOf(currentSamity.facebookPageUrl) }
    var youtubeChannelUrl by remember { mutableStateOf(currentSamity.youtubeChannelUrl) }

    // Banking Details
    var bankAccountName by remember { mutableStateOf(currentSamity.bankAccountName) }
    var bankName by remember { mutableStateOf(currentSamity.bankName) }
    var bankBranch by remember { mutableStateOf(currentSamity.bankBranch) }
    var bankAccountNo by remember { mutableStateOf(currentSamity.bankAccountNo) }
    var routingNo by remember { mutableStateOf(currentSamity.routingNo) }

    // Mobile Banking
    var bkash by remember { mutableStateOf(currentSamity.bkashNumber) }
    var nagad by remember { mutableStateOf(currentSamity.nagadNumber) }
    var rocket by remember { mutableStateOf(currentSamity.rocketNumber) }
    var upay by remember { mutableStateOf(currentSamity.upayNumber) }
    var mfsAccountType by remember { mutableStateOf(currentSamity.mobileBankingAccountType) } // Personal, Merchant
    var currency by remember { mutableStateOf(currentSamity.currency) }

    // Subscription & Fees
    var defaultFeeStr by remember { mutableStateOf(currentSamity.defaultMonthlyFee.toInt().toString()) }
    var dueDayStr by remember { mutableStateOf(currentSamity.dueDayOfMonth.toString()) }
    var gracePeriodStr by remember { mutableStateOf(currentSamity.gracePeriodDays.toString()) }
    var lateFeeStr by remember { mutableStateOf(currentSamity.lateFeeAmount.toInt().toString()) }
    var fiscalYearStart by remember { mutableStateOf(currentSamity.fiscalYearStartDate) }
    var fiscalYearEnd by remember { mutableStateOf(currentSamity.fiscalYearEndDate) }

    // Executive Officials
    var presidentName by remember { mutableStateOf(currentSamity.presidentName) }
    var secretaryName by remember { mutableStateOf(currentSamity.secretaryName) }
    var treasurerName by remember { mutableStateOf(currentSamity.treasurerName) }
    var treasurerPhone by remember { mutableStateOf(currentSamity.treasurerPhone) }

    // Status
    var samityStatus by remember { mutableStateOf(currentSamity.status) } // Active, Inactive, Archived

    // Photo picker launcher for Samity Logo
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            logoUri = uri.toString()
            Toast.makeText(context, if (isBn) "লোগো নির্বাচন সম্পন্ন" else "Logo selected", Toast.LENGTH_SHORT).show()
        }
    }

    val samityTypes = listOf(
        "General Samity" to "সাধারণ সমবায় সমিতি",
        "Club" to "ক্লাব ও যুব সংগঠন",
        "Social Organization" to "সামাজিক সংগঠন",
        "Savings Samity" to "সঞ্চয় ও ঋণদান সমিতি",
        "Business Samity" to "ব্যবসায়ী সমিতি",
        "Religious Committee" to "মসজিদ/ধর্মীয় কমিটি",
        "School Committee" to "বিদ্যালয়/শিক্ষা কমিটি",
        "Charity Organization" to "দাতব্য সংস্থা",
        "Other" to "অন্যান্য সংগঠন"
    )

    val divisions = listOf("ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "বরিশাল", "সিলেট", "রংপুর", "ময়মনসিংহ")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "সমিতি সেটআপ ও প্রোফাইল" else "Samity Setup & Profiles",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isBn) "১ বা একাধিক সমিতি তৈরি ও পূর্ণাঙ্গ পরিচালনা" else "Multi-Tenant Samity Management",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Mode Toggle Bar (Edit Active vs Create New)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        isCreatingNew = false
                        samityId = currentSamity.id
                        nameBn = currentSamity.nameBn
                        nameEn = currentSamity.nameEn
                        samityType = currentSamity.samityType
                        regNo = currentSamity.regNo
                        establishedDate = currentSamity.establishedDate
                        sloganBn = currentSamity.sloganBn
                        sloganEn = currentSamity.sloganEn
                        logoUri = currentSamity.logoUri
                        addressBn = currentSamity.addressBn
                        addressEn = currentSamity.addressEn
                        division = currentSamity.division
                        district = currentSamity.district
                        upazila = currentSamity.upazila
                        postOffice = currentSamity.postOffice
                        villageOrArea = currentSamity.villageOrArea
                        phone = currentSamity.phone
                        mobileNumber = currentSamity.mobileNumber
                        whatsapp = currentSamity.whatsappNumber
                        email = currentSamity.email
                        website = currentSamity.website
                        facebookPageUrl = currentSamity.facebookPageUrl
                        youtubeChannelUrl = currentSamity.youtubeChannelUrl
                        bankName = currentSamity.bankName
                        bankBranch = currentSamity.bankBranch
                        bankAccountNo = currentSamity.bankAccountNo
                        bankAccountName = currentSamity.bankAccountName
                        routingNo = currentSamity.routingNo
                        bkash = currentSamity.bkashNumber
                        nagad = currentSamity.nagadNumber
                        rocket = currentSamity.rocketNumber
                        upay = currentSamity.upayNumber
                        mfsAccountType = currentSamity.mobileBankingAccountType
                        currency = currentSamity.currency
                        defaultFeeStr = currentSamity.defaultMonthlyFee.toInt().toString()
                        dueDayStr = currentSamity.dueDayOfMonth.toString()
                        gracePeriodStr = currentSamity.gracePeriodDays.toString()
                        lateFeeStr = currentSamity.lateFeeAmount.toInt().toString()
                        fiscalYearStart = currentSamity.fiscalYearStartDate
                        fiscalYearEnd = currentSamity.fiscalYearEndDate
                        presidentName = currentSamity.presidentName
                        secretaryName = currentSamity.secretaryName
                        treasurerName = currentSamity.treasurerName
                        treasurerPhone = currentSamity.treasurerPhone
                        samityStatus = currentSamity.status
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (!isCreatingNew) SamityNavyPrimary.copy(alpha = 0.12f) else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBn) "চলতি সমিতি এডিট" else "Edit Active Samity", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        isCreatingNew = true
                        samityId = "samity_${System.currentTimeMillis().toString().takeLast(4)}"
                        nameBn = ""
                        nameEn = ""
                        samityType = "General Samity"
                        regNo = "REG-DH-${(1000..9999).random()}"
                        establishedDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
                        sloganBn = ""
                        sloganEn = ""
                        logoUri = ""
                        addressBn = ""
                        addressEn = ""
                        division = "ঢাকা"
                        district = "ঢাকা"
                        upazila = ""
                        postOffice = ""
                        villageOrArea = ""
                        phone = ""
                        mobileNumber = ""
                        whatsapp = ""
                        email = ""
                        website = ""
                        facebookPageUrl = ""
                        youtubeChannelUrl = ""
                        bankAccountName = ""
                        bankName = "সোনালী ব্যাংক পিএলসি"
                        bankBranch = "শাখা"
                        bankAccountNo = ""
                        routingNo = ""
                        bkash = ""
                        nagad = ""
                        rocket = ""
                        upay = ""
                        mfsAccountType = "Merchant"
                        currency = "BDT"
                        defaultFeeStr = "2000"
                        dueDayStr = "10"
                        gracePeriodStr = "5"
                        lateFeeStr = "50"
                        fiscalYearStart = "০১ জুলাই"
                        fiscalYearEnd = "৩০ জুন"
                        presidentName = ""
                        secretaryName = ""
                        treasurerName = ""
                        treasurerPhone = ""
                        samityStatus = "Active"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCreatingNew) SamityNavyPrimary else Color.Gray.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).testTag("create_new_samity_toggle_btn")
                ) {
                    Text(if (isBn) "+ নতুন সমিতি তৈরি" else "+ Create New", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // SECTION 1: Basic Identity & Type (প্রাথমিক পরিচয় ও ধরন)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "১. প্রাথমিক পরিচিতি ও সংগঠন ধরন" else "1. Basic Identity & Type",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    OutlinedTextField(
                        value = samityId,
                        onValueChange = { samityId = it },
                        label = { Text("Samity ID") },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nameBn,
                        onValueChange = { nameBn = it },
                        label = { Text(if (isBn) "সমিতির নাম (বাংলায়) *" else "Samity Name (Bangla) *") },
                        modifier = Modifier.fillMaxWidth().testTag("samity_name_bn_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = nameEn,
                        onValueChange = { nameEn = it },
                        label = { Text(if (isBn) "সমিতির নাম (ইংরেজিতে) *" else "Samity Name (English) *") },
                        modifier = Modifier.fillMaxWidth().testTag("samity_name_en_input"),
                        singleLine = true
                    )

                    // Samity Type Selector Chips
                    Text(text = if (isBn) "সমিতি / সংগঠনের ধরন *" else "Samity Type *", fontSize = 12.sp, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = samityType == "Savings Samity",
                            onClick = { samityType = "Savings Samity" },
                            label = { Text(if (isBn) "সঞ্চয় সমিতি" else "Savings", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = samityType == "Business Samity",
                            onClick = { samityType = "Business Samity" },
                            label = { Text(if (isBn) "ব্যবসায়ী সমিতি" else "Business", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = samityType == "Club",
                            onClick = { samityType = "Club" },
                            label = { Text(if (isBn) "ক্লাব" else "Club", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = samityType == "General Samity",
                            onClick = { samityType = "General Samity" },
                            label = { Text(if (isBn) "সাধারণ" else "General", fontSize = 11.sp) }
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = regNo,
                            onValueChange = { regNo = it },
                            label = { Text(if (isBn) "রেজিস্ট্রেশন নম্বর (ঐচ্ছিক)" else "Reg. No (Optional)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = establishedDate,
                            onValueChange = { establishedDate = it },
                            label = { Text(if (isBn) "প্রতিষ্ঠার তারিখ" else "Established Date") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Logo Upload Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(if (isBn) "সমিতির লোগো (Samity Logo)" else "Samity Logo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                if (logoUri.isNotEmpty()) (if (isBn) "লোগো ফাইল যুক্ত আছে" else "Logo selected")
                                else (if (isBn) "ডিফল্ট লোগো সক্রিয়" else "Default logo active"),
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                logoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("upload_logo_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBn) "লোগো নির্বাচন" else "Upload Logo", fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = sloganBn,
                        onValueChange = { sloganBn = it },
                        label = { Text(if (isBn) "স্লোগান (বাংলায়)" else "Slogan in Bangla") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sloganEn,
                        onValueChange = { sloganEn = it },
                        label = { Text(if (isBn) "স্লোগান (ইংরেজিতে)" else "Slogan in English") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // SECTION 2: Address & Location (ঠিকানা ও ভৌগোলিক অবস্থান)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "২. ঠিকানা ও ভৌগোলিক অবস্থান" else "2. Address & Location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    OutlinedTextField(
                        value = addressBn,
                        onValueChange = { addressBn = it },
                        label = { Text(if (isBn) "পূর্ণাঙ্গ ঠিকানা (বাংলায়) *" else "Full Address (Bangla) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = division,
                            onValueChange = { division = it },
                            label = { Text(if (isBn) "বিভাগ" else "Division") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = district,
                            onValueChange = { district = it },
                            label = { Text(if (isBn) "জেলা" else "District") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = upazila,
                            onValueChange = { upazila = it },
                            label = { Text(if (isBn) "উপজেলা / থানা" else "Upazila / Thana") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = postOffice,
                            onValueChange = { postOffice = it },
                            label = { Text(if (isBn) "ডাকঘর ও পোস্টকোড" else "Post Office") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = villageOrArea,
                        onValueChange = { villageOrArea = it },
                        label = { Text(if (isBn) "গ্রাম / এলাকা / রোড" else "Village / Area / Road") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // SECTION 3: Contact & Social Media Information (যোগাযোগ ও সোশ্যাল মিডিয়া)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "৩. যোগাযোগ ও সোশ্যাল মিডিয়া" else "3. Contacts & Social Media",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBn) "টেলিফোন নম্বর" else "Phone Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = mobileNumber,
                            onValueChange = { mobileNumber = it },
                            label = { Text(if (isBn) "মোবাইল নম্বর *" else "Mobile Number *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text(if (isBn) "হোয়াটসঅ্যাপ নম্বর" else "WhatsApp Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(if (isBn) "ইমেইল অ্যাড্রেস" else "Email Address") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = website,
                        onValueChange = { website = it },
                        label = { Text(if (isBn) "অফিশিয়াল ওয়েবসাইট" else "Website URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = facebookPageUrl,
                            onValueChange = { facebookPageUrl = it },
                            label = { Text(if (isBn) "ফেসবুক পেইজ লিংক" else "Facebook Page URL") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = youtubeChannelUrl,
                            onValueChange = { youtubeChannelUrl = it },
                            label = { Text(if (isBn) "ইউটিউব চ্যানেল (ঐচ্ছিক)" else "YouTube Channel") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // SECTION 4: Bank & Mobile Banking (ব্যাংক ও মোবাইল ব্যাংকিং)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "৪. ব্যাংক ও মোবাইল ব্যাংকিং বিবরণী" else "4. Bank & Mobile Banking Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    OutlinedTextField(
                        value = bankAccountName,
                        onValueChange = { bankAccountName = it },
                        label = { Text(if (isBn) "ব্যাংক হিসাবের নাম (Account Name)" else "Bank Account Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text(if (isBn) "ব্যাংকের নাম" else "Bank Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = bankBranch,
                            onValueChange = { bankBranch = it },
                            label = { Text(if (isBn) "শাখা" else "Branch Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bankAccountNo,
                            onValueChange = { bankAccountNo = it },
                            label = { Text(if (isBn) "একাউন্ট নম্বর" else "Account Number") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = routingNo,
                            onValueChange = { routingNo = it },
                            label = { Text(if (isBn) "রাউটিং নম্বর" else "Routing No") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    // Mobile Banking Numbers
                    Text(text = if (isBn) "মোবাইল ব্যাংকিং নম্বরসমূহ" else "Mobile Banking Numbers", fontSize = 12.sp, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = bkash,
                            onValueChange = { bkash = it },
                            label = { Text("বিকাশ (bKash)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = nagad,
                            onValueChange = { nagad = it },
                            label = { Text("নগদ (Nagad)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rocket,
                            onValueChange = { rocket = it },
                            label = { Text("রকেট (Rocket)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = upay,
                            onValueChange = { upay = it },
                            label = { Text("উপায় (Upay)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isBn) "অ্যাকাউন্ট টাইপ:" else "Account Type:", fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = mfsAccountType == "Merchant",
                                onClick = { mfsAccountType = "Merchant" },
                                label = { Text(if (isBn) "মার্চেন্ট" else "Merchant", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = mfsAccountType == "Personal",
                                onClick = { mfsAccountType = "Personal" },
                                label = { Text(if (isBn) "ব্যক্তিগত" else "Personal", fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            // SECTION 5: Subscription, Fees & Financial Year (চাঁদা, ফি ও অর্থবছর)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "৫. মাসিক চাঁদা, ফি ও অর্থবছর" else "5. Subscription, Fees & Fiscal Year",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = defaultFeeStr,
                            onValueChange = { defaultFeeStr = it },
                            label = { Text(if (isBn) "মাসিক চাঁদা (টাকা) *" else "Monthly Fee (BDT) *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = dueDayStr,
                            onValueChange = { dueDayStr = it },
                            label = { Text(if (isBn) "মাসের শেষ তারিখ (দিন)" else "Due Day of Month") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = gracePeriodStr,
                            onValueChange = { gracePeriodStr = it },
                            label = { Text(if (isBn) "গ্রেস পিরিয়ড (দিন)" else "Grace Period (Days)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = lateFeeStr,
                            onValueChange = { lateFeeStr = it },
                            label = { Text(if (isBn) "বিলম্ব ফি / জরিমানা (টাকা)" else "Late Fee Amount") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fiscalYearStart,
                            onValueChange = { fiscalYearStart = it },
                            label = { Text(if (isBn) "অর্থবছর শুরু" else "Fiscal Year Start") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = fiscalYearEnd,
                            onValueChange = { fiscalYearEnd = it },
                            label = { Text(if (isBn) "অর্থবছর শেষ" else "Fiscal Year End") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Samity Status Toggle
                    Text(text = if (isBn) "সমিতির সক্রিয়তার স্থিতি (Status) *" else "Samity Status *", fontSize = 12.sp, color = Color.Gray)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = samityStatus == "Active",
                            onClick = { samityStatus = "Active" },
                            label = { Text(if (isBn) "সক্রিয় (Active)" else "Active", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = samityStatus == "Inactive",
                            onClick = { samityStatus = "Inactive" },
                            label = { Text(if (isBn) "নিষ্ক্রিয় (Inactive)" else "Inactive", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = samityStatus == "Archived",
                            onClick = { samityStatus = "Archived" },
                            label = { Text(if (isBn) "আর্কাইভকৃত (Archived)" else "Archived", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // SECTION 6: Key Officials (দায়িত্বপ্রাপ্ত কর্মকর্তা)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isBn) "৬. দায়িত্বপ্রাপ্ত মূল কর্মকর্তাবৃন্দ" else "6. Key Executive Officials",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = SamityNavyDark
                    )

                    OutlinedTextField(
                        value = presidentName,
                        onValueChange = { presidentName = it },
                        label = { Text(if (isBn) "সভাপতির নাম" else "President Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = secretaryName,
                        onValueChange = { secretaryName = it },
                        label = { Text(if (isBn) "সাধারণ সম্পাদকের নাম" else "General Secretary Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = treasurerName,
                            onValueChange = { treasurerName = it },
                            label = { Text(if (isBn) "কোষাধ্যক্ষের নাম" else "Treasurer Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = treasurerPhone,
                            onValueChange = { treasurerPhone = it },
                            label = { Text(if (isBn) "কোষাধ্যক্ষের ফোন" else "Treasurer Phone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }

            // Save Action Button
            Button(
                onClick = {
                    if (nameBn.isBlank() || nameEn.isBlank()) {
                        Toast.makeText(context, if (isBn) "দয়া করে সমিতির বাংলা ও ইংরেজি নাম লিখুন" else "Please enter Samity name in Bangla & English", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val fee = defaultFeeStr.toDoubleOrNull() ?: 5000.0
                    val dueDay = dueDayStr.toIntOrNull() ?: 10
                    val grace = gracePeriodStr.toIntOrNull() ?: 5
                    val late = lateFeeStr.toDoubleOrNull() ?: 0.0

                    val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

                    val samityToSave = Samity(
                        id = if (isCreatingNew) "samity_${System.currentTimeMillis()}" else currentSamity.id,
                        nameBn = nameBn,
                        nameEn = nameEn,
                        sloganBn = sloganBn,
                        sloganEn = sloganEn,
                        samityType = samityType,
                        regNo = regNo,
                        establishedDate = establishedDate,
                        logoUri = logoUri,
                        addressBn = addressBn,
                        addressEn = addressEn,
                        division = division,
                        district = district,
                        upazila = upazila,
                        postOffice = postOffice,
                        villageOrArea = villageOrArea,
                        phone = phone,
                        mobileNumber = mobileNumber,
                        whatsappNumber = whatsapp,
                        email = email,
                        website = website,
                        facebookPageUrl = facebookPageUrl,
                        youtubeChannelUrl = youtubeChannelUrl,
                        bankAccountName = bankAccountName,
                        bankName = bankName,
                        bankBranch = bankBranch,
                        bankAccountNo = bankAccountNo,
                        routingNo = routingNo,
                        bkashNumber = bkash,
                        nagadNumber = nagad,
                        rocketNumber = rocket,
                        upayNumber = upay,
                        mobileBankingAccountType = mfsAccountType,
                        currency = currency,
                        defaultMonthlyFee = fee,
                        dueDayOfMonth = dueDay,
                        gracePeriodDays = grace,
                        lateFeeAmount = late,
                        fiscalYearStartDate = fiscalYearStart,
                        fiscalYearEndDate = fiscalYearEnd,
                        status = samityStatus,
                        createdBy = if (isCreatingNew) repository.userSession.currentUser.name else currentSamity.createdBy,
                        createdAt = if (isCreatingNew) nowTime else currentSamity.createdAt,
                        updatedBy = repository.userSession.currentUser.name,
                        updatedAt = nowTime,
                        presidentName = presidentName,
                        secretaryName = secretaryName,
                        treasurerName = treasurerName,
                        treasurerPhone = treasurerPhone,
                        isOwnedBySuperAdmin = if (isCreatingNew) false else currentSamity.isOwnedBySuperAdmin,
                        subscriptionPlan = currentSamity.subscriptionPlan,
                        subscriptionExpiryDate = currentSamity.subscriptionExpiryDate,
                        daysRemaining = currentSamity.daysRemaining,
                        merchantOwnerName = if (isCreatingNew) presidentName else currentSamity.merchantOwnerName,
                        merchantPhone = if (isCreatingNew) phone else currentSamity.merchantPhone,
                        accountStatus = currentSamity.accountStatus
                    )

                    if (isCreatingNew) {
                        repository.createSamity(samityToSave)
                        Toast.makeText(context, if (isBn) "নতুন সমিতি সফলভাবে তৈরি হয়েছে!" else "New Samity created successfully!", Toast.LENGTH_SHORT).show()
                        isCreatingNew = false
                    } else {
                        val index = repository.samities.indexOfFirst { it.id == currentSamity.id }
                        if (index != -1) {
                            repository.samities[index] = samityToSave
                        }
                        Toast.makeText(context, if (isBn) "সমিতির তথ্য সফলভাবে সংরক্ষিত হয়েছে!" else "Samity profile updated successfully!", Toast.LENGTH_SHORT).show()
                    }
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_samity_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCreatingNew)
                        (if (isBn) "+ নতুন সমিতি যুক্ত করুন" else "+ Add New Samity")
                    else
                        (if (isBn) "আপডেট সংরক্ষণ করুন" else "Save Changes"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
