package es.iesnervion.avazquez.askus.adapters

import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.ComentarioParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import inflate

class CommentsAdapter(comments: List<ComentarioParaMostrarDTO>
) : RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    private var comments: List<ComentarioParaMostrarDTO> = ArrayList()

    init {
        this.comments = comments
    }

    inner class CommentViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById(R.id.lbl_comment_title) as TextView
        val text = itemView.findViewById(R.id.lbl_comment_text) as TextView
        val author = itemView.findViewById(R.id.lbl_author_nick) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = parent.inflate(R.layout.comment_row)
        return CommentViewHolder(itemView)
    }

    override fun getItemCount() = comments.size
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentComment = comments[position]
        holder.text.movementMethod = ScrollingMovementMethod.getInstance()
        holder.author.text = currentComment.nickAutor
        holder.text.text = currentComment.texto
        holder.title.text = currentComment.titulo
    }
}