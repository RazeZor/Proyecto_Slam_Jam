package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecuperarPassword : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_password)

        val textoEmail: EditText = findViewById(R.id.etEmail)
        val btnRecuperar: Button = findViewById(R.id.btnRecuperar)

        firebaseAuth = FirebaseAuth.getInstance()

        btnRecuperar.setOnClickListener {
            val email = textoEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                recuperarContraseña(email)
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun recuperarContraseña(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Se ha enviado un correo para restablecer la contraseña", Toast.LENGTH_SHORT).show()
                    // Puedes navegar a otra actividad o volver a la pantalla de inicio
                } else {
                    Toast.makeText(this, "Error al enviar el correo: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
