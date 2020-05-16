package es.iesnervion.avazquez.askus.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.iesnervion.avazquez.askus.R
import es.iesnervion.avazquez.askus.interfaces.AuthActivityInterface
import es.iesnervion.avazquez.askus.models.Login
import es.iesnervion.avazquez.askus.ui.auth.viewmodel.AuthViewModel
import es.iesnervion.avazquez.askus.utils.AppConstants
import es.iesnervion.avazquez.askus.utils.AppConstants.INTERNAL_SERVER_ERROR
import es.iesnervion.avazquez.askus.utils.AppConstants.UNAUTHORIZED
import kotlinx.android.synthetic.main.fragment_login.*
import setVisibilityToGone
import setVisibilityToVisible

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(),
    View.OnClickListener {
    lateinit var viewModel: AuthViewModel
    lateinit var sharedPreference: SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    lateinit var observerLoadingData: Observer<Boolean>
    lateinit var observerError: Observer<Boolean>
    lateinit var observerCredentials: Observer<List<Char>>

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(AuthViewModel::class.java)
        btn_login.setOnClickListener(this)
        link_signup.setOnClickListener(this)
        rememberPassword.setOnClickListener(this)
        initObservers()
        sharedPreference =
                activity!!.getSharedPreferences(AppConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = sharedPreference.edit()
    }

    private fun initObservers() {
        observerLoadingData = Observer { loading ->
            if (loading) {
                progress_bar.setVisibilityToVisible()
                lbl_error_login.setVisibilityToGone()
            } else {
                progress_bar.setVisibilityToGone()
            }
        }
        observerError = Observer { error ->
            if (error) {
                lbl_error_login.setVisibilityToVisible()
                lbl_error_login.text = resources.getText(R.string.error_internet)
            }
        }
        observerCredentials = Observer {
            if (it.toString().contains("ERROR")) {
                if (it.toString().contains(UNAUTHORIZED.toString())) {
                    lbl_error_login.text = resources.getText(R.string.invalid_credentials)
                } else if (it.toString().contains(INTERNAL_SERVER_ERROR.toString())) {
                    lbl_error_login.text = resources.getText(R.string.internal_server_error)
                } else {
                    lbl_error_login.text = resources.getText(R.string.there_was_an_error)
                }
                lbl_error_login.setVisibilityToVisible()
            } else {
                lbl_error_login.setVisibilityToGone()
                progress_bar.setVisibilityToVisible()
            }
        }

        viewModel.getToken().observe(viewLifecycleOwner, observerCredentials)
        viewModel.loadingLiveData().observe(viewLifecycleOwner, observerLoadingData)
        viewModel.errorLiveData().observe(viewLifecycleOwner, observerError)
    }

    private fun rememberPasswordIfNeeded() {
        if (cb_keep_sesion.isChecked) {
            val passwordToSave = viewModel.login.password
            val nicknameToSave = viewModel.login.nickname
            editor.putString("passwordToSave", passwordToSave)
            editor.putString("nicknameToSave", nicknameToSave)
            editor.putBoolean("remember_password", true)
        } else {
            editor.putString("passwordToSave", "")
            editor.putString("nicknameToSave", "")
            editor.putBoolean("remember_password", false)
        }
        editor.commit()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_login -> {
                viewModel.login = Login(
                    input_nickname.text.toString().trim({ it <= ' ' }),
                    input_password.text.toString().trim({ it <= ' ' })
                )
                if (viewModel.login.nickname.isNotEmpty() && viewModel.login.password.isNotEmpty()) {
                    lbl_error_login.setVisibilityToGone()
                    rememberPasswordIfNeeded()
                    viewModel.checkLogin()
                } else {
                    lbl_error_login.text = resources.getText(R.string.fillFields)
                    lbl_error_login.setVisibilityToVisible()
                }
            }
            R.id.link_signup -> {
                if (context is AuthActivityInterface) {
                    (context as AuthActivityInterface).goToSignUp()
                }
            }
            R.id.rememberPassword -> {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(getString(R.string.password))
                builder.setPositiveButton("OK") { _, _ ->
                }
                builder.setMessage(getString(R.string.message_dialog_password_lost))
                //TODO("Mejorar esto del email con esto de stackoverflow -> https://stackoverflow.com/questions/40386812/open-email-client-in-alertdialog")
                builder.show()
            }
        }
    }
}
