package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerPerfilDeOtros : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvInstrumentos: TextView
    private lateinit var reference: DatabaseReference
    private lateinit var BtnInvi: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil_de_otros)

        // Inicializar variables
        tvNombre = findViewById(R.id.VERIdNombre)
        tvEmail = findViewById(R.id.VEREmail)
        tvDescripcion = findViewById(R.id.VERDescripcion)
        tvInstrumentos = findViewById(R.id.VERinstrumentos)
        BtnInvi= findViewById(R.id.BtnInvitar)

        // Obtener el ID del usuario que se desea ver
        val userId = intent.getStringExtra("id") ?: return
        Log.d("VerPerfilDeOtros", "User ID recibido: $userId")
        reference = FirebaseDatabase.getInstance().getReference("Usuario").child(userId)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val nombre = snapshot.child("nombre").value?.toString() // Cambié a ?.toString() para evitar NPE
                    val email = snapshot.child("email").value?.toString() // Cambié a ?.toString() para evitar NPE
                    val descripcion = snapshot.child("descripcion").value?.toString() // Cambié a ?.toString() para evitar NPE
                    val instrumentos = snapshot.child("instrumentos").value as? List<String> ?: emptyList()

                    tvNombre.text = nombre
                    tvEmail.text = email
                    tvDescripcion.text = descripcion
                    tvInstrumentos.text = instrumentos.joinToString(",")

                    Log.d("VerPerfilDeOtros", "Nombre: $nombre, Email: $email, Descripción: $descripcion, Instrumentos: $instrumentos")
                } else {
                    Toast.makeText(this@VerPerfilDeOtros, "No se encontraron datos de usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPerfilDeOtros, "Error al cargar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
        findViewById<Button>(R.id.BtnInvitar).setOnClickListener {
            DialogosDeBanda()
        }


    }

    private fun DialogosDeBanda() {
        // Llama a ObtenerBanda para obtener las bandas del usuario actual
        ObtenerBanda { listaBandas ->
            val nombresBandas = listaBandas.map { it.nombreBanda } // Mapea los nombres de las bandas
            val opcionesBandas = nombresBandas.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Selecciona una banda para invitar")
                .setItems(opcionesBandas) { _, which ->
                    val bandaSeleccionada = listaBandas[which]
                    InvitarUsuarioABanda(bandaSeleccionada)
                }
                .show()
        }
    }

    private fun InvitarUsuarioABanda(banda: Banda) {
        val userId = intent.getStringExtra("id") ?: return // ID del usuario que se está viendo en el perfil
        val bandaRef = FirebaseDatabase.getInstance().getReference("Bandas").child(banda.idBanda)

        // Verificar si el usuario ya es integrante
        bandaRef.child("integrantes").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "El usuario ya es integrante de la banda", Toast.LENGTH_SHORT).show()
            } else {
                // Agregar el ID del usuario a la lista de integrantes de la banda
                bandaRef.child("integrantes").child(userId).setValue(true)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Usuario invitado a la banda ${banda.nombreBanda}", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error al invitar a la banda: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al verificar el usuario: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ObtenerBanda(callback: (List<Banda>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val bandasRef = FirebaseDatabase.getInstance().getReference("Bandas")

        bandasRef.orderByChild("integrantes/$userId").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Filtra y convierte a objetos Banda solo los nodos que tienen al usuario como integrante
                    val listaBandas = snapshot.children.mapNotNull { it.getValue(Banda::class.java) }
                    callback(listaBandas)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@VerPerfilDeOtros, "Error al cargar bandas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



}
