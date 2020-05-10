package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
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
import es.iesnervion.avazquez.askus.utils.UtilClass.Companion.getFormattedCurrentDatetime
import kotlinx.android.synthetic.main.fragment_add_post.*
import setVisibilityToGone
import setVisibilityToVisible

/**
 * A simple [Fragment] subclass.
 */
class AddPostFragment : Fragment(), View.OnClickListener {
    lateinit var viewModel: MainViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var tagsObserver: Observer<List<TagDTO>>
    lateinit var postSentObserver: Observer<Int>
    lateinit var errorObserver: Observer<Boolean>
    var tagNames = mutableListOf<String>()
    var tagIds = mutableListOf<Int>()
    var userID: Int = 0

    companion object {
        fun newInstance(idTagUserWasSeeing: Int): AddPostFragment {
            val myFragment = AddPostFragment()
            val args = Bundle()
            args.putInt("idTag", idTagUserWasSeeing)
            myFragment.arguments = args
            return myFragment
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
        spinner_tag_two.isEnabled = false
        spinner_tag_one.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long) {
                spinner_tag_two.isEnabled = position != 0
                if (!spinner_tag_two.isEnabled) {
                    spinner_tag_two.setSelection(0)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                spinner_tag_two.isEnabled = false
            }
        }
    }

    private fun initObservers() {
        tagsObserver = Observer {
            if (it.isNotEmpty()) {
                tagNames.clear()
                tagIds.clear()
                tagNames.add(resources.getString(R.string.spinner_select_category))
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

        postSentObserver = Observer {
            if (it == 204) {
                Toast.makeText(context, getString(R.string.post_sended), Toast.LENGTH_SHORT)
                    .show()
                if (context is HomeActivityCallback) {
                    (context as HomeActivityCallback)
                        .onPostAdded(arguments?.getInt("idTag") ?: 0)
                }
            } else {
                Toast.makeText(context, getString(R.string.error_sending_post), Toast.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.responseCodePostSent().observe(viewLifecycleOwner, postSentObserver)

        errorObserver = Observer {
            if (it) {
                Toast.makeText(context, getString(R.string.error_internet), Toast.LENGTH_LONG)
                    .show()
            }
        }
        viewModel.getError().observe(viewLifecycleOwner, errorObserver)
    }

    private fun setPrivateBtnImg() {
        if (viewModel.newPost.isPrivada ?: false) {
            is_private.setImageResource(R.drawable.ic_lock_black_24dp)
        } else {
            is_private.setImageResource(R.drawable.ic_lock_open_black_24dp)
        }
        is_private.scaleType = ImageView.ScaleType.FIT_END
    }

    private fun fieldsAreFilled(): Boolean {
        return input_title_post.text.isNotEmpty() &&
                input_title_body.text.isNotEmpty() &&
                spinner_tag_one.selectedItemPosition > 0
    }

    private fun setFieldsToViewModel() {
        viewModel.newPost.idAutor = userID
        viewModel.newPost.id = 0
        viewModel.newPost.fechaCreacion = getFormattedCurrentDatetime()
        viewModel.newPost.fechaPublicacion = getFormattedCurrentDatetime()
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
    override fun onClick(v: View) {
        when (v.id) {
            R.id.is_private -> {
                viewModel.newPost.isPrivada = !(viewModel.newPost.isPrivada ?: false)
                setPrivateBtnImg()
            }
            R.id.btnSend -> {
                lbl_select_one_category.setVisibilityToGone()
                if (fieldsAreFilled()) {
                    setFieldsToViewModel()
                    viewModel.sendNewPost()
                } else {
                    if (spinner_tag_one.selectedItemPosition == 0) {
                        lbl_select_one_category.setVisibilityToVisible()
                    }
                    Toast.makeText(context,
                        resources.getText(R.string.fillFields),
                        Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }
}
