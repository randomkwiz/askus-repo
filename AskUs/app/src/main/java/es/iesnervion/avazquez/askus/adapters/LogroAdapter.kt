package es.iesnervion.avazquez.askus.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.LogroDTO
import es.iesnervion.avazquez.askus.R
import inflate

class LogroAdapter(val listaLogrosOriginal: List<LogroDTO>,
    val listaLogrosConseguidos: List<Int>? = listOf(0)) :
    RecyclerView.Adapter<LogroAdapter.LogroViewHolder>() {
    private val listaOriginal = listaLogrosOriginal.toList()
    private var listaConseguidos: List<Int> = listaLogrosConseguidos?.toList() ?: listOf(0)

    class LogroViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val nombre = item.findViewById(R.id.logro_name) as TextView
        val img = item.findViewById(R.id.logro_img) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogroViewHolder {
        val itemView = parent.inflate(R.layout.logro_row)
        return LogroViewHolder(itemView)
    }

    override fun getItemCount() = listaOriginal.size
    override fun onBindViewHolder(holder: LogroViewHolder, position: Int) {
        val currentItem = this.listaOriginal[position]
        holder.nombre.text = currentItem.nombre
        if (currentItem.id in listaConseguidos) {
            holder.img.setBackgroundResource(R.drawable.cup)
        } else {
            holder.img.setBackgroundResource(R.drawable.askcom)
        }
    }
}