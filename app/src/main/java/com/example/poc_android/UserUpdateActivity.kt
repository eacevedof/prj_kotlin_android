package com.example.poc_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import android.util.Log
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver

class UserUpdateActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.dbHelper = DatabaseHelper(this)
        // Obtener el ID del usuario del intent
        this.userId = intent.getLongExtra("USER_ID", -1)
        Log.d("UserUpdateActivity.onCreate", "User ID: $userId") // Para depuración
        if (this.userId == -1L) {
            // Manejar el error si no se proporciona el ID
            this.finish() // Cerrar la actividad si no hay ID
            return
        }

        setContent {
            UserUpdateScreen(dbHelper = this.dbHelper, userId = this.userId, this)
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

    Log.d("UserUpdateActivity.UserUpdateScreen", "User ID: $userId")

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val TextFieldValueSaver = listSaver<TextFieldValue, Any>(
        save = { listOf(it.text, it.selection.start, it.selection.end, it.composition?.start, it.composition?.end) as List<Any> },
        restore = {
            TextFieldValue(
                text = it[0] as String,
                selection = androidx.compose.ui.text.TextRange(it[1] as Int, it[2] as Int),
                composition = if (it[3] != null && it[4] != null)
                    androidx.compose.ui.text.TextRange(it[3] as Int, it[4] as Int)
                else null
            )
        }
    )

    // Estado para los campos del formulario
    val birthDateState = rememberSaveable { mutableStateOf(LocalDate.of(2000, 1, 1)) }
    val nameState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val emailState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val phoneState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val usernameState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val passwordState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
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
                }
                else {
                    // Manejar el caso en que el usuario no existe
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Usuario no encontrado")
                    }
                    context.finish() // Cerrar la actividad
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
                                    context.finish() // Cerrar la actividad después de la actualización
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
                            dbHelper.deleteUserById(userId)
                            withContext(Dispatchers.Main) {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Usuario eliminado")
                                }
                                showDeleteDialog.value = false
                                    context.finish() // Cerrar la actividad después de la eliminación
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

@Preview(showBackground = true)
@Composable
fun UserUpdatePreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tempDbHelper = remember { DatabaseHelper(context) }
    //necesito pasarle un id de usuario, como no puedo crear un usuario en la preview, le paso un id cualquiera
    UserUpdateScreen(dbHelper = tempDbHelper, userId = 1, context = context as ComponentActivity)
}
