package com.riapebriand.assesment2.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.riapebriand.assesment2.database.WishlistDb
import com.riapebriand.assesment2.ui.screen.DetailViewModel
import com.riapebriand.assesment2.ui.screen.MainViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = WishlistDb.getInstance(context).dao
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
//            val repository = HabitRepository(dao)
            return MainViewModel(dao) as T
        } else if (modelClass.isAssignableFrom(DetailViewModel::class.java))
            return DetailViewModel(dao) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}