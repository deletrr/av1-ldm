package com.fatec.av1.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.ui.theme.AppGreen
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    label: String,
    ano: String, mes: String, dia: String,
    onDateChange: (ano: String, mes: String, dia: String) -> Unit
) {
    var showModal by remember { mutableStateOf(false) }

    // Texto exibido no campo (apenas leitura)
    val textoExibido = if (dia.isNotBlank()) {
        "${dia.padStart(2, '0')}/${mes.padStart(2, '0')}/$ano"
    } else {
        "Selecionar data"
    }

    Column {
        Text(
            label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Campo de texto que abre o calendário ao ser clicado
        OutlinedTextField(
            value = textoExibido,
            onValueChange = { },
            readOnly = true, // Impede a digitação manual para evitar erros
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showModal = true },
            label = { Text("Data") },
            trailingIcon = {
                IconButton(onClick = { showModal = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Abrir calendário")
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                focusedLabelColor = AppGreen,
                cursorColor = AppGreen
            ),
            enabled = true
        )
    }

    // Modal do Calendário
    if (showModal) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showModal = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selecionada = datePickerState.selectedDateMillis
                        if (selecionada != null) {
                            // Converte milissegundos para data local (usando kotlinx-datetime)
                            val instant = Instant.fromEpochMilliseconds(selecionada)
                            val dataLocal = instant.toLocalDateTime(TimeZone.UTC).date

                            onDateChange(
                                dataLocal.year.toString(),
                                dataLocal.monthNumber.toString(),
                                dataLocal.dayOfMonth.toString()
                            )
                        }
                        showModal = false
                    }
                ) {
                    Text("OK", color = AppGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showModal = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}