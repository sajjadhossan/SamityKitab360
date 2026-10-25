package com.example.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a financial Deposit within a specific Samity tenant.
 */
@Entity(
    tableName = "room_deposits",
    indices = [
        Index(value = ["samity_id"]),
        Index(value = ["samity_id", "voucherNo"], unique = true)
    ]
)
data class DepositEntity(
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "samity_id") override val samityId: String,
    val voucherNo: String,
    val memberId: String,
    val memberName: String,
    val amount: Double,
    val depositDate: String,
    val depositMethod: String,
    val approvalStatus: String = "Approved"
) : SamityScopedEntity
