package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class PerfilBandaActivity : AppCompatActivity() {
    private lateinit var EditarBanda : Button
    private lateinit var EliminarIntegrante : Button
    private lateinit var integrantes:TextView
    private lateinit var genero:TextView
    private lateinit var DescripcionBanda:TextView
    private lateinit var Nombre:TextView
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_banda)
        IniciarVar()
        cargarDatos()

        EditarBanda.setOnClickListener {

        }

        EliminarIntegrante.setOnClickListener {

        }

    }

    private fun IniciarVar(){
        EditarBanda = findViewById(R.id.editar)
        EliminarIntegrante=findViewById(R.id.Eliminar)
        genero = findViewById(R.id.Genero)
        integrantes = findViewById(R.id.Integrantes)
        DescripcionBanda=findViewById(R.id.Descripcion)
        Nombre = findViewById(R.id.IdNombre)


    }

    private fun cargarDatos() {
        // Obtener el ID de la banda del Intent
        val bandaId = intent.getStringExtra("BANDA_ID")

        if (bandaId != null) {
            reference = FirebaseDatabase.getInstance().getReference("Bandas").child(bandaId)

            reference.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Mapear los datos a un objeto Banda
                    val banda = snapshot.getValue(Banda::class.java)

                    // Asignar los datos a los TextViews
                    if (banda != null) {
                        Nombre.text = banda.nombreBanda
                        DescripcionBanda.text = banda.descripcion
                        genero.text = banda.genero
                        obtenerNombresIntegrantes(banda.integrantes)
                    }
                } else {
                    // Manejar el caso en que no se encuentra la banda
                    Toast.makeText(this, "Banda no encontrada", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerNombresIntegrantes(ids: List<String>) {
        val nombres = mutableListOf<String>()
        val referenceUsuarios = FirebaseDatabase.getInstance().getReference("Usuario")
        val totalIntegrantes = ids.size
        var contador = 0

        for (id in ids) {
            referenceUsuarios.child(id).child("nombre").get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val nombre = snapshot.value.toString()
                    nombres.add(nombre)
                }
                contador++
                if (contador == totalIntegrantes) {
                    // Todos los nombres han sido recuperados
                    integrantes.text = "Integrantes: ${nombres.joinToString(", ")}"
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Error al cargar nombres de integrantes", Toast.LENGTH_SHORT).show()
            }
        }
    }



}