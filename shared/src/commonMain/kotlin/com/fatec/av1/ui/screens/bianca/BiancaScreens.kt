package com.fatec.av1.ui.screens.bianca

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.model.AlbumComArtista
import com.fatec.av1.ui.AlbumState
import com.fatec.av1.ui.MusicaItem
import com.fatec.av1.ui.components.*
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun BiancaContent(vm: AlbumState) {
    LaunchedEffect(Unit) { vm.carregar() }
    var showForm by remember { mutableStateOf(false) }
    if (showForm) AlbumFormContent(vm) { showForm = false }
    else AlbumListaContent(vm, onAdd = { vm.limpar(); showForm = true }, onEdit = { vm.preencher(it); showForm = true })
}

@Composable
fun AlbumListaContent(vm: AlbumState, onAdd: () -> Unit, onEdit: (AlbumComArtista) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AppSectionTitle("Álbuns")
            FloatingActionButton(onClick = onAdd, containerColor = AppGreen, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.background)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (vm.lista.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Album, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                    Text("Nenhum álbum cadastrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.lista) { a ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(a.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(a.nomeArtista.ifBlank { "Sem artista" }, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Lançamento: ${a.lancamento}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.width(4.dp))
                                    Text(a.duracao, fontSize = 12.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Icon(if (a.disponivel) Icons.Default.CloudDone else Icons.Default.CloudOff, null,
                                        tint = if (a.disponivel) AppGreen else MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                            }
                            Row {
                                IconActionButton(Icons.Default.Edit, "Editar", { onEdit(a) }, AppGreen)
                                IconActionButton(Icons.Default.Delete, "Apagar", { vm.deletar(a) }, MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumFormContent(vm: AlbumState, onVoltar: () -> Unit) {
    var showArtistaMenu by remember { mutableStateOf(false) }
    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, null) }
                AppSectionTitle(if (vm.editando) "Editar Álbum" else "Novo Álbum")
            }
        }
        item { AppTextField(vm.titulo, { vm.titulo = it }, "Título do Álbum") }
        item {
            Box {
                OutlinedButton(onClick = { showArtistaMenu = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Column { Text("Artista", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(vm.artistaSelecionado?.nome ?: "Selecionar…", fontSize = 14.sp) }
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                }
                DropdownMenu(expanded = showArtistaMenu, onDismissRequest = { showArtistaMenu = false }) {
                    if (vm.artistas.isEmpty()) DropdownMenuItem(text = { Text("Cadastre um artista primeiro") }, onClick = { showArtistaMenu = false })
                    else vm.artistas.forEach { artista -> DropdownMenuItem(text = { Text(artista.nome) }, leadingIcon = { Icon(Icons.Default.Person, null) }, onClick = { vm.artistaSelecionado = artista; showArtistaMenu = false }) }
                }
            }
        }
        item { PlatformDatePicker("Data de Lançamento", vm.lancamentoAno, vm.lancamentoMes, vm.lancamentoDia, onDateChange = { a, m, d -> vm.lancamentoAno = a; vm.lancamentoMes = m; vm.lancamentoDia = d }) }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = vm.disponivel, onCheckedChange = { vm.disponivel = it }, colors = SwitchDefaults.colors(checkedThumbColor = AppGreen, checkedTrackColor = AppGreen.copy(alpha = 0.5f)))
                Spacer(Modifier.width(8.dp)); Text(if (vm.disponivel) "Disponível" else "Indisponível")
            }
        }
        item { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Text("Duração total", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(vm.duracaoTotal, fontWeight = FontWeight.Bold, color = AppGreen) } }
        item { Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) { Text("Músicas", fontWeight = FontWeight.Bold, fontSize = 16.sp); TextButton(onClick = { vm.adicionarMusica() }) { Icon(Icons.Default.Add, null, tint = AppGreen); Spacer(Modifier.width(4.dp)); Text("Adicionar", color = AppGreen) } } }
        itemsIndexed(vm.musicas) { idx, musica ->
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, null, tint = AppGreen, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(value = musica.titulo, onValueChange = { vm.atualizarMusica(idx, musica.copy(titulo = it)) }, label = { Text("Título", fontSize = 12.sp) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(6.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen))
                        OutlinedTextField(value = musica.duracao, onValueChange = { vm.atualizarMusica(idx, musica.copy(duracao = it)) }, label = { Text("Duração (m:ss)", fontSize = 12.sp) }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(6.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, focusedLabelColor = AppGreen))
                    }
                    IconButton(onClick = { vm.removerMusica(idx) }) { Icon(Icons.Default.RemoveCircleOutline, null, tint = MaterialTheme.colorScheme.error) }
                }
            }
        }
        item { Spacer(Modifier.height(4.dp)) }
        item { AppButton("Gravar", { vm.salvar(); onVoltar() }) }
        item { AppButton("Limpar Campos", { vm.limpar() }, outlined = true) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
