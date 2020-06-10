package es.iesnervion.avazquez.askus.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import es.iesnervion.avazquez.askus.DTOs.TagDTO
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.HomeActivityCallback
import es.iesnervion.avazquez.askus.ui.fragments.tabs.all.viewmodel.MainViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.NO_CONTENT
import es.iesnervion.avazquez.askus.utils.AppConstants.PASSWORD_MIN_LENGHT
import kotlinx.android.synthetic.main.fragment_settings.*
import setVisibilityToGone
import setVisibilityToVisible

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
    var currentPassword = ""
    var tagNames = mutableListOf<String>()
    var tagIds = mutableListOf<Int>()
    var idCurrentUser = 0
    var token = ""
    var changePasswordClicked = false
    var newPassword = ""
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
        token = sharedPreference.getString("token", "").toString()
        idCurrentUser = sharedPreference.getInt("user_id", 0)
        idMainTagSelected = sharedPreference.getInt("idTagToShowWhenAppIsOpened", 0)
        isDarkModeOn = sharedPreference.getBoolean("isDarkModeEnabled", false)
        currentPassword = sharedPreference.getString("passwordToSave", "").toString()
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
        settings__change_password__save.setOnClickListener(this)
        settings__btn_expandable_view.setOnClickListener(this)
        btn_delete_account.setOnClickListener(this)
        settings__switch_dark_mode.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_logout                      -> onLogOutClicked()
            R.id.btn_delete_account              -> onDeleteAccountClicked()
            R.id.settings__switch_dark_mode      -> onDarkModeSwitched()
            R.id.settings__btn_expandable_view   -> onExpandBtnClicked()
            R.id.settings__change_password__save -> onChangePasswordSaveBtnClicked()
        }
    }

    private fun onResponseCodeReceived(responseCode: Int) {
        if (changePasswordClicked) {
            if (responseCode == NO_CONTENT) {
                Snackbar.make(settings__container, getString(R.string.password_changed),
                    Snackbar.LENGTH_SHORT).show()
                editor.putString("passwordToSave", newPassword)
                currentPassword = newPassword
                editor.commit()
                onExpandBtnClicked()
            } else {
                Snackbar.make(settings__container, getString(R.string.error_changing_password),
                    Snackbar.LENGTH_SHORT).show()
            }
        }
        changePasswordClicked = false
    }

    private fun onChangePasswordSaveBtnClicked() {
        changePasswordClicked = true
        if (newPasswordMatchs() && currentPasswordIscorrect() && settings_input_new_password.text?.length ?: 0 >= PASSWORD_MIN_LENGHT) {
            settings__change_password_lbl_error.text = ""
            newPassword = settings_input_new_password.text.toString()
            viewModel.changePassword(token = token, idUser = idCurrentUser,
                newPassword = settings_input_new_password.text.toString())
        } else {
            if (!newPasswordMatchs()) {
                settings__change_password_lbl_error.text = getString(R.string.passwords_dont_match)
            }
            if (!currentPasswordIscorrect()) {
                settings__change_password_lbl_error.text = getString(R.string.incorrect_password)
            }
            if (settings_input_new_password.text?.length ?: 0 < PASSWORD_MIN_LENGHT) {
                settings__change_password_lbl_error.text =
                        resources.getString(R.string.invalidPassword)
            }
        }
    }

    private fun currentPasswordIscorrect() =
            settings__input_old_password.text.toString() == currentPassword

    private fun onExpandBtnClicked() {
        if (settings__expandable_view_change_password.visibility == View.VISIBLE) {
            //si está abierto se debe cerrar
            settings__expandable_view_change_password.setVisibilityToGone()
            settings__input_old_password.isEnabled = false
            settings_input_new_password.isEnabled = false
            settings__input_repeat_new_password.isEnabled = false
            settings__change_password__save.isEnabled = false
            settings__btn_expandable_view.setBackgroundResource(
                R.drawable.ic_expand_more_black_24dp)
        } else {
            settings__expandable_view_change_password.setVisibilityToVisible()
            settings__input_old_password.isEnabled = true
            settings_input_new_password.isEnabled = true
            settings__input_repeat_new_password.isEnabled = true
            settings__change_password__save.isEnabled = true
            settings__btn_expandable_view.setBackgroundResource(
                R.drawable.ic_expand_less_black_24dp)
        }
    }

    private fun onResponseCodeDeleteUserReceived(responseCode: Int) {
        if (responseCode == NO_CONTENT) {
            Snackbar.make(settings__container, getString(R.string.account_successfully_removed),
                Snackbar.LENGTH_SHORT).show()
            closeSession()
        } else {
            Snackbar.make(settings__container, getString(R.string.error_deleting_account),
                Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun newPasswordMatchs() =
            settings_input_new_password.text.toString() == settings__input_repeat_new_password.text.toString()

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
        context?.let {
            AlertDialog.Builder(it).setIcon(R.drawable.ic_baseline_delete_forever_24)
                .setTitle(getString(R.string.delete_account))
                .setMessage(getString(R.string.delete_account_text))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    viewModel.deleteAccount(idCurrentUser = idCurrentUser, token = token)
                }.setNegativeButton(resources.getString(R.string.no), null).show()
        }
    }

    private fun onLogOutClicked() {
        context?.let {
            AlertDialog.Builder(it).setIcon(R.drawable.ic_exit_to_app_black_24dp)
                .setTitle(resources.getString(R.string.exit))
                .setMessage(resources.getString(R.string.user_want_to_exit))
                .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                    closeSession()
                }.setNegativeButton(resources.getString(R.string.no), null).show()
        }
    }

    private fun closeSession() {
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
        viewModel.getResponseCodePasswordChange()
            .observe(viewLifecycleOwner, Observer(::onResponseCodeReceived))
        viewModel.getResponseCodeDeleteUser()
            .observe(viewLifecycleOwner, Observer(::onResponseCodeDeleteUserReceived))
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
