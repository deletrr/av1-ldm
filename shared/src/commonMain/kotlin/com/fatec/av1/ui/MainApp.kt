package com.fatec.av1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.fatec.av1.ui.screens.amanda.AmandaContent
import com.fatec.av1.ui.screens.auth.LoginContent
import com.fatec.av1.ui.screens.auth.RegistroContent
import com.fatec.av1.ui.screens.bianca.BiancaContent
import com.fatec.av1.ui.screens.daniel.DanielContent
import com.fatec.av1.ui.screens.danilo.DaniloContent
import com.fatec.av1.ui.screens.home.HomeContent
import com.fatec.av1.ui.theme.AppGreen
import kotlinx.coroutines.CoroutineScope

enum class Tab(val label: String, val icon: ImageVector) {
    Home("Home", Icons.Default.Home),
    Artistas("Artistas", Icons.Default.Person),
    Albuns("Álbuns", Icons.Default.Album),
    Clipes("Clipes", Icons.Default.Videocam),
    Playlists("Playlists", Icons.Default.FolderOpen),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val scope = rememberCoroutineScope()
    val authState = remember { AuthState(scope) }
    val artistaState = remember { ArtistaState(scope) }
    val albumState = remember { AlbumState(scope) }
    val clipeState = remember { ClipeState(scope) }
    val playlistState = remember { PlaylistState(scope) }

    if (!authState.logado) {
        var showLogin by remember { mutableStateOf(true) }
        if (showLogin) {
            LoginContent(authState) { authState.limpar(); showLogin = false }
        } else {
            RegistroContent(authState) { authState.limpar(); showLogin = true }
        }
        return
    }

    var currentTab by remember { mutableStateOf(Tab.Home) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") },
                actions = {
                    IconButton(onClick = { authState.logout() }) {
                        Icon(Icons.Default.ExitToApp, "Sair", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, tab.label) },
                        label = { Text(tab.label, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AppGreen,
                            selectedTextColor = AppGreen,
                            indicatorColor = AppGreen.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (currentTab) {
                Tab.Home -> HomeContent(artistaState, albumState, playlistState, clipeState)
                Tab.Artistas -> AmandaContent(artistaState)
                Tab.Albuns -> BiancaContent(albumState)
                Tab.Clipes -> DanielContent(clipeState)
                Tab.Playlists -> DaniloContent(playlistState)
            }
        }
    }
}
