package com.riapebriand.assesment2.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.riapebriand.assesment2.R
import com.riapebriand.assesment2.ui.theme.AppTheme
import com.riapebriand.assesment2.ui.theme.Assesment2Theme
import com.riapebriand.assesment2.ui.theme.Typography
import com.riapebriand.assesment2.ui.theme.blueColorScheme
import com.riapebriand.assesment2.ui.theme.pinkColorScheme
import com.riapebriand.assesment2.ui.theme.yellowColorScheme
import com.riapebriand.assesment2.util.SettingsDataStore
import com.riapebriand.assesment2.util.ViewModelFactory

const val KEY_ID_WISHLIST = "idWishlist"
val fixedCategories = listOf("Gadget", "Buku", "Liburan", "Pakaian", "Lainnya")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: Long? = null) {
    val context = LocalContext.current
    val dataStore = SettingsDataStore(context)
    val selectedTheme by dataStore.selectedThemeFlow.collectAsState(initial = AppTheme.PINK)
    val colorScheme = when (selectedTheme) {
        AppTheme.PINK -> pinkColorScheme
        AppTheme.BLUE -> blueColorScheme
        AppTheme.YELLOW -> yellowColorScheme
    }

    val factory = ViewModelFactory(context)
    val viewModel: DetailViewModel = viewModel(factory = factory)

    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf(fixedCategories.first()) }
    var isTercapai by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        if (id == null) return@LaunchedEffect
        val data = viewModel.getWishlist(id) ?: return@LaunchedEffect
        nama = data.nama
        deskripsi = data.deksripsi
        kategori = data.kategori
        isTercapai = data.isTercapai
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.kembali),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    title = {
                        Text(
                            text = if (id == null) stringResource(R.string.tambah_item)
                            else stringResource(R.string.ubah_item)
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    ),
                    actions = {
                        IconButton(onClick = {
                            if (nama.isEmpty() || deskripsi.isEmpty()) {
                                Toast.makeText(context, "Data tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                                return@IconButton
                            }

                            if (id == null) {
                                viewModel.insert(nama, deskripsi, kategori, isTercapai)
                            } else {
                                viewModel.update(id, nama, deskripsi, kategori, isTercapai)
                            }
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.simpan),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        if (id != null) {
                            DeleteAction {
                                showDialog = true
                            }
                        }
                    }
                )
            }
        ) { padding ->
            WishlistForm(
                name = nama,
                onNameChange = { nama = it },
                description = deskripsi,
                onDescriptionChange = { deskripsi = it },
                category = kategori,
                onCategoryChange = { kategori = it },
                isAchieved = isTercapai,
                onAchievedChange = { isTercapai = it },
                modifier = Modifier.padding(padding)
            )
        }

        if (id != null && showDialog) {
            DisplayAlertDialog(
                onDismissRequest = { showDialog = false }
            ) {
                showDialog = false
                viewModel.delete(id)
                navController.popBackStack()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistForm(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    isAchieved: Boolean,
    onAchievedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.nama)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.deskripsi)) },
            modifier = Modifier.fillMaxWidth()
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.kategori)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                fixedCategories.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onCategoryChange(it)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            Text("Status", style = MaterialTheme.typography.labelMedium)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = !isAchieved,
                        onClick = { onAchievedChange(false) }
                    )
                    Text("Belum Tercapai")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isAchieved,
                        onClick = { onAchievedChange(true) }
                    )
                    Text("Tercapai")
                }
            }
        }
    }
}

@Composable
fun DeleteAction(onDeleteClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.lainnya),
            tint = MaterialTheme.colorScheme.primary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.hapus)) },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun DetailWishlistScreenPreview() {
    Assesment2Theme {
        DetailScreen(rememberNavController())
    }
}
