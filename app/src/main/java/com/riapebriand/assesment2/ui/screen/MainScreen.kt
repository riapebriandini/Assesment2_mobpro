package com.riapebriand.assesment2.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.riapebriand.assesment2.R
import com.riapebriand.assesment2.model.Wishlist
import com.riapebriand.assesment2.navigation.Screen
import com.riapebriand.assesment2.ui.theme.AppTheme
import com.riapebriand.assesment2.ui.theme.Assesment2Theme
import com.riapebriand.assesment2.ui.theme.blueColorScheme
import com.riapebriand.assesment2.ui.theme.pinkColorScheme
import com.riapebriand.assesment2.ui.theme.yellowColorScheme
import com.riapebriand.assesment2.util.SettingsDataStore
import com.riapebriand.assesment2.util.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current
    val dataStore = SettingsDataStore(context)
    val showList by dataStore.layoutFlow.collectAsState(true)
    val selectedTheme by dataStore.selectedThemeFlow.collectAsState(initial = AppTheme.PINK)

    val factory = ViewModelFactory(context)
    val viewModel: MainViewModel = viewModel(factory = factory)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val colorScheme = when (selectedTheme) {
        AppTheme.PINK -> pinkColorScheme
        AppTheme.BLUE -> blueColorScheme
        AppTheme.YELLOW -> yellowColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                    ),
                    actions = {
                        // Tombol switch grid/list
                        IconButton(onClick = {
                            scope.launch { dataStore.saveLayout(!showList) }
                        }) {
                            Icon(
                                painter = painterResource(
                                    if (showList) R.drawable.baseline_grid_view_24
                                    else R.drawable.baseline_view_list_24
                                ),
                                contentDescription = stringResource(
                                    if (showList) R.string.grid else R.string.list
                                )
                            )
                        }

                        // Tombol ganti tema
                        var themeExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { themeExpanded = true }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_palette_24),
                                contentDescription = "Ganti Tema"
                            )
                        }
                        DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                            AppTheme.values().forEach { theme ->
                                DropdownMenuItem(
                                    text = { Text(theme.displayName) },
                                    onClick = {
                                        themeExpanded = false
                                        scope.launch { dataStore.saveTheme(theme) }
                                    }
                                )
                            }
                        }

                        // Tombol ke recycle bin
                        IconButton(onClick = {
                            navController.navigate(Screen.RecycleBin.route)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.baseline_delete_24),
                                contentDescription = "Recycle Bin"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate(Screen.FormBaru.route)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.tambah_item)
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionColor = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) { innerPadding ->
            ScreenContent(
                showList = showList,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                onDelete = { item ->
                    scope.launch {
                        viewModel.softDelete(item.id)
                        val result = snackbarHostState.showSnackbar(
                            message = "Item dihapus",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.restore(item.id)
                            snackbarHostState.showSnackbar(
                                message = "Item berhasil dipulihkan",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ScreenContent(
    showList: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onDelete: (Wishlist) -> Unit
) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: MainViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    if (data.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.list_kosong))
        }
    } else {
        if (showList) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                items(data) { item ->
                    ListItem(item = item, onClick = {
                        navController.navigate(Screen.FormUbah.withId(item.id))
                    }, onDelete = { onDelete(item) })

                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 0.5.dp
                    )
                }
            }

        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = modifier.fillMaxSize(),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 84.dp)
            ) {
                items(data) {
                    GridItem(wishlist = it, onClick = {
                        navController.navigate(Screen.FormUbah.withId(it.id))
                    }, onDelete = { onDelete(it) })
                }
            }
        }
    }
}

@Composable
fun ListItem(item: Wishlist, onClick: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(item.nama, fontWeight = FontWeight.Bold)
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.baseline_delete_outline_24),
                    contentDescription = "Hapus"
                )
            }
        }
        Text(item.deksripsi)
        Text(
            text = if (item.isTercapai) "Tercapai" else "Belum Tercapai",
            color = if (item.isTercapai) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun GridItem(wishlist: Wishlist, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(wishlist.nama, fontWeight = FontWeight.Bold)
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_delete_outline_24),
                        contentDescription = "Hapus"
                    )
                }
            }
            Text(wishlist.deksripsi)
            Text(
                text = if (wishlist.isTercapai) "Tercapai" else "Belum Tercapai",
                color = if (wishlist.isTercapai) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )

        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment2Theme {
        MainScreen(rememberNavController())
    }
}