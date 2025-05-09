package com.riapebriand.assesment2.navigation

import com.riapebriand.assesment2.ui.screen.KEY_ID_WISHLIST

sealed class Screen(val route: String) {
    data object Home: Screen("mainScreen")
    data object FormBaru: Screen("detailScreen")
    data object RecycleBin : Screen("recycleBin")
    data object  FormUbah: Screen("detailScreen/{$KEY_ID_WISHLIST}"){
        fun withId(id: Long) = "detailScreen/$id"
    }
}