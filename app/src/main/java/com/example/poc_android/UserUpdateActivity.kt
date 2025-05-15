package com.example.poc_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class UserUpdateActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        // Obtener el ID del usuario del intent
        userId = intent.getLongExtra("USER_ID", -1)
        if (userId == -1L) {
            // Manejar el error si no se proporciona el ID
            finish() // Cerrar la actividad si no hay ID
            return
        }

        setContent {
            UserUpdateScreen(dbHelper = dbHelper, userId = userId, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserUpdateScreen(dbHelper: DatabaseHelper, userId: Long, context: ComponentActivity) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estado para los campos del formulario
    val nameState = rememberSaveable { mutableStateOf(TextFieldValue("")) }
    val birthDateState = rememberSaveable { mutableStateOf(LocalDate.of(2000, 1, 1)) }
    val emailState = rememberSaveable { mutableStateOf(TextFieldValue("")) }
    val phoneState = rememberSaveable { mutableStateOf(TextFieldValue("")) }
    val usernameState = rememberSaveable { mutableStateOf(TextFieldValue("")) }
    val passwordState = rememberSaveable { mutableStateOf(TextFieldValue("")) }  // Estado para la contraseña

    // Para el DatePicker
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthDateState.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        yearRange = 1900..2025
    )

    // Estado para controlar el diálogo de confirmación de eliminación
    val showDeleteDialog = rememberSaveable { mutableStateOf(false) }

    // Cargar los datos del usuario al inicio
    LaunchedEffect(userId) {
        coroutineScope.launch(Dispatchers.IO) {
            val user = dbHelper.getUserById(userId)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    nameState.value = TextFieldValue(user.name)
                    birthDateState.value =
                        Instant.ofEpochMilli(user.birthDate.time).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    emailState.value = TextFieldValue(user.email)
                    phoneState.value = TextFieldValue(user.phone)
                    usernameState.value = TextFieldValue(user.username)
                    passwordState.value =
                        TextFieldValue(user.accessPassword) // Cargar la contraseña
                } else {
                    // Manejar el caso en que el usuario no existe
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Usuario no encontrado")
                    }
                    finish() // Cerrar la actividad
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Usuario") }) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, //para mostrar snackbar
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Botón para actualizar
                Button(
                    onClick = {
                        // Lógica para actualizar el usuario en la base de datos
                        val updatedUser = User(
                            id = userId,
                            name = nameState.value.text,
                            birthDate = Date.from(
                                birthDateState.value.atStartOfDay(ZoneId.systemDefault()).toInstant()
                            ),
                            email = emailState.value.text,
                            phone = phoneState.value.text,
                            username = usernameState.value.text,
                            accessPassword = passwordState.value.text // Obtener la contraseña del estado
                        )
                        coroutineScope.launch(Dispatchers.IO) {
                            dbHelper.updateUser(updatedUser)
                            withContext(Dispatchers.Main) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Usuario actualizado")
                                }
                                finish() // Cerrar la actividad después de la actualización
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Actualizar")
                }

                // Botón para eliminar (mostrar diálogo de confirmación)
                Button(
                    onClick = { showDeleteDialog.value = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error) //cambiar color
                ) {
                    Text("Eliminar")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campos del formulario (igual que en MainActivity)
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo para la fecha de nacimiento con DatePickerDialog
            Column {
                OutlinedTextField(
                    value = birthDateState.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    onValueChange = { }, // El campo no es editable directamente
                    label = { Text("Fecha de Nacimiento") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true, // Establecer como solo lectura
                    trailingIcon = {
                        Button(onClick = { openDialog.value = true }) {
                            Text("Seleccionar Fecha")
                        }
                    }
                )

                if (openDialog.value) {
                    DatePickerDialog(
                        onDismissRequest = { openDialog.value = false },
                        confirmButton = {
                            Button(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    birthDateState.value =
                                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                            .toLocalDate()
                                }
                                openDialog.value = false
                            }) {
                                Text("Confirmar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { openDialog.value = false }) {
                                Text("Cancelar")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phoneState.value,
                onValueChange = { phoneState.value = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = usernameState.value,
                onValueChange = { usernameState.value = it },
                label = { Text("Nombre de Acceso") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                label = { Text("Contraseña de Acceso") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation() // Para ocultar la contraseña
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Eliminar Usuario") },
            text = { Text("¿Estás seguro de que quieres eliminar este usuario?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            dbHelper.deleteUser(userId)
                            withContext(Dispatchers.Main) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Usuario eliminado")
                                }
                                showDeleteDialog.value = false
                                finish() // Cerrar la actividad después de la eliminación
                            }
                        }
                    }
                ) {
                    Text("Sí, Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun DatabaseHelper.getUserById(userId: Long): User? {
    val db = this.readableDatabase
    val cursor = db.query(
        "users",
        arrayOf("id", "name", "birthDate", "email", "phone", "username", "accessPassword"),
        "id = ?",
        arrayOf(userId.toString()),
        null, null, null
    )
    var user: User? = null
    if (cursor.moveToFirst()) {
        user = User(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            uuid = cursor.getString(cursor.getColumnIndexOrThrow("uuid")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            birthDate = Date(cursor.getLong(cursor.getColumnIndexOrThrow("birthDate"))),
            email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
            phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
            username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
            accessPassword = cursor.getString(cursor.getColumnIndexOrThrow("accessPassword"))
        )
    }
    cursor.close()
    return user
}

@Preview(showBackground = true)
@Composable
fun UserUpdatePreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tempDbHelper = remember { DatabaseHelper(context) }
    //necesito pasarle un id de usuario, como no puedo crear un usuario en la preview, le paso un id cualquiera
    UserUpdateScreen(dbHelper = tempDbHelper, userId = 1, context = context as ComponentActivity)
}
