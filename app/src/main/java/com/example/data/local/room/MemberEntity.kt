package com.example.data.local.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity representing a Member within a specific Samity tenant.
 */
@Entity(
    tableName = "room_members",
    indices = [
        Index(value = ["samity_id", "memberCode"], unique = true),
        Index(value = ["samity_id"])
    ]
)
data class MemberEntity(
    @PrimaryKey override val id: String,
    @ColumnInfo(name = "samity_id") override val samityId: String,
    val memberCode: String,
    val nameBn: String,
    val nameEn: String = "",
    val phone: String,
    val monthlyFee: Double = 5000.0,
    val totalPaid: Double = 0.0,
    val totalDue: Double = 0.0,
    val status: String = "Active"
) : SamityScopedEntity
