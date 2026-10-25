package com.example.data.model

data class Samity(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val sloganBn: String = "",
    val sloganEn: String = "",
    val samityType: String = "Savings Samity", // General Samity, Club, Social Organization, Savings Samity, Business Samity, Religious Committee, School Committee, Charity Organization, Other
    val regNo: String = "",
    val establishedDate: String = "",
    val logoUri: String = "",
    val addressBn: String = "",
    val addressEn: String = "",
    val division: String = "ঢাকা",
    val district: String = "ঢাকা",
    val upazila: String = "উত্তরা",
    val postOffice: String = "উত্তরা-১২৩০",
    val villageOrArea: String = "রোড # ৭, সেক্টর # ৩",
    val phone: String = "",
    val mobileNumber: String = "",
    val whatsappNumber: String = "",
    val email: String = "",
    val website: String = "",
    val facebookPageUrl: String = "",
    val youtubeChannelUrl: String = "",
    val bankName: String = "",
    val bankBranch: String = "",
    val bankAccountNo: String = "",
    val bankAccountName: String = "",
    val routingNo: String = "",
    val bkashNumber: String = "",
    val nagadNumber: String = "",
    val rocketNumber: String = "",
    val upayNumber: String = "",
    val mobileBankingAccountType: String = "Merchant", // Personal, Merchant
    val currency: String = "BDT",
    val defaultMonthlyFee: Double = 5000.0,
    val dueDayOfMonth: Int = 10,
    val gracePeriodDays: Int = 5,
    val lateFeeAmount: Double = 0.0,
    val fiscalYearStartDate: String = "০১ জুলাই",
    val fiscalYearEndDate: String = "৩০ জুন",
    val status: String = "Active", // Active, Inactive, Archived
    val createdBy: String = "Super Admin",
    val createdAt: String = "2026-01-01",
    val updatedBy: String = "Super Admin",
    val updatedAt: String = "2026-10-24",
    val presidentName: String = "",
    val secretaryName: String = "",
    val treasurerName: String = "",
    val treasurerPhone: String = "",
    // SaaS Vendor & Commercial Client Fields
    val isOwnedBySuperAdmin: Boolean = false,
    val subscriptionPlan: String = "Commercial 100-Member Pack",
    val subscriptionExpiryDate: String = "৩১ ডিসেম্বর ২০২৭",
    val daysRemaining: Int = 365,
    val merchantOwnerName: String = "",
    val merchantPhone: String = "",
    val accountStatus: String = "Active" // Active, Trial, Expired, Suspended
)

data class ConnectedDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String, // Mobile, Tablet, Desktop, POS
    val userRole: String,
    val userName: String,
    val samityId: String,
    val isOnline: Boolean = true,
    val lastPing: String,
    val ipOrLocation: String = "ঢাকা, বাংলাদেশ"
)

data class LiveSyncNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val titleBn: String,
    val titleEn: String,
    val descriptionBn: String,
    val descriptionEn: String,
    val amount: Double? = null,
    val memberCode: String? = null,
    val memberName: String? = null,
    val voucherNo: String? = null,
    val originDevice: String,
    val timestamp: String
)

sealed class RealtimeSyncEvent {
    data class DepositSynced(
        val voucherNo: String,
        val memberName: String,
        val memberCode: String,
        val amount: Double,
        val depositDate: String,
        val originDevice: String,
        val timestamp: String
    ) : RealtimeSyncEvent()

    data class MemberDueUpdated(
        val memberCode: String,
        val memberName: String,
        val totalPaid: Double,
        val newDue: Double,
        val status: String,
        val originDevice: String,
        val timestamp: String
    ) : RealtimeSyncEvent()

    data class BulkSlipSynced(
        val bankReceiptNo: String,
        val totalAmount: Double,
        val memberCount: Int,
        val originDevice: String,
        val timestamp: String
    ) : RealtimeSyncEvent()
}

data class Member(
    val id: String,
    val samityId: String,
    val memberCode: String,
    val nameBn: String,
    val nameEn: String,
    val fatherName: String,
    val phone: String,
    val whatsappNumber: String,
    val telegramHandle: String,
    val facebookUrl: String,
    val email: String,
    val address: String,
    val occupation: String,
    val bloodGroup: String,
    val membershipType: String, // President, General Secretary, Treasurer, Executive Member, General Member, Life Member
    val monthlyFee: Double,
    val paymentCycle: String = "Monthly",
    val joinDate: String,
    val status: String = "Active", // Active, Inactive, Suspended
    val currentMonthPaid: Double = 0.0,
    val totalPaid: Double = 0.0,
    val totalDue: Double = 0.0,
    val advanceBalance: Double = 0.0,
    val lastReminderDate: String? = null,
    val reminderCount: Int = 0
)

data class Deposit(
    val id: String,
    val samityId: String,
    val voucherNo: String,
    val depositDate: String,
    val memberId: String,
    val memberName: String,
    val memberCode: String,
    val depositType: String, // Monthly Subscription, Special Subscription, Donation, Fine, Event Collection
    val depositMethod: String, // Bank Counter, Online Banking, bKash, Nagad, Rocket, Cash, Cheque
    val amount: Double,
    val bankName: String,
    val bankBranch: String,
    val bankAccountNo: String,
    val bankReceiptNo: String,
    val transactionId: String = "",
    val paymentMonth: String,
    val paymentYear: Int,
    val approvalStatus: String = "Approved", // Pending, Approved, Rejected
    val remarks: String = "",
    val entryBy: String = "Treasurer",
    val isBulkItem: Boolean = false,
    val bulkSlipNo: String? = null
)

