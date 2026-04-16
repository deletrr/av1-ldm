package com.fatec.av1.ui.screens.danilo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.model.Playlist
import com.fatec.av1.ui.PlaylistState
import com.fatec.av1.ui.components.*
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun DaniloContent(vm: PlaylistState) {
    LaunchedEffect(Unit) { vm.carregar() }
    var showForm by remember { mutableStateOf(false) }
    var detalheId by remember { mutableStateOf<Long?>(null) }

    when {
        showForm -> PlaylistFormContent(vm) { showForm = false }
        detalheId != null -> PlaylistDetalheContent(vm, detalheId!!) { detalheId = null }
        else -> PlaylistListaContent(vm, onAdd = { vm.limpar(); showForm = true }, onEdit = { vm.preencher(it); showForm = true }, onDetalhe = { vm.carregarDetalhe(it.id); detalheId = it.id })
    }
}

@Composable
fun PlaylistListaContent(vm: PlaylistState, onAdd: () -> Unit, onEdit: (Playlist) -> Unit, onDetalhe: (Playlist) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AppSectionTitle("Playlists")
            FloatingActionButton(onClick = onAdd, containerColor = AppGreen, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.background)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (vm.lista.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FolderOpen, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                    Text("Nenhuma playlist cadastrada", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.lista) { p ->
                    Card(Modifier.fillMaxWidth().clickable { onDetalhe(p) }, shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(AppGreen.copy(alpha = 0.2f)), Alignment.Center) {
                                    Icon(Icons.Default.FolderOpen, null, tint = AppGreen, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(p.nome, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(p.descricao, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                    Text("${p.totalItens} itens  •  ${p.criadaEm}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (p.publica) Icons.Default.Public else Icons.Default.Lock, null,
                                    tint = if (p.publica) AppGreen else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                IconActionButton(Icons.Default.Edit, "Editar", { onEdit(p) }, AppGreen)
                                IconActionButton(Icons.Default.Delete, "Apagar", { vm.deletar(p) }, MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistDetalheContent(vm: PlaylistState, playlistId: Long, onVoltar: () -> Unit) {
    val playlist = vm.lista.find { it.id == playlistId }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, null) }
            Text(playlist?.nome ?: "Playlist", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (vm.detalheAlbums.isNotEmpty()) {
                item { Text("Álbuns", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppGreen, modifier = Modifier.padding(vertical = 6.dp)) }
                items(vm.detalheAlbums) { a ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) { Icon(Icons.Default.Album, null, tint = AppGreen, modifier = Modifier.size(22.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(a.titulo, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text(a.nomeArtista, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text(a.duracao, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (vm.detalheMusicas.isNotEmpty()) {
                item { Text("Músicas", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppGreen, modifier = Modifier.padding(vertical = 6.dp)) }
                items(vm.detalheMusicas) { m ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), Alignment.Center) { Icon(Icons.Default.MusicNote, null, tint = AppGreen, modifier = Modifier.size(20.dp)) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) { Text(m.titulo, fontWeight = FontWeight.Medium, fontSize = 14.sp); Text("${m.tituloAlbum}  •  ${m.nomeArtista}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        Text(m.duracao, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (vm.detalheAlbums.isEmpty() && vm.detalheMusicas.isEmpty()) {
                item { Box(Modifier.fillMaxWidth().padding(top = 64.dp), Alignment.Center) { Text("Playlist vazia", color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            }
        }
    }
}

@Composable
fun PlaylistFormContent(vm: PlaylistState, onVoltar: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, null) }; AppSectionTitle(if (vm.editando) "Editar Playlist" else "Nova Playlist") } }
        item { AppTextField(vm.nome, { vm.nome = it }, "Nome da Playlist") }
        item { AppTextField(vm.descricao, { vm.descricao = it }, "Descrição", singleLine = false) }
        item { PlatformDatePicker("Data de Criação", vm.criadaEmAno, vm.criadaEmMes, vm.criadaEmDia, onDateChange = { a, m, d -> vm.criadaEmAno = a; vm.criadaEmMes = m; vm.criadaEmDia = d }) }
        item { Row(verticalAlignment = Alignment.CenterVertically) { Switch(checked = vm.publica, onCheckedChange = { vm.publica = it }, colors = SwitchDefaults.colors(checkedThumbColor = AppGreen, checkedTrackColor = AppGreen.copy(alpha = 0.5f))); Spacer(Modifier.width(8.dp)); Text(if (vm.publica) "Pública" else "Privada") } }
        item {
            OutlinedTextField(value = vm.busca, onValueChange = { vm.atualizarBusca(it) }, label = { Text("Pesquisar álbum ou música") }, leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = { if (vm.busca.isNotBlank()) IconButton({ vm.atualizarBusca("") }) { Icon(Icons.Default.Clear, null) } },
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen))
        }
        if (vm.albumsFiltrados.isNotEmpty()) {
            item { Text("Álbuns disponíveis", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppGreen) }
            items(vm.albumsFiltrados) { a ->
                Row(Modifier.fillMaxWidth().clickable { vm.toggleAlbum(a.id) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = a.id in vm.albumsSelecionados, onCheckedChange = { vm.toggleAlbum(a.id) }, colors = CheckboxDefaults.colors(checkedColor = AppGreen))
                    Spacer(Modifier.width(8.dp)); Column { Text(a.titulo, fontSize = 14.sp); Text(a.nomeArtista, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        if (vm.musicasFiltradas.isNotEmpty()) {
            item { Text("Músicas disponíveis", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = AppGreen) }
            items(vm.musicasFiltradas) { m ->
                Row(Modifier.fillMaxWidth().clickable { vm.toggleMusica(m.id) }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = m.id in vm.musicasSelecionadas, onCheckedChange = { vm.toggleMusica(m.id) }, colors = CheckboxDefaults.colors(checkedColor = AppGreen))
                    Spacer(Modifier.width(8.dp)); Column { Text(m.titulo, fontSize = 14.sp); Text("${m.tituloAlbum} • ${m.nomeArtista}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
        }
        item { Spacer(Modifier.height(4.dp)) }
        item { AppButton("Gravar", { vm.salvar(); onVoltar() }) }
        item { AppButton("Limpar Campos", { vm.limpar() }, outlined = true) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
