package com.example.poc_android

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

class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            UserFormScreen(dbHelper = dbHelper)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserFormScreen(dbHelper: DatabaseHelper) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val TextFieldValueSaver = listSaver<TextFieldValue, Any>(
        save = { listOf(it.text, it.selection.start, it.selection.end) },
        restore = {
            TextFieldValue(
                text = it[0] as String,
                selection = androidx.compose.ui.text.TextRange(it[1] as Int, it[2] as Int)
            )
        }
    )

    // Define los estados para los campos del formulario
    val birthDateState = rememberSaveable { mutableStateOf(LocalDate.of(2000, 1, 1)) }
    val nameState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val emailState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val phoneState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val usernameState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }
    val passwordState = rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue("")) }

    //Para el DatePicker
    val openDialog = rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = birthDateState.value.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        yearRange = 1900..2025 // Ahora incluye 2025
    )


    Scaffold(
        topBar = { TopAppBar(title = { Text("Formulario de Usuario") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campos del formulario
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
                                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
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

            // Botón para guardar
            Button(
                onClick = {
                    // Lógica para guardar el usuario en la base de datos
                    val newUser = User(
                        name = nameState.value.text,
                        birthDate = Date.from(birthDateState.value.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        email = emailState.value.text,
                        phone = phoneState.value.text,
                        username = usernameState.value.text,
                        accessPassword = passwordState.value.text
                    )

                    // Guardar el usuario en la base de datos
                    val newId = dbHelper.createUser(newUser)
                    // Limpiar los campos después de guardar
                    nameState.value = TextFieldValue("")
                    emailState.value = TextFieldValue("")
                    phoneState.value = TextFieldValue("")
                    usernameState.value = TextFieldValue("")
                    passwordState.value = TextFieldValue("")
                    birthDateState.value = LocalDate.of(2000, 1, 1) // Restablecer a la fecha predeterminada

                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Usuario guardado correctamente ${newId}")
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Usuario")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    // Necesitas pasar una instancia de DatabaseHelper para la vista previa.
    // Puedes crear una instancia temporal para la vista previa, pero no guardará datos reales.
    val context = androidx.compose.ui.platform.LocalContext.current
    val tempDbHelper = remember { DatabaseHelper(context) }
    UserFormScreen(dbHelper = tempDbHelper)
}
