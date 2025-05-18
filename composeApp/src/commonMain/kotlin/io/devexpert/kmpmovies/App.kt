package io.devexpert.kmpmovies

import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import coil3.compose.setSingletonImageLoaderFactory
import coil3.ImageLoader
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.devexpert.kmpmovies.ui.screens.home.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    //import coil3.compose.setSingletonImageLoaderFactory
    // setea un imageloader global para que lo use  asyncImage cada vez que lo necesite.
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .crossfade(true)
            .logger(DebugLogger()) //por si hay problemas poder verlo en los logs
            .build()
    }
    HomeScreen()
}