data class BulkDeposit(
    val id: String,
    val samityId: String,
    val bankReceiptNo: String,
    val bankName: String,
    val branchName: String,
    val accountNo: String,
    val depositDate: String,
    val totalAmount: Double,
    val depositorName: String,
    val depositorPhone: String,
    val status: String = "Approved",
    val items: List<BulkDepositItem>
)

data class BulkDepositItem(
    val memberId: String,
    val memberCode: String,
    val memberName: String,
    val phone: String,
    val amount: Double,
    val paymentMonth: String,
    val voucherNo: String
)

data class Expense(
    val id: String,
    val samityId: String,
    val voucherNo: String,
    val expenseDate: String,
    val category: String, // Office Rent, Electricity, Meeting Cost, Charity, Stationery, Travel, Bank Charge
    val titleBn: String,
    val titleEn: String,
    val description: String,
    val amount: Double,
    val paymentMethod: String, // Bank, Cash, bKash, Cheque
    val paidTo: String,
    val paidBy: String,
    val approvalStatus: String = "Approved" // Pending, Approved, Rejected
)

data class ReminderLog(
    val id: String,
    val samityId: String,
    val memberId: String,
    val memberName: String,
    val phone: String,
    val channel: String, // WhatsApp, SMS, Telegram, Facebook Messenger
    val dueMonth: String,
    val dueAmount: Double,
    val sentAt: String,
    val sentBy: String,
    val deliveryStatus: String = "Sent",
    val paidAfterReminder: Boolean = false
)

data class AuditLog(
    val id: String,
    val samityId: String,
    val actionBn: String,
    val actionEn: String,
    val performedBy: String,
    val userRole: String,
    val details: String,
    val timestamp: String
)

enum class UserRole(val titleBn: String, val titleEn: String) {
    SUPER_ADMIN("সুপার অ্যাডমিন", "Super Admin"),
    PRESIDENT("সভাপতি", "President"),
    GENERAL_SECRETARY("সাধারণ সম্পাদক", "General Secretary"),
    TREASURER("কোষাধ্যক্ষ", "Treasurer"),
    GENERAL_MEMBER("সাধারণ সদস্য", "General Member")
}

data class AppUser(
    val id: String,
    val name: String,
    val samityId: String,
    val role: UserRole,
    val memberId: String? = null,
    val email: String,
    val phone: String
)

data class Signatory(
    val id: String,
    val designationBn: String,
    val designationEn: String,
    val name: String,
    val phone: String,
    val isVisible: Boolean = true,
    val signatureText: String // simulated stylized signature
)

data class LetterheadConfig(
    val samityId: String,
    val showLogo: Boolean = true,
    val showSlogan: Boolean = true,
    val showRegNo: Boolean = true,
    val showAddress: Boolean = true,
    val showContact: Boolean = true,
    val showWatermark: Boolean = true,
    val watermarkText: String = "PAID / পরিশোধিত",
    val primaryColorHex: String = "#0F3B66",
    val footerTextBn: String = "নিয়মিত চাঁদা দিন, সমিতিকে গতিশীল রাখুন। হিসাব কিতাব ৩৬০ ডিগ্রি দ্বারা প্রস্তুতকৃত।",
    val footerTextEn: String = "Pay subscription regularly, keep the Samity active. Powered by Hishab Kitab 360."
)

enum class PaymentStatusType(val labelBn: String, val labelEn: String) {
    PAID("পরিশোধিত", "Paid"),
    UNPAID("বকেয়া", "Unpaid"),
    PARTIAL("আংশিক পরিশোধিত", "Partially Paid"),
    ADVANCE("অগ্রিম পরিশোধিত", "Advance Paid"),
    WAIVED("মওকুফ", "Waived")
}

data class MemberMonthlyStatus(
    val member: Member,
    val month: String,
    val year: Int,
    val requiredAmount: Double,
    val paidAmount: Double,
    val dueAmount: Double,
    val status: PaymentStatusType,
    val depositVoucherNo: String? = null,
    val depositDate: String? = null,
    val paymentMethod: String? = null
)

/**
 * Commercial Software License & Feature Activation Model.
 * Configured by the Software Seller (Super Admin) for each purchasing Samity.
 */
data class SamityLicenseConfig(
    val samityId: String,
    val licenseKey: String = "SK360-LIC-2026-DH9842",
    val planName: String = "Commercial 100-Member Pack",
    val maxMembersLimit: Int = 100,
    val validUntil: String = "৩১ ডিসেম্বর ২০২৭",
    val isActive: Boolean = true,
    // Module Feature Flags (Toggled based on client purchase)
    val enableBankDeposit: Boolean = true,
    val enableBulkDeposit: Boolean = true,
    val enableExpenses: Boolean = true,
    val enableReminders: Boolean = true,
    val enableWhatsApp: Boolean = true,
    val enableSms: Boolean = true,
    val enableLetterhead: Boolean = true,
    val enableDocuments: Boolean = true,
    val enableMemberPortal: Boolean = true,
    val enableReports: Boolean = true,
    val enableAuditTrail: Boolean = true
)

/**
 * Individual Member Portal Login & Activation Credential.
 * Enables 100+ members to securely log in with their Member Code & PIN.
 */
data class MemberCredential(
    val id: String,
    val samityId: String,
    val memberId: String,
    val memberCode: String,
    val phone: String,
    val pinHash: String = "1234",
    val isActivated: Boolean = true,
    val activatedAt: String? = "২০-১০-২০২৬"
)

