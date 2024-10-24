package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Registro : AppCompatActivity() {
    private lateinit var tvAppTitle: TextView
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etEmail: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etContraseña: EditText
    private lateinit var etRepetirContraseña: EditText
    private lateinit var checkboxGuitarra: CheckBox
    private lateinit var checkboxBajo: CheckBox
    private lateinit var checkboxBateria: CheckBox
    private lateinit var checkboxTeclado: CheckBox
    private lateinit var checkboxOtro: CheckBox
    private lateinit var etOtroInstrumento: EditText
    private lateinit var btnRegistrar: Button
    private lateinit var auth : FirebaseAuth
    private lateinit var reference : DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializarVariables()

        btnRegistrar.setOnClickListener{
            validarDatos()
        }

    }


    private fun inicializarVariables() {
        etNombre = findViewById(R.id.etNombre) // Asegúrate de usar R.id. para referenciar los IDs
        etApellido = findViewById(R.id.etApellido)
        etEmail = findViewById(R.id.etEmail)
        etContraseña=findViewById(R.id.etContraseña)
        etRepetirContraseña=findViewById(R.id.etRepetirContraseña)
        etDescripcion = findViewById(R.id.etDescripcion)
        checkboxGuitarra = findViewById(R.id.checkboxGuitarra)
        checkboxBajo = findViewById(R.id.checkboxBajo)
        checkboxBateria = findViewById(R.id.checkboxBateria)
        checkboxTeclado = findViewById(R.id.checkboxTeclado)
        checkboxOtro = findViewById(R.id.checkboxOtro)
        etOtroInstrumento = findViewById(R.id.etOtroInstrumento)
        btnRegistrar = findViewById(R.id.btnRegistrar)
        auth = FirebaseAuth.getInstance()


    }

    private fun validarDatos() {
        // Obtener los textos de los EditText
        val nombre: String = etNombre.text.toString()
        val apellido: String = etApellido.text.toString()
        val email: String = etEmail.text.toString()
        val contraseña: String = etContraseña.text.toString()
        val repetirContraseña: String = etRepetirContraseña.text.toString()
        val descripcion: String = etDescripcion.text.toString()

        // Variable para verificar si hay errores
        var hayErrores = false

        // Validación de nombre
        if (nombre.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese nombre de usuario", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de apellido
        if (apellido.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese apellido", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de correo
        if (email.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese correo electrónico", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de contraseña
        if (contraseña.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese contraseña", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de repetir contraseña
        if (repetirContraseña.isEmpty()) {
            Toast.makeText(applicationContext, "Repita la contraseña", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de que las contraseñas coincidan
        if (contraseña != repetirContraseña) {
            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de descripción
        if (descripcion.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese una descripción", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Validación de selección de instrumentos
        if (!checkboxGuitarra.isChecked && !checkboxBajo.isChecked && !checkboxBateria.isChecked &&
            !checkboxTeclado.isChecked && !checkboxOtro.isChecked) {
            Toast.makeText(applicationContext, "Seleccione al menos un instrumento", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        // Proceder solo si no hay errores
        if (!hayErrores) {
            // Aquí puedes llamar a la función para registrar al usuario
            registrarUsuario(email,contraseña)
        }
    }

    private fun registrarUsuario(email: String, contraseña: String) {
        auth.createUserWithEmailAndPassword(email, contraseña)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val id: String = auth.currentUser!!.uid
                    reference = FirebaseDatabase.getInstance().reference.child("Usuario").child(id)

                    // Obtener datos del formulario
                    val nombre: String = etNombre.text.toString()
                    val apellido: String = etApellido.text.toString()
                    val descripcion: String = etDescripcion.text.toString()

                    // Crear una lista de instrumentos seleccionados
                    val instrumentos = mutableListOf<String>()

                    // Añadir instrumentos si están seleccionados
                    if (checkboxGuitarra.isChecked) instrumentos.add("Guitarra")
                    if (checkboxBajo.isChecked) instrumentos.add("Bajo")
                    if (checkboxBateria.isChecked) instrumentos.add("Batería")
                    if (checkboxTeclado.isChecked) instrumentos.add("Teclado")
                    if (checkboxOtro.isChecked) {
                        val otroInstrumento: String = etOtroInstrumento.text.toString()
                        if (otroInstrumento.isNotEmpty()) {
                            instrumentos.add(otroInstrumento)
                        }
                    }

                    // Crear un HashMap para almacenar los datos en la base de datos
                    val userMap = HashMap<String, Any>()
                    userMap["id"] = id
                    userMap["nombre"] = nombre
                    userMap["apellido"] = apellido
                    userMap["email"] = email
                    userMap["descripcion"] = descripcion
                    userMap["instrumentos"] = instrumentos // Guardar los instrumentos como un array
                    userMap["buscar"] = nombre.lowercase()
                    userMap["fotoPerfil"] = "" // la usare mas adelante
                    userMap["ubicacion"] = mapOf(
                        "latitud" to 0.0, // Valor inicial
                        "longitud" to 0.0  // Valor inicial
                    )

                    // Guardar en Firebase Realtime Database
                    reference.setValue(userMap).addOnCompleteListener { saveTask ->
                        if (saveTask.isSuccessful) {
                            Toast.makeText(applicationContext, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(applicationContext, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }



}