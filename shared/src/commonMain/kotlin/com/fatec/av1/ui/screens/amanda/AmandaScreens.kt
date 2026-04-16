package com.fatec.av1.ui.screens.amanda

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.model.Artista
import com.fatec.av1.ui.ArtistaState
import com.fatec.av1.ui.components.*
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun AmandaContent(vm: ArtistaState) {
    LaunchedEffect(Unit) { vm.carregar() }
    var showForm by remember { mutableStateOf(false) }

    if (showForm) {
        ArtistaFormContent(vm) { showForm = false }
    } else {
        ArtistaListaContent(vm, onAdd = { vm.limpar(); showForm = true }, onEdit = { vm.preencher(it); showForm = true })
    }
}

@Composable
fun ArtistaListaContent(vm: ArtistaState, onAdd: () -> Unit, onEdit: (Artista) -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            AppSectionTitle("Artistas")
            FloatingActionButton(onClick = onAdd, containerColor = AppGreen, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.background)
            }
        }
        Spacer(Modifier.height(12.dp))
        if (vm.lista.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(64.dp))
                    Text("Nenhum artista cadastrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(vm.lista) { a ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Row(Modifier.padding(16.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(a.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(a.genero, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Início: ${a.dataInicio}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(if (a.ativo) "Ativo" else "Inativo", fontSize = 12.sp, color = if (a.ativo) AppGreen else MaterialTheme.colorScheme.error)
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
fun ArtistaFormContent(vm: ArtistaState, onVoltar: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, null) }
            AppSectionTitle(if (vm.editando) "Editar Artista" else "Novo Artista")
        }
        AppTextField(vm.nome, { vm.nome = it }, "Nome")
        AppTextField(vm.genero, { vm.genero = it }, "Gênero Musical")
        PlatformDatePicker("Data de Início", vm.dataInicioAno, vm.dataInicioMes, vm.dataInicioDia,
            onDateChange = { a, m, d -> vm.dataInicioAno = a; vm.dataInicioMes = m; vm.dataInicioDia = d })
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = vm.ativo, onCheckedChange = { vm.ativo = it },
                colors = SwitchDefaults.colors(checkedThumbColor = AppGreen, checkedTrackColor = AppGreen.copy(alpha = 0.5f)))
            Spacer(Modifier.width(8.dp))
            Text(if (vm.ativo) "Ativo" else "Inativo")
        }
        Spacer(Modifier.height(8.dp))
        AppButton("Gravar", { vm.salvar(); onVoltar() })
        AppButton("Limpar Campos", { vm.limpar() }, outlined = true)
    }
}
