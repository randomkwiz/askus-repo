package es.iesnervion.avazquez.askus.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.PostModeracionDTO
import es.iesnervion.avazquez.askus.R
import inflate

class ModerationPostAdapter :
    RecyclerView.Adapter<ModerationPostAdapter.ModerationPostViewHolder>() {
    private var moderationPostList: MutableList<PostModeracionDTO> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModerationPostViewHolder {
        val itemView = parent.inflate(R.layout.moderation_row)
        return ModerationPostViewHolder(itemView)
    }

    override fun getItemCount() = moderationPostList.size
    override fun onBindViewHolder(holder: ModerationPostViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItems(postItems: MutableList<PostModeracionDTO>) {
        //checkear que el item a añadir no exista ya
        val idInTheList = moderationPostList.map { it.idPublicacion }
        postItems.removeAll {
            it.idPublicacion in idInTheList
        }
        if (postItems.isNotEmpty()) {
            moderationPostList.addAll(postItems)
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): PostModeracionDTO {
        return moderationPostList[position]
    }

    fun getLastPosition(): Int {
        return moderationPostList.lastIndex
    }

    inner class ModerationPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById(R.id.moderation_row__title) as TextView
        val text = itemView.findViewById(R.id.moderation_row__body) as TextView
        val card = itemView.findViewById(R.id.moderation_row__card) as CardView
        fun onBind(position: Int) {
            val currentItem = moderationPostList[position]
            title.text = currentItem.tituloPost
            text.text = currentItem.cuerpoPost
            if (position % 2 == 0) {
                card.setCardBackgroundColor(Color.GRAY)
            } else {
                card.setCardBackgroundColor(Color.LTGRAY)
            }
        }
    }
}