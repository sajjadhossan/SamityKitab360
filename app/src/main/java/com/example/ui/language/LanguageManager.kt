package com.example.ui.language

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object LanguageManager {
    var isBangla by mutableStateOf(true)

    fun toggle() {
        isBangla = !isBangla
    }

    // Commercial Software Product Branding
    val appTitle: String get() = if (isBangla) "সমিতি কিতাব ৩৬০" else "Samity Kitab 360"
    val appSubtitle: String get() = if (isBangla) "হিসাব কিতাব ৩৬০ কমার্শিয়াল সমবায় সংস্করণ" else "Hishab Kitab 360 Commercial ERP Edition"

    // Tabs / Nav
    val navDashboard: String get() = if (isBangla) "ড্যাশবোর্ড" else "Dashboard"
    val navMembers: String get() = if (isBangla) "সদস্যবৃন্দ" else "Members"
    val navDeposits: String get() = if (isBangla) "ব্যাংক জমা" else "Bank Deposits"
    val navBulkDeposit: String get() = if (isBangla) "বাল্ক জমা" else "Bulk Deposit"
    val navPaymentStatus: String get() = if (isBangla) "পরিশোধ স্থিতি" else "Payment Status"
    val navReminders: String get() = if (isBangla) "রিমাইন্ডার সেন্টার" else "Reminder Center"
    val navExpenses: String get() = if (isBangla) "ব্যয় ব্যবস্থাপনা" else "Expenses"
    val navFundBalance: String get() = if (isBangla) "তহবিল ও স্থিতি" else "Fund & Balance"
    val navReports: String get() = if (isBangla) "রিপোর্টস" else "Reports"
    val navLetterhead: String get() = if (isBangla) "লেটারহেড ও স্বাক্ষর" else "Letterhead & Sign"
    val navDocuments: String get() = if (isBangla) "নথি জেনারেটর" else "Doc Generator"
    val navApprovals: String get() = if (isBangla) "অনুমোদন" else "Approvals"
    val navAudit: String get() = if (isBangla) "অডিট লগ" else "Audit Log"
    val navSamitySetup: String get() = if (isBangla) "সমিতি সেটআপ" else "Samity Setup"
    val navMemberPortal: String get() = if (isBangla) "আমার প্রোফাইল" else "My Portal"

    // Dashboard Cards
    val totalMembers: String get() = if (isBangla) "মোট সদস্য" else "Total Members"
    val paidMembers: String get() = if (isBangla) "পরিশোধিত সদস্য" else "Paid Members"
    val unpaidMembers: String get() = if (isBangla) "বকেয়া সদস্য" else "Unpaid Members"
    val totalExpected: String get() = if (isBangla) "প্রত্যাশিত আদায়" else "Expected Collection"
    val totalCollected: String get() = if (isBangla) "মোট আদায়" else "Total Collected"
    val totalDue: String get() = if (isBangla) "মোট বকেয়া" else "Total Due"
    val totalExpense: String get() = if (isBangla) "মোট ব্যয়" else "Total Expense"
    val availableFund: String get() = if (isBangla) "মোট উপলব্ধ তহবিল" else "Available Fund"
    val bankBalance: String get() = if (isBangla) "ব্যাংক স্থিতি" else "Bank Balance"
    val cashBalance: String get() = if (isBangla) "নগদ স্থিতি" else "Cash Balance"
    val mobileBankBalance: String get() = if (isBangla) "মোবাইল ব্যাংকিং স্থিতি" else "Mobile Banking Balance"

    // Action Buttons
    val btnAddMember: String get() = if (isBangla) "নতুন সদস্য যোগ করুন" else "Add New Member"
    val btnNewDeposit: String get() = if (isBangla) "নতুন জমা এন্ট্রি" else "New Deposit"
    val btnNewBulkDeposit: String get() = if (isBangla) "বাল্ক জমা স্লিপ এন্ট্রি" else "New Bulk Slip"
    val btnNewExpense: String get() = if (isBangla) "নতুন ব্যয় ভাউচার" else "New Expense Voucher"
    val btnPrintReceipt: String get() = if (isBangla) "রিসিট প্রিন্ট" else "Print Receipt"
    val btnSendReminder: String get() = if (isBangla) "রিমাইন্ডার পাঠান" else "Send Reminder"
    val btnShareWhatsApp: String get() = if (isBangla) "হোয়াটসঅ্যাপে পাঠান" else "Send via WhatsApp"
    val btnShareSms: String get() = if (isBangla) "এসএমএস পাঠান" else "Send SMS"
    val btnSave: String get() = if (isBangla) "সংরক্ষণ করুন" else "Save"
    val btnCancel: String get() = if (isBangla) "বাতিল" else "Cancel"
    val btnApprove: String get() = if (isBangla) "অনুমোদন করুন" else "Approve"
    val btnReject: String get() = if (isBangla) "প্রত্যাখ্যান করুন" else "Reject"
    val btnSwitchSamity: String get() = if (isBangla) "সমিতি পরিবর্তন করুন" else "Switch Samity"
    val btnCreateSamity: String get() = if (isBangla) "নতুন সমিতি তৈরি করুন" else "Create New Samity"
    val btnSwitchRole: String get() = if (isBangla) "ইউজার রোল পরিবর্তন" else "Switch User Role"
    val btnPrint: String get() = if (isBangla) "প্রিন্ট করুন" else "Print Document"
    val btnDueNotice: String get() = if (isBangla) "বকেয়া নোটিশ" else "Due Notice"

    // Status Badges
    val statusPaid: String get() = if (isBangla) "পরিশোধিত" else "Paid"
    val statusUnpaid: String get() = if (isBangla) "বকেয়া" else "Unpaid"
    val statusPartial: String get() = if (isBangla) "আংশিক" else "Partial"
    val statusAdvance: String get() = if (isBangla) "অগ্রিম" else "Advance"
    val statusWaived: String get() = if (isBangla) "মওকুফ" else "Waived"
    val statusPending: String get() = if (isBangla) "অপেক্ষমাণ" else "Pending"
    val statusApproved: String get() = if (isBangla) "অনুমোদিত" else "Approved"

    // Search and Filters
    val searchMemberPlaceholder: String get() = if (isBangla) "নাম, আইডি বা ফোন নম্বর খুঁজুন..." else "Search by name, ID or phone..."
    val all: String get() = if (isBangla) "সব" else "All"
    val filterByStatus: String get() = if (isBangla) "স্থিতি অনুযায়ী ফিল্টার" else "Filter by Status"
    val currentMonth: String get() = if (isBangla) "চলতি মাস" else "Current Month"
}
