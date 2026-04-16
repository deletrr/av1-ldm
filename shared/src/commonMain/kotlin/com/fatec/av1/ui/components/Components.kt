package com.fatec.av1.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun AppSectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppGreen,
            focusedLabelColor = AppGreen,
            cursorColor = AppGreen,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@Composable
fun AppButton(text: String, onClick: () -> Unit, outlined: Boolean = false, enabled: Boolean = true) {
    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(24.dp),
            enabled = enabled,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AppGreen)
        ) { Text(text) }
    } else {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(24.dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(containerColor = AppGreen, contentColor = Color.Black)
        ) { Text(text, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun IconActionButton(icon: ImageVector, desc: String, onClick: () -> Unit, tint: Color) {
    IconButton(onClick = onClick) {
        Icon(icon, contentDescription = desc, tint = tint)
    }
}

/**
 * DatePicker multiplataforma.
 * - Android: mostra DatePickerDialog nativo com calendário visual
 * - Web (wasmJs): campos de texto (DD/MM/AAAA) com validação e leitura/escrita no mesmo formato do Firebase
 *
 * O formato de armazenamento no Firebase é sempre "DD/MM/AAAA".
 */
@Composable
expect fun PlatformDatePicker(
    label: String,
    ano: String, mes: String, dia: String,
    onDateChange: (ano: String, mes: String, dia: String) -> Unit
)
