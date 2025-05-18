package io.devexpert.kmpmovies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import io.devexpert.kmpmovies.Movie
import io.devexpert.kmpmovies.movies

@Composable
@Preview
fun App() {
    //jetpack compose: ruta de entrada de compose-mt, todo lo que hagamos aqui sera multiplataforma
    //internamente cada plataforma lo compilará a nativo
    MaterialTheme {
        //surface permitie configurar los colores de la ui segun dispositivo, tema claro u oscuro
        Surface (modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 120.dp),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                //aqui vamos a mostrar un listado de peliculas, vamos a psarle un listado de dtos/entities movies
                items(movies, key = {it.id}) { movie ->
                    //hay q crear este composable
                    MovieItem(movie = movie)
                }
            }
        }
    }
}


@Composable
fun MovieItem(movie: Movie) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2 /3f) //con esto siempre mantendra una altura con la anchura
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.primaryContainer)
        )
        Text (
            text = movie.title,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            modifier = Modifier.padding(8.dp)
        )
    }
}