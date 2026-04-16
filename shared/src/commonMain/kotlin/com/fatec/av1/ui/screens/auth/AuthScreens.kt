package com.fatec.av1.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fatec.av1.ui.AuthState
import com.fatec.av1.ui.components.AppButton
import com.fatec.av1.ui.theme.AppGreen

@Composable
fun LoginContent(vm: AuthState, onIrParaRegistro: () -> Unit) {
    // Surface garante fundo e cor de texto corretos em todas as plataformas (web darkTheme forçado, Android claro/escuro)
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.MusicNote, null, tint = AppGreen, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(8.dp))
            Text("AV1 LDM", fontWeight = FontWeight.Bold, fontSize = 28.sp, color = AppGreen)
            Text(
                "Integrantes: Amanda, Bianca, Daniel e Danilo",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Faça login para continuar",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                focusedLabelColor = AppGreen,
                cursorColor = AppGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            )

            OutlinedTextField(
                value = vm.email, onValueChange = { vm.email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.senha, onValueChange = { vm.senha = it },
                label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(8.dp))
            vm.erro?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }
            AppButton("Entrar", onClick = { vm.login() }, enabled = !vm.carregando)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onIrParaRegistro) { Text("Não tem conta? Registre-se", color = AppGreen) }
            if (vm.carregando) { Spacer(Modifier.height(16.dp)); CircularProgressIndicator(color = AppGreen) }
        }
    }
}

@Composable
fun RegistroContent(vm: AuthState, onIrParaLogin: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.PersonAdd, null, tint = AppGreen, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                "Criar Conta",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(24.dp))

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppGreen,
                focusedLabelColor = AppGreen,
                cursorColor = AppGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            )

            OutlinedTextField(
                value = vm.nome, onValueChange = { vm.nome = it },
                label = { Text("Nome") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.email, onValueChange = { vm.email = it },
                label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.senha, onValueChange = { vm.senha = it },
                label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = vm.confirmaSenha, onValueChange = { vm.confirmaSenha = it },
                label = { Text("Confirmar Senha") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true, shape = RoundedCornerShape(8.dp),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                colors = tfColors
            )
            Spacer(Modifier.height(8.dp))
            vm.erro?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))
            }
            AppButton("Registrar", onClick = { vm.registrar() }, enabled = !vm.carregando)
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onIrParaLogin) { Text("Já tem conta? Faça login", color = AppGreen) }
            if (vm.carregando) { Spacer(Modifier.height(16.dp)); CircularProgressIndicator(color = AppGreen) }
        }
    }
}
