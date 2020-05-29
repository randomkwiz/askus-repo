package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.fragments.tabs.all.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment(), View.OnClickListener {
    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    lateinit var viewModel: MainViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    var isDarkModeOn = false
    var idMainTagSelected = 0
    var tagNames = mutableListOf<String>()
    var tagIds = mutableListOf<Int>()
    override fun onCreateView(inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
        initListeners()
        initObservers()
        spinnerOnItemSelected()
        idMainTagSelected = sharedPreference.getInt("idTagToShowWhenAppIsOpened", 0)
        isDarkModeOn = sharedPreference.getBoolean("isDarkModeEnabled", false)

        //Para que al iniciar se ponga correctamente
        //Theme
        if (isDarkModeOn) {
            settings__switch_dark_mode.isChecked = true
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        } else {
            settings__switch_dark_mode.isChecked = false
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }
    }

    private fun spinnerOnItemSelected() {
        settings__spinner_main_tag.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parentView: AdapterView<*>?,
                        selectedItemView: View,
                        position: Int,
                        id: Long) {
                        val idSelectedTag = tagIds[position]
                        editor.putInt("idTagToShowWhenAppIsOpened", idSelectedTag)
                        editor.apply()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        //no-op
                    }
                }
    }

    private fun initListeners() {
        btn_logout.setOnClickListener(this)
        btn_delete_account.setOnClickListener(this)
        settings__switch_dark_mode.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout                 -> onLogOutClicked()
            R.id.btn_delete_account         -> onDeleteAccountClicked()
            R.id.settings__switch_dark_mode -> onDarkModeSwitched()
        }
    }

    private fun onDarkModeSwitched() {
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            editor.putBoolean("isDarkModeEnabled", false)
        } else {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            editor.putBoolean("isDarkModeEnabled", true)
        }
        editor.apply()
    }

    private fun onDeleteAccountClicked() {
    }

    private fun onLogOutClicked() {
        deleteAllRememberedData()
        if (context is HomeActivityCallback) {
            (context as HomeActivityCallback).logOut()
        }
    }

    private fun onTagsLoaded(tagList: List<TagDTO>) {
        if (tagList.isNotEmpty()) {
            tagNames.clear()
            tagIds.clear()
            tagNames.add(resources.getString(R.string.menu_home))
            tagIds.add(0)

            tagNames.addAll(tagList.map { it.nombre })
            tagIds.addAll(tagList.map { it.id })
            val adapter = context?.let { it1 ->
                ArrayAdapter(it1, android.R.layout.simple_spinner_item, tagNames)
            }
            settings__spinner_main_tag.adapter = adapter
        }
        //Tag
        if (idMainTagSelected != 0) {
            settings__spinner_main_tag.setSelection(tagIds.indexOf(idMainTagSelected))
        }
    }

    private fun initObservers() {
        viewModel.allTags().observe(viewLifecycleOwner, Observer(::onTagsLoaded))
    }

    private fun deleteAllRememberedData() {
        editor.putString("nicknameToSave", "")
        editor.putString("passwordToSave", "")
        editor.putString("token", "")
        editor.putBoolean("remember_password", false)
        editor.putInt("user_id", 0)
        editor.commit()
    }
}
