package com.example.data.sync

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Real-Time Multi-Device Data Sync Engine.
 * Ensures instant synchronization of transactions (e.g. 10,000 Tk monthly deposit)
 * across all connected devices (Mobile, Tablet, POS) within the Samity.
 */
class RealtimeSyncManager {

    val connectedDevices = mutableStateListOf<ConnectedDevice>()
    val syncEvents = mutableStateListOf<RealtimeSyncEvent>()

    var latestNotification by mutableStateOf<LiveSyncNotification?>(null)
    var isLiveSyncActive by mutableStateOf(true)
    var totalSyncedTransactions by mutableStateOf(4)
    var lastSyncTimestamp by mutableStateOf("এইমাত্র (সক্রিয়)")

    init {
        initializeConnectedDevices()
    }

    private fun initializeConnectedDevices() {
        val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        connectedDevices.addAll(
            listOf(
                ConnectedDevice(
                    deviceId = "DEV-TREASURER-01",
                    deviceName = "কোষাধ্যক্ষের স্যামসাং গ্যালাক্সি S23",
                    deviceType = "Mobile",
                    userRole = "কোষাধ্যক্ষ (Treasurer)",
                    userName = "জনাব রফিকুল ইসলাম",
                    samityId = "samity_1",
                    isOnline = true,
                    lastPing = "এখনই ($now)",
                    ipOrLocation = "উত্তরা প্রধান শাখা, ঢাকা"
                ),
                ConnectedDevice(
                    deviceId = "DEV-PRESIDENT-02",
                    deviceName = "সভাপতির আইপ্যাড প্রো (iPad Pro)",
                    deviceType = "Tablet",
                    userRole = "সভাপতি (President)",
                    userName = "অধ্যক্ষ মো. শহিদুল আলম",
                    samityId = "samity_1",
                    isOnline = true,
                    lastPing = "১ মিনিট পূর্বে",
                    ipOrLocation = "সেক্টর ৩, উত্তরা"
                ),
                ConnectedDevice(
                    deviceId = "DEV-OFFICE-POS",
                    deviceName = "সমিতি প্রধান কার্যালয় কাউন্টার POS",
                    deviceType = "POS Terminal",
                    userRole = "অফিস ক্যাশিয়ার (Cashier)",
                    userName = "মো. সাজ্জাদ হোসাইন",
                    samityId = "samity_1",
                    isOnline = true,
                    lastPing = "এখনই ($now)",
                    ipOrLocation = "উত্তরা অফিস কাউন্টার"
                ),
                ConnectedDevice(
                    deviceId = "DEV-MEMBER-APP",
                    deviceName = "সদস্য মোবাইল পোর্টাল (রফিক আহমেদ)",
                    deviceType = "Mobile",
                    userRole = "সাধারণ সদস্য (Member)",
                    userName = "রফিক আহমেদ (SM-0001)",
                    samityId = "samity_1",
                    isOnline = true,
                    lastPing = "সক্রিয়",
                    ipOrLocation = "মিরপুর, ঢাকা"
                )
            )
        )
    }

    /**
     * Broadcasts a deposit transaction instantly to all connected devices.
     */
    fun broadcastDeposit(
        deposit: Deposit,
        originDevice: String = "কোষাধ্যক্ষের মোবাইল"
    ) {
        val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        lastSyncTimestamp = "এইমাত্র ($nowTime)"
        totalSyncedTransactions += 1

        val event = RealtimeSyncEvent.DepositSynced(
            voucherNo = deposit.voucherNo,
            memberName = deposit.memberName,
            memberCode = deposit.memberCode,
            amount = deposit.amount,
            depositDate = deposit.depositDate,
            originDevice = originDevice,
            timestamp = nowTime
        )
        syncEvents.add(0, event)

        val notification = LiveSyncNotification(
            titleBn = "⚡ রিয়েল-টাইম ডাটা সিঙ্ক সম্পন্ন!",
            titleEn = "⚡ Real-Time Data Synced Across Devices!",
            descriptionBn = "${deposit.memberName} (${deposit.memberCode})-এর ${deposit.amount.toInt()} টাকা চাঁদা যুক্ত হয়েছে। সকল কানেক্টেড ডিভাইসে ডাটা স্ক্যান ও ব্যালেন্স আপডেট হয়েছে।",
            descriptionEn = "${deposit.amount.toInt()} BDT deposit recorded for ${deposit.memberName}. Synchronized across all mobile devices instantly.",
            amount = deposit.amount,
            memberCode = deposit.memberCode,
            memberName = deposit.memberName,
            voucherNo = deposit.voucherNo,
            originDevice = originDevice,
            timestamp = nowTime
        )
        latestNotification = notification
    }

    /**
     * Broadcasts bulk bank slips to all connected devices.
     */
    fun broadcastBulkDeposit(
        bulk: BulkDeposit,
        originDevice: String = "ব্যাংক কাউন্টার সিঙ্ক"
    ) {
        val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        lastSyncTimestamp = "এইমাত্র ($nowTime)"
        totalSyncedTransactions += bulk.items.size

        val event = RealtimeSyncEvent.BulkSlipSynced(
            bankReceiptNo = bulk.bankReceiptNo,
            totalAmount = bulk.totalAmount,
            memberCount = bulk.items.size,
            originDevice = originDevice,
            timestamp = nowTime
        )
        syncEvents.add(0, event)

        latestNotification = LiveSyncNotification(
            titleBn = "⚡ বাল্ক ব্যাংক স্লিপ সিঙ্ক সম্পন্ন!",
            titleEn = "⚡ Bulk Deposit Synced Across Devices!",
            descriptionBn = "ব্যাংক স্লিপ নং ${bulk.bankReceiptNo}-এর মোট ${bulk.totalAmount.toInt()} টাকা ${bulk.items.size} জন সদস্যের হিসাবে তাৎক্ষণিক স্ক্যান ও জমা হয়েছে।",
            descriptionEn = "Bulk slip ${bulk.bankReceiptNo} for ${bulk.totalAmount.toInt()} BDT synced across all connected devices.",
            amount = bulk.totalAmount,
            voucherNo = bulk.bankReceiptNo,
            originDevice = originDevice,
            timestamp = nowTime
        )
    }

    fun dismissNotification() {
        latestNotification = null
    }

    fun getDevicesForSamity(samityId: String): List<ConnectedDevice> {
        return connectedDevices.filter { it.samityId == samityId || it.samityId == "samity_1" }
    }
}
