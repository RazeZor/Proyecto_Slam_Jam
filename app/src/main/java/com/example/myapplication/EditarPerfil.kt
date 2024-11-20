package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditarPerfil : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var checkboxGuitarra: CheckBox
    private lateinit var checkboxBajo: CheckBox
    private lateinit var checkboxBateria: CheckBox
    private lateinit var checkboxTeclado: CheckBox
    private lateinit var checkboxOtro: CheckBox
    private lateinit var etOtroInstrumento: EditText
    private lateinit var btnGuardar: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        reference = FirebaseDatabase.getInstance().getReference("Usuario").child(id!!)

        // Inicializar vistas
        inicializarVaribles()

        // Cargar datos existentes
        cargarDatos()

        // Botón para guardar los cambios
        btnGuardar.setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarDatos() {
        reference.get().addOnSuccessListener { snapshot ->
            etNombre.setText(snapshot.child("nombre").value.toString())
            etApellido.setText(snapshot.child("apellido").value.toString())
            etDescripcion.setText(snapshot.child("descripcion").value.toString())
            val instrumentos = snapshot.child("instrumentos").value as? List<String> ?: emptyList()

            // Configurar los checkboxes según los instrumentos
            checkboxGuitarra.isChecked = instrumentos.contains("Guitarra")
            checkboxBajo.isChecked = instrumentos.contains("Bajo")
            checkboxBateria.isChecked = instrumentos.contains("Batería")
            checkboxTeclado.isChecked = instrumentos.contains("Teclado")
            if (instrumentos.any { it !in listOf("Guitarra", "Bajo", "Batería", "Teclado") }) {
                checkboxOtro.isChecked = true
                etOtroInstrumento.setText(instrumentos.find { it !in listOf("Guitarra", "Bajo", "Batería", "Teclado") })
            }

        }
    }
    private fun inicializarVaribles(){
        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etDescripcion = findViewById(R.id.etDescripcion)
        checkboxGuitarra = findViewById(R.id.checkboxGuitarra)
        checkboxBajo = findViewById(R.id.checkboxBajo)
        checkboxBateria = findViewById(R.id.checkboxBateria)
        checkboxTeclado = findViewById(R.id.checkboxTeclado)
        checkboxOtro = findViewById(R.id.checkboxOtro)
        etOtroInstrumento = findViewById(R.id.etOtroInstrumento)
        btnGuardar = findViewById(R.id.btnGuardar)
    }

    private fun guardarCambios() {
        // Obtener los valores editados
        val nombre = etNombre.text.toString()
        val apellido = etApellido.text.toString()
        val descripcion = etDescripcion.text.toString()
        val instrumentos = mutableListOf<String>()
        if (checkboxGuitarra.isChecked) instrumentos.add("Guitarra")
        if (checkboxBajo.isChecked) instrumentos.add("Bajo")
        if (checkboxBateria.isChecked) instrumentos.add("Batería")
        if (checkboxTeclado.isChecked) instrumentos.add("Teclado")
        if (checkboxOtro.isChecked) {
            val otroInstrumento = etOtroInstrumento.text.toString()
            if (otroInstrumento.isNotEmpty()) instrumentos.add(otroInstrumento)
        }

        // Crear un HashMap para actualizar en Firebase
        val userMap = mapOf(
            "nombre" to nombre,
            "apellido" to apellido,
            "descripcion" to descripcion,
            "instrumentos" to instrumentos
        )

        // Guardar los cambios en Firebase
        reference.updateChildren(userMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intento = Intent(this,PerfilActivity::class.java)
                startActivity(intento)
                finish() // Cierra la actividad después de guardar
            } else {
                Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
