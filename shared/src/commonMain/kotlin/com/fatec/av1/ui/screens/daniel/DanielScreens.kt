package com.fatec.av1.ui.screens.daniel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.fatec.av1.model.Clipe
import com.fatec.av1.ui.ClipeState
import com.fatec.av1.ui.components.*
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun DanielContent(vm: ClipeState) {
    LaunchedEffect(Unit) { vm.carregar() }
    var showForm by remember { mutableStateOf(false) }
    if (showForm) ClipeFormContent(vm) { showForm = false }
    else ClipeListaContent(vm, onAdd = { vm.limpar(); showForm = true }, onEdit = { vm.preencher(it); showForm = true })
}

@Composable
fun ClipeListaContent(vm: ClipeState, onAdd: () -> Unit, onEdit: (Clipe) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AppSectionTitle("Clipes")
            FloatingActionButton(onClick = onAdd, containerColor = AppGreen, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = vm.busca, onValueChange = { vm.atualizarBusca(it) },
            placeholder = { Text("Buscar clipe ou artista…") },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = AppGreen) },
            trailingIcon = { if (vm.busca.isNotBlank()) IconButton({ vm.atualizarBusca("") }) { Icon(Icons.Default.Clear, null) } },
            modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AppGreen, unfocusedBorderColor = MaterialTheme.colorScheme.outline)
        )
        Spacer(Modifier.height(12.dp))
        if (vm.listaFiltrada.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Videocam, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(if (vm.busca.isNotBlank()) "Nenhum resultado" else "Nenhum clipe cadastrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.listaFiltrada, key = { it.id }) { c ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(AppGreen.copy(alpha = 0.2f)), Alignment.Center) {
                                    Icon(Icons.Default.Videocam, null, tint = AppGreen, modifier = Modifier.size(24.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(c.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(c.artista, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Timer, null, Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(3.dp)); Text(c.duracao.ifBlank { "--:--" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.width(8.dp)); Text("• ${c.dataLancamento}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(if (c.disponivel) Icons.Default.CloudDone else Icons.Default.CloudOff, null, tint = if (c.disponivel) AppGreen else MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                                IconActionButton(Icons.Default.Edit, "Editar", { onEdit(c) }, AppGreen)
                                IconActionButton(Icons.Default.Delete, "Apagar", { vm.deletar(c) }, MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClipeFormContent(vm: ClipeState, onVoltar: () -> Unit) {
    var showArtistaMenu by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, null) }
            AppSectionTitle(if (vm.editando) "Editar Clipe" else "Novo Clipe")
        }
        AppTextField(vm.titulo, { vm.titulo = it }, "Título do Clipe")
        Box {
            OutlinedButton(onClick = { showArtistaMenu = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(8.dp),
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
        AppTextField(vm.duracao, { vm.duracao = it }, "Duração (mm:ss)")
        PlatformDatePicker("Data de Lançamento", vm.lancamentoAno, vm.lancamentoMes, vm.lancamentoDia, onDateChange = { a, m, d -> vm.lancamentoAno = a; vm.lancamentoMes = m; vm.lancamentoDia = d })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = vm.disponivel, onCheckedChange = { vm.disponivel = it }, colors = SwitchDefaults.colors(checkedThumbColor = AppGreen, checkedTrackColor = AppGreen.copy(alpha = 0.5f)))
            Spacer(Modifier.width(8.dp)); Text(if (vm.disponivel) "Disponível" else "Indisponível")
        }
        Spacer(Modifier.height(8.dp))
        AppButton("Gravar", { vm.salvar(); onVoltar() })
        AppButton("Limpar Campos", { vm.limpar() }, outlined = true)
    }
}
