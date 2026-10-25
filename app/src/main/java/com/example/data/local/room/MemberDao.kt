package com.example.data.local.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Members supporting dynamic scoped queries.
 */
@Dao
interface MemberDao {
    @RawQuery(observedEntities = [MemberEntity::class])
    fun queryMembersFlow(query: SupportSQLiteQuery): Flow<List<MemberEntity>>

    @RawQuery
    fun queryMemberList(query: SupportSQLiteQuery): List<MemberEntity>

    @RawQuery
    fun querySingleMember(query: SupportSQLiteQuery): MemberEntity?

    @RawQuery
    fun countMembers(query: SupportSQLiteQuery): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<MemberEntity>)

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)
}
