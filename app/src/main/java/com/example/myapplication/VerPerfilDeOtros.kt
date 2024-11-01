package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class VerPerfilDeOtros : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvInstrumentos: TextView
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil_de_otros)

        // Inicializar variables
        tvNombre = findViewById(R.id.VERIdNombre)
        tvEmail = findViewById(R.id.VEREmail)
        tvDescripcion = findViewById(R.id.VERDescripcion)
        tvInstrumentos = findViewById(R.id.VERinstrumentos)

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
                    tvInstrumentos.text = instrumentos.joinToString(", ")

                    Log.d("VerPerfilDeOtros", "Nombre: $nombre, Email: $email, Descripción: $descripcion, Instrumentos: $instrumentos")
                } else {
                    Toast.makeText(this@VerPerfilDeOtros, "No se encontraron datos de usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@VerPerfilDeOtros, "Error al cargar los datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}
