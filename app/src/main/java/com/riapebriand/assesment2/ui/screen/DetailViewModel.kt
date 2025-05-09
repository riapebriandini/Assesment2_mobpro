package com.riapebriand.assesment2.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riapebriand.assesment2.database.WishlistDao
import com.riapebriand.assesment2.model.Wishlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel(private val dao: WishlistDao) : ViewModel() {

    fun insert(name: String, description: String, category: String, isAchieved: Boolean = false) {
        val item = Wishlist(
            nama = name,
            deksripsi = description,
            kategori = category,
            isTercapai = isAchieved
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    suspend fun getWishlist(id: Long): Wishlist? {
        return dao.getWishlistById(id)
    }

    fun update(id: Long, name: String, description: String, category: String, isAchieved: Boolean) {
        val item = Wishlist(
            id = id,
            nama = name,
            deksripsi = description,
            kategori = category,
            isTercapai = isAchieved
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(item)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
                dao.deleteById(id)
        }
    }
}