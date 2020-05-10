package es.iesnervion.avazquez.askus.adapters

import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import inflate

class PostAdapter(posts: List<PostCompletoParaMostrarDTO>,
    listener: RecyclerViewClickListener)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>()
    , View.OnClickListener {
    private lateinit var listener: View.OnClickListener
    private var recyclerViewClickListener: RecyclerViewClickListener = listener
    private var posts: List<PostCompletoParaMostrarDTO> = ArrayList()

    init {
        this.posts = posts
    }

    inner class PostViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title = itemView.findViewById(R.id.lbl_post_title) as TextView
        val text = itemView.findViewById(R.id.lbl_post_text) as TextView
        val nComments = itemView.findViewById(R.id.lbl_comments_quantity) as TextView
        val author = itemView.findViewById(R.id.lbl_author_nick) as TextView
        val tagList = itemView.findViewById(R.id.lbl_tag_lists) as TextView
        val upvotes = itemView.findViewById(R.id.upvotes_count) as TextView
        val downvotes = itemView.findViewById(R.id.downvotes_count) as TextView
        val arrowUp = itemView.findViewById(R.id.arrow_up) as ImageButton
        val arrowDown = itemView.findViewById(R.id.arrow_down) as ImageButton

        init {
            arrowUp.setOnClickListener(this)
            arrowDown.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onClick(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = parent.inflate(R.layout.post_row)
        return PostViewHolder(itemView, recyclerViewClickListener)
    }

    override fun getItemCount() = posts.size
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.text.movementMethod = ScrollingMovementMethod.getInstance()
        holder.author.text = currentPost.nickAutor
        holder.text.text = currentPost.cuerpoPost
        holder.title.text = currentPost.tituloPost
        holder.nComments.text = currentPost.cantidadComentarios.toString()
        holder.tagList.text = currentPost.listadoTags.joinToString()
        holder.upvotes.text = currentPost.cantidadVotosPositivos.toString()
        holder.downvotes.text = currentPost.cantidadVotosNegativos.toString()
    }

    override fun onClick(v: View) {
        listener.onClick(v)
    }
}