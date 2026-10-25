package com.example.data.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Deposits supporting dynamic scoped queries.
 */
@Dao
interface DepositDao {
    @RawQuery(observedEntities = [DepositEntity::class])
    fun queryDepositsFlow(query: SupportSQLiteQuery): Flow<List<DepositEntity>>

    @RawQuery
    fun queryDepositList(query: SupportSQLiteQuery): List<DepositEntity>

    @RawQuery
    fun querySingleDeposit(query: SupportSQLiteQuery): DepositEntity?

    @RawQuery
    fun sumDeposits(query: SupportSQLiteQuery): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deposits: List<DepositEntity>)

    @Update
    suspend fun updateDeposit(deposit: DepositEntity)

    @Delete
    suspend fun deleteDeposit(deposit: DepositEntity)
}
