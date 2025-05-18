package io.devexpert.kmpmovies

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    //ruta de entrada de compose-mt, todo lo que hagamos aqui sera multiplataforma
    //internamente cada plataforma lo compilará a nativo
    MaterialTheme {
        Text("\n\n\n\nhello world")
    }
}