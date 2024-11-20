//package com.example.myapplication
//
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.database.*
//
//class VerIntegrantesActivity : AppCompatActivity() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: IntegrantesAdapter
//    private lateinit var textViewNoIntegrantes: TextView
//    private lateinit var databaseReference: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_ver_integrantes)
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        recyclerView = findViewById(R.id.recyclerViewIntegrantes)
//        textViewNoIntegrantes = findViewById(R.id.textViewNoIntegrantes)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        // Obtén el ID de la banda desde el intent
//        val bandaId = intent.getStringExtra("BANDA_ID")
//        if (bandaId != null) {
//            cargarIntegrantes(bandaId)
//        } else {
//            Toast.makeText(this, "Error: ID de banda no encontrado", Toast.LENGTH_SHORT).show()
//            finish()
//        }
//    }
//
//    private fun cargarIntegrantes(bandaId: String) {
//        // Referencia a la base de datos
//        databaseReference = FirebaseDatabase.getInstance()
//            .getReference("Bandas")
//            .child(bandaId)
//            .child("miembros")
//
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val integrantes = mutableListOf<Integrante>()
//                for (integranteSnapshot in snapshot.children) {
//                    val integrante = integranteSnapshot.getValue(Integrante::class.java)
//                    if (integrante != null) {
//                        integrantes.add(integrante)
//                    }
//                }
//
//                if (integrantes.isEmpty()) {
//                    textViewNoIntegrantes.visibility = View.VISIBLE
//                } else {
//                    textViewNoIntegrantes.visibility = View.GONE
//                }
//
//                adapter = IntegrantesAdapter(integrantes)
//                recyclerView.adapter = adapter
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@VerIntegrantesActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//}
