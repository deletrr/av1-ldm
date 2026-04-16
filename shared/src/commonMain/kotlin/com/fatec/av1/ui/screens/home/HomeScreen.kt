package com.fatec.av1.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.model.*
import com.fatec.av1.ui.*
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun HomeContent(artistaVm: ArtistaState, albumVm: AlbumState, playlistVm: PlaylistState, clipeVm: ClipeState) {
    LaunchedEffect(Unit) { artistaVm.carregar(); albumVm.carregar(); playlistVm.carregar(); clipeVm.carregar() }
    var busca by remember { mutableStateOf("") }
    val artistas = artistaVm.lista
    val albums = albumVm.lista
    val playlists = playlistVm.lista
    val clipes = clipeVm.lista

    val artistasFilt = remember(busca, artistas.toList()) { if (busca.isBlank()) artistas else artistas.filter { it.nome.contains(busca, true) || it.genero.contains(busca, true) } }
    val albumsFilt = remember(busca, albums.toList()) { if (busca.isBlank()) albums else albums.filter { it.titulo.contains(busca, true) || it.nomeArtista.contains(busca, true) } }
    val playlistsFilt = remember(busca, playlists.toList()) { if (busca.isBlank()) playlists else playlists.filter { it.nome.contains(busca, true) } }
    val clipesFilt = remember(busca, clipes.toList()) { if (busca.isBlank()) clipes else clipes.filter { it.titulo.contains(busca, true) || it.artista.contains(busca, true) } }

    LazyColumn(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item { Spacer(Modifier.height(8.dp)) }
        item {
            OutlinedTextField(value = busca, onValueChange = { busca = it }, placeholder = { Text("Buscar artistas, álbuns, clipes…") },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = AppGreen) },
                trailingIcon = { if (busca.isNotBlank()) IconButton({ busca = "" }) { Icon(Icons.Default.Clear, null) } },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, unfocusedBorderColor = MaterialTheme.colorScheme.outline, cursorColor = AppGreen))
        }
        item { Spacer(Modifier.height(4.dp)) }
        item { SectionHeader("Playlists", Icons.Default.FolderOpen) }
        if (playlistsFilt.isEmpty()) item { EmptyHint("Nenhuma playlist") }
        else item { LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(playlistsFilt) { PlaylistHomeCard(it) } } }
        item { SectionHeader("Álbuns", Icons.Default.Album) }
        if (albumsFilt.isEmpty()) item { EmptyHint("Nenhum álbum") }
        else item { LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(albumsFilt) { AlbumHomeCard(it) } } }
        item { SectionHeader("Clipes", Icons.Default.Videocam) }
        if (clipesFilt.isEmpty()) item { EmptyHint("Nenhum clipe") }
        else item { LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) { items(clipesFilt) { ClipeHomeCard(it) } } }
        item { SectionHeader("Artistas", Icons.Default.Person) }
        if (artistasFilt.isEmpty()) item { EmptyHint("Nenhum artista") }
        else items(artistasFilt) { ArtistaHomeRow(it) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable fun SectionHeader(title: String, icon: ImageVector) {
    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = AppGreen, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}
@Composable fun EmptyHint(text: String) = Text(text, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))

@Composable fun PlaylistHomeCard(p: Playlist) {
    Card(modifier = Modifier.size(140.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxSize().padding(12.dp), Arrangement.SpaceBetween) {
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(AppGreen.copy(alpha = 0.2f)), Alignment.Center) { Icon(Icons.Default.FolderOpen, null, tint = AppGreen, modifier = Modifier.size(30.dp)) }
            Column { Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1); Text("${p.totalItens} itens", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
@Composable fun AlbumHomeCard(a: AlbumComArtista) {
    Card(modifier = Modifier.size(width = 130.dp, height = 150.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxSize().padding(12.dp), Arrangement.SpaceBetween) {
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)), Alignment.Center) { Icon(Icons.Default.Album, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(30.dp)) }
            Column { Text(a.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1); Text(a.nomeArtista, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) }
        }
    }
}
@Composable fun ClipeHomeCard(c: Clipe) {
    Card(modifier = Modifier.size(width = 130.dp, height = 150.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxSize().padding(12.dp), Arrangement.SpaceBetween) {
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(AppGreen.copy(alpha = 0.15f)), Alignment.Center) { Icon(Icons.Default.Videocam, null, tint = AppGreen, modifier = Modifier.size(30.dp)) }
            Column { Text(c.titulo, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1); Text(c.artista, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) }
        }
    }
}
@Composable fun ArtistaHomeRow(a: Artista) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(CircleShape).background(AppGreen.copy(alpha = 0.2f)), Alignment.Center) { Icon(Icons.Default.Person, null, tint = AppGreen) }
        Spacer(Modifier.width(12.dp))
        Column { Text(a.nome, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text(a.genero, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
    }
}
