package com.example.myapplication

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BandasAdapter(private val bandas: List<Banda>, private val onClick: (Banda) -> Unit) :
    RecyclerView.Adapter<BandasAdapter.BandaViewHolder>() {

    inner class BandaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombreBanda)
        private val buttonPerfil: Button = itemView.findViewById(R.id.buttonPerfilBanda)

        fun bind(banda: Banda) {

            textViewNombre.text = banda.nombreBanda
            buttonPerfil.setOnClickListener{
                val context = itemView.context
                val intent = Intent(context, PerfilBandaActivity::class.java).apply{
                    putExtra("BANDA_ID", banda.idBanda)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BandaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banda, parent, false)
        return BandaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BandaViewHolder, position: Int) {
        val banda = bandas[position]
        holder.bind(banda)
    }

    override fun getItemCount(): Int {
        return bandas.size
    }
}

