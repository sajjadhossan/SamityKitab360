package com.example.data.local.room

/**
 * Common interface for all database entities that belong to a specific samity tenant.
 * Guarantees that every persisted record has an explicit 'samityId'.
 */
interface SamityScopedEntity {
    val id: String
    val samityId: String
}
