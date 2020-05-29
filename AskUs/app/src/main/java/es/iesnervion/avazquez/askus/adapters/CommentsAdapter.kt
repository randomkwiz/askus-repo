package es.iesnervion.avazquez.askus.adapters

import android.graphics.Color
import android.os.Build
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.iesnervion.avazquez.askus.DTOs.ComentarioParaMostrarDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.adapters.viewholders.BaseViewHolder
import es.iesnervion.avazquez.askus.adapters.viewholders.ProgressHolder
import es.iesnervion.avazquez.askus.interfaces.RecyclerViewClickListener
import inflate

class CommentsAdapter(private val idAutorPost: Int, listener: RecyclerViewClickListener) :
    RecyclerView.Adapter<BaseViewHolder>(), View.OnClickListener {

    private var comments: MutableList<ComentarioParaMostrarDTO> = mutableListOf()
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private lateinit var listener: View.OnClickListener
    private var recyclerViewClickListener: RecyclerViewClickListener = listener

    var isLoaderVisible = false
    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            val pos = (comments.size - 1)
            if (position == pos) VIEW_TYPE_LOADING
            else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    inner class CommentViewHolder(itemView: View, val listener: RecyclerViewClickListener) :
        BaseViewHolder(itemView), View.OnClickListener {
        val title = itemView.findViewById(R.id.lbl_comment_title) as TextView
        val text = itemView.findViewById(R.id.lbl_comment_text) as TextView
        val author = itemView.findViewById(R.id.lbl_author_nick) as TextView
        val container = itemView.findViewById(R.id.comment_row_layout) as LinearLayout
        override fun onBind(position: Int) {
            val currentComment = comments[position]
            text.movementMethod = ScrollingMovementMethod.getInstance()
            author.text = currentComment.nickAutor
            text.text = currentComment.texto
            title.text = currentComment.titulo
            if (position == 0 && currentComment.idUsuario == idAutorPost) {
                container.setBackgroundColor(Color.LTGRAY)
            }
        }

        init {
            author.setOnClickListener(this)
        }
        override fun clear() {
        }

        override fun onClick(v: View) {
            listener.onClick(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val itemView: View?
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                itemView = parent.inflate(R.layout.comment_row)
                CommentViewHolder(itemView, recyclerViewClickListener)
            }
            else             -> {
                itemView = parent.inflate(R.layout.loading_row)
                ProgressHolder(itemView)
            }
        }
    }

    override fun getItemCount() = comments.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    fun addItems(postItems: MutableList<ComentarioParaMostrarDTO>) {
        //checkear que el item a añadir no exista ya
        val idInTheList = comments.map { it.id }
        postItems.removeAll {
            it.id in idInTheList
        }
        if (postItems.isNotEmpty()) {
            comments.addAll(postItems)
            notifyDataSetChanged()
        }
    }

    fun addLoading() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            comments.removeIf {
                it.id == 0
            }
        }
        isLoaderVisible = true
        comments.add(ComentarioParaMostrarDTO(id = 0, idPublicacion = 0, fechaPublicacion = "",
            idComentarioAlQueResponde = 0, idUsuario = 0, isBanned = true, isBorrado = true,
            texto = "", titulo = "", nickAutor = ""))
        val pos = (comments.size - 1)
        notifyItemInserted(pos)
    }

    fun removeLoading() {
        isLoaderVisible = false
        val position: Int = (comments.size - 1)
        if (position >= 0) {
            val item = getItem(position)
            if (item.id == 0) {
                comments.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun clear() {
        comments.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ComentarioParaMostrarDTO {
        return comments[position]
    }

    override fun onClick(v: View) {
        listener.onClick(v)
    }
}