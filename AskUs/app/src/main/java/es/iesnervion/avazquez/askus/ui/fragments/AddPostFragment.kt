package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.fragments.tabs.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_add_post.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddPostFragment : Fragment(), View.OnClickListener {
    lateinit var viewModel: MainViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var tagsObserver: Observer<List<TagDTO>>
    var tagNames = mutableListOf<String>()
    var tagIds = mutableListOf<Int>()
    var userID: Int = 0

    companion object {
        fun newInstance(): AddPostFragment {
            return AddPostFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        sharedPreference =
            activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        userID = sharedPreference.getInt("user_id", 0)
        val userData = sharedPreference.getString("user_nickname",
            resources.getString(R.string.anonymous))
        lbl_author_nick.text = userData
        is_private.setOnClickListener(this)
        btnSend.setOnClickListener(this)
        setPrivateBtnImg()
        initObservers()
    }

    private fun initObservers() {
        tagsObserver = Observer {
            if (it.isNotEmpty()) {
                tagNames.clear()
                tagIds.clear()
                tagNames.add("Elige un tag")
                tagIds.add(0)
                for (x in it.iterator()) {
                    tagNames.add(x.nombre)
                    tagIds.add(x.id)
                }
                val adapter = context?.let { it1 ->
                    ArrayAdapter(it1,
                        android.R.layout.simple_spinner_item, tagNames)
                }
                spinner_tag_one.adapter = adapter
                spinner_tag_two.adapter = adapter
            }
        }
        viewModel.allTags().observe(viewLifecycleOwner, tagsObserver)
    }

    private fun setPrivateBtnImg() {
        if (viewModel.newPost.isPrivada ?: false) {
            is_private.setImageResource(R.drawable.ic_lock_black_24dp)
        } else {
            is_private.setImageResource(R.drawable.ic_lock_open_black_24dp)
        }
        is_private.scaleType = ImageView.ScaleType.FIT_END
    }

    fun fieldsAreFilled(): Boolean {
        return input_title_post.text.isNotEmpty() &&
                input_title_body.text.isNotEmpty() &&
                spinner_tag_one.selectedItemPosition > 0
    }

    fun setFieldsToViewModel() {
        viewModel.newPost.idAutor = userID
        viewModel.newPost.id = 0
        viewModel.newPost.fechaCreacion = getFormatedDateTime()
        viewModel.newPost.fechaPublicacion = getFormatedDateTime()
        viewModel.newPost.texto = input_title_body.text.toString()
        viewModel.newPost.titulo = input_title_post.text.toString()
        val idTagOne = tagIds[spinner_tag_one.selectedItemPosition]
        val idTagTwo = tagIds[spinner_tag_two.selectedItemPosition]
        if (idTagTwo == 0) {
            viewModel.tagList = listOf(idTagOne)
        } else {
            viewModel.tagList = listOf(idTagOne, idTagTwo)
        }
    }

    private fun getFormatedDateTime(): String {
        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        val formatedDate = formatter.format(date)
        return formatedDate
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.is_private -> {
                viewModel.newPost.isPrivada = !(viewModel.newPost.isPrivada ?: false)
                setPrivateBtnImg()
            }
            R.id.btnSend -> {
                if (fieldsAreFilled()) {
                    setFieldsToViewModel()
                    viewModel.sendNewPost()
                    Toast.makeText(context, getString(R.string.post_sended), Toast.LENGTH_LONG)
                        .show()
                    if (context is HomeActivityCallback) {
                        (context as HomeActivityCallback).onPostAdded()
                    }
                } else {
                    Toast.makeText(context,
                        resources.getText(R.string.fillFields),
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
