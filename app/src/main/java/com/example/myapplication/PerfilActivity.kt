package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class PerfilActivity : AppCompatActivity() {

    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvDescripcion: TextView
    private lateinit var tvInstrumentos: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private lateinit var BtnEditar:Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar variables
        tvNombre = findViewById(R.id.IdNombre)
        tvEmail = findViewById(R.id.Email)
        tvDescripcion = findViewById(R.id.Descripcion)
        tvInstrumentos = findViewById(R.id.instrumentos)
        auth = FirebaseAuth.getInstance()
        BtnEditar = findViewById(R.id.editar)

        // Obtener el ID del usuario actual
        val id = auth.currentUser?.uid

        if (id != null) {
            // Obtener referencia a la base de datos
            reference = FirebaseDatabase.getInstance().getReference("Usuario").child(id)


            reference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Obtener datos desde la base de datos
                        val nombre = snapshot.child("nombre").value.toString()
                        val email = snapshot.child("email").value.toString()
                        val descripcion = snapshot.child("descripcion").value.toString()
                        val instrumentos = snapshot.child("instrumentos").value as ArrayList<String>

                        // Mostrar los datos en los TextViews
                        tvNombre.text = nombre
                        tvEmail.text = email
                        tvDescripcion.text = descripcion
                        tvInstrumentos.text = instrumentos.joinToString(", ")
                    } else {
                        Toast.makeText(this@PerfilActivity, "No se encontraron datos de usuario", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PerfilActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No se ha iniciado sesión", Toast.LENGTH_SHORT).show()
        }

        val BtnEditar = findViewById<Button>(R.id.editar)
        BtnEditar.setOnClickListener{
            val intent = Intent(this,EditarPerfil::class.java)
            startActivity(intent)
        }

    }





}
