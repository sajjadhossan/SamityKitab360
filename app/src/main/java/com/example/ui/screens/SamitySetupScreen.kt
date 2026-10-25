package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import com.example.data.model.Samity
import com.example.data.repository.SamityRepository
import com.example.ui.language.LanguageManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SamitySetupScreen(
    repository: SamityRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Hardware/System back button handling
    BackHandler { onBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isBn = LanguageManager.isBangla
    val currentSamity = repository.activeSamity
    val scrollState = rememberScrollState()

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
    var division by remember { mutableStateOf(currentSamity.division.ifBlank { "ঢাকা" }) }
    var district by remember { mutableStateOf(currentSamity.district.ifBlank { "ঢাকা" }) }
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
    var mfsAccountType by remember { mutableStateOf(currentSamity.mobileBankingAccountType) }
    var currency by remember { mutableStateOf(currentSamity.currency.ifBlank { "BDT" }) }

    // Subscription & Fees
    var defaultFeeStr by remember { mutableStateOf(currentSamity.defaultMonthlyFee.toInt().toString()) }
    var dueDayStr by remember { mutableStateOf(currentSamity.dueDayOfMonth.toString()) }
    var gracePeriodStr by remember { mutableStateOf(currentSamity.gracePeriodDays.toString()) }
    var lateFeeStr by remember { mutableStateOf(currentSamity.lateFeeAmount.toInt().toString()) }
    var fiscalYearStart by remember { mutableStateOf(currentSamity.fiscalYearStartDate.ifBlank { "০১ জুলাই" }) }
    var fiscalYearEnd by remember { mutableStateOf(currentSamity.fiscalYearEndDate.ifBlank { "৩০ জুন" }) }

    // Executive Officials
    var presidentName by remember { mutableStateOf(currentSamity.presidentName) }
    var secretaryName by remember { mutableStateOf(currentSamity.secretaryName) }
    var treasurerName by remember { mutableStateOf(currentSamity.treasurerName) }
    var treasurerPhone by remember { mutableStateOf(currentSamity.treasurerPhone) }

    // Status
    var samityStatus by remember { mutableStateOf(currentSamity.status.ifBlank { "Active" }) }

    // Touched States for Form Validation
    var submitted by remember { mutableStateOf(false) }
    var touchedNameBn by remember { mutableStateOf(false) }
    var touchedNameEn by remember { mutableStateOf(false) }
    var touchedAddressBn by remember { mutableStateOf(false) }
    var touchedDivision by remember { mutableStateOf(false) }
    var touchedDistrict by remember { mutableStateOf(false) }
    var touchedUpazila by remember { mutableStateOf(false) }
    var touchedMobile by remember { mutableStateOf(false) }
    var touchedEmail by remember { mutableStateOf(false) }
    var touchedFee by remember { mutableStateOf(false) }
    var touchedDueDay by remember { mutableStateOf(false) }

    // Dropdown expanded states
    var divisionExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var samityTypeExpanded by remember { mutableStateOf(false) }

    // Photo picker launcher for Samity Logo
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            logoUri = uri.toString()
            Toast.makeText(context, if (isBn) "লোগো নির্বাচন সম্পন্ন" else "Logo selected", Toast.LENGTH_SHORT).show()
        }
    }

    val logoBitmap = remember(logoUri) {
        if (logoUri.isNotBlank()) {
            try {
                val uri = Uri.parse(logoUri)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    android.graphics.BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }
    }

    // Supported Samity Types
    val samityTypes = listOf(
        "General Samity" to ("সাধারণ সমবায় সমিতি" to "General Samity"),
        "Club" to ("ক্লাব ও যুব সংগঠন" to "Club & Youth Organization"),
        "Social Organization" to ("সামাজিক সংগঠন" to "Social Organization"),
        "Savings Samity" to ("সঞ্চয় ও ঋণদান সমিতি" to "Savings & Credit Samity"),
        "Business Samity" to ("ব্যবসায়ী সমিতি" to "Business / Traders Samity"),
        "Religious Committee" to ("মসজিদ/ধর্মীয় কমিটি" to "Religious Committee"),
        "School Committee" to ("বিদ্যালয়/শিক্ষা কমিটি" to "School Management Committee"),
        "Charity Organization" to ("দাতব্য সংস্থা" to "Charity Organization"),
        "Other" to ("অন্যান্য সংগঠন" to "Other Organization")
    )

    // Bangladesh Divisions & Districts Mapping
    val divisionDistricts = mapOf(
        "ঢাকা" to listOf("ঢাকা", "গাজীপুর", "নারায়ণগঞ্জ", "নরসিংদী", "মুন্সীগঞ্জ", "মানিকগঞ্জ", "ফরিদপুর", "রাজবাড়ী", "গোপালগঞ্জ", "মাদারীপুর", "শরীয়তপুর", "টাঙ্গাইল", "কিশোরগঞ্জ"),
        "চট্টগ্রাম" to listOf("চট্টগ্রাম", "কক্সবাজার", "কুমিল্লা", "ফেনী", "ব্রাহ্মণবাড়িয়া", "নোয়াখালী", "লক্ষ্মীপুর", "চাঁদপুর", "রাঙ্গামাটি", "খাগড়াছড়ি", "বান্দরবান"),
        "রাজশাহী" to listOf("রাজশাহী", "বগুড়া", "পাবনা", "সিরাজগঞ্জ", "নওগাঁ", "নাটোর", "চাঁপাইনবাবগঞ্জ", "জয়পুরহাট"),
        "খুলনা" to listOf("খুলনা", "যশোর", "সাতক্ষীরা", "ঝিনাইদহ", "মাগুরা", "নড়াইল", "বাগেরহাট", "কুষ্টিয়া", "চুয়াডাঙ্গা", "মেহেরপুর"),
        "বরিশাল" to listOf("বরিশাল", "পটুয়াখালী", "ভোলা", "পিরোজপুর", "বরগুনা", "ঝালকাঠি"),
        "সিলেট" to listOf("সিলেট", "মৌলভীবাজার", "হবিগঞ্জ", "সুনামগঞ্জ"),
        "রংপুর" to listOf("রংপুর", "দিনাজপুর", "গাইবান্ধা", "কুড়িগ্রাম", "লালমনিরহাট", "নীলফামারী", "পঞ্চগড়", "ঠাকুরগাঁও"),
        "ময়মনসিংহ" to listOf("ময়মনসিংহ", "জামালপুর", "নেত্রকোণা", "শেরপুর")
    )

    val divisions = divisionDistricts.keys.toList()

    // =========================================================================
    // VALIDATION RULES
    // =========================================================================
    val bengaliRegex = Regex("[\\u0980-\\u09FF]")
    val englishRegex = Regex("^[a-zA-Z0-9\\s.,&'()-]+$")
    val bdPhoneRegex = Regex("^(?:\\+?8801|01)[3-9]\\d{8}$")
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    val urlRegex = Regex("^(https?://|www\\.)[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/.*)?$")

    val errorNameBn = when {
        nameBn.isBlank() -> if (isBn) "সমিতির বাংলা নাম আবশ্যক" else "Samity name in Bangla is required"
        !bengaliRegex.containsMatchIn(nameBn) -> if (isBn) "বাংলা অক্ষরে সমিতির নাম লিখুন" else "Must contain Bengali script"
        nameBn.trim().length < 3 -> if (isBn) "কমপক্ষে ৩ অক্ষর হতে হবে" else "Minimum 3 characters"
        else -> null
    }

    val errorNameEn = when {
        nameEn.isBlank() -> if (isBn) "সমিতির ইংরেজি নাম আবশ্যক" else "Samity name in English is required"
        !englishRegex.matches(nameEn.trim()) -> if (isBn) "ইংরেজি অক্ষর ও সংখ্যা ব্যবহার করুন" else "Valid English letters/numbers only"
        nameEn.trim().length < 3 -> if (isBn) "কমপক্ষে ৩ অক্ষর হতে হবে" else "Minimum 3 characters"
        else -> null
    }

    val errorAddressBn = when {
        addressBn.isBlank() -> if (isBn) "পূর্ণাঙ্গ ঠিকানা আবশ্যক" else "Full address is required"
        addressBn.trim().length < 5 -> if (isBn) "বিস্তারিত ঠিকানা লিখুন (কমপক্ষে ৫ অক্ষর)" else "Provide full address details"
        else -> null
    }

    val errorDivision = when {
        division.isBlank() -> if (isBn) "বিভাগ নির্বাচন করুন" else "Division is required"
        else -> null
    }

    val errorDistrict = when {
        district.isBlank() -> if (isBn) "জেলা আবশ্যক" else "District is required"
        else -> null
    }

    val errorUpazila = when {
        upazila.isBlank() -> if (isBn) "উপজেলা বা থানা আবশ্যক" else "Upazila/Thana is required"
        else -> null
    }

    val errorMobile = when {
        mobileNumber.isBlank() -> if (isBn) "মোবাইল নম্বর আবশ্যক" else "Mobile number is required"
        !bdPhoneRegex.matches(mobileNumber.trim()) -> if (isBn) "সঠিক ১১ ডিজিট বাংলাদেশি মোবাইল নম্বর দিন (যেমন: 01712345678)" else "Must be valid 11-digit BD mobile (e.g. 01712345678)"
        else -> null
    }

    val errorEmail = when {
        email.isNotBlank() && !emailRegex.matches(email.trim()) -> if (isBn) "সঠিক ইমেইল ফরম্যাট দিন (যেমন: info@samity.org)" else "Invalid email address format"
        else -> null
    }

    val errorFee = when {
        defaultFeeStr.isBlank() -> if (isBn) "মাসিক চাঁদা আবশ্যক" else "Monthly fee is required"
        (defaultFeeStr.toDoubleOrNull() ?: 0.0) <= 0.0 -> if (isBn) "চাঁদার পরিমাণ ০ এর বেশি হতে হবে" else "Must be greater than 0"
        else -> null
    }

    val errorDueDay = when {
        dueDayStr.isBlank() -> if (isBn) "মাসের শেষ তারিখ আবশ্যক" else "Due day is required"
        (dueDayStr.toIntOrNull() ?: 0) !in 1..31 -> if (isBn) "১ থেকে ৩১ এর মধ্যে তারিখ দিন" else "Must be between 1 and 31"
        else -> null
    }

    val errorLateFee = when {
        lateFeeStr.isNotBlank() && (lateFeeStr.toDoubleOrNull() ?: -1.0) < 0.0 -> if (isBn) "বিলম্ব ফি ঋণাত্মক হতে পারে না" else "Late fee cannot be negative"
        else -> null
    }

    val errorWebsite = when {
        website.isNotBlank() && !website.contains(".") -> if (isBn) "সঠিক ওয়েব ঠিকানা দিন" else "Invalid website address"
        else -> null
    }

    val errorFacebook = when {
        facebookPageUrl.isNotBlank() && !facebookPageUrl.contains(".") -> if (isBn) "সঠিক ফেসবুক পেইজ লিংক দিন" else "Invalid Facebook URL"
        else -> null
    }

    val errorTreasurerPhone = when {
        treasurerPhone.isNotBlank() && !bdPhoneRegex.matches(treasurerPhone.trim()) -> if (isBn) "সঠিক ১১ ডিজিট মোবাইল নম্বর দিন" else "Must be valid 11-digit BD phone"
        else -> null
    }

    val allErrors = listOfNotNull(
        errorNameBn,
        errorNameEn,
        errorAddressBn,
        errorDivision,
        errorDistrict,
        errorUpazila,
        errorMobile,
        errorEmail,
        errorFee,
        errorDueDay,
        errorLateFee,
        errorWebsite,
        errorFacebook,
        errorTreasurerPhone
    )

    val isFormValid = allErrors.isEmpty()

    fun resetToActiveSamity() {
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
        division = currentSamity.division.ifBlank { "ঢাকা" }
        district = currentSamity.district.ifBlank { "ঢাকা" }
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
        bankAccountName = currentSamity.bankAccountName
        bankName = currentSamity.bankName
        bankBranch = currentSamity.bankBranch
        bankAccountNo = currentSamity.bankAccountNo
        routingNo = currentSamity.routingNo
        bkash = currentSamity.bkashNumber
        nagad = currentSamity.nagadNumber
        rocket = currentSamity.rocketNumber
        upay = currentSamity.upayNumber
        mfsAccountType = currentSamity.mobileBankingAccountType
        currency = currentSamity.currency.ifBlank { "BDT" }
        defaultFeeStr = currentSamity.defaultMonthlyFee.toInt().toString()
        dueDayStr = currentSamity.dueDayOfMonth.toString()
        gracePeriodStr = currentSamity.gracePeriodDays.toString()
        lateFeeStr = currentSamity.lateFeeAmount.toInt().toString()
        fiscalYearStart = currentSamity.fiscalYearStartDate.ifBlank { "০১ জুলাই" }
        fiscalYearEnd = currentSamity.fiscalYearEndDate.ifBlank { "৩০ জুন" }
        presidentName = currentSamity.presidentName
        secretaryName = currentSamity.secretaryName
        treasurerName = currentSamity.treasurerName
        treasurerPhone = currentSamity.treasurerPhone
        samityStatus = currentSamity.status.ifBlank { "Active" }
        submitted = false
    }

    fun prepareNewSamity() {
        isCreatingNew = true
        samityId = "samity_${System.currentTimeMillis().toString().takeLast(5)}"
        nameBn = ""
        nameEn = ""
        samityType = "Savings Samity"
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
        bankBranch = "প্রধান শাখা"
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
        submitted = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isBn) "সমিতি সেটআপ ও পরিচালনা" else "Samity Setup & Management",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isCreatingNew)
                                (if (isBn) "+ নতুন সমবায় সমিতি যুক্তকরণ" else "+ Registering New Cooperative")
                            else
                                (if (isBn) "সক্রিয় সমিতি: ${currentSamity.nameBn}" else "Active Samity: ${currentSamity.nameEn}"),
                            fontSize = 11.sp,
                            color = SamityGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("samity_setup_back_btn")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isFormValid) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isFormValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFormValid) (if (isBn) "সঠিক" else "Valid")
                                else "${allErrors.size} ${if (isBn) "ত্রুটি" else "errors"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SamityNavyPrimary)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    if (submitted && !isFormValid) {
                        Text(
                            text = if (isBn) "অনুগ্রহ করে লাল চিহ্নিত ${allErrors.size}টি ফিল্ড সংশোধন করুন"
                            else "Please fix ${allErrors.size} highlighted fields above",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    Button(
                        onClick = {
                            submitted = true
                            touchedNameBn = true
                            touchedNameEn = true
                            touchedAddressBn = true
                            touchedDivision = true
                            touchedDistrict = true
                            touchedUpazila = true
                            touchedMobile = true
                            touchedEmail = true
                            touchedFee = true
                            touchedDueDay = true

                            if (!isFormValid) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (isBn) "ফরমে ত্রুটি রয়েছে! অনুগ্রহ করে আবশ্যকীয় ফিল্ডগুলো পূরণ করুন।"
                                        else "Validation error! Please correct all required fields."
                                    )
                                }
                                return@Button
                            }

                            val fee = defaultFeeStr.toDoubleOrNull() ?: 2000.0
                            val dueDay = dueDayStr.toIntOrNull() ?: 10
                            val grace = gracePeriodStr.toIntOrNull() ?: 5
                            val late = lateFeeStr.toDoubleOrNull() ?: 0.0
                            val nowTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date())

                            val samityToSave = Samity(
                                id = if (isCreatingNew) samityId else currentSamity.id,
                                nameBn = nameBn.trim(),
                                nameEn = nameEn.trim(),
                                sloganBn = sloganBn.trim(),
                                sloganEn = sloganEn.trim(),
                                samityType = samityType,
                                regNo = regNo.trim(),
                                establishedDate = establishedDate.trim(),
                                logoUri = logoUri,
                                addressBn = addressBn.trim(),
                                addressEn = addressEn.trim(),
                                division = division.trim(),
                                district = district.trim(),
                                upazila = upazila.trim(),
                                postOffice = postOffice.trim(),
                                villageOrArea = villageOrArea.trim(),
                                phone = phone.trim(),
                                mobileNumber = mobileNumber.trim(),
                                whatsappNumber = whatsapp.trim(),
                                email = email.trim(),
                                website = website.trim(),
                                facebookPageUrl = facebookPageUrl.trim(),
                                youtubeChannelUrl = youtubeChannelUrl.trim(),
                                bankAccountName = bankAccountName.trim(),
                                bankName = bankName.trim(),
                                bankBranch = bankBranch.trim(),
                                bankAccountNo = bankAccountNo.trim(),
                                routingNo = routingNo.trim(),
                                bkashNumber = bkash.trim(),
                                nagadNumber = nagad.trim(),
                                rocketNumber = rocket.trim(),
                                upayNumber = upay.trim(),
                                mobileBankingAccountType = mfsAccountType,
                                currency = currency,
                                defaultMonthlyFee = fee,
                                dueDayOfMonth = dueDay,
                                gracePeriodDays = grace,
                                lateFeeAmount = late,
                                fiscalYearStartDate = fiscalYearStart.trim(),
                                fiscalYearEndDate = fiscalYearEnd.trim(),
                                status = samityStatus,
                                createdBy = if (isCreatingNew) repository.userSession.currentUser.name else currentSamity.createdBy,
                                createdAt = if (isCreatingNew) nowTime else currentSamity.createdAt,
                                updatedBy = repository.userSession.currentUser.name,
                                updatedAt = nowTime,
                                presidentName = presidentName.trim(),
                                secretaryName = secretaryName.trim(),
                                treasurerName = treasurerName.trim(),
                                treasurerPhone = treasurerPhone.trim(),
                                isOwnedBySuperAdmin = if (isCreatingNew) false else currentSamity.isOwnedBySuperAdmin,
                                subscriptionPlan = currentSamity.subscriptionPlan,
                                subscriptionExpiryDate = currentSamity.subscriptionExpiryDate,
                                daysRemaining = currentSamity.daysRemaining,
                                merchantOwnerName = if (isCreatingNew) presidentName.trim() else currentSamity.merchantOwnerName,
                                merchantPhone = if (isCreatingNew) mobileNumber.trim() else currentSamity.merchantPhone,
                                accountStatus = currentSamity.accountStatus
                            )

                            if (isCreatingNew) {
                                val success = repository.createSamity(samityToSave)
                                if (success) {
                                    Toast.makeText(context, if (isBn) "নতুন সমিতি সফলভাবে তৈরি হয়েছে!" else "New Samity created successfully!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isBn) "অনুমতি নেই! শুধুমাত্র অ্যাডমিন নতুন সমিতি তৈরি করতে পারেন।"
                                            else "Permission denied! Admin role required to create samity."
                                        )
                                    }
                                }
                            } else {
                                val success = repository.updateSamity(samityToSave)
                                if (success) {
                                    Toast.makeText(context, if (isBn) "সমিতির তথ্য সফলভাবে সংরক্ষিত হয়েছে!" else "Samity profile updated successfully!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isBn) "সংরক্ষণ ব্যর্থ হয়েছে! অনুমতি যাচাই করুন।"
                                            else "Failed to update profile! Check role permissions."
                                        )
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SamityNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_samity_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCreatingNew)
                                (if (isBn) "নতুন সমিতি সংরক্ষণ করুন" else "Register & Save Samity")
                            else
                                (if (isBn) "পরিবর্তনগুলো সংরক্ষণ করুন" else "Save All Changes"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mode Toggle Bar (Edit Active vs Create New)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { resetToActiveSamity() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isCreatingNew) SamityNavyPrimary else Color(0xFFF1F5F9),
                            contentColor = if (!isCreatingNew) Color.White else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("edit_active_samity_toggle_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "চলতি সমিতি এডিট" else "Edit Active Samity", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { prepareNewSamity() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCreatingNew) SamityNavyPrimary else Color(0xFFF1F5F9),
                            contentColor = if (isCreatingNew) Color.White else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("create_new_samity_toggle_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBn) "+ নতুন সমিতি তৈরি" else "+ Create New", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // =================================================================
            // SECTION 1: PRIMARY IDENTITY & BILINGUAL NAME VALIDATION
            // =================================================================
            SectionCard(
                title = if (isBn) "১. প্রাথমিক পরিচিতি ও সংগঠন ধরন" else "1. Basic Identity & Samity Type",
                icon = Icons.Default.Business,
                badge = if (errorNameBn == null && errorNameEn == null) "OK" else "প্রয়োজনীয়"
            ) {
                // Samity ID
                OutlinedTextField(
                    value = samityId,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isBn) "সমিতি আইডি (সিস্টেম জেনারেটেড)" else "Samity ID (System Generated)") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = SamityNavyPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("samity_id_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFF8FAFC)
                    ),
                    enabled = false
                )

                // Bengali Name with Strict Bengali Character Validation
                val showNameBnError = (submitted || touchedNameBn) && errorNameBn != null
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = {
                        nameBn = it
                        touchedNameBn = true
                    },
                    label = { Text(if (isBn) "সমিতির নাম (বাংলায়) *" else "Samity Name in Bangla *") },
                    placeholder = { Text(if (isBn) "যেমন: উত্তরা যুব কল্যাণ সমবায় সমিতি" else "e.g. উত্তরা যুব কল্যাণ সমবায় সমিতি") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = SamityNavyPrimary) },
                    trailingIcon = {
                        if (showNameBnError) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        } else if (nameBn.isNotBlank() && errorNameBn == null) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF2E7D32))
                        }
                    },
                    isError = showNameBnError,
                    supportingText = {
                        if (showNameBnError) {
                            Text(text = errorNameBn ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        } else {
                            Text(text = if (isBn) "বাংলা ইউনিকোড অক্ষরে সমিতির দাপ্তরিক নাম" else "Official name in Bengali script", fontSize = 11.sp, color = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("samity_name_bn_input"),
                    singleLine = true
                )

                // English Name with Latin / Alphanumeric Validation
                val showNameEnError = (submitted || touchedNameEn) && errorNameEn != null
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = {
                        nameEn = it
                        touchedNameEn = true
                    },
                    label = { Text(if (isBn) "সমিতির নাম (ইংরেজিতে) *" else "Samity Name in English *") },
                    placeholder = { Text("e.g. Uttara Youth Welfare Cooperative Society") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = SamityNavyPrimary) },
                    trailingIcon = {
                        if (showNameEnError) {
                            Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                        } else if (nameEn.isNotBlank() && errorNameEn == null) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = Color(0xFF2E7D32))
                        }
                    },
                    isError = showNameEnError,
                    supportingText = {
                        if (showNameEnError) {
                            Text(text = errorNameEn ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        } else {
                            Text(text = if (isBn) "ইংরেজি অক্ষরে বৈধ নাম (কমপক্ষে ৩ অক্ষর)" else "Official name in English (min 3 chars)", fontSize = 11.sp, color = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("samity_name_en_input"),
                    singleLine = true
                )

                // Samity Type Exposed Dropdown Menu
                ExposedDropdownMenuBox(
                    expanded = samityTypeExpanded,
                    onExpandedChange = { samityTypeExpanded = !samityTypeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentTypePair = samityTypes.firstOrNull { it.first == samityType }
                    val displayType = if (isBn) (currentTypePair?.second?.first ?: samityType) else (currentTypePair?.second?.second ?: samityType)

                    OutlinedTextField(
                        value = displayType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBn) "সমিতি / সংগঠনের ধরন *" else "Samity Type *") },
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = null, tint = SamityNavyPrimary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = samityTypeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("samity_type_dropdown"),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = samityTypeExpanded,
                        onDismissRequest = { samityTypeExpanded = false }
                    ) {
                        samityTypes.forEach { (typeKey, labels) ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(labels.first, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(labels.second, fontSize = 11.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    samityType = typeKey
                                    samityTypeExpanded = false
                                },
                                leadingIcon = {
                                    if (samityType == typeKey) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SamityNavyPrimary)
                                    }
                                }
                            )
                        }
                    }
                }

                // Quick Select Chips for Common Types
                Text(
                    text = if (isBn) "দ্রুত নির্বাচন:" else "Quick Select:",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val topTypes = listOf(
                        "Savings Samity" to (if (isBn) "সঞ্চয় সমিতি" else "Savings"),
                        "General Samity" to (if (isBn) "সাধারণ" else "General"),
                        "Business Samity" to (if (isBn) "ব্যবসায়ী" else "Business"),
                        "Club" to (if (isBn) "ক্লাব" else "Club")
                    )
                    topTypes.forEach { (key, label) ->
                        FilterChip(
                            selected = samityType == key,
                            onClick = { samityType = key },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.testTag("chip_samity_type_$key")
                        )
                    }
                }

                // Registration Number & Established Date
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = regNo,
                        onValueChange = { regNo = it },
                        label = { Text(if (isBn) "রেজিস্ট্রেশন নম্বর (ঐচ্ছিক)" else "Reg. No (Optional)") },
                        modifier = Modifier.weight(1f).testTag("samity_reg_no_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = establishedDate,
                        onValueChange = { establishedDate = it },
                        label = { Text(if (isBn) "প্রতিষ্ঠার তারিখ" else "Established Date") },
                        placeholder = { Text("01 Jan 2020") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.weight(1f).testTag("samity_est_date_input"),
                        singleLine = true
                    )
                }

                // Logo Upload & Preview Area
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    color = Color(0xFFF8FAFC),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .border(1.dp, SamityNavyPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoBitmap != null) {
                                Image(
                                    bitmap = logoBitmap,
                                    contentDescription = "Samity Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = "Default Logo",
                                    tint = SamityNavyPrimary,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isBn) "সমিতির অফিশিয়াল লোগো" else "Official Samity Logo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (logoUri.isNotBlank()) (if (isBn) "লোগো ইমেজ সংযুক্ত আছে" else "Custom logo linked")
                                else (if (isBn) "লেটারহেড ও রসিদে প্রদর্শনের জন্য লোগো আপলোড করুন" else "Upload logo for receipts & letterheads"),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                logoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("upload_logo_btn")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBn) "আপলোড" else "Upload", fontSize = 11.sp)
                        }
                    }
                }

                // Slogans
                OutlinedTextField(
                    value = sloganBn,
                    onValueChange = { sloganBn = it },
                    label = { Text(if (isBn) "স্লোগান (বাংলায়)" else "Slogan in Bangla") },
                    placeholder = { Text(if (isBn) "যেমন: একতাই বল, সমবায়ে সমৃদ্ধি" else "e.g. একতাই বল, সমবায়ে সমৃদ্ধি") },
                    modifier = Modifier.fillMaxWidth().testTag("samity_slogan_bn_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = sloganEn,
                    onValueChange = { sloganEn = it },
                    label = { Text(if (isBn) "স্লোগান (ইংরেজিতে)" else "Slogan in English") },
                    placeholder = { Text("e.g. Unity is Strength, Prosperity in Cooperative") },
                    modifier = Modifier.fillMaxWidth().testTag("samity_slogan_en_input"),
                    singleLine = true
                )
            }

            // =================================================================
            // SECTION 2: LOCATION & GEOGRAPHIC HIERARCHY
            // =================================================================
            SectionCard(
                title = if (isBn) "২. ভৌগোলিক অবস্থান ও পূর্ণাঙ্গ ঠিকানা" else "2. Geographic Location & Address",
                icon = Icons.Default.LocationOn,
                badge = if (errorAddressBn == null && errorDivision == null && errorDistrict == null && errorUpazila == null) "OK" else "প্রয়োজনীয়"
            ) {
                // Division Selector (8 Bangladesh Divisions)
                ExposedDropdownMenuBox(
                    expanded = divisionExpanded,
                    onExpandedChange = { divisionExpanded = !divisionExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = division,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBn) "বিভাগ (Division) *" else "Division *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = SamityNavyPrimary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("division_dropdown"),
                        singleLine = true,
                        isError = (submitted || touchedDivision) && errorDivision != null
                    )

                    ExposedDropdownMenu(
                        expanded = divisionExpanded,
                        onDismissRequest = { divisionExpanded = false }
                    ) {
                        divisions.forEach { divName ->
                            DropdownMenuItem(
                                text = { Text(divName, fontWeight = FontWeight.SemiBold) },
                                onClick = {
                                    division = divName
                                    touchedDivision = true
                                    divisionExpanded = false
                                    // Update default district based on division
                                    val availableDistricts = divisionDistricts[divName] ?: emptyList()
                                    if (district !in availableDistricts && availableDistricts.isNotEmpty()) {
                                        district = availableDistricts.first()
                                    }
                                }
                            )
                        }
                    }
                }

                // District Selector (Mapped to Division)
                val districtsForDivision = divisionDistricts[division] ?: emptyList()
                ExposedDropdownMenuBox(
                    expanded = districtExpanded,
                    onExpandedChange = { districtExpanded = !districtExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val showDistrictError = (submitted || touchedDistrict) && errorDistrict != null
                    OutlinedTextField(
                        value = district,
                        onValueChange = {
                            district = it
                            touchedDistrict = true
                        },
                        label = { Text(if (isBn) "জেলা (District) *" else "District *") },
                        placeholder = { Text(if (isBn) "জেলা নির্বাচন বা লিখুন" else "Select or type district") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = SamityNavyPrimary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                        isError = showDistrictError,
                        supportingText = if (showDistrictError) {
                            { Text(errorDistrict ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("district_dropdown"),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = districtExpanded,
                        onDismissRequest = { districtExpanded = false }
                    ) {
                        districtsForDivision.forEach { distName ->
                            DropdownMenuItem(
                                text = { Text(distName) },
                                onClick = {
                                    district = distName
                                    touchedDistrict = true
                                    districtExpanded = false
                                }
                            )
                        }
                    }
                }

                // Upazila / Thana & Post Office
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val showUpazilaError = (submitted || touchedUpazila) && errorUpazila != null
                    OutlinedTextField(
                        value = upazila,
                        onValueChange = {
                            upazila = it
                            touchedUpazila = true
                        },
                        label = { Text(if (isBn) "উপজেলা / থানা *" else "Upazila / Thana *") },
                        placeholder = { Text(if (isBn) "যেমন: উত্তরা" else "e.g. Uttara") },
                        isError = showUpazilaError,
                        supportingText = if (showUpazilaError) {
                            { Text(errorUpazila ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("upazila_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = postOffice,
                        onValueChange = { postOffice = it },
                        label = { Text(if (isBn) "ডাকঘর ও কোড" else "Post Office & Code") },
                        placeholder = { Text(if (isBn) "উত্তরা-১২৩০" else "1230") },
                        modifier = Modifier.weight(1f).testTag("post_office_input"),
                        singleLine = true
                    )
                }

                // Village / Area / Road
                OutlinedTextField(
                    value = villageOrArea,
                    onValueChange = { villageOrArea = it },
                    label = { Text(if (isBn) "গ্রাম / এলাকা / হোল্ডিং / রোড" else "Village / Area / Road / Holding") },
                    placeholder = { Text(if (isBn) "রোড # ৭, সেক্টর # ৩" else "Road # 7, Sector # 3") },
                    modifier = Modifier.fillMaxWidth().testTag("village_area_input"),
                    singleLine = true
                )

                // Full Address in Bangla (Required)
                val showAddressError = (submitted || touchedAddressBn) && errorAddressBn != null
                OutlinedTextField(
                    value = addressBn,
                    onValueChange = {
                        addressBn = it
                        touchedAddressBn = true
                    },
                    label = { Text(if (isBn) "পূর্ণাঙ্গ ঠিকানা (বাংলায়) *" else "Full Address in Bangla *") },
                    placeholder = { Text(if (isBn) "বাড়ি # ১২, রোড # ৭, সেক্টর # ৩, উত্তরা, ঢাকা-১২৩০" else "Full address") },
                    isError = showAddressError,
                    supportingText = {
                        if (showAddressError) {
                            Text(text = errorAddressBn ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                        } else {
                            Text(text = if (isBn) "রসিদ ও অফিশিয়াল নথিপত্রে এই পূর্ণ ঠিকানা যুক্ত থাকবে" else "This address will be printed on receipts & reports", fontSize = 11.sp, color = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("address_bn_input"),
                    minLines = 2,
                    maxLines = 3
                )

                // Full Address in English (Optional)
                OutlinedTextField(
                    value = addressEn,
                    onValueChange = { addressEn = it },
                    label = { Text(if (isBn) "পূর্ণাঙ্গ ঠিকানা (ইংরেজিতে)" else "Full Address in English") },
                    placeholder = { Text("House # 12, Road # 7, Sector # 3, Uttara, Dhaka-1230") },
                    modifier = Modifier.fillMaxWidth().testTag("address_en_input"),
                    minLines = 2,
                    maxLines = 3
                )
            }

            // =================================================================
            // SECTION 3: CONTACT & DIGITAL COMMUNICATION
            // =================================================================
            SectionCard(
                title = if (isBn) "৩. যোগাযোগ, ইমেইল ও সোশ্যাল মিডিয়া" else "3. Contacts & Digital Presence",
                icon = Icons.Default.Phone,
                badge = if (errorMobile == null && errorEmail == null) "OK" else "প্রয়োজনীয়"
            ) {
                // Mobile & Telephone
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val showMobileError = (submitted || touchedMobile) && errorMobile != null
                    OutlinedTextField(
                        value = mobileNumber,
                        onValueChange = {
                            mobileNumber = it
                            touchedMobile = true
                        },
                        label = { Text(if (isBn) "মোবাইল নম্বর *" else "Mobile Number *") },
                        placeholder = { Text("01712345678") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SamityNavyPrimary) },
                        trailingIcon = {
                            if (showMobileError) {
                                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            } else if (mobileNumber.isNotBlank() && errorMobile == null) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                            }
                        },
                        isError = showMobileError,
                        supportingText = if (showMobileError) {
                            { Text(errorMobile ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else {
                            { Text(if (isBn) "এসএমএস ও যোগাযোগের মূল নম্বর" else "Primary contact & SMS number", fontSize = 11.sp, color = Color.Gray) }
                        },
                        modifier = Modifier.weight(1f).testTag("samity_mobile_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isBn) "টেলিফোন (ঐচ্ছিক)" else "Telephone (Optional)") },
                        placeholder = { Text("02-9876543") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("samity_phone_input"),
                        singleLine = true
                    )
                }

                // WhatsApp & Email
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text(if (isBn) "হোয়াটসঅ্যাপ নম্বর" else "WhatsApp Number") },
                        placeholder = { Text("01712345678") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("samity_whatsapp_input"),
                        singleLine = true
                    )

                    val showEmailError = (submitted || touchedEmail) && errorEmail != null
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            touchedEmail = true
                        },
                        label = { Text(if (isBn) "ইমেইল অ্যাড্রেস" else "Email Address") },
                        placeholder = { Text("info@samity.org") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SamityNavyPrimary) },
                        isError = showEmailError,
                        supportingText = if (showEmailError) {
                            { Text(errorEmail ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("samity_email_input"),
                        singleLine = true
                    )
                }

                // Website URL
                val showWebsiteError = submitted && errorWebsite != null
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text(if (isBn) "অফিশিয়াল ওয়েবসাইট" else "Official Website") },
                    placeholder = { Text("https://samity.org.bd") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    isError = showWebsiteError,
                    supportingText = if (showWebsiteError) {
                        { Text(errorWebsite ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth().testTag("samity_website_input"),
                    singleLine = true
                )

                // Facebook Page & YouTube
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val showFbError = submitted && errorFacebook != null
                    OutlinedTextField(
                        value = facebookPageUrl,
                        onValueChange = { facebookPageUrl = it },
                        label = { Text(if (isBn) "ফেসবুক পেইজ লিংক" else "Facebook Page URL") },
                        placeholder = { Text("facebook.com/samity") },
                        isError = showFbError,
                        supportingText = if (showFbError) {
                            { Text(errorFacebook ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("samity_facebook_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = youtubeChannelUrl,
                        onValueChange = { youtubeChannelUrl = it },
                        label = { Text(if (isBn) "ইউটিউব লিংক (ঐচ্ছিক)" else "YouTube Channel") },
                        placeholder = { Text("youtube.com/@samity") },
                        modifier = Modifier.weight(1f).testTag("samity_youtube_input"),
                        singleLine = true
                    )
                }
            }

            // =================================================================
            // SECTION 4: BANK & MOBILE BANKING (MFS)
            // =================================================================
            SectionCard(
                title = if (isBn) "৪. ব্যাংক অ্যাকাউন্ট ও মোবাইল ব্যাংকিং (MFS)" else "4. Bank & Mobile Banking Details",
                icon = Icons.Default.AccountBalance
            ) {
                // Bank Account Name
                OutlinedTextField(
                    value = bankAccountName,
                    onValueChange = { bankAccountName = it },
                    label = { Text(if (isBn) "ব্যাংক হিসাবের নাম (Account Name)" else "Bank Account Name") },
                    placeholder = { Text(if (isBn) "যেমন: উত্তরা সমবায় সমিতি সাধারণ তহবিল" else "Samity General Fund") },
                    modifier = Modifier.fillMaxWidth().testTag("bank_acc_name_input"),
                    singleLine = true
                )

                // Bank Name & Branch
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = { Text(if (isBn) "ব্যাংকের নাম" else "Bank Name") },
                        placeholder = { Text("সোনালী ব্যাংক পিএলসি") },
                        modifier = Modifier.weight(1f).testTag("bank_name_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = bankBranch,
                        onValueChange = { bankBranch = it },
                        label = { Text(if (isBn) "শাখা (Branch)" else "Branch Name") },
                        placeholder = { Text("উত্তরা শাখা") },
                        modifier = Modifier.weight(1f).testTag("bank_branch_input"),
                        singleLine = true
                    )
                }

                // Account No & Routing No
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bankAccountNo,
                        onValueChange = { bankAccountNo = it },
                        label = { Text(if (isBn) "হিসাব নম্বর (Account No)" else "Account Number") },
                        placeholder = { Text("1234567890123") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f).testTag("bank_acc_no_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = routingNo,
                        onValueChange = { routingNo = it },
                        label = { Text(if (isBn) "রাউটিং নম্বর" else "Routing No") },
                        placeholder = { Text("200261234") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f).testTag("bank_routing_input"),
                        singleLine = true
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFE2E8F0))

                // Mobile Banking MFS Numbers
                Text(
                    text = if (isBn) "মোবাইল ব্যাংকিং গেটওয়ে নম্বরসমূহ (বিকাশ, নগদ, রকেট, উপায়):"
                    else "Mobile Banking (bKash, Nagad, Rocket, Upay):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SamityNavyDark
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bkash,
                        onValueChange = { bkash = it },
                        label = { Text("বিকাশ (bKash)") },
                        placeholder = { Text("017xxxxxxxx") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("mfs_bkash_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nagad,
                        onValueChange = { nagad = it },
                        label = { Text("নগদ (Nagad)") },
                        placeholder = { Text("018xxxxxxxx") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("mfs_nagad_input"),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = rocket,
                        onValueChange = { rocket = it },
                        label = { Text("রকেট (Rocket)") },
                        placeholder = { Text("019xxxxxxxx-x") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("mfs_rocket_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = upay,
                        onValueChange = { upay = it },
                        label = { Text("উপায় (Upay)") },
                        placeholder = { Text("016xxxxxxxx") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("mfs_upay_input"),
                        singleLine = true
                    )
                }

                // MFS Account Type Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "মোবাইল ব্যাংকিং অ্যাকাউন্ট টাইপ:" else "MFS Account Type:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = mfsAccountType == "Merchant",
                            onClick = { mfsAccountType = "Merchant" },
                            label = { Text(if (isBn) "মার্চেন্ট (Merchant)" else "Merchant", fontSize = 11.sp) },
                            modifier = Modifier.testTag("mfs_type_merchant_chip")
                        )
                        FilterChip(
                            selected = mfsAccountType == "Personal",
                            onClick = { mfsAccountType = "Personal" },
                            label = { Text(if (isBn) "ব্যক্তিগত (Personal)" else "Personal", fontSize = 11.sp) },
                            modifier = Modifier.testTag("mfs_type_personal_chip")
                        )
                    }
                }
            }

            // =================================================================
            // SECTION 5: SUBSCRIPTION, FEES & FISCAL YEAR
            // =================================================================
            SectionCard(
                title = if (isBn) "৫. মাসিক চাঁদা, জরিমানা ও অর্থবছর" else "5. Subscription Fees & Fiscal Year",
                icon = Icons.Default.AttachMoney,
                badge = if (errorFee == null && errorDueDay == null && errorLateFee == null) "OK" else "প্রয়োজনীয়"
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    val showFeeError = (submitted || touchedFee) && errorFee != null
                    OutlinedTextField(
                        value = defaultFeeStr,
                        onValueChange = {
                            defaultFeeStr = it
                            touchedFee = true
                        },
                        label = { Text(if (isBn) "মাসিক চাঁদা (টাকা) *" else "Monthly Fee (BDT) *") },
                        placeholder = { Text("2000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = SamityNavyPrimary, fontSize = 16.sp) },
                        isError = showFeeError,
                        supportingText = if (showFeeError) {
                            { Text(errorFee ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("monthly_fee_input"),
                        singleLine = true
                    )

                    val showDueDayError = (submitted || touchedDueDay) && errorDueDay != null
                    OutlinedTextField(
                        value = dueDayStr,
                        onValueChange = {
                            dueDayStr = it
                            touchedDueDay = true
                        },
                        label = { Text(if (isBn) "মাসের শেষ তারিখ (দিন) *" else "Due Day of Month *") },
                        placeholder = { Text("10") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showDueDayError,
                        supportingText = if (showDueDayError) {
                            { Text(errorDueDay ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else {
                            { Text(if (isBn) "প্রতি মাসের ১-৩১ তারিখ" else "Day 1-31 of month", fontSize = 11.sp, color = Color.Gray) }
                        },
                        modifier = Modifier.weight(1f).testTag("due_day_input"),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = gracePeriodStr,
                        onValueChange = { gracePeriodStr = it },
                        label = { Text(if (isBn) "গ্রেস পিরিয়ড (দিন)" else "Grace Period (Days)") },
                        placeholder = { Text("5") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("grace_period_input"),
                        singleLine = true
                    )

                    val showLateFeeError = submitted && errorLateFee != null
                    OutlinedTextField(
                        value = lateFeeStr,
                        onValueChange = { lateFeeStr = it },
                        label = { Text(if (isBn) "বিলম্ব জরিমানা (টাকা)" else "Late Fee Amount (BDT)") },
                        placeholder = { Text("50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = showLateFeeError,
                        supportingText = if (showLateFeeError) {
                            { Text(errorLateFee ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("late_fee_input"),
                        singleLine = true
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = fiscalYearStart,
                        onValueChange = { fiscalYearStart = it },
                        label = { Text(if (isBn) "অর্থবছর শুরু" else "Fiscal Year Start") },
                        placeholder = { Text("০১ জুলাই") },
                        modifier = Modifier.weight(1f).testTag("fiscal_start_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fiscalYearEnd,
                        onValueChange = { fiscalYearEnd = it },
                        label = { Text(if (isBn) "অর্থবছর শেষ" else "Fiscal Year End") },
                        placeholder = { Text("৩০ জুন") },
                        modifier = Modifier.weight(1f).testTag("fiscal_end_input"),
                        singleLine = true
                    )
                }

                // Samity Status Toggle
                Text(
                    text = if (isBn) "সমিতির অবস্থা (Samity Status) *" else "Samity Status *",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = samityStatus == "Active",
                        onClick = { samityStatus = "Active" },
                        label = { Text(if (isBn) "সক্রিয় (Active)" else "Active", fontSize = 11.sp) },
                        modifier = Modifier.testTag("status_active_chip")
                    )
                    FilterChip(
                        selected = samityStatus == "Inactive",
                        onClick = { samityStatus = "Inactive" },
                        label = { Text(if (isBn) "নিষ্ক্রিয় (Inactive)" else "Inactive", fontSize = 11.sp) },
                        modifier = Modifier.testTag("status_inactive_chip")
                    )
                    FilterChip(
                        selected = samityStatus == "Archived",
                        onClick = { samityStatus = "Archived" },
                        label = { Text(if (isBn) "আর্কাইভকৃত (Archived)" else "Archived", fontSize = 11.sp) },
                        modifier = Modifier.testTag("status_archived_chip")
                    )
                }
            }

            // =================================================================
            // SECTION 6: EXECUTIVE OFFICIALS
            // =================================================================
            SectionCard(
                title = if (isBn) "৬. পরিচালনা পর্ষদ ও দায়িত্বপ্রাপ্ত কর্মকর্তা" else "6. Key Executive Officials",
                icon = Icons.Default.Person
            ) {
                OutlinedTextField(
                    value = presidentName,
                    onValueChange = { presidentName = it },
                    label = { Text(if (isBn) "সভাপতির নাম (President Name)" else "President Name") },
                    placeholder = { Text(if (isBn) "জনাব মো: আব্দুল করিম" else "Md. Abdul Karim") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SamityNavyPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("president_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = secretaryName,
                    onValueChange = { secretaryName = it },
                    label = { Text(if (isBn) "সাধারণ সম্পাদকের নাম (Secretary Name)" else "Secretary Name") },
                    placeholder = { Text(if (isBn) "জনাব রফিকুল ইসলাম" else "Rafiqul Islam") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SamityNavyPrimary) },
                    modifier = Modifier.fillMaxWidth().testTag("secretary_name_input"),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = treasurerName,
                        onValueChange = { treasurerName = it },
                        label = { Text(if (isBn) "কোষাধ্যক্ষের নাম" else "Treasurer Name") },
                        placeholder = { Text(if (isBn) "জনাব আনোয়ার হোসেন" else "Anwar Hossain") },
                        modifier = Modifier.weight(1f).testTag("treasurer_name_input"),
                        singleLine = true
                    )

                    val showTrPhoneError = submitted && errorTreasurerPhone != null
                    OutlinedTextField(
                        value = treasurerPhone,
                        onValueChange = { treasurerPhone = it },
                        label = { Text(if (isBn) "কোষাধ্যক্ষের ফোন" else "Treasurer Phone") },
                        placeholder = { Text("01712345678") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isError = showTrPhoneError,
                        supportingText = if (showTrPhoneError) {
                            { Text(errorTreasurerPhone ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                        } else null,
                        modifier = Modifier.weight(1f).testTag("treasurer_phone_input"),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    badge: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SamityNavyPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = SamityNavyPrimary, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SamityNavyDark
                    )
                }

                if (badge != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (badge == "OK") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (badge == "OK") Color(0xFF2E7D32) else Color(0xFFD32F2F),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Divider(color = Color(0xFFF1F5F9))

            content()
        }
    }
}
