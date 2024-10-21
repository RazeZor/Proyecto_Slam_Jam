package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val nombre = findViewById<EditText>(R.id.etNombre)
        val apellido = findViewById<EditText>(R.id.etApellido)
        val email = findViewById<EditText>(R.id.etEmail)
        val direccion = findViewById<EditText>(R.id.etDireccion)
        val descripcion = findViewById<EditText>(R.id.etDescripcion)
        val checkboxGuitarra = findViewById<CheckBox>(R.id.checkboxGuitarra)
        val checkboxBajo = findViewById<CheckBox>(R.id.checkboxBajo)
        val checkboxBateria = findViewById<CheckBox>(R.id.checkboxBateria)
        val checkboxTeclado = findViewById<CheckBox>(R.id.checkboxTeclado)
        val checkboxOtro = findViewById<CheckBox>(R.id.checkboxOtro)
        val otroInstrumento = findViewById<EditText>(R.id.etOtroInstrumento)
        val btnRegistrarse = findViewById<Button>(R.id.btnRegistrarse)

        btnRegistrarse.setOnClickListener {
            // Lógica para manejar el registro del usuario
            val nombreValue = nombre.text.toString()
            val apellidoValue = apellido.text.toString()
            val emailValue = email.text.toString()
            val direccionValue = direccion.text.toString()
            val descripcionValue = descripcion.text.toString()
            val instrumentos = mutableListOf<String>()

            if (checkboxGuitarra.isChecked) instrumentos.add("Guitarra")
            if (checkboxBajo.isChecked) instrumentos.add("Bajo")
            if (checkboxBateria.isChecked) instrumentos.add("Batería")
            if (checkboxTeclado.isChecked) instrumentos.add("Teclado")
            if (checkboxOtro.isChecked) {
                val otroInstrumentoValue = otroInstrumento.text.toString()
                if (otroInstrumentoValue.isNotEmpty()) instrumentos.add(otroInstrumentoValue)
            }

            // Aquí puedes agregar la lógica para guardar los datos en Firebase
        }
    }
}