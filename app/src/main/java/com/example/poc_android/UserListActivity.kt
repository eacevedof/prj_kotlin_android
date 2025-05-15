package com.example.poc_android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.widget.DatePicker
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon

class UserListActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("UserListActivity", "onCreate called")
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            UserListScreen(dbHelper = dbHelper, this) // Pasar el contexto
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(dbHelper: DatabaseHelper, context: ComponentActivity) { // Recibir contexto
    // Estado para la lista de usuarios
    val users = remember { mutableStateListOf<User>() }
    val coroutineScope = rememberCoroutineScope()

    // Cargar los usuarios desde la base de datos al inicio
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            Log.d("UserListActivity", "UserListScreen 1")
            val userList = dbHelper.getAllUsers()
            Log.d("UserListActivity", "UserListScreen 2")
            withContext(Dispatchers.Main) {
                users.addAll(userList)
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lista de Usuarios") }) },
        floatingActionButton = { // Botón flotante para añadir usuarios
            FloatingActionButton(onClick = {
                val intent = Intent(context, CreateUserActivity::class.java) // Usar el contexto
                context.startActivity(intent)
            }) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Filled.Add, contentDescription = "Add User")
            }
        }
    ) { paddingValues ->
        if (users.isEmpty()) {
            // Mostrar un mensaje si no hay usuarios
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay usuarios registrados.",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 18.sp
                    ),
                    color = Color.Gray
                )
            }
        } else {
            // Mostrar la lista de usuarios
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre elementos
            ) {
                items(users) { user ->
                    UserCard(user = user, context = context) // Pasar el contexto a UserCard
                }
            }
        }
    }
}

@Composable
fun UserCard(user: User, context: ComponentActivity) { // Recibir contexto
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { // Añadir un click listener a la Card
                val intent = Intent(context, UserUpdateActivity::class.java)
                intent.putExtra("USER_ID", user.id) // Pasar el ID del usuario
                context.startActivity(intent)
            },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Nombre: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Fecha de Nacimiento: ${
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.birthDate)
                }", style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Teléfono: ${user.phone}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Nombre de Acceso: ${user.username}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    // Para la vista previa, necesitas pasar una instancia de DatabaseHelper.
    // Puedes usar una instancia temporal, pero no mostrará datos reales de la base de datos.
    val context = androidx.compose.ui.platform.LocalContext.current
    val tempDbHelper = remember { DatabaseHelper(context) }

    // Crea datos de muestra para la vista previa
    val sampleUsers = listOf(
        User(
            name = "John Doe",
            birthDate = Date(),
            email = "john.doe@example.com",
            phone = "1234567890",
            username = "johndoe",
            accessPassword = "password",
            id = 1
        ),
        User(
            name = "Jane Smith",
            birthDate = Date(),
            email = "jane.smith@example.com",
            phone = "9876543210",
            username = "janesmith",
            accessPassword = "password",
            id = 2
        )
    )

    // Muestra la lista de usuarios de ejemplo en la vista previa
    Column {
        UserListScreen(dbHelper = tempDbHelper, context = (context as ComponentActivity))
    }
}
