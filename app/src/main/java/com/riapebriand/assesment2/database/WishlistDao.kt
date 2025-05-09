package com.riapebriand.assesment2.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.riapebriand.assesment2.model.Wishlist
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {

    @Insert
    suspend fun insert(wishlist: Wishlist)

    @Update
    suspend fun update(wishlist: Wishlist)

    @Query("SELECT * FROM wishlist WHERE isHapus = 0")
    fun getWishlist(): Flow<List<Wishlist>>

    @Query("SELECT * FROM wishlist WHERE id = :id")
    suspend fun getWishlistById(id: Long): Wishlist?

    @Query("UPDATE wishlist SET isHapus = 1 WHERE id = :id")
    suspend fun softDeleteById(id: Long)

    @Query("UPDATE wishlist SET isHapus = 0, deletedAt = null WHERE id = :id")
    suspend fun restoreById(id: Long)

    @Query("SELECT * FROM wishlist WHERE isHapus = 1 ORDER BY id DESC")
    fun getDeletedWishlist(): Flow<List<Wishlist>>

    @Query("DELETE FROM wishlist WHERE id = :id")
    suspend fun deleteById(id: Long)

}
