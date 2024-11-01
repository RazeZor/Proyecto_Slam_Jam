package com.example.myapplication

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class VerBandaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BandasAdapter
    private lateinit var reference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var textViewNoBandas: TextView
    private lateinit var ButonIrPerfil:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_banda)

        // Inicializa Firebase
        auth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().getReference("Bandas")

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        textViewNoBandas = findViewById(R.id.textViewNoBandas)


        // Cargar bandas
        cargarBandas()
    }

    private fun cargarBandas() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            reference.orderByChild("lider").equalTo(userId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bandas = mutableListOf<Banda>()
                    for (bandasSnapshot in snapshot.children) {
                        val banda = bandasSnapshot.getValue(Banda::class.java)
                        if (banda != null) {
                            bandas.add(banda)
                        }
                    }

                    adapter = BandasAdapter(bandas) { banda ->
                        // Manejar clic en el botón "Perfil Banda"
                        Toast.makeText(this@VerBandaActivity, "Perfil de ${banda.nombreBanda}", Toast.LENGTH_SHORT).show()
                        // Aquí puedes navegar a la actividad del perfil de la banda
                    }

                    recyclerView.adapter = adapter

                    // Mostrar/ocultar mensaje si no hay bandas
                    if (bandas.isEmpty()) {
                        textViewNoBandas.visibility = View.VISIBLE
                    } else {
                        textViewNoBandas.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@VerBandaActivity, "Error al cargar bandas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
        }
    }
}
