package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CrearBandaActivity : AppCompatActivity() {
    private lateinit var Nombre: EditText
    private lateinit var Genero: EditText
    private lateinit var Descripcion: EditText
    private lateinit var SelImagen: Button
    private lateinit var crearBtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_banda)

        IniciarVariables()
        SelImagen.setOnClickListener {
            seleccionarFoto()
        }
        crearBtn.setOnClickListener {
            ValidarBanda()
        }
    }

    private fun ValidarBanda() {
        val nombre: String = Nombre.text.toString()
        val genero: String = Genero.text.toString()
        val descripcion: String = Descripcion.text.toString()
        var hayErrores = false

        if (nombre.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese el nombre de su banda", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }
        if (genero.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese el genero que toca su banda", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }
        if (descripcion.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese la descripcion de su banda", Toast.LENGTH_SHORT).show()
            hayErrores = true
        }

        if (!hayErrores) {
            registrarBanda(nombre, genero, descripcion)
        }
    }

    private fun registrarBanda(nombre: String, genero: String, descripcion: String) {
        val bandaId = reference.push().key // Generar un ID único para la banda
        val userId = auth.currentUser?.uid
        val integrantes = mutableListOf<String>() // Inicialmente vacío, luego puedes agregar usuarios

        if (bandaId != null && userId != null) {
            val userMap = HashMap<String, Any>()
            userMap["IdBanda"] = bandaId
            userMap["Lider"] = userId
            userMap["NombreBanda"] = nombre
            userMap["Genero"] = genero
            userMap["descripcionDeBanda"] = descripcion
            userMap["integrantes"] = integrantes

            // Guardar la banda en Firebase
            reference.child(bandaId).setValue(userMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Banda creada exitosamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al crear la banda", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Error al obtener ID de banda o usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun seleccionarFoto() {
        TODO("Not yet implemented")
    }

    private fun IniciarVariables() {
        Nombre = findViewById(R.id.BandaNombre)
        Genero = findViewById(R.id.BandaGenero)
        Descripcion = findViewById(R.id.BandaDescripcion)
        SelImagen = findViewById(R.id.btnSeleccionarImagen)
        crearBtn = findViewById(R.id.btnCrearBanda)
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("Bandas")
    }
}
