package es.iesnervion.avazquez.askus.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.PostCompletoParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.viewholders.BaseViewHolder
import es.iesnervion.avazquez.askus.adapters.viewholders.ProgressHolder
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import inflate

class PostAdapter(listener: RecyclerViewClickListener) : RecyclerView.Adapter<BaseViewHolder>(),
    View.OnClickListener {
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private lateinit var listener: View.OnClickListener
    private var recyclerViewClickListener: RecyclerViewClickListener = listener
    private var posts: MutableList<PostCompletoParaMostrarDTO> = mutableListOf()
    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            val pos = (posts.size - 1)
            if (position == pos) VIEW_TYPE_LOADING
            else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        var itemView: View?
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                itemView = parent.inflate(R.layout.post_row)
                PostViewHolder(itemView, recyclerViewClickListener)
            }
            else             -> {
                itemView = parent.inflate(R.layout.loading_row)
                ProgressHolder(itemView)
            }
        }
    }

    override fun getItemCount() = posts.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onClick(v: View) {
        listener.onClick(v)
    }

    fun addItems(postItems: MutableList<PostCompletoParaMostrarDTO>) {
        //checkear que el item a añadir no exista ya
        val idInTheList = posts.map { it.IdPost }
        postItems.removeAll {
            it.IdPost in idInTheList
        }
        if (postItems.isNotEmpty()) {
            posts.addAll(postItems)
            notifyDataSetChanged()
        }
    }

    fun addLoading() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            posts.removeIf {
                it.IdPost == 0
            }
        }
        isLoaderVisible = true
        posts.add(
            PostCompletoParaMostrarDTO(0, 0, "", "", "", 0, "", "", 0, 0, false, listOf(), null))
        val pos = (posts.size - 1)
        notifyItemInserted(pos)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = (posts.size - 1)
        if (position >= 0 && getItem(position).IdPost == 0) {
            posts.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        posts.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PostCompletoParaMostrarDTO {
        return posts[position]
    }

    inner class PostViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        View.OnClickListener, BaseViewHolder(itemView) {
        val title = itemView.findViewById(R.id.lbl_post_title_post_row) as TextView
        val text = itemView.findViewById(R.id.lbl_post_text_post_row) as TextView
        val nComments = itemView.findViewById(R.id.lbl_comments_quantity) as TextView
        val author = itemView.findViewById(R.id.lbl_author_nick_post_row) as TextView
        val tagList = itemView.findViewById(R.id.lbl_tag_lists_post_row) as TextView
        val upvotes = itemView.findViewById(R.id.upvotes_count) as TextView
        val downvotes = itemView.findViewById(R.id.downvotes_count) as TextView
        val arrowUp = itemView.findViewById(R.id.arrow_up) as ImageButton
        val arrowDown = itemView.findViewById(R.id.arrow_down) as ImageButton
        val cardView = itemView.findViewById(R.id.element_card_view) as CardView

        init {
            //cardView.setOnClickListener(this)
            arrowUp.setOnClickListener(this)
            arrowDown.setOnClickListener(this)
            title.setOnClickListener(this)
            text.setOnClickListener(this)
            author.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            listener.onClick(v, adapterPosition)
        }

        override fun clear() {
        }

        override fun onBind(position: Int) {
            //        super.onBind(position)
            val currentPost = posts[position]
            text.movementMethod = ScrollingMovementMethod.getInstance()
            author.text = currentPost.nickAutor
            text.text = currentPost.cuerpoPost.replace("(?m)(^ *| +(?= |$))".toRegex(), "")
                .replace("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+".toRegex(), "$1")
            title.text = currentPost.tituloPost.replace("(?m)(^ *| +(?= |$))".toRegex(), "")
                .replace("(?m)^$([\r\n]+?)(^$[\r\n]+?^)+".toRegex(), "$1")
            nComments.text = currentPost.cantidadComentarios.toString()
            tagList.text = currentPost.listadoTags.joinToString()
            upvotes.text = currentPost.cantidadVotosPositivos.toString()
            downvotes.text = currentPost.cantidadVotosNegativos.toString()
            //Primero limpia las casillas
            ImageViewCompat.setImageTintList(arrowUp, null)
            ImageViewCompat.setImageTintList(arrowDown, null)
            //Luego si es necesario pone los tintes
            if (currentPost.votoDeUsuarioLogeado != null) {
                if (currentPost.votoDeUsuarioLogeado.valoracion) {
                    //es positiva
                    ImageViewCompat.setImageTintList(arrowUp,
                        ColorStateList.valueOf(Color.parseColor("#0C8C00")))
                } else {
                    //es negativa
                    ImageViewCompat.setImageTintList(arrowDown,
                        ColorStateList.valueOf(Color.parseColor("#FF0000")))
                }
            }

        }
    }
}