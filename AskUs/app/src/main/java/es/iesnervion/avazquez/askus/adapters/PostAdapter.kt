package es.iesnervion.avazquez.askus.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abdulhakeem.seemoretextview.SeeMoreTextView
import es.iesnervion.avazquez.askus.DTOs.PublicacionDTO
import es.iesnervion.avazquez.askus.R
import kotlinx.android.synthetic.main.post_row.view.*
import kotlin.random.Random

class PostAdapter(posts: List<PublicacionDTO>, private val context: Context)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var posts: List<PublicacionDTO> = ArrayList()

    init {
        this.posts = posts
    }

    inner class PostViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById(R.id.lbl_post_title) as TextView
        val text = itemView.findViewById(R.id.lbl_post_text) as TextView
        val nComments = itemView.findViewById(R.id.lbl_comments_quantity) as TextView
        val author = itemView.findViewById(R.id.lbl_author_nick) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.post_row, parent, false)
        //TODO("Hacer que esto del see more / see less funcione")
        //itemView.lbl_post_text.setTextMaxLength(100)
        return PostViewHolder(itemView)
    }

    override fun getItemCount() = posts.size
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = posts[position];
        holder.author.text = currentPost.idAutor.toString()
        holder.text.text = currentPost.texto
        holder.title.text = currentPost.titulo
        //TODO cambiar esto
        holder.nComments.text = Random.nextInt(0,10000).toString()

    }
}