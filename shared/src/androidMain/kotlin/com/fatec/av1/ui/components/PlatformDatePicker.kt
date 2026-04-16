package com.fatec.av1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.ui.theme.AppGreen

/**
 * Android: usa DatePickerDialog nativo com calendário visual.
 * Formato armazenado no Firebase: "DD/MM/AAAA"
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun PlatformDatePicker(
    label: String,
    ano: String, mes: String, dia: String,
    onDateChange: (ano: String, mes: String, dia: String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }

    // Converte campos individuais para milissegundos para pre-selecionar no calendário
    val initialMillis = remember(ano, mes, dia) {
        runCatching {
            val d = dia.padStart(2, '0').toInt()
            val m = mes.padStart(2, '0').toInt()
            val a = ano.toInt()
            // Calendário simplificado: usa java.util.Calendar via epoch
            val cal = java.util.Calendar.getInstance()
            cal.set(a, m - 1, d, 12, 0, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.getOrElse {
            java.util.Calendar.getInstance().timeInMillis
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) = true
        }
    )

    // Texto exibido no botão
    val textoData = if (dia.isNotBlank() && mes.isNotBlank() && ano.isNotBlank()) {
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
        OutlinedButton(
            onClick = { showPicker = true },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(textoData, fontSize = 14.sp)
                Icon(Icons.Default.CalendarMonth, null, tint = AppGreen)
            }
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"))
                        cal.timeInMillis = millis
                        val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString()
                        val m = (cal.get(java.util.Calendar.MONTH) + 1).toString()
                        val a = cal.get(java.util.Calendar.YEAR).toString()
                        onDateChange(a, m, d)
                    }
                    showPicker = false
                }) { Text("OK", color = AppGreen) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
                weekdayContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                navigationContentColor = MaterialTheme.colorScheme.onSurface,
                yearContentColor = MaterialTheme.colorScheme.onSurface,
                currentYearContentColor = AppGreen,
                selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
                selectedYearContainerColor = AppGreen,
                dayContentColor = MaterialTheme.colorScheme.onSurface,
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                selectedDayContainerColor = AppGreen,
                todayContentColor = AppGreen,
                todayDateBorderColor = AppGreen,
            )
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true,
            )
        }
    }
}
